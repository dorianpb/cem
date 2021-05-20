package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemEndermanModel;
import net.dorianpb.cem.internal.CemFairy;
import net.dorianpb.cem.internal.CemModelRegistry;
import net.dorianpb.cem.internal.CemRenderer;
import net.minecraft.client.render.entity.EndermanEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;

public class CemEndermanRenderer extends EndermanEntityRenderer implements CemRenderer{
	private final EndermanEntityModel<EndermanEntity> vanilla;
	private final float origShadowRadius;
	private final String id;
	private CemModelRegistry registry;
	
	public CemEndermanRenderer(EntityRenderDispatcher dispatcher){
		super(dispatcher);
		this.id = "enderman";
		CemFairy.addRenderer(this, id);
		this.vanilla = this.model;
		this.origShadowRadius = this.shadowRadius;
	}
	
	@Override
	public void apply(CemModelRegistry registry){
		this.registry = registry;
		try{
			this.model = new CemEndermanModel(0.0F, registry);
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
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
	}
	
	@Override
	public Identifier getTexture(EndermanEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}