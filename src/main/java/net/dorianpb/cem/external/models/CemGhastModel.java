package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.entity.mob.GhastEntity;

import java.util.HashMap;
import java.util.Map;

public class CemGhastModel extends GhastEntityModel<GhastEntity> implements CemModel{
	private static final Map<String, String>         partNames           = new HashMap<>();
	private static final Map<String, ModelTransform> modelTransformFixes = new HashMap<>();
	private final        CemModelRegistry            registry;
	
	static{
		for(int i = 0; i < 9; i++){
			partNames.put("tentacle" + (i + 1), "tentacle" + i);
		}
	}
	
	static{
		modelTransformFixes.put("body", ModelTransform.pivot(0.0F, 4.0F, 0.0F));
		modelTransformFixes.put("tentacle1", ModelTransform.pivot(-3.7F, 11.0F, -5.0F));
		modelTransformFixes.put("tentacle2", ModelTransform.pivot(1.3F, 11.0F, -5.0F));
		modelTransformFixes.put("tentacle3", ModelTransform.pivot(6.3F, 11.0F, -5.0F));
		modelTransformFixes.put("tentacle4", ModelTransform.pivot(-6.3F, 11.0F, -5.0F));
		modelTransformFixes.put("tentacle5", ModelTransform.pivot(-1.3F, 11.0F, 0.0F));
		modelTransformFixes.put("tentacle6", ModelTransform.pivot(3.7F, 11.0F, 0.0F));
		modelTransformFixes.put("tentacle7", ModelTransform.pivot(-3.7F, 11.0F, 5.0F));
		modelTransformFixes.put("tentacle8", ModelTransform.pivot(1.3F, 11.0F, 5.0F));
		modelTransformFixes.put("tentacle9", ModelTransform.pivot(6.3F, 11.0F, 5.0F));
	}
	
	public CemGhastModel(CemModelRegistry registry){
		super(registry.prepRootPart(partNames, null, () -> getTexturedModelData().createModel(), modelTransformFixes));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(GhastEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}