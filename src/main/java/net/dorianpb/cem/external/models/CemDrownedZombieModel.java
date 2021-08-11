package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.entity.mob.DrownedEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CemDrownedZombieModel extends DrownedEntityModel<DrownedEntity> implements CemModel{
	private static final Map<String, String> partNames = new HashMap<>();
	private final        CemModelRegistry    registry;
	
	static{
		partNames.put("headwear", "hat");
	}
	
	public CemDrownedZombieModel(CemModelRegistry registry, @Nullable Float inflate){
		super(registry.prepRootPart(partNames, () -> getTexturedModelData(Dilation.NONE).createModel(), null, inflate));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(DrownedEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}