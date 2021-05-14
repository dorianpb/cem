package net.dorianpb.cem.mixins;

import net.dorianpb.cem.external.renderers.CemBannerRenderer;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRendererMixin{
	//ordinal = line num - 38
	
	@Shadow
	protected abstract <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, BlockEntityRenderer<E> blockEntityRenderer);
	
	@Redirect(method = "<init>",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;register(Lnet/minecraft/block/entity/BlockEntityType;" +
	                            "Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;)V",
	                   ordinal = 12))
	private void addBanner(BlockEntityRenderDispatcher blockEntityRenderDispatcher, BlockEntityType<BannerBlockEntity> blockEntityType,
	                       BlockEntityRenderer<BannerBlockEntity> blockEntityRenderer){
		register(BlockEntityType.BANNER, new CemBannerRenderer(blockEntityRenderDispatcher));
	}
}