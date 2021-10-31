package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.models.CemModelRegistry.CemPrepRootPartParamsBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.entity.mob.PhantomEntity;

import java.util.*;

public class CemPhantomModel extends PhantomEntityModel<PhantomEntity> implements CemModel{
	private static final Map<String, String>         partNames           = new HashMap<>();
	private static final Map<String, List<String>>   familyTree          = new LinkedHashMap<>();
	private static final Map<String, ModelTransform> modelTransformFixes = new HashMap<>();
	private final        CemModelRegistry            registry;
	
	static{
		partNames.put("tail", "tail_base");
		partNames.put("tail2", "tail_tip");
		partNames.put("left_wing", "left_wing_base");
		partNames.put("right_wing", "right_wing_base");
	}
	
	static{
		familyTree.put("tail", Collections.singletonList("tail2"));
		familyTree.put("left_wing", Collections.singletonList("left_wing_tip"));
		familyTree.put("right_wing", Collections.singletonList("right_wing_tip"));
		familyTree.put("body", Arrays.asList("head", "tail", "right_wing", "left_wing"));
	}
	
	static{
		modelTransformFixes.put("body", ModelTransform.pivot(0.0F, 19.0F, 0.0F));
		modelTransformFixes.put("head", ModelTransform.pivot(0.0F, 1.25F, -8.0F));
	}
	
	public CemPhantomModel(CemModelRegistry registry){
		super(registry.prepRootPart((new CemPrepRootPartParamsBuilder()).setPartNameMap(partNames)
		                                                                .setFamilyTree(familyTree)
		                                                                .setVanillaReferenceModelFactory(() -> getTexturedModelData().createModel())
		                                                                .setFixes(modelTransformFixes)
		                                                                .create()));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(PhantomEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}