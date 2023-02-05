package net.dorianpb.cem.mixins;


import net.dorianpb.cem.internal.models.CemModelPart;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

@Mixin(FeatureRenderer.class)
public abstract class FeatureRendererMixin{
	
	private static final Map<EntityModel<? extends LivingEntity>, Optional<CemModelRegistry>> textures = new WeakHashMap<>();
	
	@ModifyVariable(method = "renderModel", at = @At("HEAD"), index = 1, argsOnly = true)
	private static <T extends LivingEntity> Identifier cem$replaceFeatureRendererTexture(Identifier texture, EntityModel<T> model){
		Optional<CemModelRegistry> registry = textures.computeIfAbsent(model, FeatureRendererMixin::getRegistry);
		
		if(registry.isPresent()){
			if(registry.get().hasTexture()){
				return registry.get().getTexture();
			}
		}
		return texture;
	}
	
	@NotNull
	private static <T extends LivingEntity> Optional<CemModelRegistry> getRegistry(EntityModel<T> model){
		Optional<Field> fieldOptional = Arrays.stream(model.getClass().getFields()).filter(field -> field.getType().equals(ModelPart.class)).findAny();
		
		if(fieldOptional.isPresent() && fieldOptional.get().canAccess(model)){
			try{
				ModelPart modelPart = (ModelPart) fieldOptional.get().get(model);
				if(modelPart instanceof CemModelPart){
					if(((CemModelPart) modelPart).getIdentifier() instanceof EntityModelLayer layer){
						return Optional.of(CemRegistryManager.getRegistry(layer));
					}
				}
			} catch(IllegalAccessException e){
				throw new RuntimeException(e);
			}
		}
		return Optional.empty();
	}
	
	@Inject(method = "render(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;" +
	                 "Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V",
	        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V", shift = Shift.AFTER))
	private static <T extends LivingEntity> void cem$applyCemAnimationsToFeatureRenderer(EntityModel<T> contextModel,
	                                                                                     EntityModel<T> model,
	                                                                                     Identifier texture,
	                                                                                     MatrixStack matrices,
	                                                                                     VertexConsumerProvider vertexConsumers,
	                                                                                     int light,
	                                                                                     T entity,
	                                                                                     float limbAngle,
	                                                                                     float limbDistance,
	                                                                                     float age,
	                                                                                     float headYaw,
	                                                                                     float headPitch,
	                                                                                     float tickDelta,
	                                                                                     float red,
	                                                                                     float green,
	                                                                                     float blue,
	                                                                                     CallbackInfo ci){
		Optional<CemModelRegistry> registry = textures.computeIfAbsent(model, FeatureRendererMixin::getRegistry);
		registry.ifPresent(cemModelRegistry -> cemModelRegistry.applyAnimations(limbAngle, limbDistance, age, headYaw, headPitch, entity));
	}
	
}