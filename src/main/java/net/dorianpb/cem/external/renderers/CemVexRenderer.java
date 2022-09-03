package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemVexModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.VexEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;

public class CemVexRenderer extends VexEntityRenderer implements CemRenderer {
    private final CemModelRegistry registry;

    public CemVexRenderer(EntityRendererFactory.Context context){
        super(context);
        this.registry = CemRegistryManager.getRegistry(getType());
        try{
            this.model = new CemVexModel(registry);
            if(registry.hasShadowRadius()){
                this.shadowRadius = registry.getShadowRadius();
            }
        } catch(Exception e){
            modelError(e);
        }
    }

    private static EntityType<? extends Entity> getType(){
        return EntityType.VEX;
    }

    @Override
    public String getId(){
        return getType().toString();
    }

    @Override
    public Identifier getTexture(VexEntity entity){
        if(this.registry != null && this.registry.hasTexture()){
            return this.registry.getTexture();
        }
        return super.getTexture(entity);
    }
}
