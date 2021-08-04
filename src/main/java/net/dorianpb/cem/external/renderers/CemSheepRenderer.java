package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemSheepModel;
import net.dorianpb.cem.external.models.CemSheepModel.CemSheepWoolModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

public class CemSheepRenderer extends SheepEntityRenderer implements CemRenderer{
	private final CemModelRegistry registry;
	
	public CemSheepRenderer(EntityRendererFactory.Context context){
		super(context);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemSheepModel(registry);
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
			this.features.replaceAll((feature) -> {
				if(feature instanceof SheepWoolFeatureRenderer && CemRegistryManager.hasEntity("sheep_wool")){
					return new CemSheepWoolFeatureRenderer(this, context.getModelLoader());
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
		return EntityType.SHEEP;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(SheepEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
	
	public static class CemSheepWoolFeatureRenderer extends SheepWoolFeatureRenderer implements CemRenderer{
		private static final Identifier       origSKIN = SKIN;
		private final        CemModelRegistry registry;
		
		public CemSheepWoolFeatureRenderer(CemSheepRenderer featureRendererContext, EntityModelLoader modelLoader){
			super(featureRendererContext, modelLoader);
			this.registry = CemRegistryManager.getRegistry(this.getId());
			try{
				this.model = new CemSheepWoolModel(registry);
				if(this.registry.hasTexture()){
					SKIN = this.registry.getTexture();
				}
				else{
					SKIN = origSKIN;
				}
			} catch(Exception e){
				modelError(e);
			}
		}
		
		@Override
		public String getId(){
			return "sheep_wool";
		}
		
		@Override
		public Identifier getTexture(SheepEntity entity){
			if(this.registry != null && this.registry.hasTexture()){
				return this.registry.getTexture();
			}
			return super.getTexture(entity);
		}
	}
}