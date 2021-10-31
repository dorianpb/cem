package net.dorianpb.cem.external.renderers;

import net.dorianpb.cem.internal.api.CemRenderer;
import net.dorianpb.cem.internal.models.CemModelEntry.CemModelPart;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.models.CemModelRegistry.CemPrepRootPartParamsBuilder;
import net.dorianpb.cem.internal.util.CemRegistryManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CemBannerRenderer extends BannerBlockEntityRenderer implements CemRenderer{
	private static final Map<String, String>       partNames  = new HashMap<>();
	private static final Map<String, List<String>> familyTree = new LinkedHashMap<>();
	
	static{
		partNames.put("slate", "flag");
		partNames.put("stand", "pole"); //jojo reference?
		partNames.put("top", "bar");
	}
	
	public CemBannerRenderer(BlockEntityRendererFactory.Context context){
		super(context);
		CemModelRegistry registry = CemRegistryManager.getRegistry(this.getType());
		try{
			CemModelPart root = registry.prepRootPart((new CemPrepRootPartParamsBuilder()).setPartNameMap(partNames)
			                                                                              .setFamilyTree(familyTree)
			                                                                              .setVanillaReferenceModelFactory(() -> getTexturedModelData().createModel())
			                                                                              .create());
			this.banner = root.getChild("flag");
			this.pillar = root.getChild("pole"); //haha demon slayer reference lol
			this.crossbar = root.getChild("bar");
			this.pillar.pivotY -= 12;
			this.crossbar.pivotY -= 12;
		} catch(Exception e){
			modelError(e);
		}
	}
	
	private BlockEntityType<? extends BlockEntity> getType(){
		return BlockEntityType.BANNER;
	}
	
	@Override
	public String getId(){
		return this.getType().toString();
	}
}