package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemWolfModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemWolfRenderer extends WolfEntityRenderer implements CemRenderer{
	private static final Map<String, String>         partNames           = new HashMap<>();
	private static final Map<String, List<String>>   parentChildPairs    = new LinkedHashMap<>();
	private static final Map<String, ModelTransform> modelTransformFixes = new HashMap<>();
	private              CemModelRegistry            registry;
	
	static{
		partNames.put("leg1", "right_hind_leg");
		partNames.put("leg2", "left_hind_leg");
		partNames.put("leg3", "right_front_leg");
		partNames.put("leg4", "left_front_leg");
		partNames.put("mane", "upper_body");
	}
	
	static{
		modelTransformFixes.put("upper_body", ModelTransform.pivot(-1.0F, 14.0F, 2.0F));
		modelTransformFixes.put("tail", ModelTransform.pivot(-1.0F, 12.0F, 10.0F));
	}
	
	public CemWolfRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(getType())){
			this.registry = CemRegistryManager.getRegistry(getType());
			try{
				this.model = new CemWolfModel(this.registry.prepRootPart(partNames, parentChildPairs, context.getPart(EntityModelLayers.WOLF), null, modelTransformFixes),
				                              registry
				);
				if(registry.hasShadowRadius()){
					this.shadowRadius = registry.getShadowRadius();
				}
			} catch(Exception e){
				modelError(e);
			}
		}
	}
	
	private static EntityType<? extends Entity> getType(){
		return EntityType.WOLF;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(WolfEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}