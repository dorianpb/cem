package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CemCatModel extends CatEntityModel<CatEntity> implements CemModel{
	private static final Map<String, String>         partNames           = new HashMap<>();
	private static final Map<String, ModelTransform> modelTransformFixes = new HashMap<>();
	private final        CemModelRegistry            registry;
	
	static{
		partNames.put("front_right_leg", "right_front_leg");
		partNames.put("front_left_leg", "left_front_leg");
		partNames.put("back_right_leg", "right_hind_leg");
		partNames.put("back_left_leg", "left_hind_leg");
		partNames.put("tail", "tail1");
	}
	
	static{
		modelTransformFixes.put("body", ModelTransform.pivot(0.0F, 6.0F, 6.1F));
		modelTransformFixes.put("tail1", ModelTransform.pivot(0.0F, 14.5F, 9.0F));
		modelTransformFixes.put("tail2", ModelTransform.pivot(0.0F, 22.5F, 9.0F));
		
	}
	
	public CemCatModel(CemModelRegistry registry, @Nullable Float inflate){
		super(CemModel.prepare(registry, partNames, null, () -> TexturedModelData.of(getModelData(Dilation.NONE), 0, 0).createModel(), modelTransformFixes, inflate));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(CatEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}