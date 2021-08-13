package net.dorianpb.cem.mixins;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(EntityRenderers.class)
@SuppressWarnings("unused")
public interface EntityRendererAccessor{
	@Invoker
	static void callRegister(EntityType<? extends Entity> type, EntityRendererFactory<? extends Entity> factory){
		throw new UnsupportedOperationException();
	}
	
	@Accessor
	static Map<EntityType<?>, EntityRendererFactory<?>> getRENDERER_FACTORIES(){
		throw new UnsupportedOperationException();
	}
}