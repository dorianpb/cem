package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.cemOcelotModel;
import net.dorianpb.cem.internal.cemFairy;
import net.dorianpb.cem.internal.cemModelRegistry;
import net.dorianpb.cem.internal.cemRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.OcelotEntityRenderer;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

public class cemOcelotRenderer extends OcelotEntityRenderer implements cemRenderer{
    private final OcelotEntityModel<OcelotEntity> vanilla;
    private final String id;
    private cemModelRegistry registry;
    
    public cemOcelotRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.id="ocelot";
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
            this.model = new cemOcelotModel(0.0F, registry);
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
    public Identifier getTexture(OcelotEntity ocelotEntity){
        if(this.registry!=null && this.registry.hasTexture()){
            return this.registry.getTexture();
        }
        return super.getTexture(ocelotEntity);
    }
}