package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.cemModelRegistry;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.entity.mob.EndermanEntity;

public class cemEndermanModel extends EndermanEntityModel<EndermanEntity>{
    private final cemModelRegistry registry;
    
    public cemEndermanModel(float scale, cemModelRegistry registry) {
        super(scale);
        this.registry = registry;
        this.registry.initModels(this);
        this.head = this.registry.getModel("head");
        this.helmet = this.registry.getModel("headwear");
        this.torso = this.registry.getModel("body");
        this.leftArm = this.registry.getModel("left_arm");
        this.rightArm = this.registry.getModel("right_arm");
        this.leftLeg = this.registry.getModel("left_leg");
        this.rightLeg = this.registry.getModel("right_leg");
    }
    
    
    @Override
    public void setAngles(EndermanEntity livingEntity, float f, float g, float h, float i, float j){
        super.setAngles(livingEntity, f, g, h, i, j);
        this.registry.applyAnimations(f,g,h,i,j,livingEntity);
    }
    
}