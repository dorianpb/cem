package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.models.CemModelRegistry.CemPrepRootPartParamsBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.entity.passive.SalmonEntity;

import java.util.*;

public class CemSalmonModel extends SalmonEntityModel<SalmonEntity> implements CemModel{
	private static final Map<String, String>         partNames           = new HashMap<>();
	private static final Map<String, List<String>>   familyTree          = new LinkedHashMap<>();
	private static final Map<String, ModelTransform> modelTransformFixes = new HashMap<>();
	private final        CemModelRegistry            registry;
	
	static{
		partNames.put("tail", "back_fin");
		partNames.put("fin_left", "left_fin");
		partNames.put("fin_right", "right_fin");
		partNames.put("fin_back_1", "top_front_fin");
		partNames.put("fin_back_2", "top_back_fin");
	}
	
	static{
		familyTree.put("body_front", Collections.singletonList("fin_back_1"));
		familyTree.put("body_back", Arrays.asList("tail", "fin_back_2"));
	}
	
	static{
		modelTransformFixes.put("head", ModelTransform.pivot(0.0F, 18.0F, -4.0F));
		modelTransformFixes.put("fin_left", ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		modelTransformFixes.put("fin_right", ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		modelTransformFixes.put("body_back", ModelTransform.pivot(0.0F, 18.0F, 4.0F));
		modelTransformFixes.put("body_front", ModelTransform.pivot(0.0F, 18.0F, -4.0F));
	}
	
	public CemSalmonModel(CemModelRegistry registry){
		super(registry.prepRootPart((new CemPrepRootPartParamsBuilder()).setPartNameMap(partNames)
		                                                                .setFamilyTree(familyTree)
		                                                                .setVanillaReferenceModelFactory(() -> getTexturedModelData().createModel())
		                                                                .setFixes(modelTransformFixes)
		                                                                .create()));
		this.registry = registry;
	}
	
	@Override
	public void setAngles(SalmonEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}