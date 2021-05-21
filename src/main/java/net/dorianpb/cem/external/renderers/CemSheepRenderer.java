package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemSheepModel;
import net.dorianpb.cem.external.models.CemSheepModel.CemSheepWoolModel;
import net.dorianpb.cem.internal.CemFairy;
import net.dorianpb.cem.internal.CemModelRegistry;
import net.dorianpb.cem.internal.CemRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

public class CemSheepRenderer extends SheepEntityRenderer implements CemRenderer{
	private final SheepEntityModel<SheepEntity> vanilla;
	private final float origShadowRadius;
	private final String id;
	private CemModelRegistry registry;
	
	public CemSheepRenderer(EntityRenderDispatcher dispatcher){
		super(dispatcher);
		this.id = "sheep";
		CemFairy.addRenderer(this, id);
		this.features.set(0, new CemSheepWoolFeatureRenderer(this));
		this.vanilla = this.model;
		this.origShadowRadius = this.shadowRadius;
	}
	
	@Override
	public void apply(CemModelRegistry registry){
		this.registry = registry;
		try{
			this.model = new CemSheepModel(registry);
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
	public Identifier getTexture(SheepEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
	
	public static class CemSheepWoolFeatureRenderer extends SheepWoolFeatureRenderer implements CemRenderer{
		private final SheepWoolEntityModel<SheepEntity> vanilla;
		private final Identifier origSkin;
		private final String id;
		private CemModelRegistry registry;
		
		public CemSheepWoolFeatureRenderer(CemSheepRenderer featureRendererContext){
			super(featureRendererContext);
			this.id = "sheep_wool";
			CemFairy.addRenderer(this, id);
			this.vanilla = this.model;
			this.origSkin = SKIN;
		}
		
		@Override
		public void apply(CemModelRegistry registry){
			this.registry = registry;
			try{
				this.model = new CemSheepWoolModel(registry);
				SKIN = this.registry.getTexture();
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
			SKIN = origSkin;
		}
		
		@Override
		public Identifier getTexture(SheepEntity entity){
			if(this.registry != null && this.registry.hasTexture()){
				return this.registry.getTexture();
			}
			return super.getTexture(entity);
		}
	}
}