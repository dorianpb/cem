package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.GiantEntityModel;
import net.minecraft.entity.mob.GiantEntity;

import java.util.HashMap;
import java.util.Map;

public class CemGiantZombieModel extends GiantEntityModel implements CemModel{
	private static final Map<String, String> partNames = new HashMap<>();
	private final        CemModelRegistry    registry;
	
	static{
		partNames.put("headwear", "hat");
	}
	
	public CemGiantZombieModel(CemModelRegistry registry){
		super(registry.prepRootPart(partNames, () -> TexturedModelData.of(getModelData(Dilation.NONE, 0), 0, 0).createModel()));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(GiantEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}