package net.dorianpb.cem.internal.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.internal.LinkedTreeMap;
import net.minecraft.client.model.ModelTransform;

import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
public enum OptifineFixes{
	;
	private static final Map<String, BiMap<String, String>>       partnames      = new HashMap<>();
	private static final Map<String, Map<String, List<String>>>   parttrees      = new LinkedHashMap<>();
	private static final Map<String, Map<String, ModelTransform>> transformfixes = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public static void accept(Object key, LinkedTreeMap<String, Object> json){
		String entity = key.toString();
		try{
			if(json.containsKey("partnames")){
				partnames.put(entity, parsePartNames((LinkedTreeMap<String, String>) json.get("partnames")));
			}
			
			if(json.containsKey("parttree")){
				parttrees.put(entity, parsePartTree((LinkedTreeMap<String, LinkedTreeMap>) json.get("parttree")));
			}
			
			if(json.containsKey("modelfixes")){
				transformfixes.put(entity, parseModelFixes((LinkedTreeMap<String, ArrayList<Double>>) json.get("modelfixes")));
			}
			
		} catch(RuntimeException e){
			CemFairy.getLogger().error(e);
		}
		
	}
	
	private static BiMap<String, String> parsePartNames(LinkedTreeMap<String, String> json){
		return HashBiMap.create(json);
	}
	
	private static Map<String, List<String>> parsePartTree(LinkedTreeMap<String, LinkedTreeMap> json){
		Map<String, List<String>> parttree = new LinkedHashMap<>();
		for(Entry<String, LinkedTreeMap> entry : json.entrySet()){
			parseTree(parttree, entry.getKey(), entry.getValue());
		}
		return parttree;
	}
	
	private static void parseTree(Map<String, List<String>> map, String parent, LinkedTreeMap<String, LinkedTreeMap> json){
		for(Entry<String, LinkedTreeMap> entry : json.entrySet()){
			if(!map.containsKey(parent)){
				map.put(parent, new ArrayList<>());
			}
			
			map.get(parent).add(entry.getKey());
			
			parseTree(map, entry.getKey(), entry.getValue());
		}
	}
	
	private static Map<String, ModelTransform> parseModelFixes(LinkedTreeMap<String, ArrayList<Double>> json){
		Map<String, ModelTransform> map = new HashMap<>();
		for(Entry<String, ArrayList<Double>> entry : json.entrySet()){
			map.put(entry.getKey(), ModelTransform.pivot(entry.getValue().get(0).floatValue(), entry.getValue().get(1).floatValue(), entry.getValue().get(2).floatValue()));
		}
		return map;
	}
	
	public static BiMap<String, String> getPartNames(Object entity){
		return partnames.get(entity.toString());
	}
	
	public static Map<String, ModelTransform> getModelFixes(Object entity){
		return transformfixes.get(entity.toString());
	}
	
	public static boolean hasFixesFor(Object entity){
		String str = entity.toString();
		return partnames.containsKey(str) || parttrees.containsKey(str) || transformfixes.containsKey(str);
	}
}