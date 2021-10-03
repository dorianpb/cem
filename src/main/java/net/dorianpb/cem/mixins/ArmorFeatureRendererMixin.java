package net.dorianpb.cem.mixins;

import net.dorianpb.cem.internal.api.CemModel;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
	
	@Inject(method = "getArmorTexture", at = @At(value = "HEAD"), cancellable = true)
	private void yeah(ArmorItem item, boolean legs, String overlay, CallbackInfoReturnable<Identifier> cir){
		cir.setReturnValue(new Identifier("not_here.png"));
	}
	
}