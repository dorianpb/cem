package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemOcelotModel;
import net.dorianpb.cem.internal.CemFairy;
import net.dorianpb.cem.internal.CemModelRegistry;
import net.dorianpb.cem.internal.CemRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.OcelotEntityRenderer;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

public class CemOcelotRenderer extends OcelotEntityRenderer implements CemRenderer{
	private final OcelotEntityModel<OcelotEntity> vanilla;
	private final String id;
	private CemModelRegistry registry;
	
	public CemOcelotRenderer(EntityRenderDispatcher dispatcher){
		super(dispatcher);
		this.id = "ocelot";
		CemFairy.addRenderer(this, id);
		this.vanilla = this.model;
	}
	
	@Override
	public void apply(CemModelRegistry registry){
		this.registry = registry;
		try{
			this.model = new CemOcelotModel(0.0F, registry);
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
	public Identifier getTexture(OcelotEntity ocelotEntity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(ocelotEntity);
	}
}