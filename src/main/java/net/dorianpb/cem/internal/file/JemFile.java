package net.dorianpb.cem.internal.file;

import com.google.gson.internal.LinkedTreeMap;
import net.dorianpb.cem.internal.util.CemFairy;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InvalidObjectException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.regex.Pattern;

public class JemFile{
	private static final Pattern                   allowTextureChars = Pattern.compile("^[a-z0-9/._\\-]+$");
	private final        Identifier                texture;
	private final        List<Double>              textureSize;
	private final        Float                     shadowsize;
	private final        HashMap<String, JemModel> models;
	private final        Identifier                path;
	
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public JemFile(LinkedTreeMap<String, Object> json, Identifier path, ResourceManager resourceManager) throws InvalidObjectException{
		this.textureSize = CemFairy.JSONparseDoubleList(json.get("textureSize"));
		this.shadowsize = CemFairy.JSONparseFloat(json.get("shadowSize"));
		this.path = path;
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
		for(LinkedTreeMap model : (Iterable<LinkedTreeMap>) json.get("models")){
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
	
	private JemFile(Identifier texture, List<Double> textureSize, Float shadowsize, HashMap<String, JemModel> models, Identifier path){
		this.texture = texture;
		this.textureSize = textureSize;
		this.shadowsize = shadowsize;
		this.models = models;
		this.path = path;
	}
	
	public Identifier getTexture(){
		return this.texture;
	}
	
	public List<Double> getTextureSize(){
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
		return new JemFile(this.texture, new ArrayList<>(Arrays.asList(64.0D, 32.0D)), this.shadowsize, this.models, this.path);
	}
	
}