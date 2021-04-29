package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.cemCatModel;
import net.dorianpb.cem.internal.cemFairy;
import net.dorianpb.cem.internal.cemModelRegistry;
import net.dorianpb.cem.internal.cemRenderer;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;

public class cemCatRenderer extends CatEntityRenderer implements cemRenderer{
    private final CatEntityModel<CatEntity> vanilla;
    private final String id;
    private cemModelRegistry registry;
    
    public cemCatRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.id="cat";
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
            this.model = new cemCatModel(0.0F, registry);
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
    public Identifier getTexture(CatEntity catEntity){
        if(this.registry!=null && this.registry.hasTexture()){
            return this.registry.getTexture();
        }
        return super.getTexture(catEntity);
    }
    
}
