package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemCowModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MooshroomEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemMooshroomRenderer extends MooshroomEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new LinkedHashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private              CemModelRegistry          registry;
	
	static{
		partNames.put("leg1", "left_hind_leg");
		partNames.put("leg2", "right_hind_leg");
		partNames.put("leg3", "left_front_leg");
		partNames.put("leg4", "right_front_leg");
	}
	
	static{
		parentChildPairs.put("head", Arrays.asList("right_horn", "left_horn"));
	}
	
	public CemMooshroomRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(this.getType())){
			this.registry = CemRegistryManager.getRegistry(this.getType());
			try{
				this.registry.setChildren(parentChildPairs);
				this.model = new CemCowModel<>(this.registry.prepRootPart(partNames), registry);
				if(registry.hasShadowRadius()){
					this.shadowRadius = registry.getShadowRadius();
				}
			} catch(Exception e){
				modelError(e);
			}
		}
	}
	
	@Override
	public String getId(){
		return this.getType().toString();
	}
	
	private EntityType<? extends Entity> getType(){
		return EntityType.MOOSHROOM;
	}
	
	@Override
	public Identifier getTexture(MooshroomEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}