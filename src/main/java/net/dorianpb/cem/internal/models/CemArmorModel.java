package net.dorianpb.cem.internal.models;

import net.dorianpb.cem.internal.api.CemModel;
import net.dorianpb.cem.internal.config.CemConfigFairy;
import net.dorianpb.cem.internal.models.CemModelEntry.CemCuboid;
import net.dorianpb.cem.internal.models.CemModelEntry.CemModelPart;
import net.dorianpb.cem.internal.models.CemModelEntry.TransparentCemModelPart;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CemArmorModel<C extends CemModel, T extends LivingEntity> extends BipedEntityModel<T>{
	public CemArmorModel(C model, @Nullable Float inflate){
		super(getRoot(model, inflate));
	}
	
	private static CemModelPart getRoot(CemModel model, @Nullable Float inflate){
		CemModelPart root = new CemModelPart(64, 32);
		if(CemConfigFairy.getConfig().useTransparentParts()){
			root.addChild(EntityModelPartNames.HEAD, prepPart(castPart((CemModelPart) ((BipedEntityModel<?>) model).head), EntityModelPartNames.HEAD, inflate));
			root.addChild(EntityModelPartNames.HAT, prepPart(castPart((CemModelPart) ((BipedEntityModel<?>) model).hat), EntityModelPartNames.HAT, inflate));
			root.addChild(EntityModelPartNames.BODY, prepPart(castPart((CemModelPart) ((BipedEntityModel<?>) model).body), EntityModelPartNames.BODY, inflate));
			root.addChild(EntityModelPartNames.RIGHT_ARM, prepPart(castPart((CemModelPart) ((BipedEntityModel<?>) model).rightArm), EntityModelPartNames.RIGHT_ARM, inflate));
			root.addChild(EntityModelPartNames.LEFT_ARM, prepPart(castPart((CemModelPart) ((BipedEntityModel<?>) model).leftArm), EntityModelPartNames.LEFT_ARM, inflate));
			root.addChild(EntityModelPartNames.RIGHT_LEG, prepPart(castPart((CemModelPart) ((BipedEntityModel<?>) model).rightLeg), EntityModelPartNames.RIGHT_LEG, inflate));
			root.addChild(EntityModelPartNames.LEFT_LEG, prepPart(castPart((CemModelPart) ((BipedEntityModel<?>) model).leftLeg), EntityModelPartNames.LEFT_LEG, inflate));
		}
		else{
			root.addChild(EntityModelPartNames.HEAD, prepPart(CemModelPart.of(((BipedEntityModel<?>) model).head), EntityModelPartNames.HEAD, inflate));
			root.addChild(EntityModelPartNames.HAT, prepPart(CemModelPart.of(((BipedEntityModel<?>) model).hat), EntityModelPartNames.HAT, inflate));
			root.addChild(EntityModelPartNames.BODY, prepPart(CemModelPart.of(((BipedEntityModel<?>) model).body), EntityModelPartNames.BODY, inflate));
			root.addChild(EntityModelPartNames.RIGHT_ARM, prepPart(CemModelPart.of(((BipedEntityModel<?>) model).rightArm), EntityModelPartNames.RIGHT_ARM, inflate));
			root.addChild(EntityModelPartNames.LEFT_ARM, prepPart(CemModelPart.of(((BipedEntityModel<?>) model).leftArm), EntityModelPartNames.LEFT_ARM, inflate));
			root.addChild(EntityModelPartNames.RIGHT_LEG, prepPart(CemModelPart.of(((BipedEntityModel<?>) model).rightLeg), EntityModelPartNames.RIGHT_LEG, inflate));
			root.addChild(EntityModelPartNames.LEFT_LEG, prepPart(CemModelPart.of(((BipedEntityModel<?>) model).leftLeg), EntityModelPartNames.LEFT_LEG, inflate));
		}
		return root;
	}
	
	private static CemModelPart castPart(CemModelPart modelPart){
		if(modelPart instanceof TransparentCemModelPart){
			return TransparentCemModelPart.of((TransparentCemModelPart) modelPart);
		}
		else{
			return CemModelPart.of(modelPart);
		}
	}
	
	private static <M extends CemModelPart> M prepPart(M modelpart, String name, @Nullable Float inflate){
		Map<CemModelPart, float[]> armorCandidates = new HashMap<>();
		findArmorPlacement(modelpart, armorCandidates);
		removeCuboids(modelpart, armorCandidates);
		armorCandidates.forEach((armorPart, map) -> {
			if(armorCandidates.get(armorPart) != null){
				float x = map[0];
				float y = map[1];
				float z = map[2];
				boolean reverse = map[3] == 1;
				float extra = inflate == null? 0 : inflate;
				
				Cuboid cuboid = switch(name){
					case EntityModelPartNames.HAT -> new CemCuboid(reverse? -8 : 0, 0, 0, 8, 8, 8, extra, extra, extra, false, false, 64, 32, 0, 0);
					case EntityModelPartNames.BODY -> new CemCuboid(reverse? -8 : 0, 0, 0, 8, 12, 4, extra, extra, extra, false, false, 64, 32, 16, 16);
					case EntityModelPartNames.RIGHT_ARM -> new CemCuboid(reverse? -4 : 0, 0, 0, 4, 12, 4, extra, extra, extra, false, false, 64, 32, 40, 16);
					case EntityModelPartNames.LEFT_ARM -> new CemCuboid(reverse? -4 : 0, 0, 0, 4, 12, 4, extra, extra, extra, true, false, 64, 32, 40, 16);
					case EntityModelPartNames.RIGHT_LEG -> new CemCuboid(reverse? -4 : 0, 0, 0, 4, 12, 4, extra, extra, extra, false, false, 64, 32, 0, 16);
					case EntityModelPartNames.LEFT_LEG -> new CemCuboid(reverse? -4 : 0, 0, 0, 4, 12, 4, extra, extra, extra, true, false, 64, 32, 0, 16);
					default -> null;
				};
				
				var newarmor = new CemModelPart(64, 32, cuboid == null? Collections.emptyList() : Collections.singletonList(cuboid));
				newarmor.setPivot(x, y, z);
				armorPart.addChild("armor", newarmor);
			}
		});
		return modelpart;
	}
	
	private static <M extends CemModelPart> void findArmorPlacement(M modelpart, Map<CemModelPart, float[]> armorCandidates){
		if(modelpart.children.isEmpty() || !modelpart.cuboids.isEmpty()){
			armorCandidates.put(modelpart, null);
		}
		else{
			modelpart.children.forEach((key, child) -> {
				if(child instanceof CemModelPart){
					findArmorPlacement((CemModelPart) child, armorCandidates);
				}
			});
		}
	}
	
	private static <M extends ModelPart> void removeCuboids(M modelPart, Map<CemModelPart, float[]> armorCandidates){
		if(armorCandidates.containsKey((CemModelPart) modelPart)){
			for(ModelPart.Cuboid cuboid : modelPart.cuboids){
				armorCandidates.put((CemModelPart) modelPart, new float[]{cuboid.minX, cuboid.minY, cuboid.minZ, ((CemCuboid) cuboid).isMirrorU()? 1 : 0});
			}
		}
		modelPart.cuboids.clear();
		modelPart.children.forEach((key, child) -> removeCuboids(child, armorCandidates));
	}
}