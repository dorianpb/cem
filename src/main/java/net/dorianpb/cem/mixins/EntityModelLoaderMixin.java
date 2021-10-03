package net.dorianpb.cem.mixins;

import com.google.gson.internal.LinkedTreeMap;
import net.dorianpb.cem.internal.config.CemConfigFairy;
import net.dorianpb.cem.internal.file.JemFile;
import net.dorianpb.cem.internal.util.CemFairy;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Mixin(EntityModelLoader.class)
public abstract class EntityModelLoaderMixin{
	@Inject(method = "reload", at = @At(value = "HEAD"))
	private void cem$injectReload(ResourceManager manager, CallbackInfo ci){
		CemRegistryManager.clearRegistries();
		manager.findResources("cem", path -> path.endsWith(".jem")).forEach((id -> loadResourceFromId(manager, id, "dorianpb")));
		if(CemConfigFairy.getConfig().useOptifineFolder()){
			manager.findResources("optifine/cem", path -> path.endsWith(".jem")).forEach((id -> loadResourceFromId(manager, id, "minecraft")));
		}
	}
	
	private void loadResourceFromId(ResourceManager manager, Identifier id, String namespace){
		if(!id.getNamespace().equals(namespace)){
			return;
		}
		CemFairy.getLogger().info(id.toString());
		try(InputStream stream = manager.getResource(id).getInputStream()){
			//initialize the file
			@SuppressWarnings("unchecked")
			LinkedTreeMap<String, Object> json = CemFairy.getGson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), LinkedTreeMap.class);
			if(json == null){
				throw new Exception("Invalid File");
			}
			JemFile file = new JemFile(json, id, manager);
			
			String entityName = CemFairy.getEntityNameFromId(id);
			Optional<EntityType<?>> entityTypeOptional = EntityType.get(entityName);
			Optional<BlockEntityType<?>> blockEntityTypeOptional = Registry.BLOCK_ENTITY_TYPE.getOrEmpty(Identifier.tryParse(entityName));
			
			if(entityTypeOptional.isPresent()){
				EntityType<? extends Entity> entityType = entityTypeOptional.get();
				if(CemFairy.isUnsupported(entityType)){
					throw new Exception("Entity \"" + EntityType.getId(entityType) + "\" is unsupported!");
				}
				CemRegistryManager.addRegistry(entityType, file);
			}
			
			else if(blockEntityTypeOptional.isPresent()){
				BlockEntityType<? extends BlockEntity> entityType = blockEntityTypeOptional.get();
				if(CemFairy.isUnsupported(entityType)){
					throw new Exception("Block Entity \"" + BlockEntityType.getId(entityType) + "\" is unsupported!");
				}
				CemRegistryManager.addRegistry(entityType, file);
			}
			
			else{
				if(CemFairy.isUnsupported(entityName)){
					throw new Exception("Unknown object \"" + entityName + "\"!");
				}
				else{
					CemRegistryManager.addRegistry(entityName, file);
				}
			}
		} catch(Exception exception){
			CemFairy.getLogger().error("Error parsing " + id + ":");
			String message = exception.getMessage();
			CemFairy.getLogger().error(exception);
			if(message == null || message.trim().equals("")){
				CemFairy.getLogger().error(exception.getStackTrace()[0]);
				CemFairy.getLogger().error(exception.getStackTrace()[1]);
				CemFairy.getLogger().error(exception.getStackTrace()[2]);
			}
		}
	}
}