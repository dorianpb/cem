package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemCatModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;

public class CemCatRenderer extends CatEntityRenderer implements CemRenderer{
	private final CemModelRegistry registry;
	
	public CemCatRenderer(EntityRendererFactory.Context context){
		super(context);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemCatModel(registry, null);
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
			this.features.replaceAll((feature) -> {
				if(feature instanceof CatCollarFeatureRenderer){
					return new CemCatCollarFeatureRenderer(this, context.getModelLoader());
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
		return EntityType.CAT;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(CatEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
	
	public static class CemCatCollarFeatureRenderer extends CatCollarFeatureRenderer implements CemRenderer{
		private final CemModelRegistry registry;
		
		public CemCatCollarFeatureRenderer(CemCatRenderer featureRendererContext, EntityModelLoader modelLoader){
			super(featureRendererContext, modelLoader);
			this.registry = CemRegistryManager.getRegistry(EntityType.CAT);
			try{
				this.model = new CemCatModel(registry, 0.01F);
			} catch(Exception e){
				modelError(e);
			}
		}
		
		@Override
		public String getId(){
			return "cat_collar";
		}
		
		@Override
		public Identifier getTexture(CatEntity entity){
			if(this.registry != null && this.registry.hasTexture()){
				return this.registry.getTexture();
			}
			return super.getTexture(entity);
		}
	}
}