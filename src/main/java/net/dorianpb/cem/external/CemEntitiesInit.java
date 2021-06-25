package net.dorianpb.cem.external;

import net.dorianpb.cem.external.renderers.*;
import net.dorianpb.cem.internal.api.CemEntityInitializer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;

public class CemEntitiesInit extends CemEntityInitializer{
	@Override
	public void onInit(){
		register(EntityType.PIGLIN, CemPiglinRenderer.class, EntityType.PIGLIN);
		register(EntityType.PIGLIN_BRUTE, CemPiglinRenderer.class, EntityType.PIGLIN_BRUTE);
		register(EntityType.ZOMBIFIED_PIGLIN, CemPiglinRenderer.class, EntityType.ZOMBIFIED_PIGLIN);
		register(EntityType.ENDERMAN, CemEndermanRenderer.class);
		register(EntityType.CREEPER, CemCreeperRenderer.class);
		register("creeper_charge");
		register(EntityType.CAT, CemCatRenderer.class);
		register(EntityType.BLAZE, CemBlazeRenderer.class);
		register(EntityType.ARMOR_STAND, CemArmorStandRenderer.class);
		register(EntityType.OCELOT, CemOcelotRenderer.class);
		register(EntityType.BAT, CemBatRenderer.class);
		register(EntityType.BEE, CemBeeRenderer.class);
		register(EntityType.CHICKEN, CemChickenRenderer.class);
		register(EntityType.COW, CemCowRenderer.class);
		register(EntityType.SHEEP, CemSheepRenderer.class);
		register("sheep_wool");
		register(EntityType.PIG, CemPigRenderer.class);
		register(BlockEntityType.BANNER, CemBannerRenderer.class);
		register(EntityType.MOOSHROOM, CemMooshroomRenderer.class);
		register(EntityType.ZOMBIE, CemZombieRenderer.class);
		register(EntityType.HUSK, CemHuskZombieRenderer.class);
		register(EntityType.DROWNED, CemDrownedZombieRenderer.class);
		register("drowned_outer");
		register(EntityType.SKELETON, CemSkeletonRenderer.class);
		register(EntityType.WITHER_SKELETON, CemWitherSkeletonRenderer.class);
		register(EntityType.STRAY, CemStraySkeletonRenderer.class);
		register("stray_outer");
		register(EntityType.PILLAGER, CemPillagerRenderer.class);
		register(EntityType.VINDICATOR, CemVindicatorRenderer.class);
		register(EntityType.EVOKER, CemEvokerRenderer.class);
		register(EntityType.ILLUSIONER, CemIllusionerRenderer.class);
		register(EntityType.FOX, CemFoxRenderer.class);
		register(EntityType.SLIME, CemSlimeRenderer.class);
		register("slime_gel");
		register(EntityType.MAGMA_CUBE, CemMagmaCubeRenderer.class);
		register(EntityType.SPIDER, CemSpiderRenderer.class);
		register(EntityType.CAVE_SPIDER, CemCaveSpiderRenderer.class);
	}
}