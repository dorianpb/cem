package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.entity.passive.BeeEntity;

import java.util.*;

public class CemBeeModel extends BeeEntityModel<BeeEntity> implements CemModel{
	private static final Map<String, String>       partNames  = new HashMap<>();
	private static final Map<String, List<String>> familyTree = new LinkedHashMap<>();
	private final        CemModelRegistry          registry;
	
	static{
		partNames.put("body", "bone");
		partNames.put("torso", "body");
	}
	
	static{
		familyTree.put("torso", Arrays.asList("stinger", "left_antenna", "right_antenna"));
		familyTree.put("body", Arrays.asList("torso", "right_wing", "left_wing", "front_legs", "middle_legs", "back_legs"));
	}
	
	public CemBeeModel(CemModelRegistry registry){
		super(CemModel.prepare(registry, partNames, familyTree, () -> getTexturedModelData().createModel(), null, null));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(BeeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}