package net.dorianpb.cem.mixins;


import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @SuppressWarnings("MethodMayBeStatic")
    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"))
    private <T extends Entity> void cem$applyCemAnimationsToLivingEntity(EntityModel<T> model,
                                                                         T entity,
                                                                         float limbAngle,
                                                                         float limbDistance,
                                                                         float animationProgress,
                                                                         float headYaw,
                                                                         float headPitch) {
        model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        if(CemRegistryManager.hasEntity(entity.getType())) {
            CemRegistryManager.getRegistry(entity.getType()).applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
        }
    }

}