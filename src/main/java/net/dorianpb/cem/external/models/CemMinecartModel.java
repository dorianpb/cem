package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
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
		modelTransformFixes.put("front", ModelTransform.pivot(0.0F, 23.0F, 9.0F));
		
	}
	
	public CemMinecartModel(CemModelRegistry registry){
		super(registry.prepRootPart(partNames, () -> getTexturedModelData().createModel(), modelTransformFixes, null));
		this.registry = registry;
		this.registry.getPrePreparedPart().getChild("front").setPivot(-9.0F, 23.0F, 0.0F);
		for(String key : new String[]{"front", "back", "left", "right", "bottom"}){
			var entry = this.registry.getEntryByPartName(key);
			if(entry != null){
				entry.getModel().pivotY += -19;
				this.rotatePart(entry, 'y', 90);
			}
		}
	}
	
	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		//		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}