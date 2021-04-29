package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.cemPiglinModel;
import net.dorianpb.cem.internal.cemFairy;
import net.dorianpb.cem.internal.cemModelRegistry;
import net.dorianpb.cem.internal.cemRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PiglinEntityRenderer;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

public class cemPiglinRenderer extends PiglinEntityRenderer implements cemRenderer{
    private final PiglinEntityModel<MobEntity> vanilla;
    private final String id;
    private cemModelRegistry registry;

    public cemPiglinRenderer(EntityRenderDispatcher dispatcher, String id) {
        super(dispatcher, id.equalsIgnoreCase("zombified_piglin"));
        cemFairy.addRenderer(this,id);
        this.id=id.toLowerCase();
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
            this.model = new cemPiglinModel(0.0F, 64, 64, registry);
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
    public Identifier getTexture(MobEntity mobEntity) {
        if(this.registry!=null && this.registry.hasTexture()){
            return this.registry.getTexture();
        }
        return super.getTexture(mobEntity);
    }
    
    
    
}
