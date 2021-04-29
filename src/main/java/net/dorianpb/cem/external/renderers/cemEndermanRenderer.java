package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.cemEndermanModel;
import net.dorianpb.cem.internal.cemFairy;
import net.dorianpb.cem.internal.cemModelRegistry;
import net.dorianpb.cem.internal.cemRenderer;
import net.minecraft.client.render.entity.EndermanEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;

public class cemEndermanRenderer extends EndermanEntityRenderer implements cemRenderer{
    private final EndermanEntityModel<EndermanEntity> vanilla;
    private final String id;
    private cemModelRegistry registry;

    public cemEndermanRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.id="enderman";
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
            this.model = new cemEndermanModel(0.0F, registry);
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
    public Identifier getTexture(EndermanEntity endermanEntity){
        if(this.registry!=null && this.registry.hasTexture()){
            return this.registry.getTexture();
        }
        return super.getTexture(endermanEntity);
    }
}
