package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.entity.passive.CowEntity;

public class CemCowModel<T extends CowEntity> extends CowEntityModel<T> implements CemModel{
	private final CemModelRegistry registry;
	
	public CemCowModel(ModelPart root, CemModelRegistry registry){
		super(root);
		this.registry = registry;
		this.rotatePart(this.registry.getEntryByPartName("body"), 'x', 90, false);
	}
	
	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}