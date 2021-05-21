package net.dorianpb.cem.external.models;


import net.dorianpb.cem.internal.CemModelRegistry;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.entity.passive.SheepEntity;

public class CemSheepModel extends SheepEntityModel<SheepEntity>{
	private final CemModelRegistry registry;
	
	public CemSheepModel(CemModelRegistry registry){
		super();
		this.registry = registry;
		this.registry.initModels(this);
		this.head = this.registry.getModel("head");
		this.torso = this.registry.getModel("body");
		this.backLeftLeg = this.registry.getModel("leg2");
		this.frontLeftLeg = this.registry.getModel("leg4");
		this.backRightLeg = this.registry.getModel("leg1");
		this.frontRightLeg = this.registry.getModel("leg3");
	}
	
	@Override
	public void setAngles(SheepEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
	
	public static class CemSheepWoolModel extends SheepWoolEntityModel<SheepEntity>{
		private final CemModelRegistry registry;
		
		public CemSheepWoolModel(CemModelRegistry registry){
			super();
			this.registry = registry;
			this.registry.initModels(this);
			this.head = this.registry.getModel("head");
			this.torso = this.registry.getModel("body");
			this.backLeftLeg = this.registry.getModel("leg2");
			this.frontLeftLeg = this.registry.getModel("leg4");
			this.backRightLeg = this.registry.getModel("leg1");
			this.frontRightLeg = this.registry.getModel("leg3");
		}
		
		@Override
		public void setAngles(SheepEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
			super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
			this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
		}
	}
}