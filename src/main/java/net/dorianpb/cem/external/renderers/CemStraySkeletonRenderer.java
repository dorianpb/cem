package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemSkeletonModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelEntry.CemModelPart;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.StrayEntityRenderer;
import net.minecraft.client.render.entity.feature.StrayOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemStraySkeletonRenderer extends StrayEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new HashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private final        CemModelRegistry          registry;
	
	static{
		partNames.put("headwear", "hat");
	}
	
	public CemStraySkeletonRenderer(EntityRendererFactory.Context context){
		super(context);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemSkeletonModel(this.registry.prepRootPart(partNames, parentChildPairs, context.getPart(EntityModelLayers.STRAY)), registry);
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
			this.features.replaceAll((feature) -> {
				if(feature instanceof StrayOverlayFeatureRenderer<AbstractSkeletonEntity, SkeletonEntityModel<AbstractSkeletonEntity>>){
					return new CemStrayOverlayRenderer(this, context.getModelLoader());
				}
				else{
					return feature;
				}
			});
		} catch(Exception e){
			modelError(e);
		}
	}
	
	private static EntityType<? extends Entity> getType(){
		return EntityType.STRAY;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(AbstractSkeletonEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
	
	public static class CemStrayOverlayRenderer extends StrayOverlayFeatureRenderer<AbstractSkeletonEntity, SkeletonEntityModel<AbstractSkeletonEntity>> implements CemRenderer{
		private static final Map<String, String>       partNames        = CemStraySkeletonRenderer.partNames;
		private static final Map<String, List<String>> parentChildPairs = CemStraySkeletonRenderer.parentChildPairs;
		private static final Identifier                origSKIN         = SKIN;
		private final        CemModelRegistry          registry;
		
		public CemStrayOverlayRenderer(CemStraySkeletonRenderer featureRendererContext, EntityModelLoader modelLoader){
			super(featureRendererContext, modelLoader);
			if(CemRegistryManager.hasEntity(this.getId())){
				this.registry = CemRegistryManager.getRegistry(this.getId());
				if(this.registry.hasTexture()){
					SKIN = this.registry.getTexture();
				}
				else{
					SKIN = origSKIN;
				}
			}
			else{
				this.registry = CemRegistryManager.getRegistry(CemStraySkeletonRenderer.getType());
				SKIN = origSKIN;
			}
			try{
				CemModelPart rootPart = this.registry.prepRootPart(partNames, parentChildPairs, modelLoader.getModelPart(EntityModelLayers.STRAY_OUTER), 0.25F);
				this.model = new CemSkeletonModel(rootPart, registry);
				
			} catch(Exception e){
				modelError(e);
			}
		}
		
		@Override
		public String getId(){
			return "stray_outer";
		}
		
		@Override
		public Identifier getTexture(AbstractSkeletonEntity entity){
			if(this.registry != null && this.registry.hasTexture()){
				return this.registry.getTexture();
			}
			return super.getTexture(entity);
		}
	}
}