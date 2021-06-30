package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemSheepModel;
import net.dorianpb.cem.external.models.CemSheepModel.CemSheepWoolModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
		partNames.put("leg1", "right_hind_leg");
		partNames.put("leg2", "left_hind_leg");
		partNames.put("leg3", "right_front_leg");
		partNames.put("leg4", "left_front_leg");
	}
	
	public CemSheepRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(getType())){
			this.registry = CemRegistryManager.getRegistry(getType());
			try{
				this.registry.setChildren(parentChildPairs);
				this.model = new CemSheepModel(this.registry.prepRootPart(partNames, context.getPart(EntityModelLayers.SHEEP)), registry);
				if(registry.hasShadowRadius()){
					this.shadowRadius = registry.getShadowRadius();
				}
				this.features.replaceAll((feature) -> {
					if(feature instanceof SheepWoolFeatureRenderer){
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
					this.model = new CemSheepWoolModel(this.registry.prepRootPart(partNames, modelLoader.getModelPart(EntityModelLayers.SHEEP_FUR)), registry);
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