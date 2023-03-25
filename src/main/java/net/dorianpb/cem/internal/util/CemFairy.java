package net.dorianpb.cem.internal.util;

import com.google.gson.Gson;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Helps with internal stuff, all you need to know is that it keeps track of the renderers and files
 */
public enum CemFairy {
    ;
    private static final Logger  LOGGER = LogManager.getLogger("Custom Entity Models");
    private static final Gson    GSON   = new Gson();
    private static final Pattern slash  = Pattern.compile("/");

    //Gson
    public static Gson getGson() {
        return GSON;
    }

    //file stuff
    public static Identifier transformPath(String path, Identifier location) {
        //relative to current folder
        if(path.startsWith("./")) {
            return new Identifier(location.getNamespace(),
                                  location.getPath().substring(0, location.getPath().lastIndexOf('/') + 1) + path.substring(2));
        }
        //go up a folder
        else if(path.startsWith("../")) {
            return transformPath(path.substring(3),
                                 new Identifier(location.getNamespace(), location.getPath().substring(0, location.getPath().lastIndexOf('/'))));
        }
        //relative to "assets/dorianpb/cem"
        else if(path.startsWith("~/")) {
            return new Identifier("dorianpb", "cem/" + path.substring(2));
        }
        //relative to "assets/namespace/"
        else if(path.chars().filter(i -> i == ':').count() == 1) {
            String path2 = path.substring(path.indexOf(':') + 1);
            if(path2.startsWith("/")) {
                path2 = slash.matcher(path2).replaceFirst("");
            }
            return transformPath(path2, new Identifier(path.substring(0, path.indexOf(':')), ""));
        }
        //look for file in current folder
        else {
            return new Identifier(location.getNamespace(), location.getPath().substring(0, location.getPath().lastIndexOf('/') + 1) + path);
        }
    }

    public static void postReadError(Exception exception, Identifier id) {
        LOGGER.error("Error parsing " + id + ":");
        String message = exception.getMessage();
        LOGGER.error(exception);
        if(message == null || message.trim().isEmpty()) {
            LOGGER.error(exception.getStackTrace()[0]);
            LOGGER.error(exception.getStackTrace()[1]);
            LOGGER.error(exception.getStackTrace()[2]);
        }
    }

    //logger
    public static Logger getLogger() {
        return LOGGER;
    }

    public static @Nullable ArrayList<Double> JSONparseDoubleList(Object object) {
        try {
            @SuppressWarnings("unchecked")
            Iterable<Object> obj = (Iterable<Object>) object;
            ArrayList<Double> val = new ArrayList<>();
            if(obj != null) {
                obj.forEach((value) -> val.add(JSONparseDouble(value)));
            }
            return (val.isEmpty())? null : val;
        } catch(Exception e) {
            return null;
        }
    }

    public static @Nullable Double JSONparseDouble(Object obj) {
        String val = JSONparseString(obj);
        return val == null? null : Double.valueOf(val);
    }

    public static @Nullable String JSONparseString(Object obj) {
        return obj == null? null : obj.toString();
    }

}