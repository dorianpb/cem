package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.entity.mob.BlazeEntity;

import java.util.HashMap;
import java.util.Map;

public class CemBlazeModel extends BlazeEntityModel<BlazeEntity> implements CemModel{
	private static final Map<String, String> partNames = new HashMap<>();
	private final        CemModelRegistry    registry;
	
	static{
		for(int i = 0; i < 12; i++){
			partNames.put("stick" + (i + 1), "part" + i);
		}
	}
	
	public CemBlazeModel(CemModelRegistry registry){
		super(CemModel.prepare(registry, partNames, null, () -> getTexturedModelData().createModel(), null, null));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(BlazeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}