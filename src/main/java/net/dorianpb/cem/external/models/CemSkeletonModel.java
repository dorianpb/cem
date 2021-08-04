package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CemSkeletonModel extends SkeletonEntityModel<AbstractSkeletonEntity> implements CemModel{
	private static final Map<String, String> partNames = new HashMap<>();
	private final        CemModelRegistry    registry;
	
	static{
		partNames.put("headwear", "hat");
	}
	
	public CemSkeletonModel(CemModelRegistry registry, @Nullable Float inflate){
		super(CemModel.prepare(registry, partNames, null, () -> getTexturedModelData().createModel(), null, inflate));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(AbstractSkeletonEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}