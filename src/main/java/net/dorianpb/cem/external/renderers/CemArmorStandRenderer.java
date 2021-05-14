package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.external.models.CemArmorStandModel;
import net.dorianpb.cem.internal.CemFairy;
import net.dorianpb.cem.internal.CemModelRegistry;
import net.dorianpb.cem.internal.CemRenderer;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;

public class CemArmorStandRenderer extends ArmorStandEntityRenderer implements CemRenderer{
	private final ArmorStandArmorEntityModel vanilla;
	private final String id;
	private CemModelRegistry registry;
	
	public CemArmorStandRenderer(EntityRenderDispatcher dispatcher){
		super(dispatcher);
		this.id = "armor_stand";
		CemFairy.addRenderer(this, id);
		this.vanilla = this.model;
	}
	
	@Override
	public void apply(CemModelRegistry registry){
		this.registry = registry;
		try{
			this.model = new CemArmorStandModel(0.0F, registry);
		} catch(Exception e){
			modelError(e);
		}
	}
	
	@Override
	public String getId(){
		return this.id;
	}
	
	@Override
	public void restoreModel(){
		this.model = this.vanilla;
		this.registry = null;
	}
	
	@Override
	public Identifier getTexture(ArmorStandEntity armorStandEntity){
		if(this.registry != null && this.registry.hasTexture()){
			return this.registry.getTexture();
		}
		return super.getTexture(armorStandEntity);
	}
}