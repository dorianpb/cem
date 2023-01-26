package net.dorianpb.cem.internal.file;

import com.google.gson.internal.LinkedTreeMap;
import net.dorianpb.cem.internal.util.CemFairy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class JpmFile{
	private final String            id;
	private final String            texture;
	private final List<Double>      textureSize;
	private final boolean[]         invertAxis;
	private final List<Double>      translate;
	private final ArrayList<Double> rotate;
	private final Boolean[]         mirrorTexture;
	private final List<JpmBox>      boxes;
	private final List<JpmSprite>   sprites;
	private final List<JpmFile>     submodels;
	
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	JpmFile(LinkedTreeMap json){
		this.id = CemFairy.JSONparseString(json.get("id"));
		this.texture = CemFairy.JSONparseString(json.get("texture"));
		this.textureSize = CemFairy.JSONparseDoubleList(json.get("textureSize"));
		
		String axes = CemFairy.JSONparseString(json.getOrDefault("invertAxis", ""));
		assert axes != null;
		this.invertAxis = new boolean[]{axes.contains("x"), axes.contains("y"), axes.contains("z")};
		
		this.translate = CemFairy.JSONparseDoubleList(json.getOrDefault("translate", new ArrayList<>(Arrays.asList(0.0D, 0.0D, 0.0D))));
		
		this.rotate = CemFairy.JSONparseDoubleList(json.getOrDefault("rotate", new ArrayList<>(Arrays.asList(0.0D, 0.0D, 0.0D))));
		for(int i = 0; i < Objects.requireNonNull(this.rotate).size(); i++){
			this.rotate.set(i, -Math.toRadians(this.rotate.get(i)));
		}
		
		String mirror = CemFairy.JSONparseString(json.getOrDefault("mirrorTexture", ""));
		assert mirror != null;
		this.mirrorTexture = new Boolean[]{mirror.contains("u"), mirror.contains("v")};
		
		if(json.containsKey("boxes")){
			this.boxes = new ArrayList<>();
			for(LinkedTreeMap cube : (Iterable<LinkedTreeMap>) json.get("boxes")){
				this.boxes.add(new JpmBox(cube));
			}
		}
		else{
			this.boxes = null;
		}
		if(json.containsKey("sprites")){
			this.sprites = new ArrayList<>();
			for(LinkedTreeMap sprite : (Iterable<LinkedTreeMap>) json.get("sprites")){
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
				for(LinkedTreeMap submodel : (Iterable<LinkedTreeMap>) json.get("submodels")){
					this.submodels.add(new JpmFile(submodel));
				}
			}
		}
		else{
			this.submodels = null;
		}
	}
	
	public Iterable<JpmBox> getBoxes(){
		return this.boxes;
	}
	
	public List<Double> getTranslate(){
		return this.translate;
	}
	
	public boolean[] getInvertAxis(){
		return this.invertAxis;
	}
	
	public Iterable<JpmFile> getSubmodels(){
		return this.submodels;
	}
	
	public List<Double> getRotate(){
		return this.rotate;
	}
	
	public String getId(){
		return this.id;
	}
	
	public Boolean[] getMirrorTexture(){
		return this.mirrorTexture;
	}
	
}