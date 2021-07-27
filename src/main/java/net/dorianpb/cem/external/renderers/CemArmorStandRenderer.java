package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemArmorStandModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemArmorStandRenderer extends ArmorStandEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new HashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private              CemModelRegistry          registry;
	
	static{
		partNames.put("headwear", "hat");
		partNames.put("right", "right_body_stick");
		partNames.put("left", "left_body_stick");
		partNames.put("waist", "shoulder_stick");
		partNames.put("base", "base_plate");
	}
	
	public CemArmorStandRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(getType())){
			this.registry = CemRegistryManager.getRegistry(getType());
			try{
				this.model = new CemArmorStandModel(this.registry.prepRootPart(partNames, parentChildPairs, context.getPart(EntityModelLayers.ARMOR_STAND)), registry);
				if(registry.hasShadowRadius()){
					this.shadowRadius = registry.getShadowRadius();
				}
			} catch(Exception e){
				modelError(e);
			}
		}
	}
	
	private static EntityType<? extends Entity> getType(){
		return EntityType.ARMOR_STAND;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(ArmorStandEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}