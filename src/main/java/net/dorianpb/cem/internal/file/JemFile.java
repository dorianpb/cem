package net.dorianpb.cem.internal.file;

import com.google.gson.internal.LinkedTreeMap;
import net.dorianpb.cem.internal.util.CemFairy;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class JemFile{
	private final String                    texture;
	private final ArrayList<Double>         textureSize;
	private final Float                     shadowsize;
	private final HashMap<String, JemModel> models;
	private final Identifier                path;
	
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public JemFile(LinkedTreeMap<String, Object> json, Identifier path, ResourceManager resourceManager){
		this.texture = (String) json.get("texture");
		this.textureSize = (ArrayList<Double>) json.get("textureSize");
		this.shadowsize = (Float) json.get("shadowSize");
		this.path = path;
		models = new HashMap<>();
		for(LinkedTreeMap model : (ArrayList<LinkedTreeMap>) json.get("models")){
			JemModel newmodel = new JemModel(model, this.path, resourceManager);
			models.put(newmodel.getPart(), newmodel);
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
	}
	
	public Identifier getTexture(){
		if(this.texture != null){
			return CemFairy.transformPath(this.texture, this.path);
		}
		return null;
	}
	
	public ArrayList<Double> getTextureSize(){
		return textureSize;
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
	
	public static class JemModel{
		private final String                        baseId;
		private final String                        model;
		private final String                        part;
		private final Boolean                       attach;
		private final Double                        scale;
		private final LinkedTreeMap<String, String> animations;
		private final JpmFile                       modelDef;
		
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		JemModel(LinkedTreeMap json, Identifier path, ResourceManager resourceManager){
			this.baseId = (String) json.get("baseId");
			this.model = (String) json.get("model");
			this.part = (String) json.get("part");
			this.attach = (Boolean) json.get("attach");
			this.scale = (Double) json.getOrDefault("scale", 1D);
			var yeah = ((ArrayList<LinkedTreeMap<String, Object>>) json.getOrDefault("animations", new ArrayList<>(Collections.singletonList(new LinkedTreeMap())))).get(0);
			this.animations = new LinkedTreeMap<>();
			yeah.forEach((key, value) -> this.animations.put(key, value.toString()));
			JpmFile temp = null;
			if(this.model != null){
				Identifier id = CemFairy.transformPath(this.model, path);
				try(InputStream stream = resourceManager.getResource(id).getInputStream()){
					@SuppressWarnings("unchecked")
					LinkedTreeMap<String, Object> file = CemFairy.getGson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), LinkedTreeMap.class);
					if(file == null){
						throw new Exception("Invalid File");
					}
					temp = new JpmFile(file);
				} catch(Exception exception){
					CemFairy.postReadError(exception, id);
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
			return part;
		}
		
		public Double getScale(){
			return scale;
		}
		
		String getModel(){
			return model;
		}
		
		String getId(){
			return this.getModelDef().getId();
		}
		
		public JpmFile getModelDef(){
			return modelDef;
		}
		
		public LinkedTreeMap<String, String> getAnimations(){
			return animations;
		}
		
	}
}