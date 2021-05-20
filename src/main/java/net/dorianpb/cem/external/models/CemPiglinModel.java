package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.CemModelRegistry;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.mob.MobEntity;

public class CemPiglinModel extends PiglinEntityModel<MobEntity>{
	private final CemModelRegistry registry;
	
	public CemPiglinModel(float scale, int textureWidth, int textureHeight, CemModelRegistry registry){
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
	public void setAngles(MobEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}