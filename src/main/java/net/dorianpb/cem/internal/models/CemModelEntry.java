package net.dorianpb.cem.internal.models;

import net.dorianpb.cem.internal.config.CemConfigFairy;
import net.dorianpb.cem.internal.file.JemModel;
import net.dorianpb.cem.internal.file.JpmFile;
import net.dorianpb.cem.internal.file.JpmFile.JpmBox;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class CemModelEntry{
	private final String                     id;
	private final String                     part;
	private final Map<String, CemModelEntry> children;
	private final CemModelPart               model;
	private final float[]                    translates;
	private final float[]                    rotates;
	private final boolean[]                  invertAxis;
	private final int                        gen;
	private final float[]                    offsets;
	
	
	CemModelEntry(JemModel file, int textureWidth, int textureHeight){
		this(file.getPart(), file.getModelDef(), new float[]{0, 24, 0}, 0, file.getScale().floatValue(), textureWidth, textureHeight);
		if(!file.getAnimations().isEmpty()){
			for(String key : file.getAnimations().keySet()){
				if(!key.contains(".")){
					throw new InvalidParameterException("Invalid Syntax: " + key);
				}
			}
		}
	}
	
	private CemModelEntry(String part, JpmFile data, float[] parents, int gen, float scale, int textureWidth, int textureHeight){
		this.id = data.getId();
		this.part = part;
		this.children = new HashMap<>();
		this.invertAxis = data.getInvertAxis();
		this.gen = gen;
		this.translates = new float[]{(data.getTranslate().get(0).floatValue()) * (this.invertAxis[0]? -1 : 1),
		                              (data.getTranslate().get(1).floatValue()) * (this.invertAxis[1]? -1 : 1),
		                              (data.getTranslate().get(2).floatValue()) * (this.invertAxis[2]? -1 : 1),
		                              };
		this.rotates = new float[]{data.getRotate().get(0).floatValue() * ((this.invertAxis[0])? 1 : -1),
		                           data.getRotate().get(1).floatValue() * ((this.invertAxis[1])? 1 : -1),
		                           data.getRotate().get(2).floatValue() * ((this.invertAxis[2])? 1 : -1),
		                           };
		
		this.model = new CemModelPart(textureWidth, textureHeight);
		this.initmodel(data, parents, scale);
		this.offsets = new float[]{0, 0, 0};
		//CHILD INIT
		if(data.getSubmodels() != null){
			for(JpmFile submodel : data.getSubmodels()){
				float childZ = (this.gen == 0)? ((data.getTranslate().get(2).floatValue()) * (this.invertAxis[2]? -1 : 1)) : 0;
				float childY = (this.gen == 0)? ((data.getTranslate().get(1).floatValue()) * (this.invertAxis[1]? -1 : 1)) : 0;
				float childX = (this.gen == 0)? ((data.getTranslate().get(0).floatValue()) * (this.invertAxis[0]? -1 : 1)) : 0;
				this.addChild(new CemModelEntry(null, submodel, new float[]{childX, childY, childZ}, this.gen + 1, 1, textureWidth, textureHeight));
			}
		}
		//END CHILD INIT
	}
	
	private void initmodel(JpmFile data, float[] parents, float scale){
		float[] pivot = {((this.gen == 0)
		                  ? (parents[0] - (data.getTranslate().get(0).floatValue() * (this.invertAxis[0]? -1 : 1)))
		                  : ((this.gen == 1)
		                     ? (parents[0] + (data.getTranslate().get(0).floatValue() * (this.invertAxis[0]? -1 : 1)))
		                     : data.getTranslate().get(0).floatValue() * (this.invertAxis[1]? -1 : 1))),
		                 ((this.gen == 0)
		                  ? (parents[1] - (data.getTranslate().get(1).floatValue() * (this.invertAxis[1]? -1 : 1)))
		                  : ((this.gen == 1)
		                     ? (parents[1] + (data.getTranslate().get(1).floatValue() * (this.invertAxis[1]? -1 : 1)))
		                     : data.getTranslate().get(1).floatValue() * (this.invertAxis[1]? -1 : 1))),
		                 ((this.gen == 0)
		                  ? (parents[2] - (data.getTranslate().get(2).floatValue() * (this.invertAxis[2]? -1 : 1)))
		                  : ((this.gen == 1)
		                     ? (parents[2] + (data.getTranslate().get(2).floatValue() * (this.invertAxis[2]? -1 : 1)))
		                     : data.getTranslate().get(2).floatValue() * (this.invertAxis[2]? -1 : 1))),
		                 };
		///MUST SUBTRACT FROM PARENT FOR this.gen1
		float[] translate = {(data.getTranslate().get(0).floatValue()), (data.getTranslate().get(1).floatValue()), (data.getTranslate().get(2).floatValue()),
		                     };
		if(data.getBoxes() != null){
			for(JpmBox box : data.getBoxes()){
				//apply translates first, then ?invert pos, then ?subtract pos by size so that it is drawn correctly
				//top level model pivots need to translated up by 24, then 1st this.gen children need to work off of that rather than the translate values provided by the
				// jpmFile
				//only top level models need translates applied, others are relative to parent (even this.gen1)
				if(box.useUvMap()){
					this.model.addCuboid(((box.getCoordinates().get(0).floatValue() + ((this.gen == 0)? translate[0] : 0)) * ((this.invertAxis[0])? -1 : 1)) -
					                     ((this.invertAxis[0])? box.getCoordinates().get(3).floatValue() : 0),
					
					                     ((box.getCoordinates().get(1).floatValue() + ((this.gen == 0)? translate[1] : 0)) * ((this.invertAxis[1])? -1 : 1)) -
					                     ((this.invertAxis[1])? box.getCoordinates().get(4).floatValue() : 0),
					
					                     ((box.getCoordinates().get(2).floatValue() + ((this.gen == 0)? translate[2] : 0)) * ((this.invertAxis[2])? -1 : 1)) -
					                     ((this.invertAxis[2])? box.getCoordinates().get(5).floatValue() : 0),
					                     box.getCoordinates().get(3).intValue(),
					                     box.getCoordinates().get(4).intValue(),
					                     box.getCoordinates().get(5).intValue(),
					                     box.getSizeAdd().floatValue(),
					                     data.getMirrorTexture()[0],
					                     data.getMirrorTexture()[1],
					                     box.getUv("north"),
					                     box.getUv("south"),
					                     box.getUv("east"),
					                     box.getUv("west"),
					                     box.getUv("up"),
					                     box.getUv("down")
					                    );
				}
				else{
					this.model.addCuboid(((box.getCoordinates().get(0).floatValue() + ((this.gen == 0)? translate[0] : 0)) * ((this.invertAxis[0])? -1 : 1)) -
					                     ((this.invertAxis[0])? box.getCoordinates().get(3).floatValue() : 0),
					
					                     ((box.getCoordinates().get(1).floatValue() + ((this.gen == 0)? translate[1] : 0)) * ((this.invertAxis[1])? -1 : 1)) -
					                     ((this.invertAxis[1])? box.getCoordinates().get(4).floatValue() : 0),
					
					                     ((box.getCoordinates().get(2).floatValue() + ((this.gen == 0)? translate[2] : 0)) * ((this.invertAxis[2])? -1 : 1)) -
					                     ((this.invertAxis[2])? box.getCoordinates().get(5).floatValue() : 0),
					                     box.getCoordinates().get(3).intValue(),
					                     box.getCoordinates().get(4).intValue(),
					                     box.getCoordinates().get(5).intValue(),
					                     box.getSizeAdd().floatValue(),
					                     data.getMirrorTexture()[0],
					                     data.getMirrorTexture()[1],
					                     box.getTextureOffset().get(0).intValue(),
					                     box.getTextureOffset().get(1).intValue()
					                    );
				}
			}
		}
		//pivot point is relative to parent, so 0,0,0 means "same as parent"
		//remember to invert them okay
		//pivot points are given to me in perfect form, i think
		this.model.setPivot(pivot[0], pivot[1], pivot[2]);
		this.model.setRotation(this.rotates[0], this.rotates[1], this.rotates[2]);
		this.model.setScale(scale, scale, scale);
	}
	
	private void addChild(CemModelEntry child){
		if(this.children.containsKey(child.id)){
			throw new InvalidParameterException("Child " + child.id + " already exists for parent " + this.id);
		}
		this.children.put(child.id, child);
		this.model.addChild(child.id, child.model);
	}
	
	String getId(){
		return this.id;
	}
	
	public CemModelPart getModel(){
		return this.model;
	}
	
	String getPart(){
		return this.part;
	}
	
	/** this is done because there can be a discrepancy between the translate value in the .jem and the actual part */
	void setTranslate(char axis, float requestedTranslate){
		if(CemConfigFairy.getConfig().useOldAnimations()){
			float oldTranslate;
			switch(axis){
				case 'x' -> {
					oldTranslate = this.translates[0];
					this.translates[0] = requestedTranslate;
					this.model.pivotX += (this.translates[0] - oldTranslate);
				}
				case 'y' -> {
					oldTranslate = this.translates[1];
					this.translates[1] = requestedTranslate;
					this.model.pivotY += (this.translates[1] - oldTranslate);
				}
				case 'z' -> {
					oldTranslate = this.translates[2];
					this.translates[2] = requestedTranslate;
					this.model.pivotZ += (this.translates[2] - oldTranslate);
				}
			}
		}
		else{
			float currentTranslate = this.getTranslate(axis);
			switch(axis){
				case 'x' -> {
					this.model.pivotX += requestedTranslate - currentTranslate;
					this.offsets[0] += requestedTranslate - currentTranslate;
				}
				case 'y' -> {
					this.model.pivotY += requestedTranslate - currentTranslate;
					this.offsets[1] += requestedTranslate - currentTranslate;
				}
				case 'z' -> {
					this.model.pivotZ += requestedTranslate - currentTranslate;
					this.offsets[2] += requestedTranslate - currentTranslate;
				}
			}
		}
	}
	
	public float getTranslate(char axis){
		if(CemConfigFairy.getConfig().useOldAnimations()){
			return switch(axis){
				case 'x' -> this.translates[0];
				case 'y' -> this.translates[1];
				case 'z' -> this.translates[2];
				default -> throw new IllegalStateException("Unknown axis \"" + axis + "\"");
			};
		}
		else{
			switch(axis){
				case 'x' -> {
					float value = this.gen != 0? this.translates[0] + this.offsets[0] : this.model.pivotX;
					if(this.model.getParent() != null){
						value += this.model.getParent().pivotX;
					}
					return value;
				}
				case 'y' -> {
					float value = this.gen != 0? this.translates[1] + this.offsets[1] : this.model.pivotY;
					if(this.model.getParent() != null){
						value += this.model.getParent().pivotY;
					}
					return value;
				}
				case 'z' -> {
					float value = this.gen != 0? this.translates[2] + this.offsets[2] : this.model.pivotZ;
					if(this.model.getParent() != null){
						value += this.model.getParent().pivotZ;
					}
					return value;
				}
				default -> throw new IllegalStateException("Unknown axis \"" + axis + "\"");
			}
		}
	}
	
	void setRotate(char axis, float requestedAngle){
		if(CemConfigFairy.getConfig().useOldAnimations()){
			this.model.setRotation(axis, requestedAngle);
		}
		else{
			float angle = requestedAngle;
			if(this.model.getParent() != null){
				angle -= this.model.getParent().getRotation(axis);
			}
			this.model.setRotation(axis, angle);
		}
	}
	
	Map<String, CemModelEntry> getChildren(){
		return this.children;
	}
	
}