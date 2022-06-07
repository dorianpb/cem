package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemHorseModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieHorseEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.Identifier;

public class CemUndeadHorseRenderer extends ZombieHorseEntityRenderer implements CemRenderer{
	private final EntityType<? extends AbstractHorseEntity> entityType;
	private final CemModelRegistry                          registry;
	
	public CemUndeadHorseRenderer(EntityRendererFactory.Context context, EntityType<? extends AbstractHorseEntity> entityType){
		super(context, getLayer(entityType));
		this.entityType = entityType;
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemHorseModel<>(this.registry, null);
			if(this.registry.hasShadowRadius()){
				this.shadowRadius = this.registry.getShadowRadius();
			}
		} catch(Exception e){
			modelError(e);
		}
	}
	
	private static EntityModelLayer getLayer(EntityType<? extends AbstractHorseEntity> entityType){
		if(entityType.equals(EntityType.ZOMBIE_HORSE)){
			return EntityModelLayers.ZOMBIE_HORSE;
		}
		else if(entityType.equals(EntityType.SKELETON_HORSE)){
			return EntityModelLayers.SKELETON_HORSE;
		}
		else{
			return null;
		}
	}
	
	private EntityType<? extends Entity> getType(){
		return this.entityType;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(AbstractHorseEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}