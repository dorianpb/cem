package net.dorianpb.cem.mixins;

import net.dorianpb.cem.external.renderers.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderDispatcher.class)
//ordinal = line num - 76
public abstract class EntityRendererMixin{
	@Shadow
	protected abstract <T extends Entity> void register(EntityType<T> entityType, EntityRenderer<? super T> entityRenderer);
	
	@Redirect(method = "registerRenderers",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;register(Lnet/minecraft/entity/EntityType;" +
	                            "Lnet/minecraft/client/render/entity/EntityRenderer;)V",
	                   ordinal = 62))
	private void addPiglin(EntityRenderDispatcher entityRenderDispatcher, EntityType<Entity> entityType, EntityRenderer<PiglinEntity> entityRenderer){
		register(EntityType.PIGLIN, new CemPiglinRenderer(entityRenderDispatcher, "piglin"));
	}
	
	@Redirect(method = "registerRenderers",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;register(Lnet/minecraft/entity/EntityType;" +
	                            "Lnet/minecraft/client/render/entity/EntityRenderer;)V",
	                   ordinal = 63))
	private void addPiglinBrute(EntityRenderDispatcher entityRenderDispatcher, EntityType<Entity> entityType, EntityRenderer<PiglinBruteEntity> entityRenderer){
		register(EntityType.PIGLIN_BRUTE, new CemPiglinRenderer(entityRenderDispatcher, "piglin_brute"));
	}
	
	@Redirect(method = "registerRenderers",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;register(Lnet/minecraft/entity/EntityType;" +
	                            "Lnet/minecraft/client/render/entity/EntityRenderer;)V",
	                   ordinal = 104))
	private void addZombiePiglin(EntityRenderDispatcher entityRenderDispatcher, EntityType<Entity> entityType, EntityRenderer<ZombifiedPiglinEntity> entityRenderer){
		register(EntityType.ZOMBIFIED_PIGLIN, new CemPiglinRenderer(entityRenderDispatcher, "zombified_piglin"));
	}
	
	@Redirect(method = "registerRenderers",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;register(Lnet/minecraft/entity/EntityType;" +
	                            "Lnet/minecraft/client/render/entity/EntityRenderer;)V",
	                   ordinal = 23))
	private void addEnderman(EntityRenderDispatcher entityRenderDispatcher, EntityType<Entity> entityType, EntityRenderer<EndermanEntity> entityRenderer){
		register(EntityType.ENDERMAN, new CemEndermanRenderer(entityRenderDispatcher));
	}
	
	@Redirect(method = "registerRenderers",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;register(Lnet/minecraft/entity/EntityType;" +
	                            "Lnet/minecraft/client/render/entity/EntityRenderer;)V",
	                   ordinal = 14))
	private void addCreeper(EntityRenderDispatcher entityRenderDispatcher, EntityType<Entity> entityType, EntityRenderer<CreeperEntity> entityRenderer){
		register(EntityType.CREEPER, new CemCreeperRenderer(entityRenderDispatcher));
	}
	
	@Redirect(method = "registerRenderers",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;register(Lnet/minecraft/entity/EntityType;" +
	                            "Lnet/minecraft/client/render/entity/EntityRenderer;)V",
	                   ordinal = 7))
	private void addCat(EntityRenderDispatcher entityRenderDispatcher, EntityType<Entity> entityType, EntityRenderer<CatEntity> entityRenderer){
		register(EntityType.CAT, new CemCatRenderer(entityRenderDispatcher));
	}
	
	@Redirect(method = "registerRenderers",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;register(Lnet/minecraft/entity/EntityType;" +
	                            "Lnet/minecraft/client/render/entity/EntityRenderer;)V",
	                   ordinal = 5))
	private void addBlaze(EntityRenderDispatcher entityRenderDispatcher, EntityType<Entity> entityType, EntityRenderer<BlazeEntity> entityRenderer){
		register(EntityType.BLAZE, new CemBlazeRenderer(entityRenderDispatcher));
	}
	
	@Redirect(method = "registerRenderers",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;register(Lnet/minecraft/entity/EntityType;" +
	                            "Lnet/minecraft/client/render/entity/EntityRenderer;)V",
	                   ordinal = 1))
	private void addArmorStand(EntityRenderDispatcher entityRenderDispatcher, EntityType<Entity> entityType, EntityRenderer<ArmorStandEntity> entityRenderer){
		register(EntityType.ARMOR_STAND, new CemArmorStandRenderer(entityRenderDispatcher));
	}
	
	@Redirect(method = "registerRenderers",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;register(Lnet/minecraft/entity/EntityType;" +
	                            "Lnet/minecraft/client/render/entity/EntityRenderer;)V",
	                   ordinal = 56))
	private void addOcelot(EntityRenderDispatcher entityRenderDispatcher, EntityType<Entity> entityType, EntityRenderer<OcelotEntity> entityRenderer){
		register(EntityType.OCELOT, new CemOcelotRenderer(entityRenderDispatcher));
	}
	
	@Redirect(method = "registerRenderers",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;register(Lnet/minecraft/entity/EntityType;" +
	                            "Lnet/minecraft/client/render/entity/EntityRenderer;)V",
	                   ordinal = 3))
	private void addBat(EntityRenderDispatcher entityRenderDispatcher, EntityType<Entity> entityType, EntityRenderer<BatEntity> entityRenderer){
		register(EntityType.BAT, new CemBatRenderer(entityRenderDispatcher));
	}
	
	@Redirect(method = "registerRenderers",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;register(Lnet/minecraft/entity/EntityType;" +
	                            "Lnet/minecraft/client/render/entity/EntityRenderer;)V",
	                   ordinal = 4))
	private void addBee(EntityRenderDispatcher entityRenderDispatcher, EntityType<Entity> entityType, EntityRenderer<BeeEntity> entityRenderer){
		register(EntityType.BEE, new CemBeeRenderer(entityRenderDispatcher));
	}
	
	@Redirect(method = "registerRenderers",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;register(Lnet/minecraft/entity/EntityType;" +
	                            "Lnet/minecraft/client/render/entity/EntityRenderer;)V",
	                   ordinal = 10))
	private void addChicken(EntityRenderDispatcher entityRenderDispatcher, EntityType<Entity> entityType, EntityRenderer<ChickenEntity> entityRenderer){
		register(EntityType.CHICKEN, new CemChickenRenderer(entityRenderDispatcher));
	}
	
	@Redirect(method = "registerRenderers",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;register(Lnet/minecraft/entity/EntityType;" +
	                            "Lnet/minecraft/client/render/entity/EntityRenderer;)V",
	                   ordinal = 13))
	private void addCow(EntityRenderDispatcher entityRenderDispatcher, EntityType<Entity> entityType, EntityRenderer<CowEntity> entityRenderer){
		register(EntityType.COW, new CemCowRenderer(entityRenderDispatcher));
	}
}