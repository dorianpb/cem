package net.dorianpb.cem.mixins;


import com.google.common.collect.ImmutableMap.Builder;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemFairy;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Field;
import java.util.function.Function;

@Mixin(EntityRenderers.class)
public abstract class EntityRenderersMixin{
	
	@SuppressWarnings("unchecked")
	@Redirect(method = "method_32174",
	          at = @At(value = "INVOKE",
	                   target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)" +
	                            "Lcom/google/common/collect/ImmutableMap$Builder;"))
	private static <K extends EntityType<?>, V extends EntityRenderer<?>> Builder<K, V> cem$prepareEntityRenderer(Builder<K, V> instance, Object key, Object value){
		if(CemRegistryManager.hasEntity((EntityType<? extends Entity>) key)){
			CemModelRegistry registry = CemRegistryManager.getRegistry((EntityType<? extends Entity>) key);
			
			if(registry.hasShadowRadius()){
				((EntityRendererAccessor) value).setShadowRadius(registry.getShadowRadius());
			}
			if(registry.hasTexture()){
				try{
					Field modelField = value.getClass().getField("model");
					if(!Model.class.isAssignableFrom(modelField.getType())){
						throw new ClassCastException("Renderer " + value.getClass().getSimpleName() + " doesn't have a model that extends Model, can't assign texture!");
					}
					else if(!modelField.canAccess(value)){
						throw new IllegalAccessException("Can't access the \"model\" field of " + value.getClass().getSimpleName() + ", can't assign texture!");
					}
					else{
						Model model = (Model) modelField.get(value);
						Function<Identifier, RenderLayer> function = ((ModelAccessor) model).getLayerFactory();
						Identifier texture = registry.getTexture();
						((ModelAccessor) model).setLayerFactory(identifier -> function.apply(texture));
					}
				} catch(Exception e){
					CemFairy.getLogger().error(e.getMessage());
				}
			}
		}
		return instance.put((K) key, (V) value);
	}
}