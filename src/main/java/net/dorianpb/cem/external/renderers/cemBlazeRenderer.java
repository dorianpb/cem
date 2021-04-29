package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.cemBlazeModel;
import net.dorianpb.cem.internal.cemFairy;
import net.dorianpb.cem.internal.cemModelRegistry;
import net.dorianpb.cem.internal.cemRenderer;
import net.minecraft.client.render.entity.BlazeEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.Identifier;

public class cemBlazeRenderer extends BlazeEntityRenderer implements cemRenderer{
    private final BlazeEntityModel<BlazeEntity> vanilla;
    private final String id;
    private cemModelRegistry registry;
    
    public cemBlazeRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.id="blaze";
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
            this.model = new cemBlazeModel(registry);
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
    public Identifier getTexture(BlazeEntity blazeEntity){
        if(this.registry!=null && this.registry.hasTexture()){
            return this.registry.getTexture();
        }
        return super.getTexture(blazeEntity);
    }
}