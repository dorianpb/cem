package net.dorianpb.cem.internal.api;

import net.dorianpb.cem.internal.config.CemConfigFairy;
import net.dorianpb.cem.internal.models.CemModelEntry;
import net.dorianpb.cem.internal.models.CemModelEntry.CemModelPart;
import net.minecraft.client.model.ModelPart;

public interface CemModel{
	
	default void rotatePart(CemModelEntry cemModelEntry, char axis, float degrees){
		CemModelPart modelPart = (cemModelEntry != null)? cemModelEntry.getModel() : null;
		if(modelPart != null && !CemConfigFairy.getConfig().useTransparentParts()){
			modelPart.setRotation(axis, (float) (modelPart.getRotation(axis) + Math.toRadians((degrees + 360) % 360)));
		}
	}
	
	@FunctionalInterface
	interface VanillaReferenceModelFactory{
		ModelPart get();
	}
}