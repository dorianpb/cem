package net.dorianpb.cem.internal.models;

import net.dorianpb.cem.internal.util.SodiumCuboidFixer;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.model.ModelPart.Quad;
import net.minecraft.util.math.Direction;

public class CemCuboid extends Cuboid{
	private final CemCuboidParams params;
	
	CemCuboid(float x,
	          float y,
	          float z,
	          float sizeX,
	          float sizeY,
	          float sizeZ,
	          float extraX,
	          float extraY,
	          float extraZ,
	          boolean mirrorU,
	          boolean mirrorV,
	          int textureWidth,
	          int textureHeight,
	          float[] uvNorth,
	          float[] uvSouth,
	          float[] uvEast,
	          float[] uvWest,
	          float[] uvUp,
	          float[] uvDown){
		super(0,
		      0,
		      mirrorU? x + sizeX : x,
		      mirrorV? y + sizeY : y,
		      z,
		      mirrorU? -sizeX : sizeX,
		      mirrorV? -sizeY : sizeY,
		      sizeZ,
		      mirrorU? -extraX : extraX,
		      mirrorV? -extraY : extraY,
		      extraZ,
		      false,
		      textureWidth,
		      textureHeight
		     );
		this.params = new CemCuboidUvParams(x,
		                                    y,
		                                    z,
		                                    sizeX,
		                                    sizeY,
		                                    sizeZ,
		                                    extraX,
		                                    extraY,
		                                    extraZ,
		                                    mirrorU,
		                                    mirrorV,
		                                    textureWidth,
		                                    textureHeight,
		                                    uvNorth,
		                                    uvSouth,
		                                    uvEast, uvWest, uvDown, uvUp
		);
		this.sides[4] = new Quad(this.sides[4].vertices, uvNorth[0], uvNorth[1], uvNorth[2], uvNorth[3], textureWidth, textureHeight, false, Direction.NORTH);
		this.sides[5] = new Quad(this.sides[5].vertices, uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3], textureWidth, textureHeight, false, Direction.SOUTH);
		this.sides[0] = new Quad(this.sides[0].vertices, uvEast[0], uvEast[1], uvEast[2], uvEast[3], textureWidth, textureHeight, false, Direction.EAST);
		this.sides[1] = new Quad(this.sides[1].vertices, uvWest[0], uvWest[1], uvWest[2], uvWest[3], textureWidth, textureHeight, false, Direction.WEST);
		this.sides[2] = new Quad(this.sides[2].vertices, uvDown[0], uvDown[1], uvDown[2], uvDown[3], textureWidth, textureHeight, false, Direction.DOWN);
		this.sides[3] = new Quad(this.sides[3].vertices, uvUp[0], uvUp[1], uvUp[2], uvUp[3], textureWidth, textureHeight, false, Direction.UP);
		if(SodiumCuboidFixer.needFix()){
			SodiumCuboidFixer.replacequad(this, uvNorth, uvSouth, uvEast, uvWest, uvUp, uvDown, textureWidth, textureHeight);
		}
	}
	
	public CemCuboid(float x,
	                 float y,
	                 float z,
	                 float sizeX,
	                 float sizeY,
	                 float sizeZ,
	                 float extraX,
	                 float extraY,
	                 float extraZ,
	                 boolean mirrorU,
	                 boolean mirrorV,
	                 int textureWidth,
	                 int textureHeight,
	                 int u,
	                 int v){
		super(u, v, x, mirrorV? y + sizeY : y, z, sizeX, mirrorV? -sizeY : sizeY, sizeZ, extraX, extraY, extraZ, mirrorU, textureWidth, textureHeight);
		this.params = new CemCuboidTexOffsetParams(x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirrorU, mirrorV, textureWidth, textureHeight, u, v);
	}
	
	CemCuboid inflate(float scale){
		return (this.params instanceof CemCuboidTexOffsetParams)
		       ? new CemCuboid(this.params.getX(),
		                       this.params.getY(),
		                       this.params.getZ(),
		                       this.params.getSizeX(),
		                       this.params.getSizeY(),
		                       this.params.getSizeZ(),
		                       scale + this.params.getExtraX(),
		                       scale + this.params.getExtraY(),
		                       scale + this.params.getExtraZ(),
		                       this.params.isMirrorU(),
		                       this.params.isMirrorV(),
		                       this.params.getTextureWidth(),
		                       this.params.getTextureHeight(),
		                       ((CemCuboidTexOffsetParams) this.params).getU(),
		                       ((CemCuboidTexOffsetParams) this.params).getV()
		)
		       : new CemCuboid(this.params.getX(),
		                       this.params.getY(),
		                       this.params.getZ(),
		                       this.params.getSizeX(),
		                       this.params.getSizeY(),
		                       this.params.getSizeZ(),
		                       scale + this.params.getExtraX(),
		                       scale + this.params.getExtraY(),
		                       scale + this.params.getExtraZ(),
		                       this.params.isMirrorU(),
		                       this.params.isMirrorV(),
		                       this.params.getTextureWidth(),
		                       this.params.getTextureHeight(),
		                       ((CemCuboidUvParams) this.params).getUvNorth(),
		                       ((CemCuboidUvParams) this.params).getUvSouth(),
		                       ((CemCuboidUvParams) this.params).getUvEast(),
		                       ((CemCuboidUvParams) this.params).getUvWest(),
		                       ((CemCuboidUvParams) this.params).getUvUp(),
		                       ((CemCuboidUvParams) this.params).getUvDown()
		       );
	}
	
	boolean isMirrorU(){
		return this.params.isMirrorU();
	}
	
	private abstract static class CemCuboidParams{
		private final float x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ;
		private final boolean mirrorU, mirrorV;
		private final int textureWidth, textureHeight;
		
		private CemCuboidParams(float x,
		                        float y,
		                        float z,
		                        float sizeX,
		                        float sizeY,
		                        float sizeZ,
		                        float extraX,
		                        float extraY,
		                        float extraZ,
		                        boolean mirrorU,
		                        boolean mirrorV,
		                        int textureWidth,
		                        int textureHeight){
			this.x = x;
			this.y = y;
			this.z = z;
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			this.sizeZ = sizeZ;
			this.extraX = extraX;
			this.extraY = extraY;
			this.extraZ = extraZ;
			this.mirrorU = mirrorU;
			this.mirrorV = mirrorV;
			this.textureWidth = textureWidth;
			this.textureHeight = textureHeight;
		}
		
		private float getX(){
			return this.x;
		}
		
		private float getY(){
			return this.y;
		}
		
		private float getZ(){
			return this.z;
		}
		
		private float getSizeX(){
			return this.sizeX;
		}
		
		private float getSizeY(){
			return this.sizeY;
		}
		
		private float getSizeZ(){
			return this.sizeZ;
		}
		
		private float getExtraX(){
			return this.extraX;
		}
		
		private float getExtraY(){
			return this.extraY;
		}
		
		private float getExtraZ(){
			return this.extraZ;
		}
		
		private boolean isMirrorU(){
			return this.mirrorU;
		}
		
		private boolean isMirrorV(){
			return this.mirrorV;
		}
		
		int getTextureWidth(){
			return this.textureWidth;
		}
		
		int getTextureHeight(){
			return this.textureHeight;
		}
	}
	
	private static final class CemCuboidTexOffsetParams extends CemCuboidParams{
		private final int u, v;
		
		private CemCuboidTexOffsetParams(float x,
		                                 float y,
		                                 float z,
		                                 float sizeX,
		                                 float sizeY,
		                                 float sizeZ,
		                                 float extraX,
		                                 float extraY,
		                                 float extraZ,
		                                 boolean mirrorU,
		                                 boolean mirrorV,
		                                 int textureWidth,
		                                 int textureHeight,
		                                 int u,
		                                 int v){
			super(x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirrorU, mirrorV, textureWidth, textureHeight);
			this.u = u;
			this.v = v;
		}
		
		private int getU(){
			return this.u;
		}
		
		private int getV(){
			return this.v;
		}
	}
	
	private static final class CemCuboidUvParams extends CemCuboidParams{
		private final float[] uvNorth, uvSouth, uvEast, uvWest, uvDown, uvUp;
		
		private CemCuboidUvParams(float x,
		                          float y,
		                          float z,
		                          float sizeX,
		                          float sizeY,
		                          float sizeZ,
		                          float extraX,
		                          float extraY,
		                          float extraZ,
		                          boolean mirrorU,
		                          boolean mirrorV,
		                          int textureWidth,
		                          int textureHeight,
		                          float[] uvNorth,
		                          float[] uvSouth,
		                          float[] uvEast,
		                          float[] uvWest,
		                          float[] uvDown,
		                          float[] uvUp){
			super(x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirrorU, mirrorV, textureWidth, textureHeight);
			this.uvNorth = uvNorth;
			this.uvSouth = uvSouth;
			this.uvEast = uvEast;
			this.uvWest = uvWest;
			this.uvDown = uvDown;
			this.uvUp = uvUp;
		}
		
		private float[] getUvNorth(){
			return this.uvNorth;
		}
		
		private float[] getUvSouth(){
			return this.uvSouth;
		}
		
		private float[] getUvEast(){
			return this.uvEast;
		}
		
		private float[] getUvWest(){
			return this.uvWest;
		}
		
		private float[] getUvDown(){
			return this.uvDown;
		}
		
		private float[] getUvUp(){
			return this.uvUp;
		}
	}
}