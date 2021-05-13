package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.cemModelRegistry;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.entity.passive.BeeEntity;

public class cemBeeModel extends BeeEntityModel<BeeEntity>{
    private final cemModelRegistry registry;
    public cemBeeModel(cemModelRegistry registry) {
        super();
        this.registry = registry;
        this.registry.initModels(this);
        this.body = this.registry.getModel("body");
        this.torso = this.registry.getModel("torso");
        this.leftWing = this.registry.getModel("left_wing");
        this.rightWing = this.registry.getModel("right_wing");
        this.frontLegs = this.registry.getModel("front_legs");
        this.middleLegs = this.registry.getModel("middle_legs");
        this.backLegs = this.registry.getModel("back_legs");
        this.stinger = this.registry.getModel("stinger");
        this.leftAntenna = this.registry.getModel("left_antenna");
        this.rightAntenna = this.registry.getModel("right_antenna");
    
        this.registry.setChild("torso","stinger");
        this.registry.setChild("torso","left_antenna");
        this.registry.setChild("torso","right_antenna");
        this.registry.setChild("body","torso");
        this.registry.setChild("body","right_wing");
        this.registry.setChild("body","left_wing");
        this.registry.setChild("body","front_legs");
        this.registry.setChild("body","middle_legs");
        this.registry.setChild("body","back_legs");
    }
    
    @Override
    public void setAngles(BeeEntity beeEntity, float f, float g, float h, float i, float j){
        super.setAngles(beeEntity, f, g, h, i, j);
        this.registry.applyAnimations(f,g,h,i,j,beeEntity);
    }

}