package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.cemModelRegistry;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;

public class cemCatModel extends CatEntityModel<CatEntity>{
    private final cemModelRegistry registry;
    public cemCatModel(float scale, cemModelRegistry registry) {
        super(scale);
        this.registry = registry;
        this.registry.initModels(this);
        this.head = this.registry.getModel("head");
        this.torso = this.registry.getModel("body");
        this.leftBackLeg = this.registry.getModel("back_left_leg");
        this.leftFrontLeg = this.registry.getModel("front_left_leg");
        this.rightBackLeg = this.registry.getModel("back_right_leg");
        this.rightFrontLeg = this.registry.getModel("front_right_leg");
        this.lowerTail = this.registry.getModel("tail");
        this.upperTail = this.registry.getModel("tail2");
    }
    
    @Override
    public void setAngles(CatEntity catEntity, float f, float g, float h, float i, float j){
        super.setAngles(catEntity, f, g, h, i, j);
        this.registry.applyAnimations(f,g,h,i,j,catEntity);
    }
}