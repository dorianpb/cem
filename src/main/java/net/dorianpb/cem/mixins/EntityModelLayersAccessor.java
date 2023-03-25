package net.dorianpb.cem.mixins;

import net.minecraft.client.render.entity.model.EntityModelLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityModelLayers.class)
public interface EntityModelLayersAccessor {
    @Accessor
    static String getMAIN() {
        throw new UnsupportedOperationException();
    }
}