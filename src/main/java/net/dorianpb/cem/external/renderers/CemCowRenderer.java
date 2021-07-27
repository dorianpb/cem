package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemCowModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class CemCowRenderer extends CowEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new HashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private              CemModelRegistry          registry;
	
	static{
		partNames.put("leg1", "right_hind_leg");
		partNames.put("leg2", "left_hind_leg");
		partNames.put("leg3", "right_front_leg");
		partNames.put("leg4", "left_front_leg");
	}
	
	static{
		parentChildPairs.put("head", Arrays.asList("right_horn", "left_horn"));
	}
	
	public CemCowRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(getType())){
			this.registry = CemRegistryManager.getRegistry(getType());
			try{
				this.model = new CemCowModel<>(this.registry.prepRootPart(partNames, parentChildPairs, context.getPart(EntityModelLayers.COW)), registry);
				if(registry.hasShadowRadius()){
					this.shadowRadius = registry.getShadowRadius();
				}
			} catch(Exception e){
				modelError(e);
			}
		}
	}
	
	private static EntityType<? extends Entity> getType(){
		return EntityType.COW;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(CowEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}