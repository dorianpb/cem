package net.dorianpb.cem.internal.models;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class CemModelPart extends ModelPart{
	private final float[]      scale;
	private final float[]      rotation;
	private final int          textureWidth;
	private final int          textureHeight;
	private       CemModelPart parent;
	
	private Object identifier;
	
	CemModelPart(){
		this(0, 0, new ArrayList<>());
	}
	
	CemModelPart(int textureWidth, int textureHeight, List<Cuboid> cuboids){
		super(cuboids, new HashMap<>());
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.scale = new float[]{1, 1, 1};
		this.rotation = new float[]{0, 0, 0};
	}
	
	CemModelPart(int textureWidth, int textureHeight){
		this(textureWidth, textureHeight, new ArrayList<>());
	}
	
	public static CemModelPart of(ModelPart modelPart){
		CemModelPart newPart;
		if(modelPart instanceof CemModelPart){
			newPart = new CemModelPart(((CemModelPart) modelPart).textureWidth, ((CemModelPart) modelPart).textureHeight);
		}
		else{
			newPart = new CemModelPart();
		}
		
		for(Entry<String, ModelPart> entry : modelPart.children.entrySet()){
			newPart.children.put(entry.getKey(), of(entry.getValue()));
		}
		newPart.cuboids.addAll(modelPart.cuboids);
		newPart.copyTransform(modelPart);
		newPart.visible = modelPart.visible;
		return newPart;
	}
	
	void setScale(float scaleX, float scaleY, float scaleZ){
		this.scale[0] = scaleX;
		this.scale[1] = scaleY;
		this.scale[2] = scaleZ;
	}
	
	void setRotation(float rotX, float rotY, float rotZ){
		this.rotation[0] = rotX;
		this.rotation[1] = rotY;
		this.rotation[2] = rotZ;
	}
	
	void setScale(char axis, float scale){
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
			case 'x' -> this.scale[0];
			case 'y' -> this.scale[1];
			case 'z' -> this.scale[2];
			default -> throw new IllegalStateException("Unknown axis \"" + axis + "\"");
		};
	}
	
	public float getRotation(char axis){
		switch(axis){
			case 'x' -> {
				float angle = this.pitch;
				if(this.parent != null){
					angle += this.parent.pitch;
				}
				return angle + this.rotation[0];
			}
			case 'y' -> {
				float angle = this.yaw;
				if(this.parent != null){
					angle += this.parent.yaw;
				}
				return angle + this.rotation[1];
			}
			case 'z' -> {
				float angle = this.roll;
				if(this.parent != null){
					angle += this.parent.roll;
				}
				return angle + this.rotation[2];
			}
			default -> throw new IllegalStateException("Unknown axis \"" + axis + "\"");
		}
	}
	
	void addChild(String name, ModelPart modelPart){
		this.children.put(name, modelPart);
	}
	
	void inflate(float scale){
		throw new RuntimeException("i should probably fix this at some point");
		//		this.cuboids.replaceAll(cuboid -> ((CemCuboid) cuboid).inflate(scale));
		//todo fix
		//		for(ModelPart child : this.children.values()){
		//			((CemModelPart) child).inflate(scale);
		//		}
	}
	
	@Override
	public void copyTransform(ModelPart part){
		super.copyTransform(part);
		if(part instanceof CemModelPart){
			for(int i = 0; i < 3; i++){
				this.rotation[i] = ((CemModelPart) part).rotation[i];
				this.scale[i] = ((CemModelPart) part).scale[i];
			}
			if(this.children.keySet().equals(part.children.keySet())){
				for(Entry<String, ModelPart> entry : part.children.entrySet()){
					this.children.get(entry.getKey()).copyTransform(entry.getValue());
				}
			}
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
		matrices.scale(this.scale[0], this.scale[1], this.scale[2]);
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
	
	CemModelPart getParent(){
		return this.parent;
	}
	
	void setParent(CemModelPart modelPart){
		this.parent = modelPart;
	}
	
	public Object getIdentifier(){
		return this.identifier;
	}
	
	void setIdentifier(Object obj){
		this.identifier = obj;
	}
	
}