package net.dorianpb.cem.mixins;

import net.dorianpb.cem.internal.api.CemModel;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin<T extends LivingEntity>{
	@Redirect(method = "renderArmor",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;setAttributes(Lnet/minecraft/client/render/entity/model/BipedEntityModel;)V"))
	private void cem$redirectSetAttributes(BipedEntityModel<T> parent, BipedEntityModel<T> child){
		parent.setAttributes(child);
		if(parent instanceof CemModel){
			child.leftArm.pitch += 1.57;
			child.rightArm.pitch += 1.57;
		}
	}
}