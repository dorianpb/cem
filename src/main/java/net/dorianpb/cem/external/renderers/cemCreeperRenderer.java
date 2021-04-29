package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.cemCreeperModel;
import net.dorianpb.cem.internal.cemFairy;
import net.dorianpb.cem.internal.cemModelRegistry;
import net.dorianpb.cem.internal.cemRenderer;
import net.minecraft.client.render.entity.CreeperEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

public class cemCreeperRenderer extends CreeperEntityRenderer implements cemRenderer{
    private final CreeperEntityModel<CreeperEntity> vanilla;
    private final String id;
    private cemModelRegistry registry;
    
    public cemCreeperRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.id="creeper";
        cemFairy.addRenderer(this,id);
        this.vanilla = this.model;
    }

    @Override
    public void restoreModel() {
        this.model = this.vanilla;
        this.registry = null;
    }

    @Override
    public void apply(cemModelRegistry registry) {
        this.registry = registry;
        try{
            this.model = new cemCreeperModel(0.0F, registry);
        }
        catch(Exception e){
            modelError(e);
        }
    }
    
    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Identifier getTexture(CreeperEntity creeperEntity) {
        if(this.registry!=null && this.registry.hasTexture()){
            return this.registry.getTexture();
        }
        return super.getTexture(creeperEntity);
    }
}