package net.dorianpb.cem.external.models;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.dorianpb.cem.internal.CemModelRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.entity.mob.BlazeEntity;

import java.util.Arrays;

public class CemBlazeModel extends BlazeEntityModel<BlazeEntity>{
	private final CemModelRegistry registry;
	
	public CemBlazeModel(CemModelRegistry registry){
		super();
		this.registry = registry;
		this.registry.initModels(this);
		this.head = this.registry.getModel("head");
		for(int i = 0; i < 12; i++){
			this.rods[i] = this.registry.getModel("stick" + (i + 1));
		}
		Builder<ModelPart> builder = ImmutableList.builder();
		builder.add(this.head);
		builder.addAll(Arrays.asList(this.rods));
		this.parts = builder.build();
	}
	
	@Override
	public void setAngles(BlazeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
	
}