package net.dorianpb.cem.mixins;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(BlockEntityRendererFactories.class)
@SuppressWarnings("unused")
public interface BlockEntityRendererAccessor{
	@Invoker
	static void callRegister(BlockEntityType<? extends BlockEntity> type, BlockEntityRendererFactory<? extends BlockEntity> factory){
		throw new UnsupportedOperationException();
	}
	
	@Accessor
	static Map<BlockEntityType<?>, BlockEntityRendererFactory<?>> getFACTORIES(){
		throw new UnsupportedOperationException();
	}
}