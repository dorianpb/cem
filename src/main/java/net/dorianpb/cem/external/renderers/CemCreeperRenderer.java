package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemCreeperModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.CreeperEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

public class CemCreeperRenderer extends CreeperEntityRenderer implements CemRenderer{
	private final CemModelRegistry registry;
	
	public CemCreeperRenderer(EntityRendererFactory.Context context){
		super(context);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemCreeperModel(registry, null);
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
			this.features.replaceAll((feature) -> {
				if(feature instanceof CreeperChargeFeatureRenderer){
					return new CemCreeperChargeFeatureRenderer(this, context.getModelLoader());
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
		return EntityType.CREEPER;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(CreeperEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
	
	public static class CemCreeperChargeFeatureRenderer extends CreeperChargeFeatureRenderer implements CemRenderer{
		private static final Identifier       origSKIN = SKIN;
		private final        CemModelRegistry registry;
		
		public CemCreeperChargeFeatureRenderer(CemCreeperRenderer featureRendererContext, EntityModelLoader modelLoader){
			super(featureRendererContext, modelLoader);
			boolean inflate = false;
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
				this.registry = CemRegistryManager.getRegistry(CemCreeperRenderer.getType());
				SKIN = origSKIN;
				inflate = true;
			}
			try{
				this.model = inflate? new CemCreeperModel(registry, 2.00F) : new CemCreeperModel(registry, null);
			} catch(Exception e){
				modelError(e);
			}
		}
		
		@Override
		public String getId(){
			return "creeper_charge";
		}
		
		@Override
		public Identifier getTexture(CreeperEntity entity){
			if(this.registry != null && this.registry.hasTexture()){
				return this.registry.getTexture();
			}
			return super.getTexture(entity);
		}
	}
}