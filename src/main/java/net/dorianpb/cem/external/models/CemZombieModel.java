package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;

import java.util.HashMap;
import java.util.Map;

public class CemZombieModel extends ZombieEntityModel<ZombieEntity> implements CemModel{
	private static final Map<String, String> partNames = new HashMap<>();
	private final        CemModelRegistry    registry;
	
	static{
		partNames.put("headwear", "hat");
	}
	
	public CemZombieModel(CemModelRegistry registry){
		super(CemModel.prepare(registry, partNames, null, () -> TexturedModelData.of(getModelData(Dilation.NONE, 0), 0, 0).createModel(), null, null));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(ZombieEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}