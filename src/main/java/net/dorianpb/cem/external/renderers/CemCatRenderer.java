package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemCatModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelEntry.CemModelPart;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemCatRenderer extends CatEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new LinkedHashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private              CemModelRegistry          registry;
	
	static{
		partNames.put("front_right_leg", "right_front_leg");
		partNames.put("front_left_leg", "left_front_leg");
		partNames.put("back_right_leg", "right_hind_leg");
		partNames.put("back_left_leg", "left_hind_leg");
		partNames.put("tail", "tail1");
	}
	
	public CemCatRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(getType())){
			this.registry = CemRegistryManager.getRegistry(getType());
			try{
				this.registry.setChildren(parentChildPairs);
				this.model = new CemCatModel(this.registry.prepRootPart(partNames, context.getPart(EntityModelLayers.CAT)), registry);
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
		private static final Map<String, String>       partNames        = CemCatRenderer.partNames;
		private static final Map<String, List<String>> parentChildPairs = CemCatRenderer.parentChildPairs;
		private final        CemModelRegistry          registry;
		
		public CemCatCollarFeatureRenderer(CemCatRenderer featureRendererContext, EntityModelLoader modelLoader){
			super(featureRendererContext, modelLoader);
			this.registry = CemRegistryManager.getRegistry(EntityType.CAT);
			try{
				this.registry.setChildren(parentChildPairs);
				CemModelPart rootPart = this.registry.prepRootPart(partNames, modelLoader.getModelPart(EntityModelLayers.CAT_COLLAR), 0.01F);
				this.model = new CemCatModel(rootPart, registry);
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