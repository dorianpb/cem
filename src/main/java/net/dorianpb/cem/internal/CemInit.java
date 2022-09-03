package net.dorianpb.cem.internal;

import net.dorianpb.cem.internal.config.CemConfigFairy;
import net.fabricmc.api.ClientModInitializer;


public class CemInit implements ClientModInitializer{
	@Override
	public void onInitializeClient(){
		CemConfigFairy.loadConfig();
	}
}

//TODO write documentation for everything so people can adopt the mod and use it like a good boi