package net.dorianpb.cem.internal;

import com.google.gson.internal.LinkedTreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.InvalidParameterException;
import java.util.HashMap;

/** Helps with internal stuff, all you need to know is that it keeps track of the renderers and files */
public class CemFairy{
	private static HashMap<String, CemRenderer> renderers;
	private static HashMap<String, JpmFile> jpmFiles;
	private static Logger LOGGER;
	
	//logger
	static Logger getLogger(){
		if(LOGGER == null){
			LOGGER = LogManager.getLogger("Custom Entity Models");
		}
		return LOGGER;
	}
	
	//renderer stuff
	
	/**
	 * Registers a new CEM renderer with the given ID
	 * @param renderer cemRenderer to add
	 * @param id       Name of entity the cemRenderer renders
	 */
	public static void addRenderer(CemRenderer renderer, String id){
		if(getRenderers().containsKey(id)){
			throw new InvalidParameterException("There is already a renderer assigned to entity " + id);
		}
		getRenderers().put(id, renderer);
	}
	
	private static HashMap<String, CemRenderer> getRenderers(){
		if(renderers == null){
			renderers = new HashMap<>();
		}
		return renderers;
	}
	
	static CemRenderer getRendererFromId(String id){
		String name = id.substring(id.lastIndexOf("/") + 1, id.lastIndexOf(".jem"));
		if(!getRenderers().containsKey(name)){
			throw new NullPointerException("No cem renderer is registered for entity " + name);
		}
		return getRenderers().get(name);
	}
	
	static void restoreModels(){
		for(CemRenderer renderer : getRenderers().values()){
			renderer.restoreModel();
		}
	}
	
	//file stuff
	static String transformPath(String path, String location){
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
	
	//jpm stuff
	static void addJpmFile(LinkedTreeMap<String, Object> file, String path){
		getJpmFiles().put(path, new JpmFile(file));
	}
	
	private static HashMap<String, JpmFile> getJpmFiles(){
		if(jpmFiles == null){
			jpmFiles = new HashMap<>();
		}
		return jpmFiles;
	}
	
	static JpmFile loadJpmFile(String path){
		return getJpmFiles().get(path);
	}
	
}