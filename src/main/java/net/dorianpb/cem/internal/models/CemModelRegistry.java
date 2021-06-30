package net.dorianpb.cem.internal.models;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.dorianpb.cem.internal.CemStringParser;
import net.dorianpb.cem.internal.CemStringParser.ParsedExpression;
import net.dorianpb.cem.internal.file.JemFile;
import net.dorianpb.cem.internal.file.JemFile.JemModel;
import net.dorianpb.cem.internal.models.CemModelEntry.CemModelPart;
import net.dorianpb.cem.internal.models.CemModelEntry.TransparentCemModelPart;
import net.dorianpb.cem.internal.util.CemFairy;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/** Contains all of the data for the CEM model */
public class CemModelRegistry{
	private final HashMap<ArrayList<String>, CemModelEntry> database; //actual storage of cemModelEntries
	private final ArrayList<CemAnimation>                   animations; //actual storage of all the cemAnimations
	private final HashMap<String, CemModelEntry>            partNameRefs; //used to refer to parts by their model names rather than id names
	private final JemFile                                   file; //stores the jemFile
	private final Map<String, List<String>>                 familyTree; //stores parent-child relationships
	private final BiMap<String, String>                     partNameMap; //stores translation from optifine to vanilla part names
	
	
	public CemModelRegistry(JemFile file){
		this.database = new HashMap<>();
		this.animations = new ArrayList<>();
		this.partNameRefs = new HashMap<>();
		this.file = file;
		this.familyTree = new LinkedHashMap<>();
		this.partNameMap = HashBiMap.create();
		//models
		for(String part : this.file.getModelList()){
			JemModel data = this.file.getModel(part);
			this.addEntry(new CemModelEntry(data, file.getTextureSize().get(0).intValue(), file.getTextureSize().get(1).intValue()), new ArrayList<>());
		}
		//animations
		for(String part : this.file.getModelList()){
			JemModel data = this.file.getModel(part);
			for(String key : data.getAnimations().keySet()){
				try{
					animations.add(new CemAnimation(this.findChild(key.substring(0, key.indexOf(".")), this.findChild(part)),
					                                data.getAnimations().get(key),
					                                key.substring(key.indexOf(".") + 1), this
					));
				} catch(Exception e){
					CemFairy.getLogger().error("Error applying animation:");
					CemFairy.getLogger().error(e.getMessage());
				}
			}
		}
	}
	
	public CemModelPart prepRootPart(Map<String, String> partNameMap, ModelPart vanillaModel){
		return this.prepRootPart(partNameMap, vanillaModel, null);
	}
	
	public CemModelPart prepRootPart(Map<String, String> partNameMap, ModelPart vanillaModel, @Nullable Float inflate){
		CemModelPart newRoot = new CemModelPart();
		this.partNameMap.clear();
		this.partNameMap.putAll(partNameMap);
		//populate it first
		for(String partName : this.partNameRefs.keySet()){
			this.getParent(newRoot, partNameMap.getOrDefault(partName, partName));
		}
		CemModelPart part = this.prepRootPart(newRoot, partNameMap);
		if(inflate != null){
			part.inflate(inflate);
		}
		for(String key : part.children.keySet()){
			try{
				TransparentCemModelPart replacement = new TransparentCemModelPart(part.getChild(key), vanillaModel.getChild(key).getTransform());
				part.addChild(key, replacement);
			} catch(Exception ignored){
			}
		}
		return part;
	}
	
	private CemModelPart getParent(ModelPart root, String name){
		ArrayList<String> names = new ArrayList<>();
		while(true){
			name = this.findParent(name);
			if(name != null){
				names.add(name);
			}
			else{
				break;
			}
		}
		if(names.size() == 0){
			return (CemModelPart) root;
		}
		else{
			ModelPart part = root;
			for(int i = names.size() - 1; i >= 0; i--){
				part = part.getChild(partNameMap.getOrDefault(names.get(i), names.get(i)));
			}
			return (CemModelPart) part;
		}
		
	}
	
	private CemModelPart prepRootPart(ModelPart root, Map<String, String> partNameMap){
		CemModelPart newRoot = CemModelPart.of(root);
		for(String partName : this.partNameRefs.keySet()){
			this.getParent(newRoot, partName).addChild(partNameMap.getOrDefault(partName, partName), Objects.requireNonNull(this.getEntryByPartName(partName)).getModel());
		}
		return newRoot;
	}
	
	private String findParent(String name){
		for(String key : this.familyTree.keySet()){
			if(this.familyTree.get(key).contains(name)){
				return key;
			}
		}
		return null;
	}
	
	public CemModelEntry getEntryByPartName(String key){
		if(this.partNameRefs.containsKey(key)){
			return this.partNameRefs.get(key);
		}
		CemFairy.getLogger().warn("Model part " + key + " isn't specified in " + this.file.getPath());
		return null;
	}
	
	public void setChildren(Map<String, List<String>> childMap){
		this.familyTree.clear();
		this.familyTree.putAll(childMap);
		for(String parent : this.familyTree.keySet()){
			for(String child : this.familyTree.get(parent)){
				this.setChild(parent, child);
			}
		}
	}
	
	/**
	 * Sets the second part to be the child of the first during model creation.
	 * MAKE SURE TO DO THIS TO THE YOUNGEST PARTS FIRST AND WORK YOUR WAY UP!
	 * For example, if you have a parent, a child, and grandchild, you should call
	 * setChild("child","grandchild"), then setChild("parent","child")! Will silently fail if both parts are not
	 * present.
	 * @param parentPart Name of parent part.
	 * @param childPart  Name of child part.
	 */
	private void setChild(String parentPart, String childPart){
		CemModelEntry parent = this.getEntryByPartName(parentPart);
		CemModelEntry child = this.getEntryByPartName(childPart);
		if(parent == null || child == null){
			return;
		}
		setChild(parent.getModel(), child.getModel(), child.getPart());
	}
	
	public static void setChild(CemModelPart parentPart, ModelPart childPart, String name){
		parentPart.addChild(name, childPart);
		childPart.pivotX = (parentPart.pivotX - childPart.pivotX) * -1;
		childPart.pivotY = (parentPart.pivotY - childPart.pivotY) * -1;
		childPart.pivotZ = (parentPart.pivotZ - childPart.pivotZ) * -1;
	}
	
	private void addEntry(CemModelEntry entry, ArrayList<String> parentRefmap){
		ArrayList<String> refmap;
		if(parentRefmap != null && parentRefmap.size() > 0){
			@SuppressWarnings("unchecked")
			ArrayList<String> temp = (ArrayList<String>) parentRefmap.clone();
			refmap = temp;
		}
		else{
			refmap = new ArrayList<>();
			if(entry.getPart() != null){
				this.partNameRefs.put(entry.getPart(), entry);
			}
		}
		refmap.add((entry.getId() == null)? entry.getPart() : entry.getId());
		this.database.put(refmap, entry);
		for(CemModelEntry child : entry.getChildren().values()){
			this.addEntry(child, refmap);
		}
	}
	
	/**
	 * Test if the user specified a special texture to use
	 * @return If a texture is specified in the .jem file
	 */
	public boolean hasTexture(){
		return this.file.getTexture() != null;
	}
	
	/**
	 * Returns an Identifier for the texture specified in the .jem file
	 * @return Identifier of the texture
	 */
	public Identifier getTexture(){
		if(this.file.getTexture() == null){
			throw new NullPointerException("Trying to retrieve a null texture");
		}
		return this.file.getTexture();
	}
	
	/**
	 * Test if the user specified a shadow size to use
	 * @return If a shadow size is specified in the .jem file
	 */
	public boolean hasShadowRadius(){
		return this.file.getShadowsize() != null;
	}
	
	/**
	 * @return User-specified shadow radius
	 */
	public float getShadowRadius(){
		return this.file.getShadowsize();
	}
	
	public void applyAnimations(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, LivingEntity livingEntity){
		for(CemAnimation anim : this.animations){
			anim.apply(limbAngle, limbDistance, age, head_yaw, head_pitch, livingEntity);
		}
	}
	
	public CemModelEntry findChild(String target, CemModelEntry parent){
		CemModelEntry victim = null;
		ArrayList<String> hit = null;
		ArrayList<String> refmap = new ArrayList<>(Arrays.asList(target.split(":")));
		if(refmap.size() == 1 && this.partNameRefs.containsKey(refmap.get(0))){
			victim = this.partNameRefs.get(refmap.get(0));
			return victim;
		}
		else if(parent != null && (refmap.get(0).equals("this") || refmap.get(0).equals("part"))){
			if(refmap.size() == 1){
				return parent;
			}
			else{
				StringBuilder newTarget = new StringBuilder();
				newTarget.append((parent.getId() == null)? parent.getPart() : parent.getId());
				for(int d = 1; d < refmap.size(); d++){
					newTarget.append(":").append(refmap.get(d));
				}
				return findChild(newTarget.toString(), parent);
			}
		}
		else{
			for(ArrayList<String> part : this.database.keySet()){
				ArrayList<Integer> hello = new ArrayList<>();
				for(String ref : refmap){
					hello.add(part.indexOf(ref));
				}
				boolean hi = hello.size() != 1 || hello.get(0) > -1;
				for(int i = 0; i < hello.size() - 1; i++){
					hi = hi && hello.get(i) < hello.get(i + 1) && hello.get(i) > -1;
				}
				if(hi && (hit == null || part.size() < hit.size())){
					hit = part;
				}
				victim = this.database.get(hit);
			}
		}
		if(victim == null){
			throw new NullPointerException("Model part " + target + " isn't specified in " + this.file.getPath());
		}
		return victim;
	}
	
	private CemModelEntry findChild(String target){
		return this.findChild(target, null);
	}
	
	private static class CemAnimation{
		private final CemModelRegistry registry;
		private final CemModelEntry    target;
		private final ParsedExpression expression;
		private final char             operation;
		private final char             axis;
		
		CemAnimation(CemModelEntry target, String expr, String var, CemModelRegistry registry){
			this.target = target;
			this.registry = registry;
			this.expression = CemStringParser.parse(expr, this.registry, this.target);
			this.operation = var.charAt(0);
			this.axis = var.charAt(1);
		}
		
		void apply(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, LivingEntity livingEntity){
			float val = this.expression.eval(limbAngle, limbDistance, age, head_yaw, head_pitch, livingEntity, this.registry);
			switch(operation){
				case 't' -> this.target.setTranslate(this.axis, val);
				case 'r' -> this.target.getModel().setRotation(this.axis, val);
				case 's' -> target.getModel().setScale(this.axis, val);
				default -> throw new IllegalStateException("Unknown operation \"" + operation + "\"");
			}
		}
	}
}