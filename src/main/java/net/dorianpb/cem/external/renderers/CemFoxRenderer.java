package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemFoxModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FoxEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class CemFoxRenderer extends FoxEntityRenderer implements CemRenderer{
	private static final Map<String, String>         partNames           = new HashMap<>();
	private static final Map<String, List<String>>   parentChildPairs    = new LinkedHashMap<>();
	private static final Map<String, ModelTransform> modelTransformFixes = new HashMap<>();
	private              CemModelRegistry            registry;
	
	static{
		partNames.put("leg1", "right_hind_leg");
		partNames.put("leg2", "left_hind_leg");
		partNames.put("leg3", "right_front_leg");
		partNames.put("leg4", "left_front_leg");
	}
	
	static{
		parentChildPairs.put("head", Arrays.asList("right_ear", "left_ear", "nose"));
		parentChildPairs.put("body", Collections.singletonList("tail"));
	}
	
	static{
		modelTransformFixes.put("body", ModelTransform.pivot(0.0F, 7.5F, 3.5F));
		modelTransformFixes.put("tail", ModelTransform.pivot(-4.0F, 5.5F, 6.0F));
	}
	
	public CemFoxRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(getType())){
			this.registry = CemRegistryManager.getRegistry(getType());
			try{
				this.model = new CemFoxModel(this.registry.prepRootPart(partNames, parentChildPairs, context.getPart(EntityModelLayers.FOX), null, modelTransformFixes),
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
		return EntityType.FOX;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(FoxEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}