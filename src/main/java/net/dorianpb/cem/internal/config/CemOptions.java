package net.dorianpb.cem.internal.config;

@SuppressWarnings("unused")
public interface CemOptions{
	
	default boolean useOptifineFolder(){
		return false;
	}
	
	default boolean useTransparentParts(){
		return true;
	}
}