package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.CemModelRegistry;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;

public class CemPigModel extends PigEntityModel<PigEntity>{
	private final CemModelRegistry registry;
	
	public CemPigModel(float scale, CemModelRegistry registry){
		super(scale);
		this.registry = registry;
		this.registry.initModels(this);
		this.head = this.registry.getModel("head");
		this.torso = this.registry.getModel("body");
		this.backLeftLeg = this.registry.getModel("leg1");
		this.backRightLeg = this.registry.getModel("leg2");
		this.frontLeftLeg = this.registry.getModel("leg3");
		this.frontRightLeg = this.registry.getModel("leg4");
	}
	
	@Override
	public void setAngles(PigEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}