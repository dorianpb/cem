package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemPiglinModel;
import net.dorianpb.cem.internal.CemFairy;
import net.dorianpb.cem.internal.CemModelRegistry;
import net.dorianpb.cem.internal.CemRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PiglinEntityRenderer;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

public class CemPiglinRenderer extends PiglinEntityRenderer implements CemRenderer{
	private final PiglinEntityModel<MobEntity> vanilla;
	private final float origShadowRadius;
	private final String id;
	private CemModelRegistry registry;
	
	public CemPiglinRenderer(EntityRenderDispatcher dispatcher, String id){
		super(dispatcher, id.equalsIgnoreCase("zombified_piglin"));
		CemFairy.addRenderer(this, id);
		this.id = id.toLowerCase();
		this.vanilla = this.model;
		this.origShadowRadius = this.shadowRadius;
	}
	
	@Override
	public void apply(CemModelRegistry registry){
		this.registry = registry;
		try{
			this.model = new CemPiglinModel(0.0F, 64, 64, registry);
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
	public Identifier getTexture(MobEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}