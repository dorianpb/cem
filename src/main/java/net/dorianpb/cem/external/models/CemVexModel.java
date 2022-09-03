package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.entity.mob.VexEntity;

public class CemVexModel extends VexEntityModel implements CemModel {
    private final CemModelRegistry registry;

    public CemVexModel(CemModelRegistry registry){
        super(registry.prepRootPart((new CemModelRegistry.CemPrepRootPartParamsBuilder())
                .setVanillaReferenceModelFactory(() -> getTexturedModelData().createModel())
                .create()));
        this.registry = registry;
    }

    @Override
    public void setAngles(VexEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
        super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
    }
}
