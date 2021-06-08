package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;

public class CemCreeperModel extends CreeperEntityModel<CreeperEntity>{
	private final CemModelRegistry registry;
	
	public CemCreeperModel(ModelPart root, CemModelRegistry registry){
		super(root);
		this.registry = registry;
	}
	
	@Override
	public void setAngles(CreeperEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}