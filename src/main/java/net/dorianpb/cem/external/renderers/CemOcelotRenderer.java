package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemOcelotModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.OcelotEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemOcelotRenderer extends OcelotEntityRenderer implements CemRenderer{
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
	
	public CemOcelotRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(EntityType.OCELOT)){
			this.registry = CemRegistryManager.getRegistry(EntityType.OCELOT);
			try{
				this.registry.setChildren(parentChildPairs);
				this.model = new CemOcelotModel(this.registry.prepRootPart(partNames), registry);
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
		return EntityType.OCELOT.toString();
	}
	
	@Override
	public Identifier getTexture(OcelotEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}