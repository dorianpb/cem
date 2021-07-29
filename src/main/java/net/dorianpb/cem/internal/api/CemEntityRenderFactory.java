package net.dorianpb.cem.internal.api;

import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface CemEntityRenderFactory{
	
	@SuppressWarnings("unchecked")
	default EntityRenderer<Entity> create(EntityRendererFactory.Context ctx,
	                                      EntityType<? extends Entity> type,
	                                      @Nullable EntityRendererFactory<? extends Entity> vanillaFactory){
		if(CemRegistryManager.hasEntity(type)){
			CemRenderer renderer = create1(ctx);
			if(EntityRenderer.class.isAssignableFrom(renderer.getClass())){
				return (EntityRenderer<Entity>) renderer;
			}
			else{
				throw new IllegalArgumentException(renderer.getId() + " needs to extend EntityRenderer!");
			}
		}
		else if(vanillaFactory != null){
			return (EntityRenderer<Entity>) vanillaFactory.create(ctx);
		}
		else{
			throw new NullPointerException("Error creating EntityRenderer for " + type);
		}
	}
	
	CemRenderer create1(EntityRendererFactory.Context ctx);
}