package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemCreeperModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelEntry.CemModelPart;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemCreeperRenderer extends CreeperEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new LinkedHashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private              CemModelRegistry          registry;
	
	static{
		partNames.put("leg1", "right_hind_leg");
		partNames.put("leg2", "left_hind_leg");
		partNames.put("leg3", "right_front_leg");
		partNames.put("leg4", "left_front_leg");
	}
	
	public CemCreeperRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(getType())){
			this.registry = CemRegistryManager.getRegistry(getType());
			try{
				this.registry.setChildren(parentChildPairs);
				this.model = new CemCreeperModel(this.registry.prepRootPart(partNames), registry);
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
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	private static EntityType<? extends Entity> getType(){
		return EntityType.CREEPER;
	}
	
	@Override
	public Identifier getTexture(CreeperEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
	
	public static class CemCreeperChargeFeatureRenderer extends CreeperChargeFeatureRenderer implements CemRenderer{
		private static final Map<String, String>       partNames        = CemCreeperRenderer.partNames;
		private static final Map<String, List<String>> parentChildPairs = CemCreeperRenderer.parentChildPairs;
		private static final Identifier                origSKIN         = SKIN;
		private final        CemModelRegistry          registry;
		
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
				this.registry.setChildren(parentChildPairs);
				CemModelPart rootPart = inflate? this.registry.prepRootPart(partNames, 2.00F) : this.registry.prepRootPart(partNames);
				this.model = new CemCreeperModel(rootPart, registry);
				
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