package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemBeeModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.BeeEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class CemBeeRenderer extends BeeEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new HashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private final        CemModelRegistry          registry;
	
	static{
		partNames.put("body", "bone");
		partNames.put("torso", "body");
	}
	
	static{
		parentChildPairs.put("torso", Arrays.asList("stinger", "left_antenna", "right_antenna"));
		parentChildPairs.put("body", Arrays.asList("torso", "right_wing", "left_wing", "front_legs", "middle_legs", "back_legs"));
	}
	
	public CemBeeRenderer(EntityRendererFactory.Context context){
		super(context);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemBeeModel(this.registry.prepRootPart(partNames, parentChildPairs, context.getPart(EntityModelLayers.BEE)), registry);
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
		} catch(Exception e){
			modelError(e);
		}
	}
	
	private static EntityType<? extends Entity> getType(){
		return EntityType.BEE;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(BeeEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}