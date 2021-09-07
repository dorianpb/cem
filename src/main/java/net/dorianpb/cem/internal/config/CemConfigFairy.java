package net.dorianpb.cem.internal.config;

import net.dorianpb.cem.internal.util.CemFairy;
import net.fabricmc.loader.api.FabricLoader;

public class CemConfigFairy{
	private static CemOptions config;
	
	public static void loadConfig(){
		if(FabricLoader.getInstance().isModLoaded("completeconfig")){
			config = CemConfig.getConfig();
			CemConfig.createScreen();
		}
		else{
			CemFairy.getLogger().warn("Unable to set up config due to missing dependencies; using defaults!");
			config = CemOptions.instance;
		}
	}
	
	public static CemOptions getConfig(){
		return config;
	}
}