package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.CemModelRegistry;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.entity.passive.CowEntity;

public class CemCowModel extends CowEntityModel<CowEntity>{
	private final CemModelRegistry registry;
	
	public CemCowModel(CemModelRegistry registry){
		super();
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
	public void setAngles(CowEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}