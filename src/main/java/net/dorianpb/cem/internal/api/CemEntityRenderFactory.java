package net.dorianpb.cem.internal.api;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;

@FunctionalInterface
public interface CemEntityRenderFactory{
	@SuppressWarnings("unchecked")
	default EntityRenderer<Entity> create(EntityRendererFactory.Context ctx){
		CemRenderer renderer = create1(ctx);
		if(EntityRenderer.class.isAssignableFrom(renderer.getClass())){
			return (EntityRenderer<Entity>) renderer;
		}
		else{
			throw new IllegalArgumentException(renderer.getId() + " needs to extend EntityRenderer!");
		}
	}
	
	CemRenderer create1(EntityRendererFactory.Context ctx);
}