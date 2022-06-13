package net.dorianpb.cem.internal.config;

import net.dorianpb.cem.internal.util.CemFairy;
import net.fabricmc.loader.api.FabricLoader;

public enum CemConfigFairy{
	;
	private static CemOptions config;
	
	public static void loadConfig(){
		if(FabricLoader.getInstance().isModLoaded("completeconfig-base")){
			config = CemConfig.getConfig();
			
			if(FabricLoader.getInstance().isModLoaded("completeconfig-gui-cloth")){
				CemConfig.createScreen();
			}
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