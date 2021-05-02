package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.cemModelRegistry;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class cemArmorStandModel extends ArmorStandEntityModel{
    private final cemModelRegistry registry;
    
    public cemArmorStandModel(float scale, cemModelRegistry registry) {
        super(scale);
        this.registry = registry;
        this.registry.initModels(this);
        this.head = this.registry.getModel("head");
        this.helmet = this.registry.getModel("headwear");
        this.helmet.visible = false; //since that's the default in minecraft too
        this.torso = this.registry.getModel("body");
        this.leftArm = this.registry.getModel("left_arm");
        this.rightArm = this.registry.getModel("right_arm");
        this.leftLeg = this.registry.getModel("left_leg");
        this.rightLeg = this.registry.getModel("right_leg");
        this.rightTorso = this.registry.getModel("right");
        this.leftTorso = this.registry.getModel("left");
        this.hip = this.registry.getModel("waist");
        this.plate = this.registry.getModel("base");
        
    }
    
    @Override
    public void setAngles(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j){
        super.setAngles(armorStandEntity, f, g, h, i, j);
        this.registry.applyAnimations(f,g,h,i,j,armorStandEntity);
    }
}