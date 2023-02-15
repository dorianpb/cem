package net.dorianpb.cem.internal.util;

import net.dorianpb.cem.internal.models.CemModelEntry.CemCuboid;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum SodiumCuboidFixer{
	;
	
	private static Method copymethod;
	
	private static Field quadsfield;
	
	private static Constructor<?> newquad;
	
	private static Field positionsfield;
	
	private static boolean success;
	
	static{
		try{
			Class<?> modelcuboidaccessor = Class.forName("me.jellysquid.mods.sodium.client.model.ModelCuboidAccessor");
			Class<?> modelcuboidquad = Class.forName("me.jellysquid.mods.sodium.client.render.immediate.model.ModelCuboid$Quad");
			copymethod = modelcuboidaccessor.getDeclaredMethod("copy");
			quadsfield = Class.forName("me.jellysquid.mods.sodium.client.render.immediate.model.ModelCuboid").getField("quads");
			newquad = modelcuboidquad.getConstructor(Vector3f[].class, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Boolean.TYPE, Direction.class);
			positionsfield = modelcuboidquad.getField("positions");
			
			success = true;
		} catch(Exception ignored){
		}
	}
	
	public static boolean needFix(){
		return success;
	}
	
	public static void replacequad(CemCuboid cuboid,
	                               float[] uvNorth,
	                               float[] uvSouth,
	                               float[] uvEast,
	                               float[] uvWest,
	                               float[] uvUp,
	                               float[] uvDown,
	                               int textureWidth,
	                               int textureHeight){
		try{
			Object modelCuboid = copymethod.invoke(cuboid);
			Object[] quads = (Object[]) quadsfield.get(modelCuboid);
			
			quads[4] = newquad.newInstance(positionsfield.get(quads[4]), uvNorth[0], uvNorth[1], uvNorth[2], uvNorth[3], textureWidth, textureHeight, false, Direction.NORTH);
			quads[5] = newquad.newInstance(positionsfield.get(quads[5]), uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3], textureWidth, textureHeight, false, Direction.SOUTH);
			quads[0] = newquad.newInstance(positionsfield.get(quads[0]), uvEast[0], uvEast[1], uvEast[2], uvEast[3], textureWidth, textureHeight, false, Direction.EAST);
			quads[1] = newquad.newInstance(positionsfield.get(quads[1]), uvWest[0], uvWest[1], uvWest[2], uvWest[3], textureWidth, textureHeight, false, Direction.WEST);
			quads[2] = newquad.newInstance(positionsfield.get(quads[2]), uvDown[0], uvDown[1], uvDown[2], uvDown[3], textureWidth, textureHeight, false, Direction.DOWN);
			quads[3] = newquad.newInstance(positionsfield.get(quads[3]), uvUp[0], uvUp[1], uvUp[2], uvUp[3], textureWidth, textureHeight, false, Direction.UP);
			
		} catch(IllegalAccessException | InvocationTargetException | InstantiationException e){
			throw new RuntimeException(e);
		}
		
	}
	
}
