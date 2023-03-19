package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemGiantZombieModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemArmorModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.GiantEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.util.Identifier;

public class CemGiantZombieRenderer extends GiantEntityRenderer implements CemRenderer{
	private final CemModelRegistry registry;
	
	public CemGiantZombieRenderer(EntityRendererFactory.Context context){
		super(context, 6.0F);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemGiantZombieModel(this.registry);
			if(this.registry.hasShadowRadius()){
				this.shadowRadius = this.registry.getShadowRadius();
			}
			this.features.replaceAll((feature) -> {
				if(feature instanceof ArmorFeatureRenderer){
					return new ArmorFeatureRenderer<>(this,
					                                  new CemArmorModel<>((CemGiantZombieModel) this.model, 0.5F),
					                                  new CemArmorModel<>((CemGiantZombieModel) this.model, 1.0F),
					                                  context.getModelManager()
					);
				}
				else{
					return feature;
				}
			});
		} catch(Exception e){
			this.modelError(e);
		}
	}
	
	private static EntityType<? extends Entity> getType(){
		return EntityType.GIANT;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(GiantEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}