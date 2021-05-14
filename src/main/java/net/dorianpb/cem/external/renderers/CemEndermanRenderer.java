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
	private final String id;
	private CemModelRegistry registry;
	
	public CemEndermanRenderer(EntityRenderDispatcher dispatcher){
		super(dispatcher);
		this.id = "enderman";
		CemFairy.addRenderer(this, id);
		this.vanilla = this.model;
	}
	
	@Override
	public void apply(CemModelRegistry registry){
		this.registry = registry;
		try{
			this.model = new CemEndermanModel(0.0F, registry);
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
	}
	
	@Override
	public Identifier getTexture(EndermanEntity endermanEntity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(endermanEntity);
	}
}