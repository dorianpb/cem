package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemSlimeModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;

public class CemSlimeRenderer extends SlimeEntityRenderer implements CemRenderer{
	private final CemModelRegistry registry;
	
	public CemSlimeRenderer(EntityRendererFactory.Context context){
		super(context);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemSlimeModel(registry);
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
			this.features.replaceAll((feature) -> {
				if(feature instanceof SlimeOverlayFeatureRenderer<SlimeEntity> && CemRegistryManager.hasEntity("slime_gel")){
					return new CemSlimeOverlayFeatureRenderer(this, context.getModelLoader());
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
		return EntityType.SLIME;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(SlimeEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
	
	public static class CemSlimeOverlayFeatureRenderer extends SlimeOverlayFeatureRenderer<SlimeEntity> implements CemRenderer{
		private final CemModelRegistry registry;
		
		public CemSlimeOverlayFeatureRenderer(CemSlimeRenderer featureRendererContext, EntityModelLoader modelLoader){
			super(featureRendererContext, modelLoader);
			this.registry = CemRegistryManager.getRegistry(this.getId());
			try{
				this.model = new CemSlimeModel(registry);
				
			} catch(Exception e){
				modelError(e);
			}
		}
		
		@Override
		public String getId(){
			return "slime_gel";
		}
		
		@Override
		public Identifier getTexture(SlimeEntity entity){
			if(this.registry != null && this.registry.hasTexture()){
				return this.registry.getTexture();
			}
			return super.getTexture(entity);
		}
	}
}