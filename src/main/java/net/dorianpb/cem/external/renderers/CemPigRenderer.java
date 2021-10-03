package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemPigModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

public class CemPigRenderer extends PigEntityRenderer implements CemRenderer{
	private final CemModelRegistry registry;
	
	public CemPigRenderer(EntityRendererFactory.Context context){
		super(context);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemPigModel(registry, null);
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
			this.features.replaceAll((feature) -> {
				if(feature instanceof SaddleFeatureRenderer){
					CemModelRegistry saddleRegistry = CemRegistryManager.getRegistry(getType());
					return new SaddleFeatureRenderer<>(this, new CemPigModel(saddleRegistry, 0.5F), new Identifier("textures/entity/pig/pig_saddle.png"));
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
		return EntityType.PIG;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(PigEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}