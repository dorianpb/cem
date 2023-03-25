package net.dorianpb.cem.internal.models;

import com.google.common.collect.BiMap;
import net.dorianpb.cem.internal.config.CemConfigFairy;
import net.dorianpb.cem.internal.file.JemFile;
import net.dorianpb.cem.internal.file.JemModel;
import net.dorianpb.cem.internal.util.CemFairy;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Contains all of the data for the CEM model
 */
public final class CemModelRegistry {
    private final HashMap<ArrayList<String>, CemModelEntry> database; //actual storage of cemModelEntries
    private final List<CemAnimation>                        animations; //actual storage of all the cemAnimations
    private final HashMap<String, CemModelEntry>            partNameRefs; //used to refer to parts by their model names rather than id names
    private final JemFile                                   file; //stores the jemFile
    private final Map<String, Boolean>                      boolanimvars;
    private final Map<String, Float>                        floatanimvars;

    public CemModelRegistry(JemFile file) {
        this.database = new HashMap<>();
        this.animations = new ArrayList<>();
        this.boolanimvars = new HashMap<>();
        this.floatanimvars = new HashMap<>();
        this.partNameRefs = new HashMap<>();
        this.file = file;
        //models
        for(String part : this.file.getModelList()) {
            JemModel data = this.file.getModel(part);
            this.addEntry(new CemModelEntry(data, file.getTextureSize().get(0).intValue(), file.getTextureSize().get(1).intValue()),
                          new ArrayList<>());
        }
        //animations
        for(String part : this.file.getModelList()) {
            JemModel data = this.file.getModel(part);
            for(String key : data.getAnimations().keySet()) {
                try {
                    if(key.startsWith("var.")) {
                        this.animations.add(new CemFloatVarAnimation(key.substring("var.".length()), data.getAnimations().get(key), this));
                    } else if(key.startsWith("varb.")) {
                        this.animations.add(new CemBoolVarAnimation(key.substring("varb.".length()), data.getAnimations().get(key), this));
                    } else {
                        this.animations.add(new CemModelAnimation(this.findChild(key.substring(0, key.indexOf('.')), this.findChild(part)),
                                                                  data.getAnimations().get(key),
                                                                  key.substring(key.indexOf('.') + 1),
                                                                  this
                        ));
                    }

                } catch(Exception e) {
                    CemFairy.getLogger().error("Error applying animation \"" + data.getAnimations().get(key) + "\" in \"" + file.getPath() + "\":");
                    CemFairy.getLogger().error(e.getMessage());
                }
            }
        }
    }

    private static void prepChild(CemModelPart parent, CemModelPart child) {
        if(parent == null || child == null) {
            return;
        }
        child.pivotX = child.pivotX - parent.pivotX;
        child.pivotY = child.pivotY - parent.pivotY;
        child.pivotZ = child.pivotZ - parent.pivotZ;
    }

    Map<String, Boolean> getBoolanimvars() {
        return this.boolanimvars;
    }

    Map<String, Float> getFloatanimvars() {
        return this.floatanimvars;
    }

    public CemModelPart prepRootPart(ModelPart vanillaPart,
                                     @Nullable BiMap<String, String> partNames,
                                     @Nullable Map<String, ModelTransform> modelFixes,
                                     @Nullable Object identifier) {
        CemModelPart newRoot = new CemModelPart();
        this.constructPart(newRoot, vanillaPart, partNames, modelFixes, identifier);
        newRoot.setIdentifier(identifier);
        return newRoot;
    }

    private void constructPart(CemModelPart cemparent,
                               ModelPart vanillapart,
                               @Nullable BiMap<String, String> partNames,
                               @Nullable Map<String, ModelTransform> modelFixes,
                               Object identifier) {
        for(String partname : vanillapart.children.keySet()) {
            //find part
            CemModelPart part, transpart = null;
            if(partNames != null && this.partNameRefs.containsKey(partNames.inverse().getOrDefault(partname, partname))) {
                part = this.partNameRefs.get(partNames.inverse().getOrDefault(partname, partname)).getModel();
            } else if(this.partNameRefs.containsKey(partname)) {
                part = this.partNameRefs.get(partname).getModel();
            } else {
                part = new CemModelPart();
            }

            //continue with this branch
            this.constructPart(part, vanillapart.getChild(partname), partNames, modelFixes, identifier);

            prepChild(cemparent, part);

            part.setIdentifier(identifier);

            //make transparent if we should
            if(CemConfigFairy.getConfig().useTransparentParts()) {
                ModelTransform transform;
                if(modelFixes != null && modelFixes.containsKey(partname)) {
                    transform = ModelTransform.of(modelFixes.get(partname).pivotX,
                                                  modelFixes.get(partname).pivotY,
                                                  modelFixes.get(partname).pivotZ,
                                                  part.pitch,
                                                  part.yaw,
                                                  part.roll
                                                 );
                } else {
                    transform = part.getTransform();
                }
                transpart = new TransparentCemModelPart(part, transform, vanillapart.getChild(partname).getTransform());
            }

            //add part to parent
            cemparent.addChild(partname, CemConfigFairy.getConfig().useTransparentParts()? transpart : part);

        }
    }

    private void addEntry(CemModelEntry entry, ArrayList<String> parentRefmap) {
        ArrayList<String> refmap;
        if(parentRefmap != null && !parentRefmap.isEmpty()) {
            refmap = new ArrayList<>(parentRefmap);
        } else {
            refmap = new ArrayList<>();
            if(entry.getPart() != null) {
                this.partNameRefs.put(entry.getPart(), entry);
            }
        }
        refmap.add((entry.getId() == null)? entry.getPart() : entry.getId());
        this.database.put(refmap, entry);
        for(CemModelEntry child : entry.getChildren().values()) {
            this.addEntry(child, refmap);
        }
    }

    /**
     * Test if the user specified a special texture to use
     *
     * @return If a texture is specified in the .jem file
     */
    public boolean hasTexture() {
        return this.file.getTexture() != null;
    }

    /**
     * Returns an Identifier for the texture specified in the .jem file
     *
     * @return Identifier of the texture
     */
    public Identifier getTexture() {
        if(this.file.getTexture() == null) {
            throw new NullPointerException("Trying to retrieve a null texture");
        }
        return this.file.getTexture();
    }

    /**
     * Test if the user specified a shadow size to use
     *
     * @return If a shadow size is specified in the .jem file
     */
    public boolean hasShadowRadius() {
        return this.file.getShadowsize() != null;
    }

    /**
     * @return User-specified shadow radius
     */
    public float getShadowRadius() {
        return this.file.getShadowsize();
    }

    @SuppressWarnings("MethodParameterNamingConvention")
    public void applyAnimations(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, Entity entity) {
        for(CemAnimation anim : this.animations) {
            anim.apply(limbAngle, limbDistance, age, head_yaw, head_pitch, entity);
        }
    }

    public CemModelEntry findChild(String target, CemModelEntry parent) {
        CemModelEntry victim = null;
        ArrayList<String> hit = null;
        ArrayList<String> refmap = new ArrayList<>(Arrays.asList(target.split(":")));
        if(refmap.size() == 1 && this.partNameRefs.containsKey(refmap.get(0))) {
            victim = this.partNameRefs.get(refmap.get(0));
            return victim;
        } else if(parent != null && (refmap.get(0).equals("this") || refmap.get(0).equals("part"))) {
            if(refmap.size() == 1) {
                return parent;
            } else {
                StringBuilder newTarget = new StringBuilder(25);
                newTarget.append((parent.getId() == null)? parent.getPart() : parent.getId());
                for(int i = 1; i < refmap.size(); i++) {
                    newTarget.append(":").append(refmap.get(i));
                }
                return this.findChild(newTarget.toString(), parent);
            }
        } else {
            for(ArrayList<String> part : this.database.keySet()) {
                ArrayList<Integer> search = new ArrayList<>();
                for(String ref : refmap) {
                    search.add(part.indexOf(ref));
                }
                boolean hi = search.size() != 1 || search.get(0) > -1;
                for(int i = 0; i < search.size() - 1; i++) {
                    hi = hi && search.get(i) < search.get(i + 1) && search.get(i) > -1;
                }
                if(hi && (hit == null || part.size() < hit.size())) {
                    hit = part;
                }
                victim = this.database.get(hit);
            }
        }
        if(victim == null) {
            throw new NullPointerException("Model part " + target + " isn't specified in " + this.file.getPath());
        }
        return victim;
    }

    private CemModelEntry findChild(String target) {
        return this.findChild(target, null);
    }

}