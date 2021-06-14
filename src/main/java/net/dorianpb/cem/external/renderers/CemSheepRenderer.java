package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemSheepModel;
import net.dorianpb.cem.external.models.CemSheepModel.CemSheepWoolModel;
import net.dorianpb.cem.external.renderers.CemDrownedZombieRenderer.CemDrownedOverlayRenderer;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemSheepRenderer extends SheepEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new LinkedHashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private              CemModelRegistry          registry;
	
	static{
		partNames.put("leg1", "left_hind_leg");
		partNames.put("leg2", "right_hind_leg");
		partNames.put("leg3", "left_front_leg");
		partNames.put("leg4", "right_front_leg");
	}
	
	public CemSheepRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(EntityType.SHEEP)){
			this.registry = CemRegistryManager.getRegistry(EntityType.SHEEP);
			try{
				this.registry.setChildren(parentChildPairs);
				this.model = new CemSheepModel(this.registry.prepRootPart(partNames), registry);
				if(registry.hasShadowRadius()){
					this.shadowRadius = registry.getShadowRadius();
				}
				this.features.replaceAll((feature) -> {
					if(feature instanceof SheepWoolFeatureRenderer){
						return new CemSheepWoolFeatureRenderer(this, context.getModelLoader());
					} else {
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
		return EntityType.SHEEP.toString();
	}
	
	@Override
	public Identifier getTexture(SheepEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
	
	public static class CemSheepWoolFeatureRenderer extends SheepWoolFeatureRenderer implements CemRenderer{
		private static final Map<String, String>       partNames        = CemSheepRenderer.partNames;
		private static final Map<String, List<String>> parentChildPairs = CemSheepRenderer.parentChildPairs;
		private static final Identifier                origSKIN         = SKIN;
		private              CemModelRegistry          registry;
		
		public CemSheepWoolFeatureRenderer(CemSheepRenderer featureRendererContext, EntityModelLoader modelLoader){
			super(featureRendererContext, modelLoader);
			if(CemRegistryManager.hasEntity(this.getId())){
				this.registry = CemRegistryManager.getRegistry(this.getId());
				try{
					this.registry.setChildren(parentChildPairs);
					this.model = new CemSheepWoolModel(this.registry.prepRootPart(partNames), registry);
					if(this.registry != null && this.registry.hasTexture()){
						SKIN = this.registry.getTexture();
					}
					else{
						SKIN = origSKIN;
					}
				} catch(Exception e){
					modelError(e);
				}
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