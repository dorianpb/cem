package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemRabbitModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.RabbitEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemRabbitRenderer extends RabbitEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new HashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private              CemModelRegistry          registry;
	
	static{
		partNames.put("left_foot", "left_hind_foot");
		partNames.put("right_foot", "right_hind_foot");
		partNames.put("left_thigh", "left_haunch");
		partNames.put("right_thigh", "right_haunch");
		partNames.put("left_arm", "left_front_leg");
		partNames.put("right_arm", "right_front_leg");
	}
	
	public CemRabbitRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(getType())){
			this.registry = CemRegistryManager.getRegistry(getType());
			try{
				this.model = new CemRabbitModel(this.registry.prepRootPart(partNames, parentChildPairs, context.getPart(EntityModelLayers.RABBIT)), registry);
				if(registry.hasShadowRadius()){
					this.shadowRadius = registry.getShadowRadius();
				}
			} catch(Exception e){
				modelError(e);
			}
		}
	}
	
	private static EntityType<? extends Entity> getType(){
		return EntityType.RABBIT;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(RabbitEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}