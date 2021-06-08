package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.entity.passive.SheepEntity;

public class CemSheepModel extends SheepEntityModel<SheepEntity>{
	private final CemModelRegistry registry;
	
	public CemSheepModel(ModelPart root, CemModelRegistry registry){
		super(root);
		this.registry = registry;
	}
	
	@Override
	public void setAngles(SheepEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
	
	public static class CemSheepWoolModel extends SheepWoolEntityModel<SheepEntity>{
		private final CemModelRegistry registry;
		
		public CemSheepWoolModel(ModelPart root, CemModelRegistry registry){
			super(root);
			this.registry = registry;
		}
		
		@Override
		public void setAngles(SheepEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
			super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
			this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
		}
	}
}