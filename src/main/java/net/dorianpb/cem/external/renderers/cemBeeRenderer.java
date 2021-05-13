package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.cemBeeModel;
import net.dorianpb.cem.internal.cemFairy;
import net.dorianpb.cem.internal.cemModelRegistry;
import net.dorianpb.cem.internal.cemRenderer;
import net.minecraft.client.render.entity.BeeEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;

public class cemBeeRenderer extends BeeEntityRenderer implements cemRenderer{
    private final BeeEntityModel<BeeEntity> vanilla;
    private final String id;
    private cemModelRegistry registry;
    
    public cemBeeRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.id="bee";
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
            this.model = new cemBeeModel(registry);
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
    public Identifier getTexture(BeeEntity beeEntity){
        if(this.registry!=null && this.registry.hasTexture()){
            return this.registry.getTexture();
        }
        return super.getTexture(beeEntity);
    }
    
}