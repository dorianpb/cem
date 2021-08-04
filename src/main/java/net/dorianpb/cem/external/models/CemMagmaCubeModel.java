package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.render.entity.model.MagmaCubeEntityModel;
import net.minecraft.entity.mob.MagmaCubeEntity;

import java.util.HashMap;
import java.util.Map;

public class CemMagmaCubeModel extends MagmaCubeEntityModel<MagmaCubeEntity> implements CemModel{
	private static final Map<String, String> partNames = new HashMap<>();
	private final        CemModelRegistry    registry;
	
	static{
		partNames.put("core", "inside_cube");
		for(int i = 0; i < 8; i++){
			partNames.put("segment" + (i + 1), "cube" + i);
		}
	}
	
	public CemMagmaCubeModel(CemModelRegistry registry){
		super(CemModel.prepare(registry, partNames, null, () -> getTexturedModelData().createModel(), null, null));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(MagmaCubeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}