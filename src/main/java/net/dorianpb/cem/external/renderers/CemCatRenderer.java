package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemCatModel;
import net.dorianpb.cem.internal.CemFairy;
import net.dorianpb.cem.internal.CemModelRegistry;
import net.dorianpb.cem.internal.CemRenderer;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;

public class CemCatRenderer extends CatEntityRenderer implements CemRenderer{
	private final CatEntityModel<CatEntity> vanilla;
	private final String id;
	private CemModelRegistry registry;
	
	public CemCatRenderer(EntityRenderDispatcher dispatcher){
		super(dispatcher);
		this.id = "cat";
		CemFairy.addRenderer(this, id);
		this.vanilla = this.model;
	}
	
	@Override
	public void apply(CemModelRegistry registry){
		this.registry = registry;
		try{
			this.model = new CemCatModel(0.0F, registry);
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
	public Identifier getTexture(CatEntity catEntity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(catEntity);
	}
	
}