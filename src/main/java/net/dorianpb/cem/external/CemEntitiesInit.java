package net.dorianpb.cem.external;

import net.dorianpb.cem.external.renderers.*;
import net.dorianpb.cem.internal.api.CemEntityInitializer;
import net.dorianpb.cem.internal.api.CemFactory;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;

public class CemEntitiesInit extends CemEntityInitializer{
	@Override
	public void onInit(){
		register(EntityType.PIGLIN, new CemFactory(CemPiglinRenderer.class, EntityType.PIGLIN));
		register(EntityType.PIGLIN_BRUTE, new CemFactory(CemPiglinRenderer.class, EntityType.PIGLIN_BRUTE));
		register(EntityType.ZOMBIFIED_PIGLIN, new CemFactory(CemPiglinRenderer.class, EntityType.ZOMBIFIED_PIGLIN));
		register(EntityType.ENDERMAN, new CemFactory(CemEndermanRenderer.class));
		register(EntityType.CREEPER, new CemFactory(CemCreeperRenderer.class));
		register("creeper_charge");
		register(EntityType.CAT, new CemFactory(CemCatRenderer.class));
		register(EntityType.BLAZE, new CemFactory(CemBlazeRenderer.class));
		register(EntityType.ARMOR_STAND, new CemFactory(CemArmorStandRenderer.class));
		register(EntityType.OCELOT, new CemFactory(CemOcelotRenderer.class));
		register(EntityType.BAT, new CemFactory(CemBatRenderer.class));
		register(EntityType.BEE, new CemFactory(CemBeeRenderer.class));
		register(EntityType.CHICKEN, new CemFactory(CemChickenRenderer.class));
		register(EntityType.COW, new CemFactory(CemCowRenderer.class));
		register(EntityType.SHEEP, new CemFactory(CemSheepRenderer.class));
		register("sheep_wool");
		register(EntityType.PIG, new CemFactory(CemPigRenderer.class));
		register(BlockEntityType.BANNER, new CemFactory(CemBannerRenderer.class));
		register(EntityType.MOOSHROOM, new CemFactory(CemMooshroomRenderer.class));
		register(EntityType.ZOMBIE, new CemFactory(CemZombieRenderer.class));
		register(EntityType.HUSK, new CemFactory(CemHuskZombieRenderer.class));
		register(EntityType.DROWNED, new CemFactory(CemDrownedZombieRenderer.class));
		register("drowned_outer");
		register(EntityType.SKELETON, new CemFactory(CemSkeletonRenderer.class));
		register(EntityType.WITHER_SKELETON, new CemFactory(CemWitherSkeletonRenderer.class));
		register(EntityType.STRAY, new CemFactory(CemStraySkeletonRenderer.class));
		register("stray_outer");
		register(EntityType.PILLAGER, new CemFactory(CemPillagerRenderer.class));
		register(EntityType.VINDICATOR, new CemFactory(CemVindicatorRenderer.class));
		register(EntityType.EVOKER, new CemFactory(CemEvokerRenderer.class));
		register(EntityType.ILLUSIONER, new CemFactory(CemIllusionerRenderer.class));
		register(EntityType.FOX, new CemFactory(CemFoxRenderer.class));
	}
}