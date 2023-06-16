package net.dorianpb.cem.internal.config;

import net.dorianpb.cem.internal.util.CemFairy;

public enum CemConfigFairy {
    ;
    private static CemOptions config;

    @SuppressWarnings("ErrorNotRethrown")
    public static void loadConfig() {
        try {
            config = CemConfig.getConfig();
        } catch(NoClassDefFoundError e) {
            CemFairy.getLogger().warn("Unable to set up config due to missing dependencies; using defaults!");
            CemFairy.getLogger().warn("Missing class: {}", e.getMessage());
            config = CemOptions.INSTANCE;
        }
    }

    public static CemOptions getConfig() {
        return config;
    }
}