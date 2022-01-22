package net.dorianpb.cem.mixins;

import net.dorianpb.cem.internal.models.CemModelEntry.CemCuboid;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelCuboidData.class)
public abstract class ModelCuboidDataMixin{
	@Final
	@Shadow
	private Vec3f offset, dimensions;
	
	@Final
	@Shadow
	private Dilation extraSize;
	
	@Final
	@Shadow
	private boolean mirror;
	
	@Final
	@Shadow
	private Vector2f textureUV, textureScale;
	
	@Inject(method = "createCuboid", at = @At("RETURN"), cancellable = true)
	private void cem$injectCuboid(int textureWidth, int textureHeight, CallbackInfoReturnable<Cuboid> cir){
		cir.setReturnValue(new CemCuboid(this.offset.getX(),
		                                 this.offset.getY(),
		                                 this.offset.getZ(),
		                                 this.dimensions.getX(),
		                                 this.dimensions.getY(),
		                                 this.dimensions.getZ(),
		                                 ((DilationAccessor) this.extraSize).getRadiusX(),
		                                 ((DilationAccessor) this.extraSize).getRadiusY(),
		                                 ((DilationAccessor) this.extraSize).getRadiusZ(),
		                                 this.mirror,
		                                 false,
		                                 (int) (textureWidth * this.textureScale.getX()),
		                                 (int) (textureHeight * this.textureScale.getY()),
		                                 (int) this.textureUV.getX(),
		                                 (int) this.textureUV.getY()
		));
	}
	
}