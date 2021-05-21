package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemPigModel;
import net.dorianpb.cem.internal.CemFairy;
import net.dorianpb.cem.internal.CemModelRegistry;
import net.dorianpb.cem.internal.CemRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

public class CemPigRenderer extends PigEntityRenderer implements CemRenderer{
	private final PigEntityModel<PigEntity> vanilla;
	private final float origShadowRadius;
	private final FeatureRenderer<PigEntity, PigEntityModel<PigEntity>> origFeature;
	private final String id;
	private CemModelRegistry registry;
	
	public CemPigRenderer(EntityRenderDispatcher dispatcher){
		super(dispatcher);
		this.id = "pig";
		CemFairy.addRenderer(this, id);
		this.vanilla = this.model;
		this.origShadowRadius = this.shadowRadius;
		this.origFeature = this.features.get(0);
	}
	
	@Override
	public void apply(CemModelRegistry registry){
		this.registry = registry;
		try{
			this.model = new CemPigModel(0.0F, registry);
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
			this.features.set(0, new SaddleFeatureRenderer<>(this, new CemPigModel(0.5F, this.registry), new Identifier("textures/entity/pig/pig_saddle.png")));
		} catch(Exception e){
			modelError(e);
		}
	}
	
	@Override
	public String getId(){
		return this.id;
	}
	
	@Override
	public void restoreModel(){
		this.model = this.vanilla;
		this.registry = null;
		this.shadowRadius = this.origShadowRadius;
		this.features.set(0, this.origFeature);
	}
	
	@Override
	public Identifier getTexture(PigEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}