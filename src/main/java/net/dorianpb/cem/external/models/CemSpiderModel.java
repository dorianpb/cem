package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.entity.mob.SpiderEntity;

import java.util.HashMap;
import java.util.Map;

public class CemSpiderModel<T extends SpiderEntity> extends SpiderEntityModel<T> implements CemModel{
	private static final Map<String, String> partNames = new HashMap<>();
	private final        CemModelRegistry    registry;
	
	static{
		partNames.put("neck", "body0");
		partNames.put("body", "body1");
		partNames.put("leg1", "right_hind_leg");
		partNames.put("leg2", "left_hind_leg");
		partNames.put("leg3", "right_middle_hind_leg");
		partNames.put("leg4", "left_middle_hind_leg");
		partNames.put("leg5", "right_middle_front_leg");
		partNames.put("leg6", "left_middle_front_leg");
		partNames.put("leg7", "right_front_leg");
		partNames.put("leg8", "left_front_leg");
	}
	
	public CemSpiderModel(CemModelRegistry registry){
		super(registry.prepRootPart(partNames, () -> getTexturedModelData().createModel()));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}