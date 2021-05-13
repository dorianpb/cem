package net.dorianpb.cem.internal;

import com.google.gson.internal.LinkedTreeMap;
import net.dorianpb.cem.internal.cemStringParser.ParsedExpression;
import net.dorianpb.cem.internal.jemFile.jemModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**Contains all of the data for the CEM model*/
public class cemModelRegistry{
    private final HashMap<ArrayList<String>,cemModelEntry> database; //actual storage of cemModelEntries
    private final ArrayList<cemAnimation> animations; //actual storage of all the cemAnimations
    private final HashMap<String, cemModelEntry> partNameRefs; //used to refer to parts by their model names rather than id names
    private final jemFile file; //stores the jemFile
    private Model in; //stores reference to the Model
    
    
    public cemModelRegistry(LinkedTreeMap<String,Object> json, String path){
        this.database = new HashMap<>();
        this.animations = new ArrayList<>();
        this.partNameRefs = new HashMap<>();
        this.file = new jemFile(json,path);
    }
    
    /**Used to set the entity model's texture size and to internally construct the cemModel translated by specified amounts
     * @param in The entity model
     * @param pivotX Pivot the whole model in the X direction by this amount
     * @param pivotY Pivot the whole model in the Y direction by this amount
     * @param pivotZ Pivot the whole model in the Z direction by this amount
     */
    public void initModels(Model in, float pivotX, float pivotY, float pivotZ){
        this.in = in;
        if(in!=null){
            in.textureWidth = this.file.getTextureSize().get(0).intValue();
            in.textureHeight = this.file.getTextureSize().get(1).intValue();
        }
        //models
        for(String part : this.file.getModelList()){
            jemModel data = this.file.getModel(part);
            cemModelEntry entry;
            if(in!=null){
                entry = new cemModelEntry(data, in, pivotX, pivotY, pivotZ);
            } else {
                entry = new cemModelEntry(data, file);
            }
            this.addEntry(entry, new ArrayList<>());
        }
        //animations
        for(String part: this.file.getModelList()){
            jemModel data = this.file.getModel(part);
            for(String key : data.getAnimations().keySet()){
                try{
                    animations.add(new cemAnimation(
                                    this.findChild(key.substring(0, key.indexOf("."))),
                                    data.getAnimations().get(key), key.substring(key.indexOf(".") + 1),
                                    this
                            )
                    );
                }
                catch(Exception e){
                    cemFairy.getLogger().error(e.getMessage());
                }
            }
        }
    }
    
    /**Used to set the entity model's texture size and to internally construct the cemModel, with no translation, which is the default
     * @param in The entity model
     */
    public void initModels(Model in){
        this.initModels(in,0,0,0);
    }
    
    /**Sets the second part to be the child of the first during model creation, call after initmodels()!
     * MAKE SURE TO DO THIS TO THE YOUNGEST PARTS FIRST AND WORK YOUR WAY UP!
     * For example, if you have a parent, a child, and grandchild, you should call
     * setChild("child","grandchild"), then setChild("parent","child")! Will silently fail if both parts are not present.
     * @param parentPart Name of parent part.
     * @param childPart Name of child part.
     */
    public void setChild(String parentPart, String childPart){
        cemModelEntry parent = this.getEntryByPartName(parentPart);
        cemModelEntry child = this.getEntryByPartName(childPart);
        if(parent==null || child==null){
            return;
        }
        parent.getModel().addChild(child.getModel());
        child.getModel().pivotX = (parent.getModel().pivotX - child.getModel().pivotX) * (child.getInvertAxis()[0] ?-1 : 1);
        child.getModel().pivotY = (parent.getModel().pivotY - child.getModel().pivotY) * (child.getInvertAxis()[1] ?-1 : 1);
        child.getModel().pivotZ = (parent.getModel().pivotZ - child.getModel().pivotZ) * (child.getInvertAxis()[2] ?-1 : 1);
        
    }
    
    
    private void addEntry(cemModelEntry entry, ArrayList<String> parentRefmap){
        ArrayList<String> refmap;
        if(parentRefmap!=null && parentRefmap.size()>0){
            @SuppressWarnings("unchecked")
            ArrayList<String> temp = (ArrayList<String>) parentRefmap.clone();
            refmap = temp;
        } else{
            refmap = new ArrayList<>();
            if(entry.getPart()!=null){
                this.partNameRefs.put(entry.getPart(), entry);
            }
        }
        refmap.add((entry.getId() == null) ? entry.getPart() : entry.getId());
        this.database.put(refmap,entry);
        for(cemModelEntry child: entry.getChildren().values()){
            this.addEntry(child, refmap);
        }
    }
    
    /**Returns the ModelPart for the specified part
     * @param key Name of part as defined in the .jem file
     * @return The ModelPart of the specified part
     */
    public ModelPart getModel(String key){
        cemModelEntry entry = this.getEntryByPartName(key);
        return entry!=null?entry.getModel():new ModelPart(in);
    }
    
    private cemModelEntry getEntryByPartName(String key){
        if(this.partNameRefs.containsKey(key)){
            return this.partNameRefs.get(key);
        }
        cemFairy.getLogger().warn("Model part "+key+" isn't specified in "+this.file.getPath());
        return null;
    }
    
    /**Test if the user specified a special texture to use
     * @return If a texture is specified in the .jem file
     */
    public boolean hasTexture(){
        return this.file.getTexture()!=null;
    }
    
    /**Returns an Identifier for the texture specified in the .jem file
     * @return Identifier of the texture
     */
    public Identifier getTexture(){
        if(this.file.getTexture()==null){throw new NullPointerException("Trying to retrieve a null texture");}
        return new Identifier("dorianpb",this.file.getTexture());
    }
    
    public void applyAnimations(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, LivingEntity livingEntity){
        for(cemAnimation anim : this.animations){
            anim.apply(limbAngle,limbDistance,age,head_yaw,head_pitch,livingEntity);
        }
    }
    
    cemModelEntry findChild(String target, cemModelEntry parent){
        cemModelEntry victim = null;
        ArrayList<String> hit = null;
        ArrayList<String> refmap = new ArrayList<>(Arrays.asList(target.split(":")));
        if(refmap.size()==1 && this.partNameRefs.containsKey(refmap.get(0))){
            victim = this.partNameRefs.get(refmap.get(0));
            return victim;
        }
        else if(parent!=null && refmap.get(0).equals("this")){
            return parent;
        }
        else{
            for(ArrayList<String> part : this.database.keySet()){
                ArrayList<Integer> hello = new ArrayList<>();
                for(String ref : refmap){
                    hello.add(part.indexOf(ref));
                }
                boolean hi = hello.size() != 1 || hello.get(0) > -1;
                for(int i=0; i<hello.size()-1; i++){
                    hi = hi && hello.get(i)<hello.get(i+1) && hello.get(i)>-1;
                }
                if(hi && (hit==null||part.size()<hit.size()) ){
                    hit = part;
                }
                victim = this.database.get(hit);
            }
        }
        if(victim==null){
            throw new NullPointerException("Model part "+target+" isn't specified in "+this.file.getPath());
        }
        return victim;
    }
    private cemModelEntry findChild(String target){
        return this.findChild(target, null);
    }
    
    private static class cemAnimation{
        private final cemModelRegistry registry;
        private final cemModelEntry target;
        private final ParsedExpression expression;
        private final char operation;
        private final char axis;
        
        cemAnimation(cemModelEntry target, String expr, String var, cemModelRegistry registry){
            this.target = target;
            this.registry = registry;
            this.expression = cemStringParser.parse(expr,this.registry,this.target);
            this.operation = var.charAt(0);
            this.axis = var.charAt(1);
        }
        
        void apply(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, LivingEntity livingEntity){
            float val = this.expression.eval(limbAngle,limbDistance,age,head_yaw,head_pitch,livingEntity,this.registry);
            switch(operation){
                case 't':
                    this.target.setTranslate(this.axis, val);
                    break;
                case 'r':
                    this.target.getModel().setRotation(this.axis,val);
                    break;
                case 's':
                    target.getModel().setScale(this.axis,val);
                    break;
                default:
                    throw new IllegalStateException("Unknown operation \""+operation+"\"");
            }
        }
    }
}