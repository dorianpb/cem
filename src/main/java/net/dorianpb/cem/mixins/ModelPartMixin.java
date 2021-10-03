package net.dorianpb.cem.mixins;

import net.dorianpb.cem.internal.models.CemModelEntry.CemModelPart;
import net.dorianpb.cem.internal.models.CemModelEntry.TransparentCemModelPart;
import net.minecraft.client.model.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPart.class)
public class ModelPartMixin{
	@Shadow public float pitch;
	
	@Shadow public float yaw;
	
	@Shadow public float roll;
	
	@Shadow public float pivotX;
	
	@Shadow public float pivotY;
	
	@Shadow public float pivotZ;
	
	@Inject(method = "copyTransform", at = @At(value = "TAIL"))
	private void cem$HandleTransparentModelPartsOnCopy(ModelPart part, CallbackInfo ci){
		if(part instanceof TransparentCemModelPart){
			this.pitch += ((CemModelPart) part.children.get("my_precious")).getRotation('x');
			this.yaw += ((CemModelPart) part.children.get("my_precious")).getRotation('y');
			this.roll += ((CemModelPart) part.children.get("my_precious")).getRotation('z');
			this.pivotX += part.children.get("my_precious").pivotX;
			this.pivotY += part.children.get("my_precious").pivotY;
			this.pivotZ += part.children.get("my_precious").pivotZ;
		}
	}
	
}