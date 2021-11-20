package net.dorianpb.cem.mixins;


import net.dorianpb.cem.internal.models.CemModelEntry.TransparentCemModelPart;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeadFeatureRenderer.class)
public abstract class HeadFeatureRendererMixin{
	@Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
	          at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;rotate(Lnet/minecraft/client/util/math/MatrixStack;)V"))
	private void cem$copyHeadRotCorrectly(ModelPart instance, MatrixStack matrix){
		if(instance instanceof TransparentCemModelPart){
			((TransparentCemModelPart) instance).rotateInnerPart(matrix);
		}
		else{
			instance.rotate(matrix);
		}
	}
}