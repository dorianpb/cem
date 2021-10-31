package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.models.CemModelRegistry.CemPrepRootPartParamsBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

import java.util.HashMap;
import java.util.Map;

public class CemMinecartModel<T extends AbstractMinecartEntity> extends MinecartEntityModel<T> implements CemModel{
	private static final Map<String, String>         partNames           = new HashMap<>();
	private static final Map<String, ModelTransform> modelTransformFixes = new HashMap<>();
	private final        CemModelRegistry            registry;
	
	static{
		partNames.put("dirt", "contents");
	}
	
	static{
		modelTransformFixes.put("left", ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		modelTransformFixes.put("dirt", ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		modelTransformFixes.put("bottom", ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		modelTransformFixes.put("back", ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		modelTransformFixes.put("front", ModelTransform.pivot(0.0F, 23.0F, 9.0F));
		modelTransformFixes.put("right", ModelTransform.pivot(0.0F, 24.0F, 0.0F));
	}
	
	public CemMinecartModel(CemModelRegistry registry){
		super(registry.prepRootPart((new CemPrepRootPartParamsBuilder()).setPartNameMap(partNames)
		                                                                .setVanillaReferenceModelFactory(() -> getTexturedModelData().createModel())
		                                                                .setFixes(modelTransformFixes)
		                                                                .create()));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		//		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}