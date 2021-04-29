package net.dorianpb.cem.internal;

public interface cemRenderer {
    
    /**Restore the original model*/
    void restoreModel();
    /**Tells renderer to activate CEM with this registry, pass it on to cemModel
     * @param registry Registry to apply to the cemRenderer
     * */
    void apply(cemModelRegistry registry);
    
    /**Returns string id of the entity represented by this cemRenderer
     * @return Name of entity
     * */
    String getId();
    
    /**Used to return errors if applying the .jem file doesn't work
     * No need to overwrite this
     * @param e Exception to log
     * */
    @SuppressWarnings("unused")
    default void modelError(Exception e){
        cemFairy.getLogger().error("Error applying model for "+getId());
        if(e.getMessage() == null || e.getMessage().trim().equals("")) {
            cemFairy.getLogger().error(e.getStackTrace()[0]);
            cemFairy.getLogger().error(e.getStackTrace()[1]);
            cemFairy.getLogger().error(e.getStackTrace()[2]);
        }
        else {
            cemFairy.getLogger().error(e.getMessage());
        }
        restoreModel();
    }
}

