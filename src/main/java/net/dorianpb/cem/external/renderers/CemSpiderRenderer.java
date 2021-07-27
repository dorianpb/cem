package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemSpiderModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemSpiderRenderer extends SpiderEntityRenderer<SpiderEntity> implements CemRenderer{
	private static final Map<String, String>       partNames        = new HashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private              CemModelRegistry          registry;
	
	static{
		partNames.put("neck", "body0");
		partNames.put("body", "body1");
		partNames.put("leg1", "right_hind_leg");
		partNames.put("leg2", "left_hind_leg");
		partNames.put("leg3", "right_middle_hind_leg");
		partNames.put("leg4", "left_middle_hind_leg");
		partNames.put("leg5", "right_middle_front_leg");
		partNames.put("leg6", "left_middle_front_leg");
		partNames.put("leg7", "right_front_leg");
		partNames.put("leg8", "left_front_leg");
	}
	
	public CemSpiderRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(getType())){
			this.registry = CemRegistryManager.getRegistry(getType());
			try{
				this.model = new CemSpiderModel<>(this.registry.prepRootPart(partNames, parentChildPairs, this.model.getPart()), registry);
				if(registry.hasShadowRadius()){
					this.shadowRadius = registry.getShadowRadius();
				}
			} catch(Exception e){
				modelError(e);
			}
		}
	}
	
	private static EntityType<? extends Entity> getType(){
		return EntityType.SPIDER;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(SpiderEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}