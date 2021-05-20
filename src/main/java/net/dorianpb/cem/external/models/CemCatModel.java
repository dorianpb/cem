package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.CemModelRegistry;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;

public class CemCatModel extends CatEntityModel<CatEntity>{
	private final CemModelRegistry registry;
	
	public CemCatModel(float scale, CemModelRegistry registry){
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
	public void setAngles(CatEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}