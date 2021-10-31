package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.models.CemModelRegistry.CemPrepRootPartParamsBuilder;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.HorseBaseEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CemHorseModel<T extends HorseBaseEntity> extends HorseEntityModel<T> implements CemModel{
	private static final Map<String, String>         partNames           = new HashMap<>();
	private static final Map<String, List<String>>   familyTree          = new LinkedHashMap<>();
	private static final Map<String, ModelTransform> modelTransformFixes = new HashMap<>();
	private final        CemModelRegistry            registry;
	
	static{
		partNames.put("neck", "head_parts");
		partNames.put("mouth", "upper_mouth");
		partNames.put("headpiece", "head_saddle");
		partNames.put("noseband", "mouth_saddle_wrap");
		partNames.put("left_bit", "left_saddle_mouth");
		partNames.put("right_bit", "right_saddle_mouth");
		partNames.put("left_rein", "left_saddle_line");
		partNames.put("right_rein", "right_saddle_line");
		partNames.put("front_left_leg", "left_front_leg");
		partNames.put("child_front_left_leg", "left_front_baby_leg");
		partNames.put("front_right_leg", "right_front_leg");
		partNames.put("child_front_right_leg", "right_front_baby_leg");
		partNames.put("back_left_leg", "left_hind_leg");
		partNames.put("child_back_left_leg", "left_hind_baby_leg");
		partNames.put("back_right_leg", "right_hind_leg");
		partNames.put("child_back_right_leg", "right_hind_baby_leg");
	}
	
	static{
		familyTree.put("body", Arrays.asList("tail", "saddle"));
		familyTree.put("head", Arrays.asList("left_ear", "right_ear"));
		familyTree.put("neck", Arrays.asList("head", "mane", "mouth", "noseband", "headpiece", "left_bit", "right_bit", "left_rein", "right_rein"));
	}
	
	static{
		modelTransformFixes.put("front_right_leg", ModelTransform.pivot(-4.0F, 14.0F, -10F));
		modelTransformFixes.put("front_left_leg", ModelTransform.pivot(4.0F, 14.0F, -10F));
		modelTransformFixes.put("back_right_leg", ModelTransform.pivot(-4.0F, 14.0F, 8.0F));
		modelTransformFixes.put("back_left_leg", ModelTransform.pivot(4.0F, 14.0F, 8.0F));
		modelTransformFixes.put("child_front_right_leg", ModelTransform.pivot(-4.0F, 14.0F, -10.0F));
		modelTransformFixes.put("child_front_left_leg", ModelTransform.pivot(4.0F, 14.0F, -10.0F));
		modelTransformFixes.put("child_back_right_leg", ModelTransform.pivot(-4.0F, 14.0F, 8.0F));
		modelTransformFixes.put("child_back_left_leg", ModelTransform.pivot(-.0F, 14.0F, 8.0F));
		modelTransformFixes.put("tail", ModelTransform.pivot(0.0F, -8.0F, 5.0F));
	}
	
	public CemHorseModel(CemModelRegistry registry, @Nullable Float inflate){
		super(registry.prepRootPart(new CemPrepRootPartParamsBuilder().setPartNameMap(partNames)
		                                                              .setFamilyTree(familyTree)
		                                                              .setVanillaReferenceModelFactory(() -> TexturedModelData.of(getModelData(Dilation.NONE), 0, 0)
		                                                                                                                      .createModel())
		                                                              .setFixes(modelTransformFixes)
		                                                              .setInflate(inflate)
		                                                              .create()));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}