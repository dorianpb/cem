package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.entity.mob.EndermanEntity;

import java.util.HashMap;
import java.util.Map;

public class CemEndermanModel extends EndermanEntityModel<EndermanEntity> implements CemModel{
	private static final Map<String, String> partNames = new HashMap<>();
	private final        CemModelRegistry    registry;
	
	static{
		partNames.put("headwear", "hat");
	}
	
	public CemEndermanModel(CemModelRegistry registry){
		super(CemModel.prepare(registry, partNames, null, () -> getTexturedModelData().createModel(), null, null));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(EndermanEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}