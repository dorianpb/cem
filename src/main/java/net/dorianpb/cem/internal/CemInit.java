package net.dorianpb.cem.internal;

import net.dorianpb.cem.internal.api.CemEntityInitializer;
import net.dorianpb.cem.internal.util.CemFairy;
import net.dorianpb.cem.mixins.BlockEntityRendererAccessor;
import net.dorianpb.cem.mixins.EntityRendererAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;


public class CemInit implements ClientModInitializer{
	@Override
	@SuppressWarnings({"unchecked", "RedundantSuppression"})
	public void onInitializeClient(){
		FabricLoader.getInstance().getEntrypointContainers("cem", CemEntityInitializer.class).forEach((container) -> {
			ModMetadata provider = container.getProvider().getMetadata();
			CemEntityInitializer entrypoint = container.getEntrypoint();
			
			entrypoint.onInit();
			CemFairy.getLogger().info("Loading " + entrypoint.getSize() + " entities from " + provider.getName() + " " + provider.getVersion());
			entrypoint.getCemEntityFactories().forEach((type, factory) -> {
				CemFairy.addSupport(type);
				EntityRendererAccessor.callRegister(type, factory::create);
			});
			entrypoint.getCemBlockEntityFactories().forEach((type, factory) -> {
				CemFairy.addSupport(type);
				BlockEntityRendererAccessor.callRegister(type, factory::create);
			});
			entrypoint.getCemOthers().forEach(CemFairy::addSupport);
		});
	}
}

//TODO write documentation for everything so people can adopt the mod and use it like a good boi