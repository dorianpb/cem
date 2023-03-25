package net.dorianpb.cem.internal.file;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import net.dorianpb.cem.internal.util.CemFairy;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public final class JemModel {
    private final String                        baseId;
    private final String                        model;
    private final String                        part;
    private final Boolean                       attach;
    private final Double                        scale;
    private final LinkedTreeMap<String, String> animations;
    private final JpmFile                       modelDef;


    @SuppressWarnings({"rawtypes", "unchecked"})
    JemModel(LinkedTreeMap json, Identifier path, ResourceFactory resourceManager) throws IOException {
        this.baseId = CemFairy.JSONparseString(json.get("baseId"));
        this.model = CemFairy.JSONparseString(json.get("model"));
        this.part = CemFairy.JSONparseString(json.get("part"));
        this.attach = JSONparseBool(json.get("attach"));
        this.scale = CemFairy.JSONparseDouble(json.getOrDefault("scale", 1.0D));
        var yeah = ((ArrayList<LinkedTreeMap<String, Object>>) json.getOrDefault("animations",
                                                                                 new ArrayList<>(Collections.singletonList(new LinkedTreeMap()))));
        this.animations = new LinkedTreeMap<>();
        yeah.forEach((value) -> value.forEach((key, value1) -> this.animations.put(key, value1.toString())));
        JpmFile temp;
        if(this.model != null) {
            Identifier id = CemFairy.transformPath(this.model, path);
            Optional<Resource> resourceOptional = resourceManager.getResource(id);
            if(resourceOptional.isPresent()) {
                try(InputStream stream = resourceOptional.get().getInputStream()) {
                    @SuppressWarnings("unchecked")
                    LinkedTreeMap<String, Object> file = CemFairy.getGson()
                                                                 .fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8),
                                                                           LinkedTreeMap.class);
                    if(file == null) {
                        throw new IOException("Invalid File");
                    }
                    temp = new JpmFile(file);
                } catch(JsonIOException | IOException | JsonSyntaxException exception) {
                    CemFairy.postReadError(exception, id);
                    throw new IOException("Error loading dependent file: " + id + exception.getMessage());
                }
            } else {
                CemFairy.getLogger().warn(" File \"" + id + "\" not found,");
                CemFairy.getLogger().warn(" falling back on reading model definition from " + path.toString() + "!");
                temp = new JpmFile(json);
            }
        } else {
            temp = new JpmFile(json);
        }
        this.modelDef = temp;
        this.validate();
    }

    private static @Nullable Boolean JSONparseBool(Object obj) {
        String val = CemFairy.JSONparseString(obj);
        return val == null? null : Boolean.valueOf(val);
    }

    private void validate() {
        if(this.part == null) {
            throw new InvalidParameterException("Element \"part\" is required");
        }
    }

    public String getPart() {
        return this.part;
    }

    public Double getScale() {
        return this.scale;
    }

    String getModel() {
        return this.model;
    }

    String getId() {
        return this.modelDef.getId();
    }

    public JpmFile getModelDef() {
        return this.modelDef;
    }

    public Map<String, String> getAnimations() {
        return Collections.unmodifiableMap(this.animations);
    }

}