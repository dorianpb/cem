package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.internal.cemFairy;
import net.dorianpb.cem.internal.cemModelRegistry;
import net.dorianpb.cem.internal.cemRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;

public class cemBannerRenderer extends BannerBlockEntityRenderer implements cemRenderer{
    private final ModelPart vanillaBanner;
    private final ModelPart vanillaPillar;
    private final ModelPart vanillaCrossbar;
    private final String id;
    private cemModelRegistry registry;

    public cemBannerRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.id="banner";
        cemFairy.addRenderer(this,id);
        this.vanillaBanner = this.banner;
        this.vanillaPillar = this.pillar;
        this.vanillaCrossbar = this.crossbar;
    }

    @Override
    public void restoreModel() {
        this.banner = this.vanillaBanner;
        this.pillar = this.vanillaPillar;
        this.crossbar = this.vanillaCrossbar;
        this.registry = null;
    }

    @Override
    public void apply(cemModelRegistry registry) {
        this.registry = registry;
        this.registry.initModels(null);
        try{
            this.banner = this.registry.getModel("slate");
            this.pillar = this.registry.getModel("stand"); //jojo reference?
            this.crossbar = this.registry.getModel("top");
            this.pillar.pivotY+=-12;
            this.crossbar.pivotY+=-12;
        }
        catch(Exception e){
            modelError(e);
        }
    }

    @Override
    public String getId() {
        return this.id;
    }
}