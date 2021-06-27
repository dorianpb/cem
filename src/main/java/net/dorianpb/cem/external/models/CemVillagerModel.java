package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.LivingEntity;

public class CemVillagerModel<T extends LivingEntity> extends VillagerResemblingModel<T>{
	private final CemModelRegistry registry;
	
	public CemVillagerModel(ModelPart root, CemModelRegistry registry){
		super(root);
		this.registry = registry;
		var headwear2 = this.registry.getEntryByPartName("headwear2");
		if(headwear2 != null && headwear2.getModel() != null){
			headwear2.getModel().setRotation('x', (float) (headwear2.getModel().getRotation('x') + Math.toRadians(-90)));
		}
		var arms = this.registry.getEntryByPartName("arms");
		if(arms != null && arms.getModel() != null){
			arms.getModel().setRotation('x', (float) (arms.getModel().getRotation('x') + Math.toRadians(-43)));
		}
	}
	
	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}