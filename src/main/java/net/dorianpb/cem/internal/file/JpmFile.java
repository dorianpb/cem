package net.dorianpb.cem.internal.file;

import com.google.gson.internal.LinkedTreeMap;
import net.dorianpb.cem.internal.util.CemFairy;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
		this.id = CemFairy.JSONparseString(json.get("id"));
		this.texture = CemFairy.JSONparseString(json.get("texture"));
		this.textureSize = CemFairy.JSONparseDoubleList(json.get("textureSize"));
		
		String axes = CemFairy.JSONparseString(json.getOrDefault("invertAxis", ""));
		this.invertAxis = new boolean[]{axes.contains("x"), axes.contains("y"), axes.contains("z")};
		
		this.translate = CemFairy.JSONparseDoubleList(json.getOrDefault("translate", new ArrayList<>(Arrays.asList(0D, 0D, 0D))));
		
		this.rotate = CemFairy.JSONparseDoubleList(json.getOrDefault("translate", new ArrayList<>(Arrays.asList(0D, 0D, 0D))));
		for(int i = 0; i < Objects.requireNonNull(this.rotate).size(); i++){
			this.rotate.set(i, -Math.toRadians(this.rotate.get(i)));
		}
		
		String mirror = CemFairy.JSONparseString(json.getOrDefault("mirrorTexture", ""));
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
			this.textureOffset = CemFairy.JSONparseDoubleList(json.get("textureOffset"));
			this.uvUp = CemFairy.JSONparseDoubleList(json.getOrDefault("uvUp", new ArrayList<>(Arrays.asList(0D, 0D, 0D, 0D))));
			this.uvDown = CemFairy.JSONparseDoubleList(json.getOrDefault("uvDown", new ArrayList<>(Arrays.asList(0D, 0D, 0D, 0D))));
			this.uvFront = CemFairy.JSONparseDoubleList(json.getOrDefault("uvFront", json.getOrDefault("uvNorth", new ArrayList<>(Arrays.asList(0D, 0D, 0D, 0D)))));
			this.uvBack = CemFairy.JSONparseDoubleList(json.getOrDefault("uvBack", json.getOrDefault("uvSouth", new ArrayList<>(Arrays.asList(0D, 0D, 0D, 0D)))));
			this.uvLeft = CemFairy.JSONparseDoubleList(json.getOrDefault("uvLeft", json.getOrDefault("uvWest", new ArrayList<>(Arrays.asList(0D, 0D, 0D, 0D)))));
			this.uvRight = CemFairy.JSONparseDoubleList(json.getOrDefault("uvRight", json.getOrDefault("uvEast", new ArrayList<>(Arrays.asList(0D, 0D, 0D, 0D)))));
			this.coordinates = CemFairy.JSONparseDoubleList(json.get("coordinates"));
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