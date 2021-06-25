package net.dorianpb.cem.internal.api;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CemEntityInitializer{
	private final Map<EntityType<? extends Entity>, CemFactory>           cemEntityFactories      = new HashMap<>();
	private final Map<BlockEntityType<? extends BlockEntity>, CemFactory> cemBlockEntityFactories = new HashMap<>();
	private final List<String>                                            cemOthers               = new ArrayList<>();
	
	public abstract void onInit();
	
	public final Map<EntityType<? extends Entity>, CemFactory> getCemEntityFactories(){
		return cemEntityFactories;
	}
	
	public final Map<BlockEntityType<? extends BlockEntity>, CemFactory> getCemBlockEntityFactories(){
		return cemBlockEntityFactories;
	}
	
	public final List<String> getCemOthers(){
		return cemOthers;
	}
	
	public final void register(String type){
		this.cemOthers.add(type);
	}
	
	public final <T extends Entity> void register(EntityType<? extends T> type, Class<? extends CemRenderer> renderer, Object... params){
		this.cemEntityFactories.put(type, new CemFactory(renderer, params));
	}
	
	public final <T extends BlockEntity> void register(BlockEntityType<? extends T> type, Class<? extends CemRenderer> renderer, Object... params){
		this.cemBlockEntityFactories.put(type, new CemFactory(renderer, params));
	}
	
	public final int getSize(){
		return this.cemEntityFactories.size() + this.cemBlockEntityFactories.size() + this.cemOthers.size();
	}
}