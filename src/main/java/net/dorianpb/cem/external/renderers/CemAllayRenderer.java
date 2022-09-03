package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemAllayModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.AllayEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;

public class CemAllayRenderer extends AllayEntityRenderer implements CemRenderer {
    private final CemModelRegistry registry;

    public CemAllayRenderer(EntityRendererFactory.Context context){
        super(context);
        this.registry = CemRegistryManager.getRegistry(getType());
        try{
            this.model = new CemAllayModel(registry);
            if(registry.hasShadowRadius()){
                this.shadowRadius = registry.getShadowRadius();
            }
        } catch(Exception e){
            modelError(e);
        }
    }

    private static EntityType<? extends Entity> getType(){
        return EntityType.ALLAY;
    }

    @Override
    public String getId(){
        return getType().toString();
    }

    @Override
    public Identifier getTexture(AllayEntity entity){
        if(this.registry != null && this.registry.hasTexture()){
            return this.registry.getTexture();
        }
        return super.getTexture(entity);
    }
}
