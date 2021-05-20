package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.CemModelRegistry;
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.entity.passive.BatEntity;

public class CemBatModel extends BatEntityModel{
	private final CemModelRegistry registry;
	
	public CemBatModel(CemModelRegistry registry){
		super();
		this.registry = registry;
		this.registry.initModels(this);
		this.head = this.registry.getModel("head");
		this.body = this.registry.getModel("body");
		this.leftWing = this.registry.getModel("left_wing");
		this.rightWing = this.registry.getModel("right_wing");
		this.leftWingTip = this.registry.getModel("outer_left_wing");
		this.rightWingTip = this.registry.getModel("outer_right_wing");
		
		this.registry.setChild("right_wing", "outer_right_wing");
		this.registry.setChild("left_wing", "outer_left_wing");
		this.registry.setChild("body", "left_wing");
		this.registry.setChild("body", "right_wing");
	}
	
	@Override
	public void setAngles(BatEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}