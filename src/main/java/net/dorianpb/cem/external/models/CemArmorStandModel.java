package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class CemArmorStandModel extends ArmorStandEntityModel{
	private final CemModelRegistry registry;
	
	public CemArmorStandModel(ModelPart root, CemModelRegistry registry){
		super(root);
		this.registry = registry;
	}
	
	@Override
	public void setAngles(ArmorStandEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}