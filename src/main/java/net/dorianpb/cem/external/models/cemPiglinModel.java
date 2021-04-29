package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.cemModelRegistry;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.mob.MobEntity;

public class cemPiglinModel extends PiglinEntityModel<MobEntity>{
    private final cemModelRegistry registry;
    
    public cemPiglinModel(float scale, int textureWidth, int textureHeight, cemModelRegistry registry) {
        super(scale, textureWidth, textureHeight);
        this.registry = registry;
        this.registry.initModels(this);
        this.head = this.registry.getModel("head");
        this.helmet = this.registry.getModel("headwear");
        this.torso = this.registry.getModel("body");
        this.leftArm = this.registry.getModel("left_arm");
        this.rightArm = this.registry.getModel("right_arm");
        this.leftLeg = this.registry.getModel("left_leg");
        this.rightLeg = this.registry.getModel("right_leg");
        this.leftEar = this.registry.getModel("left_ear");
        this.rightEar = this.registry.getModel("right_ear");
    }
    
    @Override
    public void setAngles(MobEntity mobEntity, float f, float g, float h, float i, float j){
        super.setAngles(mobEntity, f, g, h, i, j);
        this.registry.applyAnimations(f,g,h,i,j,mobEntity);
    }
}