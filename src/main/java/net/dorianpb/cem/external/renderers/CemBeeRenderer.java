package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemBeeModel;
import net.dorianpb.cem.internal.CemFairy;
import net.dorianpb.cem.internal.CemModelRegistry;
import net.dorianpb.cem.internal.CemRenderer;
import net.minecraft.client.render.entity.BeeEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;

public class CemBeeRenderer extends BeeEntityRenderer implements CemRenderer{
	private final BeeEntityModel<BeeEntity> vanilla;
	private final float origShadowRadius;
	private final String id;
	private CemModelRegistry registry;
	
	public CemBeeRenderer(EntityRenderDispatcher dispatcher){
		super(dispatcher);
		this.id = "bee";
		CemFairy.addRenderer(this, id);
		this.vanilla = this.model;
		this.origShadowRadius = this.shadowRadius;
	}
	
	@Override
	public void apply(CemModelRegistry registry){
		this.registry = registry;
		try{
			this.model = new CemBeeModel(registry);
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
	public Identifier getTexture(BeeEntity beeEntity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(beeEntity);
	}
}