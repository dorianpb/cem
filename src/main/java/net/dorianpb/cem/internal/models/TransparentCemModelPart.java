package net.dorianpb.cem.internal.models;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public final class TransparentCemModelPart extends CemModelPart{
	private final CemModelPart part;
	
	TransparentCemModelPart(ModelPart part, ModelTransform fakeTransform, ModelTransform realTransform){
		if(part instanceof CemModelPart){
			this.part = (CemModelPart) part;
		}
		else{
			this.part = CemModelPart.of(part);
		}
		this.setTransform(realTransform);
		addChild("my_precious", part);
		this.part.pivotX = part.pivotX - fakeTransform.pivotX;
		this.part.pivotY = part.pivotY - fakeTransform.pivotY;
		this.part.pivotZ = part.pivotZ - fakeTransform.pivotZ;
		this.part.setParent(this);
	}
	
	private TransparentCemModelPart(CemModelPart part){
		this.part = part;
		addChild("my_precious", part);
	}
	
	public static TransparentCemModelPart of(TransparentCemModelPart modelPart){
		TransparentCemModelPart yeet = new TransparentCemModelPart(CemModelPart.of(modelPart.part));
		yeet.copyTransform(modelPart);
		yeet.visible = modelPart.visible;
		return yeet;
	}
	
	@Override
	public ModelPart getChild(String name){
		return this.part.getChild(name);
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
	
	@Override
	public Object getIdentifier(){
		return this.part.getIdentifier();
	}
	
	public void rotateInnerPart(MatrixStack matrix){
		this.rotateInnerPart(matrix, 0, 0, 0);
	}
	
	public void rotateInnerPart(MatrixStack matrix, float xOffset, float yOffset, float zOffset){
		this.part.pivotX += this.pivotX + xOffset;
		this.part.pivotY += this.pivotY + yOffset;
		this.part.pivotZ += this.pivotZ + zOffset;
		this.part.rotate(matrix);
		this.part.pivotX -= this.pivotX + xOffset;
		this.part.pivotY -= this.pivotY + yOffset;
		this.part.pivotZ -= this.pivotZ + zOffset;
	}
}