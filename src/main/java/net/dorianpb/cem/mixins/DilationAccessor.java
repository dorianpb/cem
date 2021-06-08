package net.dorianpb.cem.mixins;

import net.minecraft.client.model.Dilation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Dilation.class)
public interface DilationAccessor{
	@Accessor
	float getRadiusX();
	
	@Accessor
	float getRadiusY();
	
	@Accessor
	float getRadiusZ();
}