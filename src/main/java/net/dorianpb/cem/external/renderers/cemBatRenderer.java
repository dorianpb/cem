package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.cemBatModel;
import net.dorianpb.cem.internal.cemFairy;
import net.dorianpb.cem.internal.cemModelRegistry;
import net.dorianpb.cem.internal.cemRenderer;
import net.minecraft.client.render.entity.BatEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;

public class cemBatRenderer extends BatEntityRenderer implements cemRenderer{
    private final BatEntityModel vanilla;
    private final String id;
    private cemModelRegistry registry;
    
    public cemBatRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.id="bat";
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
            this.model = new cemBatModel(registry);
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
    public Identifier getTexture(BatEntity batEntity){
            if(this.registry!=null && this.registry.hasTexture()){
            return this.registry.getTexture();
        }
        return super.getTexture(batEntity);
    }
    
}