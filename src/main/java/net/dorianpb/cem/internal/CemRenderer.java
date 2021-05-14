package net.dorianpb.cem.internal;

public interface CemRenderer{
	
	/**
	 * Tells renderer to activate CEM with this registry, pass it on to cemModel
	 * @param registry Registry to apply to the cemRenderer
	 */
	void apply(CemModelRegistry registry);
	
	/**
	 * Returns string id of the entity represented by this cemRenderer
	 * @return Name of entity
	 */
	String getId();
	
	/** Restore the original model */
	void restoreModel();
	
	/**
	 * Used to return errors if applying the .jem file doesn't work
	 * No need to overwrite this
	 * @param e Exception to log
	 */
	@SuppressWarnings("unused")
	default void modelError(Exception e){
		CemFairy.getLogger().error("Error applying model for " + getId());
		if(e.getMessage() == null || e.getMessage().trim().equals("")){
			CemFairy.getLogger().error(e.getStackTrace()[0]);
			CemFairy.getLogger().error(e.getStackTrace()[1]);
			CemFairy.getLogger().error(e.getStackTrace()[2]);
		}
		else{
			CemFairy.getLogger().error(e.getMessage());
		}
		restoreModel();
	}
}