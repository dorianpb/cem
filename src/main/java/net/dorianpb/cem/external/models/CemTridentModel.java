package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.models.CemModelRegistry.CemPrepRootPartParamsBuilder;
import net.minecraft.client.render.entity.model.TridentEntityModel;

import java.util.HashMap;
import java.util.Map;

public class CemTridentModel extends TridentEntityModel implements CemModel{
	private static final Map<String, String> partNames = new HashMap<>();
	private final        CemModelRegistry    registry;
	
	static{
		partNames.put("body", "pole");
	}
	
	public CemTridentModel(CemModelRegistry registry){
		super(registry.prepRootPart((new CemPrepRootPartParamsBuilder()).setPartNameMap(partNames)
		                                                                .setVanillaReferenceModelFactory(() -> getTexturedModelData().createModel())
		                                                                .create()));
		this.registry = registry;
	}
	
	//	@Override
	//	public void setAngles(TridentEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
	//		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
	//		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	//	}
}