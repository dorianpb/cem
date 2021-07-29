package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemVillagerModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WanderingTraderEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class CemWanderingTraderRenderer extends WanderingTraderEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new HashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private final        CemModelRegistry          registry;
	
	static{
		partNames.put("headwear", "hat");
		partNames.put("headwear2", "hat_rim");
		partNames.put("bodywear", "jacket");
	}
	
	static{
		parentChildPairs.put("headwear", Collections.singletonList("headwear2"));
		parentChildPairs.put("head", Arrays.asList("headwear", "nose"));
		parentChildPairs.put("body", Collections.singletonList("bodywear"));
	}
	
	public CemWanderingTraderRenderer(EntityRendererFactory.Context context){
		super(context);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemVillagerModel<>(this.registry.prepRootPart(partNames, parentChildPairs, this.model.getPart()), registry);
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
		} catch(Exception e){
			modelError(e);
		}
	}
	
	private static EntityType<? extends Entity> getType(){
		return EntityType.WANDERING_TRADER;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(WanderingTraderEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}