package net.dorianpb.cem.internal.file;

import com.google.gson.internal.LinkedTreeMap;
import net.dorianpb.cem.internal.util.CemFairy;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class JpmBox{
	private final List<Double> textureOffset;
	private final List<Double> uvUp;
	private final List<Double> uvDown;
	private final List<Double> uvFront;
	private final List<Double> uvBack;
	private final List<Double> uvLeft;
	private final List<Double> uvRight;
	private final List<Double> coordinates;
	private final Double       sizeAdd;
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	JpmBox(LinkedTreeMap json){
		this.textureOffset = CemFairy.JSONparseDoubleList(json.get("textureOffset"));
		this.uvUp = getNull(CemFairy.JSONparseDoubleList(json.get("uvUp")));
		this.uvDown = getNull(CemFairy.JSONparseDoubleList(json.get("uvDown")));
		this.uvFront = getNull(CemFairy.JSONparseDoubleList(json.getOrDefault("uvFront", json.get("uvNorth"))));
		this.uvBack = getNull(CemFairy.JSONparseDoubleList(json.getOrDefault("uvBack", json.get("uvSouth"))));
		this.uvLeft = getNull(CemFairy.JSONparseDoubleList(json.getOrDefault("uvLeft", json.get("uvWest"))));
		this.uvRight = getNull(CemFairy.JSONparseDoubleList(json.getOrDefault("uvRight", json.get("uvEast"))));
		this.coordinates = CemFairy.JSONparseDoubleList(json.get("coordinates"));
		this.sizeAdd = (Double) json.getOrDefault("sizeAdd", 0.0D);
		this.validate();
	}
	
	private static List<Double> getNull(@Nullable List<Double> obj){
		return obj == null? new ArrayList<>() : obj;
	}
	
	@SuppressWarnings("unchecked")
	private void validate(){
		if(this.textureOffset == null){
			boolean triedToUseUV = false;
			for(List<Double> uvCoords : new List[]{this.uvUp, this.uvDown, this.uvFront, this.uvBack, this.uvLeft, this.uvRight}){
				triedToUseUV = triedToUseUV || uvCoords.size() == 4;
			}
			if(triedToUseUV){
				Collection<Double> doubles = Arrays.asList(0.0D, 0.0D, 0.0D, 0.0D);
				boolean warn = false;
				for(List<Double> uvCoords : new List[]{this.uvUp, this.uvDown, this.uvFront, this.uvBack, this.uvLeft, this.uvRight}){
					if(uvCoords.isEmpty()){
						uvCoords.addAll(doubles);
						warn = true;
					}
				}
				if(warn){
					CemFairy.getLogger().warn("\tthe above file didn't specify all uv directions!");
				}
			}
			else{
				throw new InvalidParameterException("Either \"textureOffset\" or at least one of the uv directions are required!");
			}
		}
		if(this.coordinates == null){
			throw new InvalidParameterException("Element \"coordinates\" is required");
		}
	}
	
	public boolean useUvMap(){
		return this.textureOffset == null;
	}
	
	public List<Double> getTextureOffset(){
		return this.textureOffset;
	}
	
	public List<Double> getCoordinates(){
		return this.coordinates;
	}
	
	public Double getSizeAdd(){
		return this.sizeAdd;
	}
	
	public float[] getUv(String direction){
		return switch(direction.toLowerCase()){
			case "down" -> new float[]{this.uvUp.get(0).floatValue(), this.uvUp.get(1).floatValue(), this.uvUp.get(2).floatValue(), this.uvUp.get(3).floatValue()};
			case "up" -> new float[]{this.uvDown.get(0).floatValue(), this.uvDown.get(1).floatValue(), this.uvDown.get(2).floatValue(), this.uvDown.get(3).floatValue()};
			case "front", "north" ->
					new float[]{this.uvFront.get(0).floatValue(), this.uvFront.get(1).floatValue(), this.uvFront.get(2).floatValue(), this.uvFront.get(3).floatValue()};
			case "back", "south" ->
					new float[]{this.uvBack.get(0).floatValue(), this.uvBack.get(1).floatValue(), this.uvBack.get(2).floatValue(), this.uvBack.get(3).floatValue()};
			case "right", "east" ->
					new float[]{this.uvLeft.get(0).floatValue(), this.uvLeft.get(1).floatValue(), this.uvLeft.get(2).floatValue(), this.uvLeft.get(3).floatValue()};
			case "left", "west" ->
					new float[]{this.uvRight.get(0).floatValue(), this.uvRight.get(1).floatValue(), this.uvRight.get(2).floatValue(), this.uvRight.get(3).floatValue()};
			default -> throw new IllegalStateException("Unexpected value: " + direction);
		};
	}
	
}