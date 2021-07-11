package net.dorianpb.cem.internal.api;

import net.dorianpb.cem.internal.config.CemConfigFairy;
import net.dorianpb.cem.internal.models.CemModelEntry;
import net.dorianpb.cem.internal.models.CemModelEntry.CemModelPart;

public interface CemModel{
	default void rotatePart(CemModelEntry cemModelEntry, char axis, float degrees, boolean respectConfig){
		CemModelPart modelPart = (cemModelEntry != null)? cemModelEntry.getModel() : null;
		if(modelPart != null && (!respectConfig || !CemConfigFairy.getConfig().useTransparentParts())){
			modelPart.setRotation(axis, (float) (modelPart.getRotation(axis) + Math.toRadians(degrees)));
		}
	}
}