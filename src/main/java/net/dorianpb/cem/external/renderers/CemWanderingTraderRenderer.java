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

public class CemWanderingTraderRenderer extends WanderingTraderEntityRenderer implements CemRenderer{
	private final CemModelRegistry registry;
	
	public CemWanderingTraderRenderer(EntityRendererFactory.Context context){
		super(context);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemVillagerModel<>(registry);
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