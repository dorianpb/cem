package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemPigModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelEntry.CemModelPart;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemPigRenderer extends PigEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new LinkedHashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private              CemModelRegistry          registry;
	
	static{
		partNames.put("leg1", "left_hind_leg");
		partNames.put("leg2", "right_hind_leg");
		partNames.put("leg3", "left_front_leg");
		partNames.put("leg4", "right_front_leg");
	}
	
	public CemPigRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(EntityType.PIG)){
			this.registry = CemRegistryManager.getRegistry(EntityType.PIG);
			try{
				this.registry.setChildren(parentChildPairs);
				CemModelPart rootPart = this.registry.prepRootPart(partNames);
				this.model = new CemPigModel(rootPart, registry);
				if(registry.hasShadowRadius()){
					this.shadowRadius = registry.getShadowRadius();
				}
				CemModelRegistry saddleRegistry = CemRegistryManager.getRegistry(EntityType.PIG);
				saddleRegistry.setChildren(parentChildPairs);
				CemModelPart saddlePart = saddleRegistry.prepRootPart(partNames, 0.5F);
				this.features.set(0, new SaddleFeatureRenderer<>(this, new CemPigModel(saddlePart, saddleRegistry), new Identifier("textures/entity/pig/pig_saddle.png")));
			} catch(Exception e){
				modelError(e);
			}
		}
	}
	
	@Override
	public String getId(){
		return EntityType.PIG.toString();
	}
	
	@Override
	public Identifier getTexture(PigEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}