package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemBatModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.BatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class CemBatRenderer extends BatEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new LinkedHashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private              CemModelRegistry          registry;
	
	static{
		partNames.put("outer_left_wing", "left_wing_tip");
		partNames.put("outer_right_wing", "right_wing_tip");
	}
	
	static{
		parentChildPairs.put("right_wing", Collections.singletonList("outer_right_wing"));
		parentChildPairs.put("left_wing", Collections.singletonList("outer_left_wing"));
		parentChildPairs.put("body", Arrays.asList("left_wing", "right_wing"));
	}
	
	public CemBatRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(EntityType.BAT)){
			this.registry = CemRegistryManager.getRegistry(EntityType.BAT);
			try{
				this.registry.setChildren(parentChildPairs);
				this.model = new CemBatModel(this.registry.prepRootPart(partNames), registry);
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
		return EntityType.BAT.toString();
	}
	
	@Override
	public Identifier getTexture(BatEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}