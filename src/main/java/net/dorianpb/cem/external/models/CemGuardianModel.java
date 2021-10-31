package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.models.CemModelRegistry.CemPrepRootPartParamsBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.entity.mob.GuardianEntity;

import java.util.*;

public class CemGuardianModel extends GuardianEntityModel implements CemModel{
	private static final Map<String, String>         partNames           = new HashMap<>();
	private static final Map<String, List<String>>   familyTree          = new LinkedHashMap<>();
	private static final Map<String, ModelTransform> modelTransformFixes = new HashMap<>();
	private final        CemModelRegistry            registry;
	
	static{
		partNames.put("body", "head");
		for(int i = 0; i < 12; i++){
			partNames.put("spine" + (i + 1), "spike" + i);
		}
		for(int i = 0; i < 3; i++){
			partNames.put("tail" + (i + 1), "tail" + i);
		}
	}
	
	static{
		familyTree.put("tail2", Collections.singletonList("tail3"));
		familyTree.put("tail1", Collections.singletonList("tail2"));
		familyTree.put("body",
		               Arrays.asList("spine1", "spine2", "spine3", "spine4", "spine5", "spine6", "spine7", "spine8", "spine9", "spine10", "spine11", "spine12", "eye",
		                             "tail1")
		              );
	}
	
	static{
		modelTransformFixes.put("spine1", ModelTransform.pivot(0.0F, 11.5F, 7.0F));
		modelTransformFixes.put("spine2", ModelTransform.pivot(0.0F, 11.5F, -7.0F));
		modelTransformFixes.put("spine3", ModelTransform.pivot(7.0F, 11.5F, 0.0F));
		modelTransformFixes.put("spine4", ModelTransform.pivot(-7.0F, 11.5F, 0.0F));
		modelTransformFixes.put("spine5", ModelTransform.pivot(-7.0F, 18.5F, -7.0F));
		modelTransformFixes.put("spine6", ModelTransform.pivot(7.0F, 18.5F, -7.0F));
		modelTransformFixes.put("spine7", ModelTransform.pivot(7.0F, 18.5F, 7.0F));
		modelTransformFixes.put("spine8", ModelTransform.pivot(-7.0F, 18.5F, 7.0F));
		modelTransformFixes.put("spine9", ModelTransform.pivot(0.0F, 25.5F, 7.0F));
		modelTransformFixes.put("spine10", ModelTransform.pivot(0.0F, 25.5F, -7.0F));
		modelTransformFixes.put("spine11", ModelTransform.pivot(7.0F, 25.5F, 0.0F));
		modelTransformFixes.put("spine12", ModelTransform.pivot(-7.0F, 25.5F, 0.0F));
		
	}
	
	public CemGuardianModel(CemModelRegistry registry){
		super(registry.prepRootPart((new CemPrepRootPartParamsBuilder()).setPartNameMap(partNames)
		                                                                .setFamilyTree(familyTree)
		                                                                .setVanillaReferenceModelFactory(() -> getTexturedModelData().createModel())
		                                                                .setFixes(modelTransformFixes)
		                                                                .create()));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(GuardianEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}