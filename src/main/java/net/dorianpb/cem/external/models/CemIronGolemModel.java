package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.entity.passive.IronGolemEntity;

public class CemIronGolemModel extends IronGolemEntityModel<IronGolemEntity> implements CemModel{
	private final CemModelRegistry registry;
	
	
	public CemIronGolemModel(CemModelRegistry registry){
		super(registry.prepRootPart(null, () -> getTexturedModelData().createModel()));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(IronGolemEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}