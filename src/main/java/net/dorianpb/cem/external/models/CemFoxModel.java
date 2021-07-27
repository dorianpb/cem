package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.entity.passive.FoxEntity;

public class CemFoxModel extends FoxEntityModel<FoxEntity> implements CemModel{
	private final CemModelRegistry registry;
	
	public CemFoxModel(ModelPart root, CemModelRegistry registry){
		super(root);
		this.registry = registry;
		root.getChild("body").getChild("tail").setPivot(-4.0F, 15.0F, -2.0F);
	}
	
	@Override
	public void setAngles(FoxEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}