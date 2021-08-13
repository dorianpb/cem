package net.dorianpb.cem.internal.api;

import net.dorianpb.cem.mixins.BlockEntityRendererAccessor;
import net.dorianpb.cem.mixins.EntityRendererAccessor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CemEntityInitializer{
	private final Map<EntityType<? extends Entity>, EntityRendererFactory<? extends Entity>>                     cemEntityFactories      = new HashMap<>();
	private final Map<BlockEntityType<? extends BlockEntity>, BlockEntityRendererFactory<? extends BlockEntity>> cemBlockEntityFactories = new HashMap<>();
	private final List<String>                                                                                   cemOthers               = new ArrayList<>();
	
	public abstract void onInit();
	
	public final Map<EntityType<? extends Entity>, EntityRendererFactory<? extends Entity>> getCemEntityFactories(){
		return cemEntityFactories;
	}
	
	public final Map<BlockEntityType<? extends BlockEntity>, BlockEntityRendererFactory<? extends BlockEntity>> getCemBlockEntityFactories(){
		return cemBlockEntityFactories;
	}
	
	public final List<String> getCemOthers(){
		return cemOthers;
	}
	
	public final void register(String type){
		this.cemOthers.add(type);
	}
	
	public final <T extends Entity> void register(EntityType<? extends T> type, CemEntityRenderFactory factory){
		var vanilla = EntityRendererAccessor.getRENDERER_FACTORIES().get(type);
		this.cemEntityFactories.put(type, (ctx) -> factory.create(ctx, type, vanilla));
	}
	
	public final <T extends BlockEntity> void register(BlockEntityType<? extends T> type, CemBlockEntityRenderFactory factory){
		var vanilla = BlockEntityRendererAccessor.getFACTORIES().get(type);
		this.cemBlockEntityFactories.put(type, (ctx) -> factory.create(ctx, type, vanilla));
	}
	
	public final int getSize(){
		return this.cemEntityFactories.size() + this.cemBlockEntityFactories.size() + this.cemOthers.size();
	}
}