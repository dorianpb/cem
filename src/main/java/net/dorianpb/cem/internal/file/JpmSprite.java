package net.dorianpb.cem.internal.file;

import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

class JpmSprite {
    private final List<Integer> textureOffset;
    private final List<Integer> coordinates;
    private final Double        sizeAdd;

    @SuppressWarnings({"unchecked", "rawtypes"})
    JpmSprite(LinkedTreeMap json) {
        this.textureOffset = (List<Integer>) json.get("textureOffset");
        this.coordinates = (List<Integer>) json.get("coordinates");
        this.sizeAdd = (Double) json.get("sizeAdd");
    }
}