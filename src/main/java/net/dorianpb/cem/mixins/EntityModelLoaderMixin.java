package net.dorianpb.cem.mixins;

import com.google.gson.internal.LinkedTreeMap;
import net.dorianpb.cem.internal.config.CemConfigFairy;
import net.dorianpb.cem.internal.file.JemFile;
import net.dorianpb.cem.internal.util.CemFairy;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.dorianpb.cem.internal.util.OptifineFixes;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;

@Mixin(EntityModelLoader.class)
public abstract class EntityModelLoaderMixin{
 
 @SuppressWarnings("MethodMayBeStatic")
 @Inject(method = "reload", at = @At("HEAD"))
 private void cem$injectReload(ResourceManager manager, CallbackInfo ci){
  CemRegistryManager.clearRegistries();
  CemFairy.getFeatureTextures().clear();
  
  manager.findResources("cem/opticompat", path -> path.getPath().endsWith(".json"))
         .forEach((id, resource) -> loadResourceFromId(id, resource, OptifineFixes::accept, OptifineFixes::accept, false));
  
  //        manager.findResources("cem", path -> path.getPath().endsWith(".jem") && path.getNamespace().equals("dorianpb"))
  //         .forEach((id, resource) -> loadResourceFromId(manager, id));
  
  manager.findResources("cem", path -> path.getPath().endsWith(".jem") && !path.getPath().startsWith("cem/opticompat"))
         .forEach((id, resource) -> loadResourceFromId(id,
                                                       resource,
                                                       (EntityModelLayer layer, LinkedTreeMap<String, Object> json) -> acceptJemFile(layer, json, id, manager),
                                                       (BlockEntityType<? extends BlockEntity> blockEntityType, LinkedTreeMap<String, Object> json) -> acceptJemFile(
                                                               blockEntityType,
                                                               json,
                                                               id,
                                                               manager
                                                                                                                                                                    ),
                                                       true
                                                      ));
  
  if(CemConfigFairy.getConfig().useOptifineFolder()){
   manager.findResources("optifine/cem", path -> path.getPath().endsWith(".jem") && path.getNamespace().equals("minecraft"))
          .forEach((id, resource) -> loadResourceFromId(id,
                                                        resource,
                                                        (EntityModelLayer layer, LinkedTreeMap<String, Object> json) -> acceptJemFile(layer, json, id, manager),
                                                        (BlockEntityType<? extends BlockEntity> blockEntityType, LinkedTreeMap<String, Object> json) -> acceptJemFile(
                                                                blockEntityType,
                                                                json,
                                                                id,
                                                                manager
                                                                                                                                                                     ),
                                                        true
                                                       ));
  }
 }
 
 private static void loadResourceFromId(Identifier id,
                                        Resource resource,
                                        BiConsumer<EntityModelLayer, LinkedTreeMap<String, Object>> entityConsumer,
                                        BiConsumer<BlockEntityType<? extends BlockEntity>, LinkedTreeMap<String, Object>> blockEntityConsumer,
                                        boolean logging){
  if(logging){
   CemFairy.getLogger().info(id.toString());
  }
  try(InputStream stream = resource.getInputStream()){
   //initialize the file
   @SuppressWarnings("unchecked")
   LinkedTreeMap<String, Object> json = CemFairy.getGson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), LinkedTreeMap.class);
   if(json == null){
    throw new Exception("Invalid File");
   }
   
   //            JemFile file = new JemFile(json, id, manager);
   
   String entityName = getEntityNameFromId(id);
   Optional<EntityModelLayer> entityModelLayerOptional = tryParseEntityModelLayer(id);
   Optional<BlockEntityType<?>> blockEntityTypeOptional = Registry.BLOCK_ENTITY_TYPE.getOrEmpty(Identifier.tryParse(entityName));
   
   if(entityModelLayerOptional.isPresent()){
    EntityModelLayer entityModelLayer = entityModelLayerOptional.get();
    entityConsumer.accept(entityModelLayer, json);
    //                CemRegistryManager.addRegistry(entityModelLayer, file);
   }
   
   else if(blockEntityTypeOptional.isPresent()){
    BlockEntityType<? extends BlockEntity> entityType = blockEntityTypeOptional.get();
    blockEntityConsumer.accept(entityType, json);
    //                CemRegistryManager.addRegistry(entityType, file);
   }
   
   else{
    throw new NoSuchElementException("Unknown object \"" + id.getNamespace() + ":" + entityName + "\"!");
   }
  } catch(Exception exception){
   CemFairy.getLogger().error("Error parsing " + id + ":");
   String message = exception.getMessage();
   CemFairy.getLogger().error(exception);
   if(message == null || message.trim().isEmpty()){
    for(int i = 0; i < 3; i++){
     CemFairy.getLogger().error(exception.getStackTrace()[i]);
    }
   }
  }
 }
 
 private static void acceptJemFile(EntityModelLayer entityModelLayer, LinkedTreeMap<String, Object> json, Identifier id, ResourceManager manager){
  try{
   JemFile file = new JemFile(json, id, manager);
   CemRegistryManager.addRegistry(entityModelLayer, file);
   
  } catch(Exception e){
   throw new RuntimeException(e);
  }
 }
 
 private static void acceptJemFile(BlockEntityType<? extends BlockEntity> blockEntityType, LinkedTreeMap<String, Object> json, Identifier id, ResourceManager manager){
  try{
   JemFile file = new JemFile(json, id, manager);
   CemRegistryManager.addRegistry(blockEntityType, file);
   CemFairy.getLogger().error("This build of CEM doesn't support Block Entities.");
   
  } catch(Exception e){
   throw new RuntimeException(e);
  }
 }
 
 private static String getEntityNameFromId(Identifier identifier){
  String id = identifier.toString();
  return id.substring(id.lastIndexOf('/') + 1, id.lastIndexOf('.'));
 }
 
 private static Optional<EntityModelLayer> tryParseEntityModelLayer(Identifier id){
  String namespace = id.getNamespace() + ":";
  String idstr = getEntityNameFromId(id);
  
  //attempt to parse the name as is, if it works, we assume main layer, if it doesn't we assume text after last underscore is layer name if other text is an entity
  Optional<EntityType<?>> entityTypeOptional = EntityType.get(namespace + idstr);
  if(entityTypeOptional.isPresent()){
   return Optional.of(new EntityModelLayer(EntityType.getId(entityTypeOptional.get()), EntityModelLayersAccessor.getMAIN()));
  }
  else if(idstr.contains("_")){
   String name = idstr.substring(0, idstr.lastIndexOf('_'));
   String layer = idstr.substring(idstr.lastIndexOf('_') + 1);
   Optional<EntityType<?>> entityTypeOptional2 = EntityType.get(namespace + name);
   if(entityTypeOptional2.isPresent()){
    return Optional.of(new EntityModelLayer(EntityType.getId(entityTypeOptional2.get()), layer));
   }
  }
  return Optional.empty();
 }
 
 @SuppressWarnings("MethodMayBeStatic")
 @Inject(method = "getModelPart", at = @At("RETURN"), cancellable = true)
 private void cem$getModelPart(EntityModelLayer layer, CallbackInfoReturnable<ModelPart> cir){
  if(CemRegistryManager.hasEntityLayer(layer)){
   if(!OptifineFixes.hasFixesFor(layer)){
    CemFairy.getLogger().warn("No fixes for " + layer);
   }
   cir.setReturnValue(CemRegistryManager.getRegistry(layer)
                                        .prepRootPart(cir.getReturnValue(),
                                                      OptifineFixes.getPartNames(layer),
                                                      OptifineFixes.getModelFixes(layer),
                                                      layer
                                                     ));
  }
 }
}