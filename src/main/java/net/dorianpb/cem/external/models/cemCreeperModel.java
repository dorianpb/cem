package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.cemModelRegistry;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;

public class cemCreeperModel extends CreeperEntityModel<CreeperEntity>{
    private final cemModelRegistry registry;
    
    public cemCreeperModel(float scale, cemModelRegistry registry){
        super(scale);
        this.registry = registry;
        this.registry.initModels(this);
        this.head = this.registry.getModel("head");
        this.helmet = this.registry.getModel("armor");
        this.torso = this.registry.getModel("body");
        this.leftBackLeg = this.registry.getModel("leg1");
        this.leftFrontLeg = this.registry.getModel("leg3");
        this.rightBackLeg = this.registry.getModel("leg2");
        this.rightFrontLeg = this.registry.getModel("leg4");
    }
    
    @Override
    public void setAngles(CreeperEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
        super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
    }
}