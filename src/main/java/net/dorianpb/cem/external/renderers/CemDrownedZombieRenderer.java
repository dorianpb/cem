package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemDrownedZombieModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelEntry.CemModelPart;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.DrownedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemDrownedZombieRenderer extends DrownedEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new LinkedHashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private              CemModelRegistry          registry;
	
	static{
		partNames.put("headwear", "hat");
	}
	
	public CemDrownedZombieRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(this.getType())){
			this.registry = CemRegistryManager.getRegistry(this.getType());
			try{
				this.registry.setChildren(parentChildPairs);
				this.model = new CemDrownedZombieModel(this.registry.prepRootPart(partNames), registry);
				if(registry.hasShadowRadius()){
					this.shadowRadius = registry.getShadowRadius();
				}
				this.features.replaceAll((feature) -> {
					if(feature instanceof DrownedOverlayFeatureRenderer<DrownedEntity>){
						return new CemDrownedOverlayRenderer(this, context.getModelLoader());
					}
					else{
						return feature;
					}
				});
			} catch(Exception e){
				modelError(e);
			}
		}
	}
	
	@Override
	public String getId(){
		return this.getType().toString();
	}
	
	private EntityType<? extends Entity> getType(){
		return EntityType.DROWNED;
	}
	
	@Override
	public Identifier getTexture(ZombieEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
	
	public static class CemDrownedOverlayRenderer extends DrownedOverlayFeatureRenderer<DrownedEntity> implements CemRenderer{
		private static final Map<String, String>       partNames        = CemDrownedZombieRenderer.partNames;
		private static final Map<String, List<String>> parentChildPairs = CemDrownedZombieRenderer.parentChildPairs;
		private static final Identifier                origSKIN         = SKIN;
		private final        CemModelRegistry          registry;
		
		public CemDrownedOverlayRenderer(CemDrownedZombieRenderer featureRendererContext, EntityModelLoader modelLoader){
			super(featureRendererContext, modelLoader);
			this.registry = CemRegistryManager.getRegistry(EntityType.DROWNED);
			try{
				this.registry.setChildren(parentChildPairs);
				CemModelPart rootPart = this.registry.prepRootPart(partNames, 0.25F);
				this.model = new CemDrownedZombieModel(rootPart, registry);
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