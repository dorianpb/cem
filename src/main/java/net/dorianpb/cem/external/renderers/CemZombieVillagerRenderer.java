package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemZombieVillagerModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemArmorModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieVillagerEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Identifier;

public class CemZombieVillagerRenderer extends ZombieVillagerEntityRenderer implements CemRenderer{
	private final CemModelRegistry registry;
	
	public CemZombieVillagerRenderer(EntityRendererFactory.Context context){
		super(context);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemZombieVillagerModel<>(registry);
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
			
			this.features.replaceAll((feature) -> {
				if(feature instanceof ArmorFeatureRenderer){
					return new ArmorFeatureRenderer<>(this,
					                                  new CemArmorModel<>((CemZombieVillagerModel<ZombieVillagerEntity>) this.model, 0.5F),
					                                  new CemArmorModel<>((CemZombieVillagerModel<ZombieVillagerEntity>) this.model, 1.0F)
					);
				}
				else{
					return feature;
				}
			});
			
		} catch(Exception e){
			modelError(e);
		}
	}
	
	private static EntityType<? extends Entity> getType(){
		return EntityType.ZOMBIE_VILLAGER;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(ZombieVillagerEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}