package net.dorianpb.cem.internal.util;

import com.google.gson.Gson;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/** Helps with internal stuff, all you need to know is that it keeps track of the renderers and files */
public class CemFairy{
	private static final Set<EntityType<? extends Entity>>           supportedEntities      = new HashSet<>();
	private static final Set<BlockEntityType<? extends BlockEntity>> supportedBlockEntities = new HashSet<>();
	private static final Set<String>                                 supportedOthers        = new HashSet<>();
	private static final Logger                                      LOGGER                 = LogManager.getLogger("Custom Entity Models");
	private static final Gson                                        GSON                   = new Gson();
	
	//logger
	public static Logger getLogger(){
		return LOGGER;
	}
	
	//Gson
	public static Gson getGson(){
		return GSON;
	}
	
	//renderer stuff
	public static void addSupport(EntityType<? extends Entity> entityType){
		supportedEntities.add(entityType);
	}
	
	public static void addSupport(BlockEntityType<? extends BlockEntity> entityType){
		supportedBlockEntities.add(entityType);
	}
	
	public static void addSupport(String entityType){
		supportedOthers.add(entityType);
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
	public static String transformPath(String path, String location){
		String shiny = "";
		//look for file in current folder
		if(path.chars().filter(ch -> ch == '/').count() == 0){
			shiny = location.substring(0, location.lastIndexOf('/') + 1) + path;
		}
		//relative to current folder
		else if(path.startsWith("./")){
			shiny = location.substring(0, location.lastIndexOf('/') + 1) + path.substring(2);
		}
		//go up a folder
		else if(path.startsWith("../")){
			shiny = transformPath(path.substring(3), location.substring(0, location.lastIndexOf('/')));
		}
		//relative to "assets/dorianpb/cem"
		else if(path.startsWith("~/")){
			shiny = "cem/" + path.substring(2);
		}
		return shiny;
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
	
}