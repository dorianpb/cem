package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemAxolotlModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.AxolotlEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.Identifier;

public class CemAxolotlRenderer extends AxolotlEntityRenderer implements CemRenderer {
    private final CemModelRegistry registry;

    public CemAxolotlRenderer(EntityRendererFactory.Context context){
        super(context);
        this.registry = CemRegistryManager.getRegistry(getType());
        try{
            this.model = new CemAxolotlModel(registry);
            if(registry.hasShadowRadius()){
                this.shadowRadius = registry.getShadowRadius();
            }
        } catch(Exception e){
            modelError(e);
        }
    }

    private static EntityType<? extends Entity> getType(){
        return EntityType.AXOLOTL;
    }

    @Override
    public String getId(){
        return getType().toString();
    }

    @Override
    public Identifier getTexture(AxolotlEntity entity){
        if(this.registry != null && this.registry.hasTexture()){
            return this.registry.getTexture();
        }
        return super.getTexture(entity);
    }
}
