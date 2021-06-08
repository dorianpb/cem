package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemBeeModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.BeeEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemBeeRenderer extends BeeEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new LinkedHashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private              CemModelRegistry          registry;
	
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
		if(CemRegistryManager.hasEntity(EntityType.BEE)){
			this.registry = CemRegistryManager.getRegistry(EntityType.BEE);
			try{
				this.registry.setChildren(parentChildPairs);
				this.model = new CemBeeModel(this.registry.prepRootPart(partNames), registry);
				if(registry.hasShadowRadius()){
					this.shadowRadius = registry.getShadowRadius();
				}
			} catch(Exception e){
				modelError(e);
			}
		}
	}
	
	@Override
	public String getId(){
		return EntityType.BEE.toString();
	}
	
	@Override
	public Identifier getTexture(BeeEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}