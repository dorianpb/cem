package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemPiglinModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemArmorModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PiglinEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

public class CemPiglinRenderer extends PiglinEntityRenderer implements CemRenderer{
	private final EntityType<? extends MobEntity> entityType;
	private final CemModelRegistry                registry;
	
	public CemPiglinRenderer(EntityRendererFactory.Context context, EntityType<? extends MobEntity> entityType){
		super(context, getLayer(entityType, "main"), getLayer(entityType, "inner"), getLayer(entityType, "outer"), entityType.equals(EntityType.ZOMBIFIED_PIGLIN));
		this.entityType = entityType;
		this.registry = CemRegistryManager.getRegistry(entityType);
		try{
			this.model = getCemPiglinModel(this.registry, entityType.equals(EntityType.ZOMBIFIED_PIGLIN));
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
			this.features.replaceAll((feature) -> {
				if(feature instanceof ArmorFeatureRenderer){
					return new ArmorFeatureRenderer<>(this, new CemArmorModel<>((CemPiglinModel) this.model, 0.5F), new CemArmorModel<>((CemPiglinModel) this.model, 1.02F));
				}
				else{
					return feature;
				}
			});
		} catch(Exception e){
			modelError(e);
		}
	}
	
	private static EntityModelLayer getLayer(EntityType<? extends Entity> entityType, String part){
		if(entityType.equals(EntityType.PIGLIN)){
			return switch(part){
				case "main" -> EntityModelLayers.PIGLIN;
				case "inner" -> EntityModelLayers.PIGLIN_INNER_ARMOR;
				case "outer" -> EntityModelLayers.PIGLIN_OUTER_ARMOR;
				default -> throw new IllegalStateException("Unexpected value: " + part);
			};
		}
		else if(entityType.equals(EntityType.PIGLIN_BRUTE)){
			return switch(part){
				case "main" -> EntityModelLayers.PIGLIN_BRUTE;
				case "inner" -> EntityModelLayers.PIGLIN_BRUTE_INNER_ARMOR;
				case "outer" -> EntityModelLayers.PIGLIN_BRUTE_OUTER_ARMOR;
				default -> throw new IllegalStateException("Unexpected value: " + part);
			};
		}
		else if(entityType.equals(EntityType.ZOMBIFIED_PIGLIN)){
			return switch(part){
				case "main" -> EntityModelLayers.ZOMBIFIED_PIGLIN;
				case "inner" -> EntityModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR;
				case "outer" -> EntityModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR;
				default -> throw new IllegalStateException("Unexpected value: " + part);
			};
		}
		else{
			return null;
		}
	}
	
	private CemPiglinModel getCemPiglinModel(CemModelRegistry registry, boolean zombie){
		CemPiglinModel piglinEntityModel = new CemPiglinModel(registry);
		if(zombie){
			piglinEntityModel.rightEar.visible = false;
		}
		return piglinEntityModel;
	}
	
	@Override
	public String getId(){
		return entityType.toString();
	}
	
	@Override
	public Identifier getTexture(MobEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}