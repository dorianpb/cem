package net.dorianpb.cem.internal.file;

import com.google.gson.internal.LinkedTreeMap;
import net.dorianpb.cem.internal.util.CemFairy;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public class JemFile{
	public static final Pattern                   allowTextureChars = Pattern.compile("^[a-z0-9/._\\-]+$");
	private final       Identifier                texture;
	private final       ArrayList<Double>         textureSize;
	private final       Float                     shadowsize;
	private final       HashMap<String, JemModel> models;
	private final       Identifier                path;
	private final boolean modelCreatinFix;
	
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public JemFile(LinkedTreeMap<String, Object> json, LinkedTreeMap<String, Object> mcMeta,Identifier path, String packName, ResourceManager resourceManager) throws Exception{
		this.textureSize = CemFairy.JSONparseDoubleList(json.get("textureSize"));
		this.shadowsize = CemFairy.JSONparseFloat(json.get("shadowSize"));
		this.path = path;
		this.modelCreatinFix = Boolean.TRUE.equals(CemFairy.JSONparseBool(mcMeta.get("creation_model_fix")));

		String texturepath = CemFairy.JSONparseString(json.get("texture"));
		if(texturepath == null || texturepath.isEmpty()){
			
			Identifier jankTexture = CemFairy.transformPath(path.getPath().substring(path.getPath().lastIndexOf('/') + 1, path.getPath().lastIndexOf('.')) + ".png",
			                                                this.path
			                                               );
			this.texture = resourceManager.getResource(jankTexture).isPresent()? jankTexture : null;
		}
		else{
			this.texture = CemFairy.transformPath(texturepath, this.path);
		}
		
		this.models = new HashMap<>();
		for(LinkedTreeMap model : (ArrayList<LinkedTreeMap>) json.get("models")){
			JemModel newmodel = new JemModel(model, this.path, resourceManager);
			this.models.put(newmodel.getPart(), newmodel);
		}
		this.validate();
	}
	
	private void validate(){
		if(this.models == null){
			throw new InvalidParameterException("Element \"models\" is required");
		}
		if(this.textureSize == null){
			throw new InvalidParameterException("Element \"textureSize\" is required");
		}
		if(this.texture != null && !allowTextureChars.matcher(this.texture.getPath()).find()){
			throw new InvalidParameterException("Non [a-z0-9/._-] character in path of location: " + this.texture);
		}
	}
	
	private JemFile(Identifier texture, ArrayList<Double> textureSize, Float shadowsize, HashMap<String, JemModel> models, Identifier path, boolean modelCreatinFix){
		this.texture = texture;
		this.textureSize = textureSize;
		this.shadowsize = shadowsize;
		this.models = models;
		this.path = path;
		this.modelCreatinFix = modelCreatinFix;
	}
	
	public Identifier getTexture(){
		return this.texture;
	}
	
	public ArrayList<Double> getTextureSize(){
		return this.textureSize;
	}
	
	public Set<String> getModelList(){
		return this.models.keySet();
	}
	
	public JemModel getModel(String key){
		return this.models.get(key);
	}
	
	public String getPath(){
		return this.path.getPath();
	}
	
	public Float getShadowsize(){
		return this.shadowsize;
	}
	
	public JemFile getArmorVarient(){
		return new JemFile(this.texture, new ArrayList<>(Arrays.asList(64D, 32D)), this.shadowsize, this.models, this.path, this.modelCreatinFix);
	}
	
	public static class JemModel{
		private final String                        baseId;
		private final String                        model;
		private final String                        part;
		private final Boolean                       attach;
		private final Double                        scale;
		private final LinkedTreeMap<String, String> animations;
		private final JpmFile                       modelDef;
		
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		JemModel(LinkedTreeMap json, Identifier path, ResourceManager resourceManager) throws Exception{
			this.baseId = CemFairy.JSONparseString(json.get("baseId"));
			this.model = CemFairy.JSONparseString(json.get("model"));
			this.part = CemFairy.JSONparseString(json.get("part"));
			this.attach = CemFairy.JSONparseBool(json.get("attach"));
			this.scale = CemFairy.JSONparseDouble(json.getOrDefault("scale", 1D));
			var yeah = ((ArrayList<LinkedTreeMap<String, Object>>) json.getOrDefault("animations", new ArrayList<>(Collections.singletonList(new LinkedTreeMap()))));
			this.animations = new LinkedTreeMap<>();
			yeah.forEach((value) -> value.forEach((key, value1) -> this.animations.put(key, value1.toString())));
			JpmFile temp;
			if(this.model != null){
				Identifier id = CemFairy.transformPath(this.model, path);
				Optional<Resource> resourceOptional = resourceManager.getResource(id);
				if(resourceOptional.isPresent()){
					try(InputStream stream = resourceOptional.get().getInputStream()){
						@SuppressWarnings("unchecked")
						LinkedTreeMap<String, Object> file = CemFairy.getGson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), LinkedTreeMap.class);
						if(file == null){
							throw new Exception("Invalid File");
						}
						temp = new JpmFile(file);
					} catch(Exception exception){
						CemFairy.postReadError(exception, id);
						throw new Exception("Error loading dependent file: " + id + exception.getMessage());
					}
				}
				else{
					CemFairy.getLogger().warn(" File \"" + resourceOptional + " not found,");
					CemFairy.getLogger().warn(" falling back on reading model definition from " + path.toString() + "!");
					temp = new JpmFile(json);
				}
			}
			else{
				temp = new JpmFile(json);
			}
			this.modelDef = temp;
			this.validate();
		}
		
		private void validate(){
			if(this.part == null){
				throw new InvalidParameterException("Element \"part\" is required");
			}
		}
		
		public String getPart(){
			return this.part;
		}
		
		public Double getScale(){
			return this.scale;
		}
		
		String getModel(){
			return this.model;
		}
		
		String getId(){
			return this.getModelDef().getId();
		}
		
		public JpmFile getModelDef(){
			return this.modelDef;
		}
		
		public LinkedTreeMap<String, String> getAnimations(){
			return this.animations;
		}
		
	}
}