package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemGuardianModel;
import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.ElderGuardianEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class CemElderGuardianRenderer extends ElderGuardianEntityRenderer implements CemRenderer{
	private static final Map<String, String>         partNames           = new HashMap<>();
	private static final Map<String, List<String>>   parentChildPairs    = new LinkedHashMap<>();
	private static final Map<String, ModelTransform> modelTransformFixes = new HashMap<>();
	private final        CemModelRegistry            registry;
	
	static{
		partNames.put("body", "head");
		for(int i = 0; i < 12; i++){
			partNames.put("spine" + (i + 1), "spike" + i);
		}
		for(int i = 0; i < 3; i++){
			partNames.put("tail" + (i + 1), "tail" + i);
		}
	}
	
	static{
		parentChildPairs.put("tail2", Collections.singletonList("tail3"));
		parentChildPairs.put("tail1", Collections.singletonList("tail2"));
		parentChildPairs.put("body",
		                     Arrays.asList("spine1",
		                                   "spine2",
		                                   "spine3",
		                                   "spine4",
		                                   "spine5",
		                                   "spine6",
		                                   "spine7",
		                                   "spine8",
		                                   "spine9",
		                                   "spine10",
		                                   "spine11",
		                                   "spine12",
		                                   "eye",
		                                   "tail1"
		                                  )
		                    );
	}
	
	static{
		modelTransformFixes.put("spine1", ModelTransform.pivot(0.0F, 11.5F, 7.0F));
		modelTransformFixes.put("spine2", ModelTransform.pivot(0.0F, 11.5F, -7.0F));
		modelTransformFixes.put("spine3", ModelTransform.pivot(7.0F, 11.5F, 0.0F));
		modelTransformFixes.put("spine4", ModelTransform.pivot(-7.0F, 11.5F, 0.0F));
		modelTransformFixes.put("spine5", ModelTransform.pivot(-7.0F, 18.5F, -7.0F));
		modelTransformFixes.put("spine6", ModelTransform.pivot(7.0F, 18.5F, -7.0F));
		modelTransformFixes.put("spine7", ModelTransform.pivot(7.0F, 18.5F, 7.0F));
		modelTransformFixes.put("spine8", ModelTransform.pivot(-7.0F, 18.5F, 7.0F));
		modelTransformFixes.put("spine9", ModelTransform.pivot(0.0F, 25.5F, 7.0F));
		modelTransformFixes.put("spine10", ModelTransform.pivot(0.0F, 25.5F, -7.0F));
		modelTransformFixes.put("spine11", ModelTransform.pivot(7.0F, 25.5F, 0.0F));
		modelTransformFixes.put("spine12", ModelTransform.pivot(-7.0F, 25.5F, 0.0F));
		
	}
	
	public CemElderGuardianRenderer(EntityRendererFactory.Context context){
		super(context);
		this.registry = CemRegistryManager.getRegistry(getType());
		try{
			this.model = new CemGuardianModel(this.registry.prepRootPart(partNames, parentChildPairs, this.model.getPart(), null, modelTransformFixes), registry);
			if(registry.hasShadowRadius()){
				this.shadowRadius = registry.getShadowRadius();
			}
		} catch(Exception e){
			modelError(e);
		}
	}
	
	private static EntityType<? extends Entity> getType(){
		return EntityType.ELDER_GUARDIAN;
	}
	
	@Override
	public String getId(){
		return getType().toString();
	}
	
	@Override
	public Identifier getTexture(GuardianEntity entity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(entity);
	}
}