package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer.DragonEntityModel;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

import java.util.*;

public class CemEnderDragonModel extends DragonEntityModel implements CemModel{
	private static final Map<String, String>         partNames           = new HashMap<>();
	private static final Map<String, List<String>>   familyTree          = new LinkedHashMap<>();
	private static final Map<String, ModelTransform> modelTransformFixes = new HashMap<>();
	private final        CemModelRegistry            registry;
	
	static{
		partNames.put("spine", "neck");
		partNames.put("front_left_leg", "left_front_leg");
		partNames.put("front_left_shin", "left_front_leg_tip");
		partNames.put("front_left_foot", "left_front_foot");
		partNames.put("back_left_leg", "left_hind_leg");
		partNames.put("back_left_shin", "left_hind_leg_tip");
		partNames.put("back_left_foot", "left_hind_foot");
		partNames.put("front_right_leg", "right_front_leg");
		partNames.put("front_right_shin", "right_front_leg_tip");
		partNames.put("front_right_foot", "right_front_foot");
		partNames.put("back_right_leg", "right_hind_leg");
		partNames.put("back_right_shin", "right_hind_leg_tip");
		partNames.put("back_right_foot", "right_hind_foot");
	}
	
	static{
		familyTree.put("head", Collections.singletonList("jaw"));
		familyTree.put("front_left_shin", Collections.singletonList("front_left_foot"));
		familyTree.put("front_left_leg", Collections.singletonList("front_left_shin"));
		familyTree.put("back_left_shin", Collections.singletonList("back_left_foot"));
		familyTree.put("back_left_leg", Collections.singletonList("back_left_shin"));
		familyTree.put("left_wing", Collections.singletonList("left_wing_tip"));
		familyTree.put("front_right_shin", Collections.singletonList("front_right_foot"));
		familyTree.put("front_right_leg", Collections.singletonList("front_right_shin"));
		familyTree.put("back_right_shin", Collections.singletonList("back_right_foot"));
		familyTree.put("back_right_leg", Collections.singletonList("back_right_shin"));
		familyTree.put("right_wing", Collections.singletonList("right_wing_tip"));
	}
	
	static{
		modelTransformFixes.put("head", ModelTransform.pivot(0.0F, 18.0F, -24.0F));
		modelTransformFixes.put("spine", ModelTransform.pivot(0.0F, 19.0F, -13.0F));
	}
	
	public CemEnderDragonModel(CemModelRegistry registry){
		super(CemModel.prepare(registry, partNames, familyTree, () -> EnderDragonEntityRenderer.getTexturedModelData().createModel(), modelTransformFixes, null));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(EnderDragonEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}