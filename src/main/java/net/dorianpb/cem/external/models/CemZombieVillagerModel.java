package net.dorianpb.cem.external.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.mob.ZombieEntity;

import java.util.*;

public class CemZombieVillagerModel<T extends ZombieEntity> extends ZombieVillagerEntityModel<T> implements CemModel{
	private static final Map<String, String>       partNames  = new HashMap<>();
	private static final Map<String, List<String>> familyTree = new LinkedHashMap<>();
	private final        CemModelRegistry          registry;
	
	static{
		partNames.put("headwear", "hat");
		partNames.put("headwear2", "hat_rim");
		partNames.put("bodywear", "jacket");
	}
	
	static{
		familyTree.put("headwear", Collections.singletonList("headwear2"));
		familyTree.put("head", Arrays.asList("headwear", "nose"));
	}
	
	public CemZombieVillagerModel(CemModelRegistry registry){
		super(CemModel.prepare(registry, partNames, familyTree, () -> getTexturedModelData().createModel(), null, null));
		this.registry = registry;
		this.rotatePart(this.registry.getEntryByPartName("headwear2"), 'x', -90);
	}
	
	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		this.registry.applyAnimations(limbAngle, limbDistance, animationProgress, headYaw, headPitch, entity);
	}
}