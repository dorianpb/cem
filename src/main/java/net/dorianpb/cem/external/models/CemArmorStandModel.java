package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;

import java.util.HashMap;
import java.util.Map;

public class CemArmorStandModel extends ArmorStandEntityModel implements CemModel{
	private static final Map<String, String> partNames = new HashMap<>();
	private final        CemModelRegistry    registry;
	
	static{
		partNames.put("headwear", "hat");
		partNames.put("right", "right_body_stick");
		partNames.put("left", "left_body_stick");
		partNames.put("waist", "shoulder_stick");
		partNames.put("base", "base_plate");
	}
	
	public CemArmorStandModel(CemModelRegistry registry){
		super(registry.prepRootPart(partNames, () -> getTexturedModelData().createModel()));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(ArmorStandEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}