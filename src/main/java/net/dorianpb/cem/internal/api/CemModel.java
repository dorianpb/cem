package net.dorianpb.cem.internal.api;

import net.dorianpb.cem.internal.config.CemConfigFairy;
import net.dorianpb.cem.internal.models.CemModelEntry;
import net.dorianpb.cem.internal.models.CemModelEntry.CemModelPart;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface CemModel{
	/**
	 * Creates a {@link CemModelPart} to build (Block)EntityModels. Calls {@link CemModelRegistry#prepRootPart(Map, Map, ModelPart, Map, Float)} with the given arguments.
	 * Params annotated with {@link Nullable} that are passed with null will be treated as empty lists, {@code inflate} with be treated as no inflate.
	 * (There is a difference between no inflate and 0 inflate.)
	 * @param registry    Pass the registry here. Required.
	 * @param partNameMap Pass a Map giving the translation from optifine names to vanilla names here.
	 * @param familyTree  Pass a Map showing which parts are parents to others, depth-first.
	 * @param fixes       Pass a map containing manual pivot points of vanilla parts here.
	 * @param inflate     Pass inflate value here.
	 *
	 * @return CemModelPart needed to create the Model.
	 */
	static CemModelPart prepare(CemModelRegistry registry,
	                            @Nullable Map<String, String> partNameMap,
	                            @Nullable Map<String, List<String>> familyTree,
	                            VanillaReferenceModelFactory vanillaReferenceModelFactory,
	                            @Nullable Map<String, ModelTransform> fixes,
	                            @Nullable Float inflate){
		return registry.prepRootPart(partNameMap, familyTree, vanillaReferenceModelFactory.get(), fixes, inflate);
	}
	
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