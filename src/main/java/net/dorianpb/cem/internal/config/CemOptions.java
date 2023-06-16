package net.dorianpb.cem.internal.config;

@SuppressWarnings("unused")
public interface CemOptions {
    CemOptions INSTANCE = new CemOptions() {
    };

    default boolean useOptifineFolder() {
        return false;
    }

    default boolean useTransparentParts() {
        return true;
    }

    default boolean useOldAnimations() {
        return false;
    }
}