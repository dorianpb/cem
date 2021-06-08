package net.dorianpb.cem.internal.file;

import com.google.gson.internal.LinkedTreeMap;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;

public class JpmFile{
	private final String               id;
	private final String               texture;
	private final ArrayList<Double>    textureSize;
	private final boolean[]            invertAxis;
	private final ArrayList<Double>    translate;
	private final ArrayList<Double>    rotate;
	private final Boolean[]            mirrorTexture;
	private final ArrayList<JpmBox>    boxes;
	private final ArrayList<JpmSprite> sprites;
	private final ArrayList<JpmFile>   submodels;
	
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public JpmFile(LinkedTreeMap json){
		this.id = (String) json.get("id");
		this.texture = (String) json.get("texture");
		this.textureSize = (ArrayList<Double>) json.get("textureSize");
		
		String axes = (String) json.getOrDefault("invertAxis", "");
		this.invertAxis = new boolean[]{axes.contains("x"), axes.contains("y"), axes.contains("z")};
		
		this.translate = (ArrayList<Double>) json.getOrDefault("translate", new ArrayList<>(Arrays.asList(0D, 0D, 0D)));
		this.rotate = (ArrayList<Double>) json.getOrDefault("rotate", new ArrayList<>(Arrays.asList(0D, 0D, 0D)));
		for(int i = 0; i < this.rotate.size(); i++){
			this.rotate.set(i, -Math.toRadians(this.rotate.get(i)));
		}
		
		String mirror = (String) json.getOrDefault("mirrorTexture", "");
		this.mirrorTexture = new Boolean[]{mirror.contains("u"), mirror.contains("v")};
		
		if(json.containsKey("boxes")){
			this.boxes = new ArrayList<>();
			for(LinkedTreeMap cube : (ArrayList<LinkedTreeMap>) json.get("boxes")){
				this.boxes.add(new JpmBox(cube));
			}
		}
		else{
			this.boxes = null;
		}
		if(json.containsKey("sprites")){
			this.sprites = new ArrayList<>();
			for(LinkedTreeMap sprite : (ArrayList<LinkedTreeMap>) json.get("sprites")){
				this.sprites.add(new JpmSprite(sprite));
			}
		}
		else{
			this.sprites = null;
		}
		if(json.containsKey("submodel") || json.containsKey("submodels")){
			this.submodels = new ArrayList<>();
			if(json.containsKey("submodel")){
				this.submodels.add(new JpmFile((LinkedTreeMap) json.get("submodel")));
			}
			if(json.containsKey("submodels")){
				for(LinkedTreeMap submodel : (ArrayList<LinkedTreeMap>) json.get("submodels")){
					this.submodels.add(new JpmFile(submodel));
				}
			}
		}
		else{
			this.submodels = null;
		}
	}
	
	public ArrayList<JpmBox> getBoxes(){
		return boxes;
	}
	
	public ArrayList<Double> getTranslate(){
		return translate;
	}
	
	public boolean[] getInvertAxis(){
		return invertAxis;
	}
	
	public ArrayList<JpmFile> getSubmodels(){
		return submodels;
	}
	
	public ArrayList<Double> getRotate(){
		return this.rotate;
	}
	
	public String getId(){
		return id;
	}
	
	public Boolean[] getMirrorTexture(){
		return mirrorTexture;
	}
	
	public static class JpmBox{
		private final ArrayList<Double> textureOffset;
		private final ArrayList<Double> uvUp;
		private final ArrayList<Double> uvDown;
		private final ArrayList<Double> uvFront;
		private final ArrayList<Double> uvBack;
		private final ArrayList<Double> uvLeft;
		private final ArrayList<Double> uvRight;
		private final ArrayList<Double> coordinates;
		private final Double            sizeAdd;
		
		@SuppressWarnings({"unchecked", "rawtypes"})
		JpmBox(LinkedTreeMap json){
			ArrayList<Double> zeroes = new ArrayList<>(Arrays.asList(0D, 0D, 0D, 0D));
			this.textureOffset = (ArrayList<Double>) json.get("textureOffset");
			this.uvUp = (ArrayList<Double>) json.getOrDefault("uvUp", zeroes);
			this.uvDown = (ArrayList<Double>) json.getOrDefault("uvDown", zeroes);
			this.uvFront = (ArrayList<Double>) json.getOrDefault("uvFront", json.getOrDefault("uvNorth", zeroes));
			this.uvBack = (ArrayList<Double>) json.getOrDefault("uvBack", json.getOrDefault("uvSouth", zeroes));
			this.uvLeft = (ArrayList<Double>) json.getOrDefault("uvLeft", json.getOrDefault("uvWest", zeroes));
			this.uvRight = (ArrayList<Double>) json.getOrDefault("uvRight", json.getOrDefault("uvEast", zeroes));
			this.coordinates = (ArrayList<Double>) json.get("coordinates");
			this.sizeAdd = (Double) json.getOrDefault("sizeAdd", 0D);
			this.validate();
		}
		
		private void validate(){
			if(this.textureOffset == null){
				String str = "Element \"textureOffset\" is required";
				throw new InvalidParameterException((this.uvUp != null ||
				                                     this.uvDown != null ||
				                                     this.uvFront != null ||
				                                     this.uvBack != null ||
				                                     this.uvLeft != null ||
				                                     this.uvRight != null)? str + "; Specifying texture using uv coordinates is not supported" : str);
			}
			if(this.coordinates == null){
				throw new InvalidParameterException("Element \"coordinates\" is required");
			}
		}
		
		public ArrayList<Double> getTextureOffset(){
			return textureOffset;
		}
		
		public ArrayList<Double> getCoordinates(){
			return coordinates;
		}
		
		public Double getSizeAdd(){
			return sizeAdd;
		}
		
	}
	
	static class JpmSprite{
		private final ArrayList<Integer> textureOffset;
		private final ArrayList<Integer> coordinates;
		private final Double             sizeAdd;
		
		@SuppressWarnings({"unchecked", "rawtypes"})
		JpmSprite(LinkedTreeMap json){
			this.textureOffset = (ArrayList<Integer>) json.get("textureOffset");
			this.coordinates = (ArrayList<Integer>) json.get("coordinates");
			this.sizeAdd = (Double) json.get("sizeAdd");
		}
	}
}