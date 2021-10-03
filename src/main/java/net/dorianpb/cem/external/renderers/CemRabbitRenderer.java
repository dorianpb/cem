package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemRabbitModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.RabbitEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.Identifier;

public class CemRabbitRenderer extends RabbitEntityRenderer implements CemRenderer{
	private final CemModelRegistry registry;
	
	public CemRabbitRenderer(EntityRendererFactory.Context context){
		super(context);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemRabbitModel(registry);
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
		} catch(Exception e){
			modelError(e);
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