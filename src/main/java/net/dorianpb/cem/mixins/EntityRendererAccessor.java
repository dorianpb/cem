package net.dorianpb.cem.mixins;

import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderer.class)
public interface EntityRendererAccessor{
	@Accessor
	void setShadowRadius(float shadowRadius);
}