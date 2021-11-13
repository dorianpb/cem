package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemDrownedZombieModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemArmorModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.DrownedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

public class CemDrownedZombieRenderer extends DrownedEntityRenderer implements CemRenderer{
	private final CemModelRegistry registry;
	
	public CemDrownedZombieRenderer(EntityRendererFactory.Context context){
		super(context);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemDrownedZombieModel(registry);
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
			this.features.replaceAll((feature) -> {
				if(feature instanceof DrownedOverlayFeatureRenderer<DrownedEntity>){
					return new CemDrownedOverlayRenderer(this, context.getModelLoader());
				}
				else if(feature instanceof ArmorFeatureRenderer){
					return new ArmorFeatureRenderer<>(this,
					                                  new CemArmorModel<>((CemDrownedZombieModel) this.model, 0.5F),
					                                  new CemArmorModel<>((CemDrownedZombieModel) this.model, 0.5F)
					);
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
		return EntityType.DROWNED;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(ZombieEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
	
	public static class CemDrownedOverlayRenderer extends DrownedOverlayFeatureRenderer<DrownedEntity> implements CemRenderer{
		private static final Identifier       origSKIN = SKIN;
		private final        CemModelRegistry registry;
		
		public CemDrownedOverlayRenderer(CemDrownedZombieRenderer featureRendererContext, EntityModelLoader modelLoader){
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
				this.registry = CemRegistryManager.getRegistry(CemDrownedZombieRenderer.getType());
				SKIN = origSKIN;
			}
			try{
				this.model = new CemDrownedZombieModel(registry, 0.25F);
				
			} catch(Exception e){
				modelError(e);
			}
		}
		
		@Override
		public String getId(){
			return "drowned_outer";
		}
		
		@Override
		public Identifier getTexture(DrownedEntity entity){
			if(this.registry != null && this.registry.hasTexture()){
				return this.registry.getTexture();
			}
			return super.getTexture(entity);
		}
	}
}