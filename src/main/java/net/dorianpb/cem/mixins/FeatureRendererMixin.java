package net.dorianpb.cem.mixins;


import net.dorianpb.cem.internal.models.CemModelPart;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemFairy;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

@Mixin(FeatureRenderer.class)
public abstract class FeatureRendererMixin{
	
	private static final Identifier absent     = new Identifier("");
	private static       Identifier currenttex = absent;
	
	
	@Inject(method = "renderModel", at = @At("HEAD"))
	private static <T extends LivingEntity> void cem$kms(EntityModel<T> model,
	                                                     Identifier texture,
	                                                     MatrixStack matrices,
	                                                     VertexConsumerProvider vertexConsumers,
	                                                     int light,
	                                                     T entity,
	                                                     float red,
	                                                     float green,
	                                                     float blue,
	                                                     CallbackInfo ci){
		currenttex = CemFairy.getFeatureTextures().computeIfAbsent(model.hashCode(), (key) -> {
			EntityModelLayer layer = cem$getCemModelPartForFeatureRenderer(model);
			if(CemRegistryManager.hasEntityLayer(layer)){
				CemModelRegistry registry = CemRegistryManager.getRegistry(layer);
				if(registry.hasTexture()){
					return registry.getTexture();
				}
			}
			return absent;
		});
	}
	
	private static @Nullable EntityModelLayer cem$getCemModelPartForFeatureRenderer(EntityModel<? extends Entity> model){
		Optional<Field> fieldOptional = Arrays.stream(model.getClass().getFields()).filter(field -> field.getType().equals(ModelPart.class)).findAny();
		if(fieldOptional.isPresent() && fieldOptional.get().canAccess(model)){
			try{
				ModelPart modelPart = (ModelPart) fieldOptional.get().get(model);
				if(modelPart instanceof CemModelPart){
					if(((CemModelPart) modelPart).getIdentifier() instanceof EntityModelLayer){
						return (EntityModelLayer) ((CemModelPart) modelPart).getIdentifier();
					}
				}
			} catch(IllegalAccessException e){
				throw new RuntimeException(e);
			}
		}
		return null;
	}
	
	@ModifyArg(method = "renderModel",
	           at = @At(value = "INVOKE",
	                    target = "Lnet/minecraft/client/render/RenderLayer;getEntityCutoutNoCull(Lnet/minecraft/util/Identifier;" +
	                             ")Lnet/minecraft/client/render/RenderLayer;"))
	private static Identifier cem$overrideEntityFeatureTexture(Identifier texture){
		return currenttex == absent? texture : currenttex;
	}
}