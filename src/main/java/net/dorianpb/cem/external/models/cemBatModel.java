package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.cemModelRegistry;
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.entity.passive.BatEntity;

public class cemBatModel extends BatEntityModel{
    private final cemModelRegistry registry;
    public cemBatModel(cemModelRegistry registry) {
        super();
        this.registry = registry;
        this.registry.initModels(this);
        this.head = this.registry.getModel("head");
        this.body = this.registry.getModel("body");
        this.leftWing = this.registry.getModel("left_wing");
        this.rightWing = this.registry.getModel("right_wing");
        this.leftWingTip = this.registry.getModel("outer_left_wing");
        this.rightWingTip = this.registry.getModel("outer_right_wing");
        this.body.addChild(this.rightWing);
        this.body.addChild(this.leftWing);
        this.rightWing.addChild(this.rightWingTip);
        this.rightWingTip.pivotZ += 3;
        this.leftWingTip.pivotZ += 3;
        this.leftWing.addChild(this.leftWingTip);
    }
    
    @Override
    public void setAngles(BatEntity batEntity, float f, float g, float h, float i, float j){
        super.setAngles(batEntity, f, g, h, i, j);
        this.registry.applyAnimations(f,g,h,i,j,batEntity);
    }
}