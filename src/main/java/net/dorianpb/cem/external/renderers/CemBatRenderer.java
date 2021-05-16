package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemBatModel;
import net.dorianpb.cem.internal.CemFairy;
import net.dorianpb.cem.internal.CemModelRegistry;
import net.dorianpb.cem.internal.CemRenderer;
import net.minecraft.client.render.entity.BatEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;

public class CemBatRenderer extends BatEntityRenderer implements CemRenderer{
	private final BatEntityModel vanilla;
	private final float origShadowRadius;
	private final String id;
	private CemModelRegistry registry;
	
	public CemBatRenderer(EntityRenderDispatcher dispatcher){
		super(dispatcher);
		this.id = "bat";
		CemFairy.addRenderer(this, id);
		this.vanilla = this.model;
		this.origShadowRadius = this.shadowRadius;
	}
	
	@Override
	public void apply(CemModelRegistry registry){
		this.registry = registry;
		try{
			this.model = new CemBatModel(registry);
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
	public Identifier getTexture(BatEntity batEntity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(batEntity);
	}
}