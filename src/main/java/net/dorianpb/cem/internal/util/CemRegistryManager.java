package net.dorianpb.cem.internal.util;

import net.dorianpb.cem.internal.file.JemFile;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.mixins.EntityModelLayersAccessor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.HashMap;

public enum CemRegistryManager {
    ;
    private static final HashMap<EntityModelLayer, CemModelRegistry>                       EntityTypeRegistries      = new HashMap<>();
    private static final HashMap<BlockEntityType<? extends BlockEntity>, CemModelRegistry> BlockEntityTypeRegistries = new HashMap<>();
    private static final HashMap<String, CemModelRegistry>                                 OtherRegistries           = new HashMap<>();


    public static void addRegistry(EntityModelLayer entityModelLayer, JemFile file) {
        if(file != null) {
            EntityTypeRegistries.put(entityModelLayer, new CemModelRegistry(file));
        }
    }

    public static void addRegistry(BlockEntityType<? extends BlockEntity> entityType, JemFile file) {
        if(file != null) {
            BlockEntityTypeRegistries.put(entityType, new CemModelRegistry(file));
        }
    }

    public static void addRegistry(String entityType, JemFile file) {
        if(file != null) {
            OtherRegistries.put(entityType, new CemModelRegistry(file));
        }
    }

    public static boolean hasEntity(EntityType<? extends Entity> entityType) {
        return EntityTypeRegistries.containsKey(new EntityModelLayer(EntityType.getId(entityType), EntityModelLayersAccessor.getMAIN()));
    }

    public static boolean hasEntityLayer(EntityModelLayer entityModelLayer) {
        return EntityTypeRegistries.containsKey(entityModelLayer);
    }

    public static boolean hasEntity(BlockEntityType<? extends BlockEntity> entityType) {
        return BlockEntityTypeRegistries.containsKey(entityType);
    }

    public static boolean hasEntity(String entityType) {
        return OtherRegistries.containsKey(entityType);
    }

    public static CemModelRegistry getRegistry(EntityModelLayer entityModelLayer) {
        return EntityTypeRegistries.get(entityModelLayer);
    }

    public static CemModelRegistry getRegistry(EntityType<? extends Entity> entityType) {
        return EntityTypeRegistries.get(new EntityModelLayer(EntityType.getId(entityType), EntityModelLayersAccessor.getMAIN()));
    }

    //	public static CemModelRegistry getArmorRegistry(EntityType<? extends Entity> entityType){
    //		return new CemModelRegistry(EntityTypeRegistries.get(entityType).getArmorVarient());
    //	}

    public static CemModelRegistry getRegistry(BlockEntityType<? extends BlockEntity> entityType) {
        return BlockEntityTypeRegistries.get(entityType);
    }

    public static CemModelRegistry getRegistry(String entityType) {
        return OtherRegistries.get(entityType);
    }

    public static void clearRegistries() {
        EntityTypeRegistries.clear();
        BlockEntityTypeRegistries.clear();
        OtherRegistries.clear();
    }
}