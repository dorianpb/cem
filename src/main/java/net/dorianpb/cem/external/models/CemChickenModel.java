package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.CemModelRegistry;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.entity.passive.ChickenEntity;

public class CemChickenModel extends ChickenEntityModel<ChickenEntity>{
	private final CemModelRegistry registry;
	
	public CemChickenModel(CemModelRegistry registry){
		super();
		this.registry = registry;
		this.registry.initModels(this);
		this.head = this.registry.getModel("head");
		this.torso = this.registry.getModel("body");
		this.leftWing = this.registry.getModel("left_wing");
		this.rightWing = this.registry.getModel("right_wing");
		this.leftLeg = this.registry.getModel("left_leg");
		this.rightLeg = this.registry.getModel("right_leg");
		this.beak = this.registry.getModel("bill");
		this.wattle = this.registry.getModel("chin");
	}
	
	@Override
	public void setAngles(ChickenEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}