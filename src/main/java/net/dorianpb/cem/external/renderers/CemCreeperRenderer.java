package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemCreeperModel;
import net.dorianpb.cem.internal.CemFairy;
import net.dorianpb.cem.internal.CemModelRegistry;
import net.dorianpb.cem.internal.CemRenderer;
import net.minecraft.client.render.entity.CreeperEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

public class CemCreeperRenderer extends CreeperEntityRenderer implements CemRenderer{
	private final CreeperEntityModel<CreeperEntity> vanilla;
	private final float origShadowRadius;
	private final String id;
	private CemModelRegistry registry;
	
	public CemCreeperRenderer(EntityRenderDispatcher dispatcher){
		super(dispatcher);
		this.id = "creeper";
		CemFairy.addRenderer(this, id);
		this.vanilla = this.model;
		this.origShadowRadius = this.shadowRadius;
	}
	
	@Override
	public void apply(CemModelRegistry registry){
		this.registry = registry;
		try{
			this.model = new CemCreeperModel(0.0F, registry);
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
	public Identifier getTexture(CreeperEntity creeperEntity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(creeperEntity);
	}
}