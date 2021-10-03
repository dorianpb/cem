package net.dorianpb.cem.internal.api;

import net.dorianpb.cem.internal.util.CemFairy;

public interface CemRenderer{
	
	/**
	 * Used to return errors if applying the .jem file doesn't work
	 * No need to overwrite this
	 * @param e Exception to log
	 */
	@SuppressWarnings("unused")
	default void modelError(Exception e){
		CemFairy.getLogger().error("Error applying model for " + getId());
		CemFairy.getLogger().error(e);
		if(e.getMessage() == null || e.getMessage().trim().equals("")){
			CemFairy.getLogger().error(e.getStackTrace()[0]);
			CemFairy.getLogger().error(e.getStackTrace()[1]);
			CemFairy.getLogger().error(e.getStackTrace()[2]);
		}
	}
	
	/**
	 * Returns string id of the entity represented by this cemRenderer
	 * @return Name of entity
	 */
	String getId();
}