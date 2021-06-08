package net.dorianpb.cem.internal;

import net.dorianpb.cem.external.renderers.*;
import net.dorianpb.cem.internal.util.CemFairy;
import net.dorianpb.cem.mixins.BlockEntityRendererAccessor;
import net.dorianpb.cem.mixins.EntityRendererAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;


public class CemInit implements ClientModInitializer{
	@Override
	public void onInitializeClient(){
		register(EntityType.PIGLIN, (context) -> new CemPiglinRenderer(context, EntityType.PIGLIN));
		register(EntityType.PIGLIN_BRUTE, (context) -> new CemPiglinRenderer(context, EntityType.PIGLIN_BRUTE));
		register(EntityType.ZOMBIFIED_PIGLIN, (context) -> new CemPiglinRenderer(context, EntityType.ZOMBIFIED_PIGLIN));
		register(EntityType.ENDERMAN, CemEndermanRenderer::new);
		register(EntityType.CREEPER, CemCreeperRenderer::new);
		register(EntityType.CAT, CemCatRenderer::new);
		register(EntityType.BLAZE, CemBlazeRenderer::new);
		register(EntityType.ARMOR_STAND, CemArmorStandRenderer::new);
		register(EntityType.OCELOT, CemOcelotRenderer::new);
		register(EntityType.BAT, CemBatRenderer::new);
		register(EntityType.BEE, CemBeeRenderer::new);
		register(EntityType.CHICKEN, CemChickenRenderer::new);
		register(EntityType.COW, CemCowRenderer::new);
		register(EntityType.SHEEP, CemSheepRenderer::new);
		CemFairy.addSupport("sheep_wool");
		register(EntityType.PIG, CemPigRenderer::new);
		register(BlockEntityType.BANNER, CemBannerRenderer::new);
	}
	
	public <T extends Entity> void register(EntityType<? extends T> type, EntityRendererFactory<T> factory){
		CemFairy.addSupport(type);
		EntityRendererAccessor.callRegister(type, factory);
	}
	
	public <T extends BlockEntity> void register(BlockEntityType<? extends T> type, BlockEntityRendererFactory<T> factory){
		CemFairy.addSupport(type);
		BlockEntityRendererAccessor.callRegister(type, factory);
	}
}


//TODO write documentation for everything so people can adopt the mod and use it like a good boi