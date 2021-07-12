package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

public class CemMinecartModel<T extends AbstractMinecartEntity> extends MinecartEntityModel<T> implements CemModel{
	private final CemModelRegistry registry;
	
	public CemMinecartModel(ModelPart root, CemModelRegistry registry){
		super(root);
		this.registry = registry;
		for(String key : new String[]{"front", "back", "left", "right", "bottom"}){
			var entry = this.registry.getEntryByPartName(key);
			if(entry != null){
				entry.getModel().pivotY += -19;
				this.rotatePart(entry, 'y', 90);
			}
		}
	}
	
	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		//		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}