package net.dorianpb.cem.internal.util;

import net.dorianpb.cem.internal.file.JemFile;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.HashMap;

public class CemRegistryManager{
	private static final HashMap<EntityType<? extends Entity>, JemFile>           EntityTypeRegistries      = new HashMap<>();
	private static final HashMap<BlockEntityType<? extends BlockEntity>, JemFile> BlockEntityTypeRegistries = new HashMap<>();
	private static final HashMap<String, JemFile>                                 OtherRegistries           = new HashMap<>();
	
	
	public static void addRegistry(EntityType<? extends Entity> entityType, JemFile file){
		if(file != null){
			EntityTypeRegistries.put(entityType, file);
		}
	}
	
	public static void addRegistry(BlockEntityType<? extends BlockEntity> entityType, JemFile file){
		if(file != null){
			BlockEntityTypeRegistries.put(entityType, file);
		}
	}
	
	public static void addRegistry(String entityType, JemFile file){
		if(file != null){
			OtherRegistries.put(entityType, file);
		}
	}
	
	public static boolean hasEntity(EntityType<? extends Entity> entityType){
		return EntityTypeRegistries.containsKey(entityType);
	}
	
	public static boolean hasEntity(BlockEntityType<? extends BlockEntity> entityType){
		return BlockEntityTypeRegistries.containsKey(entityType);
	}
	
	public static boolean hasEntity(String entityType){
		return OtherRegistries.containsKey(entityType);
	}
	
	public static CemModelRegistry getRegistry(EntityType<? extends Entity> entityType){
		return new CemModelRegistry(EntityTypeRegistries.get(entityType));
	}
	
	public static CemModelRegistry getArmorRegistry(EntityType<? extends Entity> entityType){
		return new CemModelRegistry(EntityTypeRegistries.get(entityType).getArmorVarient());
	}
	
	public static CemModelRegistry getRegistry(BlockEntityType<? extends BlockEntity> entityType){
		return new CemModelRegistry(BlockEntityTypeRegistries.get(entityType));
	}
	
	public static CemModelRegistry getRegistry(String entityType){
		return new CemModelRegistry(OtherRegistries.get(entityType));
	}
	
	public static void clearRegistries(){
		EntityTypeRegistries.clear();
		BlockEntityTypeRegistries.clear();
		OtherRegistries.clear();
	}
}