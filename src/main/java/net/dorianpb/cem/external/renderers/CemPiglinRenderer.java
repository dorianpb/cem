package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemPiglinModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PiglinEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemPiglinRenderer extends PiglinEntityRenderer implements CemRenderer{
	private static final Map<String, String>          partNames        = new LinkedHashMap<>();
	private static final Map<String, List<String>>    parentChildPairs = new LinkedHashMap<>();
	private final        EntityType<? extends Entity> entityType;
	private              CemModelRegistry             registry;
	
	static{
		partNames.put("headwear", "hat");
	}
	
	static{
		parentChildPairs.put("head", Arrays.asList("left_ear", "right_ear"));
	}
	
	public CemPiglinRenderer(EntityRendererFactory.Context context, EntityType<? extends Entity> entityType){
		super(context, getLayer(entityType, "main"), getLayer(entityType, "inner"), getLayer(entityType, "outer"), entityType.equals(EntityType.ZOMBIFIED_PIGLIN));
		this.entityType = entityType;
		if(CemRegistryManager.hasEntity(entityType)){
			this.registry = CemRegistryManager.getRegistry(entityType);
			try{
				this.registry.setChildren(parentChildPairs);
				this.model = getCemPiglinModel(this.registry, entityType.equals(EntityType.ZOMBIFIED_PIGLIN), null);
				if(registry.hasShadowRadius()){
					this.shadowRadius = registry.getShadowRadius();
				}
				this.features.set(0,
				                  new ArmorFeatureRenderer<MobEntity, PiglinEntityModel<MobEntity>, PiglinEntityModel<MobEntity>>(this,
				                                                                                                                  getCemPiglinModel(CemRegistryManager.getRegistry(
						                                                                                                                  entityType),
				                                                                                                                                    entityType.equals(
						                                                                                                                                    EntityType.ZOMBIFIED_PIGLIN),
				                                                                                                                                    0.5F
				                                                                                                                                   ),
				                                                                                                                  getCemPiglinModel(CemRegistryManager.getRegistry(
						                                                                                                                  entityType),
				                                                                                                                                    entityType.equals(
						                                                                                                                                    EntityType.ZOMBIFIED_PIGLIN),
				                                                                                                                                    1.02F
				                                                                                                                                   )
				                  )
				                 );
			} catch(Exception e){
				modelError(e);
			}
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
	
	private static CemPiglinModel getCemPiglinModel(CemModelRegistry registry, boolean zombie, @Nullable Float inflate){
		CemPiglinModel piglinEntityModel = new CemPiglinModel(registry.prepRootPart(partNames, inflate), registry);
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