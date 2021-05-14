package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.internal.CemFairy;
import net.dorianpb.cem.internal.CemModelRegistry;
import net.dorianpb.cem.internal.CemRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;

public class CemBannerRenderer extends BannerBlockEntityRenderer implements CemRenderer{
	private final ModelPart vanillaBanner;
	private final ModelPart vanillaPillar;
	private final ModelPart vanillaCrossbar;
	private final String id;
	private CemModelRegistry registry;
	
	public CemBannerRenderer(BlockEntityRenderDispatcher dispatcher){
		super(dispatcher);
		this.id = "banner";
		CemFairy.addRenderer(this, id);
		this.vanillaBanner = this.banner;
		this.vanillaPillar = this.pillar;
		this.vanillaCrossbar = this.crossbar;
	}
	
	@Override
	public void apply(CemModelRegistry registry){
		this.registry = registry;
		this.registry.initModels(null);
		try{
			this.banner = this.registry.getModel("slate");
			this.pillar = this.registry.getModel("stand"); //jojo reference?
			this.crossbar = this.registry.getModel("top");
			this.pillar.pivotY += -12;
			this.crossbar.pivotY += -12;
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
		this.banner = this.vanillaBanner;
		this.pillar = this.vanillaPillar;
		this.crossbar = this.vanillaCrossbar;
		this.registry = null;
	}
}