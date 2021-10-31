package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.models.CemModelRegistry.CemPrepRootPartParamsBuilder;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.entity.passive.RabbitEntity;

import java.util.HashMap;
import java.util.Map;

public class CemRabbitModel extends RabbitEntityModel<RabbitEntity> implements CemModel{
	private static final Map<String, String> partNames = new HashMap<>();
	private final        CemModelRegistry    registry;
	
	static{
		partNames.put("left_foot", "left_hind_foot");
		partNames.put("right_foot", "right_hind_foot");
		partNames.put("left_thigh", "left_haunch");
		partNames.put("right_thigh", "right_haunch");
		partNames.put("left_arm", "left_front_leg");
		partNames.put("right_arm", "right_front_leg");
	}
	
	public CemRabbitModel(CemModelRegistry registry){
		super(registry.prepRootPart((new CemPrepRootPartParamsBuilder()).setPartNameMap(partNames)
		                                                                .setVanillaReferenceModelFactory(() -> getTexturedModelData().createModel())
		                                                                .create()));
		this.registry = registry;
		this.rotatePart(this.registry.getEntryByPartName("body"), 'x', -19.999F);
	}
	
	@Override
	public void setAngles(RabbitEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}