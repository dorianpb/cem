package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemVillagerModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class CemVillagerRenderer extends VillagerEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new LinkedHashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private              CemModelRegistry          registry;
	
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
	
	public CemVillagerRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(getType())){
			this.registry = CemRegistryManager.getRegistry(getType());
			try{
				this.registry.setChildren(parentChildPairs);
				this.model = new CemVillagerModel<>(this.registry.prepRootPart(partNames), registry);
				if(registry.hasShadowRadius()){
					this.shadowRadius = registry.getShadowRadius();
				}
			} catch(Exception e){
				modelError(e);
			}
		}
	}
	
	private static EntityType<? extends Entity> getType(){
		return EntityType.VILLAGER;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(VillagerEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}