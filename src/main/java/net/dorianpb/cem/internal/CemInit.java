package net.dorianpb.cem.internal;

import net.dorianpb.cem.internal.config.CemConfigFairy;
import net.fabricmc.api.ClientModInitializer;


public class CemInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CemConfigFairy.loadConfig();
    }
}