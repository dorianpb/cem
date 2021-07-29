package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemCatModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelEntry.CemModelPart;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemCatRenderer extends CatEntityRenderer implements CemRenderer{
	private static final Map<String, String>         partNames           = new HashMap<>();
	private static final Map<String, List<String>>   parentChildPairs    = new LinkedHashMap<>();
	private static final Map<String, ModelTransform> modelTransformFixes = new HashMap<>();
	private final        CemModelRegistry            registry;
	
	static{
		partNames.put("front_right_leg", "right_front_leg");
		partNames.put("front_left_leg", "left_front_leg");
		partNames.put("back_right_leg", "right_hind_leg");
		partNames.put("back_left_leg", "left_hind_leg");
		partNames.put("tail", "tail1");
	}
	
	static{
		modelTransformFixes.put("body", ModelTransform.pivot(0.0F, 6.0F, 6.1F));
		modelTransformFixes.put("tail1", ModelTransform.pivot(0.0F, 14.5F, 9.0F));
		modelTransformFixes.put("tail2", ModelTransform.pivot(0.0F, 22.5F, 9.0F));
		
	}
	
	public CemCatRenderer(EntityRendererFactory.Context context){
		super(context);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemCatModel(this.registry.prepRootPart(partNames, parentChildPairs, context.getPart(EntityModelLayers.CAT), null, modelTransformFixes),
			                             registry);
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
		private static final Map<String, String>       partNames        = CemCatRenderer.partNames;
		private static final Map<String, List<String>> parentChildPairs = CemCatRenderer.parentChildPairs;
		private final        CemModelRegistry          registry;
		
		public CemCatCollarFeatureRenderer(CemCatRenderer featureRendererContext, EntityModelLoader modelLoader){
			super(featureRendererContext, modelLoader);
			this.registry = CemRegistryManager.getRegistry(EntityType.CAT);
			try{
				CemModelPart rootPart = this.registry.prepRootPart(partNames,
				                                                   parentChildPairs,
				                                                   modelLoader.getModelPart(EntityModelLayers.CAT_COLLAR),
				                                                   0.01F,
				                                                   CemCatRenderer.modelTransformFixes
				                                                  );
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