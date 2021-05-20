package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.CemModelRegistry;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.entity.passive.BeeEntity;

public class CemBeeModel extends BeeEntityModel<BeeEntity>{
	private final CemModelRegistry registry;
	
	public CemBeeModel(CemModelRegistry registry){
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
		
		this.registry.setChild("torso", "stinger");
		this.registry.setChild("torso", "left_antenna");
		this.registry.setChild("torso", "right_antenna");
		this.registry.setChild("body", "torso");
		this.registry.setChild("body", "right_wing");
		this.registry.setChild("body", "left_wing");
		this.registry.setChild("body", "front_legs");
		this.registry.setChild("body", "middle_legs");
		this.registry.setChild("body", "back_legs");
	}
	
	@Override
	public void setAngles(BeeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
	
}