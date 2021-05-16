package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemBlazeModel;
import net.dorianpb.cem.internal.CemFairy;
import net.dorianpb.cem.internal.CemModelRegistry;
import net.dorianpb.cem.internal.CemRenderer;
import net.minecraft.client.render.entity.BlazeEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.Identifier;

public class CemBlazeRenderer extends BlazeEntityRenderer implements CemRenderer{
	private final BlazeEntityModel<BlazeEntity> vanilla;
	private final float origShadowRadius;
	private final String id;
	private CemModelRegistry registry;
	
	public CemBlazeRenderer(EntityRenderDispatcher dispatcher){
		super(dispatcher);
		this.id = "blaze";
		CemFairy.addRenderer(this, id);
		this.vanilla = this.model;
		this.origShadowRadius = this.shadowRadius;
	}
	
	@Override
	public void apply(CemModelRegistry registry){
		this.registry = registry;
		try{
			this.model = new CemBlazeModel(registry);
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
	public Identifier getTexture(BlazeEntity blazeEntity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(blazeEntity);
	}
}