package net.dorianpb.cem.mixins;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class blockEntityRendererMixin{
    //ordinal = line num - 38
    
    @Shadow
    protected abstract <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, BlockEntityRenderer<E> blockEntityRenderer);
}