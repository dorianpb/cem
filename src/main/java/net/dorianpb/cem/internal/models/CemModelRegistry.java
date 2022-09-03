package net.dorianpb.cem.internal.models;

import net.dorianpb.cem.internal.api.CemModel.VanillaReferenceModelFactory;
import net.dorianpb.cem.internal.config.CemConfigFairy;
import net.dorianpb.cem.internal.file.JemFile;
import net.dorianpb.cem.internal.file.JemFile.JemModel;
import net.dorianpb.cem.internal.models.CemModelEntry.CemModelPart;
import net.dorianpb.cem.internal.models.CemModelEntry.TransparentCemModelPart;
import net.dorianpb.cem.internal.util.CemFairy;
import net.dorianpb.cem.internal.util.CemStringParser;
import net.dorianpb.cem.internal.util.CemStringParser.ParsedExpression;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/** Contains all of the data for the CEM model */
public class CemModelRegistry{
	private final HashMap<ArrayList<String>, CemModelEntry> database; //actual storage of cemModelEntries
	private final ArrayList<CemAnimation>                   animations; //actual storage of all the cemAnimations
	private final HashMap<String, CemModelEntry>            partNameRefs; //used to refer to parts by their model names rather than id names
	private final JemFile                                   file; //stores the jemFile
	
	public CemModelRegistry(JemFile file){
		this.database = new HashMap<>();
		this.animations = new ArrayList<>();
		this.partNameRefs = new HashMap<>();
		this.file = file;
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
					                                key.substring(key.indexOf(".") + 1),
					                                this
					));
				} catch(Exception e){
					CemFairy.getLogger().error("Error applying animation \"" + data.getAnimations().get(key) + "\" in \"" + file.getPath() + "\":");
					CemFairy.getLogger().error(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Constructs a CemModelPart to use when creating a (Block)EntityModel.
	 * @param params {@link CemRootPartParams}
	 *
	 * @return ModelPart used to create a (Block)EntityModel.
	 */
	public CemModelPart prepRootPart(CemRootPartParams params){
		for(String parent : params.familyTree().keySet()){
			for(String child : params.familyTree().get(parent)){
				this.prepChild(parent, child);
			}
		}
		CemModelPart newRoot = new CemModelPart();
		Set<String> partList = new LinkedHashSet<>();
		for(int i = params.familyTree().keySet().size() - 1; i >= 0; i--){
			partList.add((String) params.familyTree().keySet().toArray()[i]);
		}
		partList.addAll(this.partNameRefs.keySet());
		for(String partName : partList){
			CemModelEntry entry = this.getEntryByPartName(partName);
			if(entry != null){
				this.getParent(params.partNameMap(), params.familyTree(), newRoot, partName).addChild(params.partNameMap().getOrDefault(partName, partName),
				                                                                                      entry.getModel());
			}
		}
		if(params.inflate() != null){
			newRoot.inflate(params.inflate());
		}
		//new model creation fix!
		if(CemConfigFairy.getConfig().useTransparentParts()){
			Map<String, ModelTransform> newFixes = new HashMap<>();
			if(params.fixes() != null){
				params.fixes().forEach(((key, modelTransform) -> newFixes.put(params.partNameMap().getOrDefault(key, key), modelTransform)));
			}
			this.makePartTransparent(newRoot, params.vanillaReferenceModelFactory().get(), newFixes);
		}
		return newRoot;
	}
	
	private CemModelPart getParent(Map<String, String> partNameMap, Map<String, List<String>> familyTree, ModelPart root, String name){
		ArrayList<String> names = new ArrayList<>();
		while(true){
			name = this.findParent(familyTree, name);
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
	
	private String findParent(Map<String, List<String>> familyTree, String name){
		for(String key : familyTree.keySet()){
			if(familyTree.get(key).contains(name)){
				return key;
			}
		}
		return null;
	}
	
	private void makePartTransparent(CemModelPart target, ModelPart vanillaModel, @Nullable Map<String, ModelTransform> fixes){
		Set<String> iterator = new HashSet<>();
		iterator.addAll(target.children.keySet());
		iterator.addAll(vanillaModel.children.keySet());
		for(String key : iterator){
			try{
				if(target.children.containsKey(key) && vanillaModel.children.containsKey(key)){
					TransparentCemModelPart replacement;
					if(fixes != null && fixes.containsKey(key)){
						var correctTransform = ModelTransform.of(fixes.get(key).pivotX,
						                                         fixes.get(key).pivotY,
						                                         fixes.get(key).pivotZ,
						                                         vanillaModel.getChild(key).getTransform().pitch,
						                                         vanillaModel.getChild(key).getTransform().yaw,
						                                         vanillaModel.getChild(key).getTransform().roll
						                                        );
						replacement = new TransparentCemModelPart(target.getChild(key), correctTransform, vanillaModel.getChild(key).getTransform());
					}
					else{
						replacement = new TransparentCemModelPart(target.getChild(key), vanillaModel.getChild(key).getTransform(), vanillaModel.getChild(key).getTransform());
					}
					this.makePartTransparent((CemModelPart) target.getChild(key), vanillaModel.getChild(key), fixes);
					target.addChild(key, replacement);
				}
			} catch(Exception exception){
				CemFairy.getLogger().warn(exception);
			}
		}
	}
	
	private void prepChild(String parentPart, String childPart){
		CemModelEntry parent = this.getEntryByPartName(parentPart);
		CemModelEntry child = this.getEntryByPartName(childPart);
		if(parent == null || child == null){
			return;
		}
		CemModelPart parentPart1 = parent.getModel();
		ModelPart childPart1 = child.getModel();
		childPart1.pivotX = childPart1.pivotX - parentPart1.pivotX;
		childPart1.pivotY = childPart1.pivotY - parentPart1.pivotY;
		childPart1.pivotZ = childPart1.pivotZ - parentPart1.pivotZ;
	}
	
	public CemModelEntry getEntryByPartName(String key){
		if(this.partNameRefs.containsKey(key)){
			return this.partNameRefs.get(key);
		}
		CemFairy.getLogger().warn("Model part " + key + " isn't specified in " + this.file.getPath());
		return null;
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
	
	public void applyAnimations(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, Entity entity){
		for(CemAnimation anim : this.animations){
			anim.apply(limbAngle, limbDistance, age, head_yaw, head_pitch, entity);
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
		
		void apply(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, Entity entity){
			float val = this.expression.eval(limbAngle, limbDistance, age, head_yaw, head_pitch, entity, this.registry);
			if(!Float.isNaN(val)){
				switch(operation){
					case 't' -> this.target.setTranslate(this.axis, val);
					case 'r' -> this.target.setRotate(this.axis, val);
					case 's' -> this.target.getModel().setScale(this.axis, val);
					default -> throw new IllegalStateException("Unknown operation \"" + operation + "\"");
				}
			}
			else{
				this.target.setTranslate(this.axis, Float.MAX_VALUE);
			}
		}
	}
	
	public static class CemRootPartParams{
		private final           Map<String, String>          partNameMap;
		private final           Map<String, List<String>>    familyTree;
		private final           VanillaReferenceModelFactory vanillaReferenceModelFactory;
		private final @Nullable Map<String, ModelTransform>  fixes;
		private final @Nullable Float                        inflate;
		
		private CemRootPartParams(Map<String, String> partNameMap,
		                          Map<String, List<String>> familyTree,
		                          VanillaReferenceModelFactory vanillaReferenceModelFactory,
		                          @Nullable Map<String, ModelTransform> fixes,
		                          @Nullable Float inflate){
			this.partNameMap = partNameMap;
			this.familyTree = familyTree;
			this.vanillaReferenceModelFactory = vanillaReferenceModelFactory;
			this.fixes = fixes;
			this.inflate = inflate;
		}
		
		public Map<String, String> partNameMap(){
			return partNameMap;
		}
		
		public Map<String, List<String>> familyTree(){
			return familyTree;
		}
		
		public VanillaReferenceModelFactory vanillaReferenceModelFactory(){
			return vanillaReferenceModelFactory;
		}
		
		public Map<String, ModelTransform> fixes(){
			return fixes;
		}
		
		public Float inflate(){
			return inflate;
		}
	}
	
	public static class CemPrepRootPartParamsBuilder{
		private           Map<String, String>          partNameMap = new HashMap<>();
		private           Map<String, List<String>>    familyTree  = new HashMap<>();
		private           VanillaReferenceModelFactory vanillaReferenceModelFactory;
		private @Nullable Map<String, ModelTransform>  fixes       = null;
		private @Nullable Float                        inflate     = null;
		
		public CemPrepRootPartParamsBuilder setPartNameMap(Map<String, String> partNameMap){
			this.partNameMap = partNameMap;
			return this;
		}
		
		public CemPrepRootPartParamsBuilder setFamilyTree(Map<String, List<String>> familyTree){
			this.familyTree = familyTree;
			return this;
		}
		
		public CemPrepRootPartParamsBuilder setVanillaReferenceModelFactory(VanillaReferenceModelFactory vanillaReferenceModelFactory){
			this.vanillaReferenceModelFactory = vanillaReferenceModelFactory;
			return this;
		}
		
		public CemPrepRootPartParamsBuilder setFixes(@Nullable Map<String, ModelTransform> fixes){
			this.fixes = fixes;
			return this;
		}
		
		public CemPrepRootPartParamsBuilder setInflate(@Nullable Float inflate){
			this.inflate = inflate;
			return this;
		}
		
		public CemRootPartParams create(){
			return new CemRootPartParams(partNameMap, familyTree, vanillaReferenceModelFactory, fixes, inflate);
		}
	}
}