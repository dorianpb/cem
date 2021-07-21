package net.dorianpb.cem.internal.models;

import net.dorianpb.cem.internal.file.JemFile.JemModel;
import net.dorianpb.cem.internal.file.JpmFile;
import net.dorianpb.cem.internal.file.JpmFile.JpmBox;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CemModelEntry{
	private final String                                    id;
	private final String                                    part;
	private final HashMap<ArrayList<String>, CemModelEntry> children;
	private final CemModelPart                              model;
	private final float[]                                   translates;
	private final float[]                                   rotates;
	private final boolean[]                                 invertAxis;
	
	
	CemModelEntry(JemModel file, int textureWidth, int textureHeight){
		this(file.getPart(), file.getModelDef(), new float[]{0, 24, 0}, 0, file.getScale().floatValue(), textureWidth, textureHeight);
		if(file.getAnimations().size() > 0){
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
		this.translates = new float[]{(data.getTranslate().get(0).floatValue()) * (this.invertAxis[0]? -1 : 1),
		                              (data.getTranslate().get(1).floatValue()) * (this.invertAxis[1]? -1 : 1),
		                              (data.getTranslate().get(2).floatValue()) * (this.invertAxis[2]? -1 : 1),
		                              };
		this.rotates = new float[]{data.getRotate().get(0).floatValue() * ((this.invertAxis[0])? 1 : -1),
		                           data.getRotate().get(1).floatValue() * ((this.invertAxis[1])? 1 : -1),
		                           data.getRotate().get(2).floatValue() * ((this.invertAxis[2])? 1 : -1),
		                           };
		
		this.model = new CemModelPart(textureWidth, textureHeight);
		
		this.initmodel(data, parents, gen, scale);
		//CHILD INIT
		if(data.getSubmodels() != null){
			for(JpmFile submodel : data.getSubmodels()){
				float childZ = (gen == 0)? ((data.getTranslate().get(2).floatValue()) * (this.invertAxis[2]? -1 : 1)) : 0;
				float childY = (gen == 0)? ((data.getTranslate().get(1).floatValue()) * (this.invertAxis[1]? -1 : 1)) : 0;
				float childX = (gen == 0)? ((data.getTranslate().get(0).floatValue()) * (this.invertAxis[0]? -1 : 1)) : 0;
				this.addChild(new CemModelEntry(null, submodel, new float[]{childX, childY, childZ}, gen + 1, 1, textureWidth, textureHeight));
			}
		}
		//END CHILD INIT
	}
	
	private void initmodel(JpmFile data, float[] parents, int gen, float scale){
		float[] pivot = new float[]{((gen == 0)
		                             ? (parents[0] - (data.getTranslate().get(0).floatValue() * (this.invertAxis[0]? -1 : 1)))
		                             : ((gen == 1)
		                                ? (parents[0] + (data.getTranslate().get(0).floatValue() * (this.invertAxis[0]? -1 : 1)))
		                                : data.getTranslate().get(0).floatValue() * (this.invertAxis[1]? -1 : 1))),
		                            ((gen == 0)
		                             ? (parents[1] - (data.getTranslate().get(1).floatValue() * (this.invertAxis[1]? -1 : 1)))
		                             : ((gen == 1)
		                                ? (parents[1] + (data.getTranslate().get(1).floatValue() * (this.invertAxis[1]? -1 : 1)))
		                                : data.getTranslate().get(1).floatValue() * (this.invertAxis[1]? -1 : 1))),
		                            ((gen == 0)
		                             ? (parents[2] - (data.getTranslate().get(2).floatValue() * (this.invertAxis[2]? -1 : 1)))
		                             : ((gen == 1)
		                                ? (parents[2] + (data.getTranslate().get(2).floatValue() * (this.invertAxis[2]? -1 : 1)))
		                                : data.getTranslate().get(2).floatValue() * (this.invertAxis[2]? -1 : 1))),
		                            };
		///MUST SUBTRACT FROM PARENT FOR GEN1
		float[] translate = new float[]{(data.getTranslate().get(0).floatValue()), (data.getTranslate().get(1).floatValue()), (data.getTranslate().get(2).floatValue()),
		                                };
		if(data.getBoxes() != null){
			for(JpmBox box : data.getBoxes()){
				//apply translates first, then ?invert pos, then ?subtract pos by size so that it is drawn correctly
				//top level model pivots need to translated up by 24, then 1st gen children need to work off of that rather than the translate values provided by the jpmFile
				//only top level models need translates applied, others are relative to parent (even gen1)
				this.model.addCuboid(((box.getCoordinates().get(0).floatValue() + ((gen == 0)? translate[0] : 0)) * ((this.invertAxis[0])? -1 : 1)) -
				                     ((this.invertAxis[0])? box.getCoordinates().get(3).floatValue() : 0),
				
				                     ((box.getCoordinates().get(1).floatValue() + ((gen == 0)? translate[1] : 0)) * ((this.invertAxis[1])? -1 : 1)) -
				                     ((this.invertAxis[1])? box.getCoordinates().get(4).floatValue() : 0),
				
				                     ((box.getCoordinates().get(2).floatValue() + ((gen == 0)? translate[2] : 0)) * ((this.invertAxis[2])? -1 : 1)) -
				                     ((this.invertAxis[2])? box.getCoordinates().get(5).floatValue() : 0),
				                     box.getCoordinates().get(3).intValue(),
				                     box.getCoordinates().get(4).intValue(),
				                     box.getCoordinates().get(5).intValue(),
				                     box.getSizeAdd().floatValue(),
				                     box.getTextureOffset().get(0).intValue(),
				                     box.getTextureOffset().get(1).intValue(),
				                     data.getMirrorTexture()[0]
				                    );
			}
		}
		//pivot point is relative to parent, so 0,0,0 means "same as parent"
		//remember to invert them okay
		//pivot points are given to me in perfect form, i think
		this.model.setPivot(pivot[0], pivot[1], pivot[2]);
		this.model.setRotation(rotates[0], rotates[1], rotates[2]);
		this.model.setScale(scale, scale, scale);
	}
	
	private void addChild(CemModelEntry child){
		ArrayList<String> key = new ArrayList<>(Collections.singletonList(child.getId()));
		if(this.children.containsKey(key)){
			throw new InvalidParameterException("Child " + key + " already exists for parent " + this.getId());
		}
		this.children.put(key, child);
		for(ArrayList<String> refs : child.children.keySet()){
			CemModelEntry val = child.children.get(refs);
			refs.add(0, child.getId());
			this.children.put(refs, val);
		}
		this.model.addChild(child.getId(), child.getModel());
	}
	
	String getId(){
		return id;
	}
	
	public CemModelPart getModel(){
		return model;
	}
	
	String getPart(){
		return part;
	}
	
	public float getTranslate(char axis){
		return switch(axis){
			case 'x' -> this.translates[0];
			case 'y' -> this.translates[1];
			case 'z' -> this.translates[2];
			default -> throw new IllegalStateException("Unknown axis \"" + axis + "\"");
		};
	}
	
	/** this is done because there can be a discrepancy between the translate value in the .jem and the actual part */
	void setTranslate(char axis, float translate){
		float oldTranslate;
		switch(axis){
			case 'x' -> {
				oldTranslate = this.translates[0];
				this.translates[0] = translate;
				this.getModel().pivotX += (this.translates[0] - oldTranslate);
			}
			case 'y' -> {
				oldTranslate = this.translates[1];
				this.translates[1] = translate;
				this.getModel().pivotY += (this.translates[1] - oldTranslate);
			}
			case 'z' -> {
				oldTranslate = this.translates[2];
				this.translates[2] = translate;
				this.getModel().pivotZ += (this.translates[2] - oldTranslate);
			}
			default -> throw new IllegalStateException("Unknown axis \"" + axis + "\"");
		}
	}
	
	HashMap<ArrayList<String>, CemModelEntry> getChildren(){
		return this.children;
	}
	
	public static class CemModelPart extends ModelPart{
		private final float[]                 scale;
		private final float[]                 rotation;
		private final int                     textureWidth;
		private final int                     textureHeight;
		private       TransparentCemModelPart parent;
		
		public CemModelPart(){
			this(0, 0);
		}
		
		public CemModelPart(int textureWidth, int textureHeight){
			super(new ArrayList<>(), new HashMap<>());
			this.textureWidth = textureWidth;
			this.textureHeight = textureHeight;
			this.scale = new float[]{1, 1, 1};
			this.rotation = new float[]{0, 0, 0};
		}
		
		public static CemModelPart of(ModelPart modelPart){
			CemModelPart yeet = new CemModelPart();
			for(String key : modelPart.children.keySet()){
				yeet.children.put(key, of(modelPart.children.get(key)));
			}
			yeet.cuboids.addAll(modelPart.cuboids);
			yeet.copyTransform(modelPart);
			yeet.visible = modelPart.visible;
			return yeet;
		}
		
		public void addCuboid(float x, float y, float z, int sizeX, int sizeY, int sizeZ, float extra, int textureOffsetU, int textureOffsetV, boolean mirror){
			this.cuboids.add(new CemCuboid(new CemCuboid.CemCuboidParams(textureOffsetU,
			                                                             textureOffsetV,
			                                                             x,
			                                                             y,
			                                                             z,
			                                                             (float) sizeX,
			                                                             (float) sizeY,
			                                                             (float) sizeZ,
			                                                             extra,
			                                                             extra,
			                                                             extra,
			                                                             mirror,
			                                                             this.textureWidth,
			                                                             this.textureHeight
			)));
		}
		
		public void setScale(float scaleX, float scaleY, float scaleZ){
			this.scale[0] = scaleX;
			this.scale[1] = scaleY;
			this.scale[2] = scaleZ;
		}
		
		public void setRotation(float rotX, float rotY, float rotZ){
			this.rotation[0] = rotX;
			this.rotation[1] = rotY;
			this.rotation[2] = rotZ;
		}
		
		public void setScale(char axis, float scale){
			switch(axis){
				case 'x' -> this.scale[0] = scale;
				case 'y' -> this.scale[1] = scale;
				case 'z' -> this.scale[2] = scale;
				default -> throw new IllegalStateException("Unknown axis \"" + axis + "\"");
			}
		}
		
		public void setRotation(char axis, float rot){
			switch(axis){
				case 'x' -> {
					this.rotation[0] = rot;
					this.pitch = 0;
				}
				case 'y' -> {
					this.rotation[1] = rot;
					this.yaw = 0;
				}
				case 'z' -> {
					this.rotation[2] = rot;
					this.roll = 0;
				}
				default -> throw new IllegalStateException("Unknown axis \"" + axis + "\"");
			}
		}
		
		public float getScale(char axis){
			return switch(axis){
				case 'x' -> scale[0];
				case 'y' -> scale[1];
				case 'z' -> scale[2];
				default -> throw new IllegalStateException("Unknown axis \"" + axis + "\"");
			};
		}
		
		public float getRotation(char axis){
			return switch(axis){
				case 'x' -> ((this.parent == null)? this.pitch : parent.pitch) + this.rotation[0];
				case 'y' -> ((this.parent == null)? this.yaw : parent.yaw) + this.rotation[1];
				case 'z' -> ((this.parent == null)? this.roll : parent.roll) + this.rotation[2];
				default -> throw new IllegalStateException("Unknown axis \"" + axis + "\"");
			};
		}
		
		public void addChild(String name, ModelPart modelPart){
			this.children.put(name, modelPart);
		}
		
		public void inflate(float scale){
			this.cuboids.replaceAll(cuboid -> ((CemCuboid) cuboid).inflate(scale));
			for(ModelPart child : this.children.values()){
				((CemModelPart) child).inflate(scale);
			}
			
		}
		
		@Override
		public ModelPart getChild(String name){
			ModelPart child = this.children.get(name);
			if(child == null){
				this.children.put(name, new CemModelPart());
				return this.getChild(name);
			}
			else{
				return super.getChild(name);
			}
		}
		
		@Override
		public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha){
			matrices.scale(scale[0], scale[1], scale[2]);
			super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
		}
		
		@Override
		public void rotate(MatrixStack matrix){
			this.pitch += this.rotation[0];
			this.yaw += this.rotation[1];
			this.roll += this.rotation[2];
			super.rotate(matrix);
			this.pitch -= this.rotation[0];
			this.yaw -= this.rotation[1];
			this.roll -= this.rotation[2];
		}
		
		private void setParent(TransparentCemModelPart transparentCemModelPart){
			this.parent = transparentCemModelPart;
		}
	}
	
	public static class TransparentCemModelPart extends CemModelPart{
		private final CemModelPart part;
		
		public TransparentCemModelPart(ModelPart part, ModelTransform modelTransform){
			super();
			if(!(part instanceof CemModelPart)){
				this.part = CemModelPart.of(part);
			}
			else{
				this.part = (CemModelPart) part;
			}
			this.setTransform(modelTransform);
			addChild("my_precious", part);
			this.part.pivotX = (pivotX - part.pivotX) * -1;
			this.part.pivotY = (pivotY - part.pivotY) * -1;
			this.part.pivotZ = (pivotZ - part.pivotZ) * -1;
			this.part.setParent(this);
		}
		
		@Override
		public ModelPart getChild(String name){
			return part.getChild(name);
		}
		
		@Override
		public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha){
			var rotations = this.getTransform();
			this.part.pitch = rotations.pitch;
			this.part.yaw = rotations.yaw;
			this.part.roll = rotations.roll;
			this.pitch = 0;
			this.yaw = 0;
			this.roll = 0;
			super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
			this.setTransform(rotations);
			
		}
	}
	
	public static class CemCuboid extends Cuboid{
		private final CemCuboidParams params;
		
		public CemCuboid(CemCuboidParams cemCuboidParams){
			super(cemCuboidParams.getU(), cemCuboidParams.getV(), cemCuboidParams.getX(), cemCuboidParams.getY(),
			      cemCuboidParams.getZ(),
			      cemCuboidParams.getSizeX(),
			      cemCuboidParams.getSizeY(),
			      cemCuboidParams.getSizeZ(),
			      cemCuboidParams.getExtraX(),
			      cemCuboidParams.getExtraY(),
			      cemCuboidParams.getExtraZ(),
			      cemCuboidParams.isMirror(),
			      cemCuboidParams.getTextureWidth(),
			      cemCuboidParams.getTextureHeight()
			     );
			this.params = cemCuboidParams;
		}
		
		public CemCuboid inflate(float scale){
			return new CemCuboid(new CemCuboidParams(this.params.getU(),
			                                         this.params.getV(),
			                                         this.params.getX(),
			                                         this.params.getY(),
			                                         this.params.getZ(),
			                                         this.params.getSizeX(),
			                                         this.params.getSizeY(),
			                                         this.params.getSizeZ(),
			                                         scale + this.params.getExtraX(),
			                                         scale + this.params.getExtraY(),
			                                         scale + this.params.getExtraZ(),
			                                         this.params.isMirror(),
			                                         this.params.getTextureWidth(),
			                                         this.params.getTextureHeight()
			));
		}
		
		public record CemCuboidParams(int u,
		                              int v,
		                              float x,
		                              float y,
		                              float z,
		                              float sizeX,
		                              float sizeY,
		                              float sizeZ,
		                              float extraX,
		                              float extraY,
		                              float extraZ,
		                              boolean mirror,
		                              float textureWidth,
		                              float textureHeight){
			
			private int getU(){
				return u;
			}
			
			private int getV(){
				return v;
			}
			
			private float getX(){
				return x;
			}
			
			private float getY(){
				return y;
			}
			
			private float getZ(){
				return z;
			}
			
			private float getSizeX(){
				return sizeX;
			}
			
			private float getSizeY(){
				return sizeY;
			}
			
			private float getSizeZ(){
				return sizeZ;
			}
			
			private float getExtraX(){
				return extraX;
			}
			
			private float getExtraY(){
				return extraY;
			}
			
			private float getExtraZ(){
				return extraZ;
			}
			
			private boolean isMirror(){
				return mirror;
			}
			
			private float getTextureWidth(){
				return textureWidth;
			}
			
			private float getTextureHeight(){
				return textureHeight;
			}
		}
	}
}