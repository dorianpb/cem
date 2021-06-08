package net.dorianpb.cem.mixins;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderers.class)
@SuppressWarnings("unused")
public interface EntityRendererAccessor{
	@Invoker
	static <T extends Entity> void callRegister(EntityType<? extends T> type, EntityRendererFactory<T> factory){
		throw new UnsupportedOperationException();
	}
}