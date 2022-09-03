package net.dorianpb.cem.internal.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dorianpb.cem.internal.config.CemConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** Helps with internal stuff, all you need to know is that it keeps track of the renderers and files */
public class CemFairy{
	private static final Set<EntityType<? extends Entity>>           supportedEntities      = new HashSet<>();
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
	
	//file stuff
	public static Identifier transformPath(String path, Identifier location){
		//relative to current folder
		if(path.startsWith("./")){
			return new Identifier(location.getNamespace(), location.getPath().substring(0, location.getPath().lastIndexOf('/') + 1) + path.substring(2));
		}
		//go up a folder
		else if(path.startsWith("../")){
			return transformPath(path.substring(3), new Identifier(location.getNamespace(), location.getPath().substring(0, location.getPath().lastIndexOf('/'))));
		}
		//relative to "assets/dorianpb/cem"
		else if(path.startsWith("~/")){
			return new Identifier("dorianpb", "cem/" + path.substring(2));
		}
		//relative to "assets/namespace/"
		else if(path.chars().filter(ch -> ch == ':').count() == 1){
			String path2 = path.substring(path.indexOf(":") + 1);
			if(path2.startsWith("/")){
				path2 = path2.replaceFirst("/", "");
			}
			return transformPath(path2, new Identifier(path.substring(0, path.indexOf(":")), ""));
		}
		//look for file in current folder
		else{
			return new Identifier(location.getNamespace(), location.getPath().substring(0, location.getPath().lastIndexOf('/') + 1) + path);
		}
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

	//get if the pack overrides creation fix
	public static boolean useCreationFix(File source) {
		JsonObject mcMetaFile = getMcMeta(source);
		if (mcMetaFile == null) return false;
		CemFairy.getLogger().warn("McMeta Fix: " + (mcMetaFile.has("pack.model_creation_fix") ? mcMetaFile.get("pack.model_creation_fix").getAsBoolean() : CemConfig.getConfig().useTransparentParts()));
		return mcMetaFile.has("pack.model_creation_fix") ? mcMetaFile.get("pack.model_creation_fix").getAsBoolean() : CemConfig.getConfig().useTransparentParts();
	}

	//mcmeta-file
	@Nullable
	public static JsonObject getMcMeta(File source) {
		boolean continueLooping = true;
		FileReader mcmetaFile = null;
		File currentSource = source;
		while(continueLooping) {
			currentSource = currentSource.getParentFile();

			if (currentSource.getName().equals("assets")) {
				continueLooping = false;
				currentSource = currentSource.getParentFile();
				if (containsFile(currentSource, "/pack.mcmeta")) {
					try {
						mcmetaFile = new FileReader(currentSource.getPath()+"/pack.mcmeta");
					} catch (FileNotFoundException e) {
						CemFairy.getLogger().error("Get MCMeta Error: " + currentSource.getPath()+"/pack.mcmeta");
						CemFairy.getLogger().error(e.getMessage());
					}
					if (mcmetaFile != null) {
						return getGson().fromJson(mcmetaFile, JsonObject.class);
					}
				}
			}
		}
		CemFairy.getLogger().warn("Couldn't Find Mc Meta File");
		return null;
	}

	public static boolean containsFile(File directory, String contains) {
		return new File(directory.getPath()+contains).exists();
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