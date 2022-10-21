package net.dorianpb.cem.internal.util;

import com.google.gson.Gson;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/** Helps with internal stuff, all you need to know is that it keeps track of the renderers and files */
public class CemFairy{
	private static final Set<EntityType<? extends Entity>> supportedEntities                = new HashSet<>();
	private static final Set<BlockEntityType<? extends BlockEntity>> supportedBlockEntities = new HashSet<>();
	private static final Set<String>                                 supportedOthers        = new HashSet<>();
	private static final Logger                                      LOGGER                 = LogManager.getLogger("Custom Entity Models");
	private static final Gson                                        GSON                   = new Gson();
	
	//Gson
	public static Gson getGson(){
		return GSON;
	}
	
	//renderer stuff
	public static boolean addSupport(EntityType<? extends Entity> entityType){
		return supportedEntities.add(entityType);
	}
	
	public static boolean addSupport(BlockEntityType<? extends BlockEntity> entityType){
		return supportedBlockEntities.add(entityType);
	}
	
	public static boolean addSupport(String entityType){
		return supportedOthers.add(entityType);
	}
	
	public static boolean isUnsupported(EntityType<? extends Entity> entityType){
		return !supportedEntities.contains(entityType);
	}
	
	public static boolean isUnsupported(BlockEntityType<? extends BlockEntity> entityType){
		return !supportedBlockEntities.contains(entityType);
	}
	
	public static boolean isUnsupported(String entityType){
		return !supportedOthers.contains(entityType);
	}
	
	public static String getEntityNameFromId(Identifier identifier){
		String id = identifier.toString();
		return id.substring(id.lastIndexOf("/") + 1, id.lastIndexOf(".jem"));
	}

	public static Identifier transformPath(String path, Identifier location) {
		var pathChunks = new LinkedList<>(Arrays.asList(path.split("/")));
		var firstChunk = pathChunks.get(0);

		// Remove the file name from the location path or trailing slash
		var locationPath = new LinkedList<>(Arrays.asList(location.getPath().split("/")));
		var lastLocationChunk = locationPath.get(locationPath.size() - 1);
		if (lastLocationChunk.equals("") || lastLocationChunk.matches(".+\\..+")) {
			locationPath.remove(locationPath.size() - 1);
			location = new Identifier(location.getNamespace(), String.join("/", locationPath));
		}

		// Uses a explicit namespace
		if (pathChunks.get(0).contains(":")) {
			var nsAndChunk = firstChunk.split(":");
			location = new Identifier(nsAndChunk[0], "");
			firstChunk = nsAndChunk[1];
			pathChunks.set(0, firstChunk);
		}

		// Specifies an absolute path
		if (firstChunk.equals("")) {
			location = new Identifier(location.getNamespace(), "");
			pathChunks.removeFirst();
		}

		// Specifies a relative path
		else if (firstChunk.equals(".")) {
			pathChunks.removeFirst();
		}

		// Specifies an optifine folder or dorianpb folder path
		else if (firstChunk.equals("~")) {
			pathChunks.removeFirst();
			location = location.getNamespace().equals("dorianpb")
				? new Identifier(location.getNamespace(), "cem")
				: new Identifier("minecraft", "optifine");
		}

		// Move the location path up for each ".." chunk at the beginning of the path
		else if (firstChunk.equals("..")) {
			var basePathChunks = new LinkedList<>(Arrays.asList(location.getPath().split("/")));
			while (pathChunks.get(0).equals("..")) {
				if (basePathChunks.size() == 0) { break; }
				pathChunks.removeFirst();
				basePathChunks.remove(basePathChunks.size() - 1);
			}
			location = new Identifier(location.getNamespace(), String.join("/", basePathChunks));

		// Deal with Optifine's rather strange pathing behaviour
		// When Optifine loads a texture path does not contain "/", it loads relative to the file.
		} else if (location.getNamespace().equals("minecraft") && path.contains("/")) {
			location = new Identifier(location.getNamespace(), "");
		}

		// Finalize the transformed path as a new Identifier
		if (!location.getPath().equals("")) { pathChunks.addFirst(location.getPath()); }
		return new Identifier(location.getNamespace(), String.join("/", pathChunks));
	}
	
	public static void postReadError(Exception exception, Identifier id){
		CemFairy.getLogger().error("Error parsing " + id + ":");
		String message = exception.getMessage();
		CemFairy.getLogger().error(exception);
		if(message == null || message.trim().equals("")){
			CemFairy.getLogger().error(exception.getStackTrace()[0]);
			CemFairy.getLogger().error(exception.getStackTrace()[1]);
			CemFairy.getLogger().error(exception.getStackTrace()[2]);
		}
	}
	
	//logger
	public static Logger getLogger(){
		return LOGGER;
	}
	
	//json
	public static Float JSONparseFloat(Object obj){
		String val = JSONparseString(obj);
		return val == null? null : Float.valueOf(val);
	}
	
	public static String JSONparseString(Object obj){
		return obj == null? null : obj.toString();
	}
	
	public static Boolean JSONparseBool(Object obj){
		String val = JSONparseString(obj);
		return val == null? null : Boolean.valueOf(val);
	}
	
	public static ArrayList<Double> JSONparseDoubleList(Object object){
		try{
			@SuppressWarnings("unchecked")
			ArrayList<Object> obj = (ArrayList<Object>) object;
			ArrayList<Double> val = new ArrayList<>();
			if(obj != null){
				obj.forEach((value) -> val.add(JSONparseDouble(value)));
			}
			return (val.size() == 0)? null : val;
		} catch(Exception e){
			return null;
		}
	}
	
	public static Double JSONparseDouble(Object obj){
		String val = JSONparseString(obj);
		return val == null? null : Double.valueOf(val);
	}
	
}