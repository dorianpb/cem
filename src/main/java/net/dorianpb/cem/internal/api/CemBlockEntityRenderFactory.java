package net.dorianpb.cem.internal.api;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

@FunctionalInterface
public interface CemBlockEntityRenderFactory{
	@SuppressWarnings("unchecked")
	default BlockEntityRenderer<BlockEntity> create(BlockEntityRendererFactory.Context ctx){
		var renderer = create1(ctx);
		if(BlockEntityRenderer.class.isAssignableFrom(renderer.getClass())){
			return (BlockEntityRenderer<BlockEntity>) renderer;
		}
		else{
			throw new IllegalArgumentException(renderer.getId() + " needs to extend EntityRender!");
		}
	}
	
	CemRenderer create1(BlockEntityRendererFactory.Context ctx);
}