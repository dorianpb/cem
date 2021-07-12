package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.entity.passive.RabbitEntity;

public class CemRabbitModel extends RabbitEntityModel<RabbitEntity> implements CemModel{
	private final CemModelRegistry registry;
	
	public CemRabbitModel(ModelPart root, CemModelRegistry registry){
		super(root);
		this.registry = registry;
		this.rotatePart(this.registry.getEntryByPartName("body"), 'x', -19.999F);
	}
	
	@Override
	public void setAngles(RabbitEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}