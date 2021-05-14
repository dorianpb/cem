package net.dorianpb.cem.internal;

import com.google.gson.internal.LinkedTreeMap;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

class JemFile{
	private final String texture;
	private final ArrayList<Double> textureSize;
	private final Float shadowsize;
	private final HashMap<String, JemModel> models;
	private final String path;
	
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	JemFile(LinkedTreeMap<String, Object> json, String path){
		this.texture = (String) json.get("texture");
		this.textureSize = (ArrayList<Double>) json.get("textureSize");
		this.shadowsize = (Float) json.get("shadowSize");
		this.path = path;
		models = new HashMap<>();
		for(LinkedTreeMap model : (ArrayList<LinkedTreeMap>) json.get("models")){
			JemModel newmodel = new JemModel(model, this.path);
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
	
	String getTexture(){
		if(this.texture != null){
			return CemFairy.transformPath(this.texture, this.path);
		}
		return null;
	}
	
	ArrayList<Double> getTextureSize(){
		return textureSize;
	}
	
	Set<String> getModelList(){
		return this.models.keySet();
	}
	
	JemModel getModel(String key){
		return this.models.get(key);
	}
	
	String getPath(){
		return this.path;
	}
	
	static class JemModel{
		private final String baseId;
		private final String model;
		private final String part;
		private final Boolean attach;
		private final Double scale;
		private final LinkedTreeMap<String, String> animations;
		private final JpmFile modelDef;
		
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		JemModel(LinkedTreeMap json, String path){
			this.baseId = (String) json.get("baseId");
			this.model = (String) json.get("model");
			this.part = (String) json.get("part");
			this.attach = (Boolean) json.get("attach");
			this.scale = (Double) json.getOrDefault("scale", 1D);
			this.animations = ((ArrayList<LinkedTreeMap<String, String>>) json.getOrDefault("animations",
			                                                                                new ArrayList<>(Collections.singletonList(new LinkedTreeMap()))
			                                                                               )).get(0);
			if(this.model != null){
				this.modelDef = CemFairy.loadJpmFile(CemFairy.transformPath(this.model, path));
			}
			else{
				this.modelDef = new JpmFile(json);
			}
			this.validate();
		}
		
		private void validate(){
			if(this.part == null){
				throw new InvalidParameterException("Element \"part\" is required");
			}
		}
		
		String getPart(){
			return part;
		}
		
		Double getScale(){
			return scale;
		}
		
		String getModel(){
			return model;
		}
		
		String getId(){
			return this.getModelDef().getId();
		}
		
		JpmFile getModelDef(){
			return modelDef;
		}
		
		LinkedTreeMap<String, String> getAnimations(){
			return animations;
		}
		
	}
}