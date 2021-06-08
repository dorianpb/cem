package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemEndermanModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.render.entity.EndermanEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemEndermanRenderer extends EndermanEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames        = new LinkedHashMap<>();
	private static final Map<String, List<String>> parentChildPairs = new LinkedHashMap<>();
	private              CemModelRegistry          registry;
	
	static{
		partNames.put("headwear", "hat");
	}
	
	public CemEndermanRenderer(EntityRendererFactory.Context context){
		super(context);
		if(CemRegistryManager.hasEntity(EntityType.ENDERMAN)){
			this.registry = CemRegistryManager.getRegistry(EntityType.ENDERMAN);
			try{
				this.registry.setChildren(parentChildPairs);
				this.model = new CemEndermanModel(this.registry.prepRootPart(partNames), registry);
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
		return EntityType.ENDERMAN.toString();
	}
	
	@Override
	public Identifier getTexture(EndermanEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}