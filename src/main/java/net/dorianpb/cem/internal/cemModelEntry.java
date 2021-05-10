package net.dorianpb.cem.internal;

import net.dorianpb.cem.internal.jemFile.jemModel;
import net.dorianpb.cem.internal.jpmFile.jpmBox;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

class cemModelEntry{
    private final String id;
    private final String part;
    private final HashMap<ArrayList<String>,cemModelEntry> children;
    private final cemModelPart model;
    private final float[] translates;
    private final float[] rotates;
    
    cemModelEntry(jemModel file, Model in, float x, float y, float z){
        this(file.getPart(),file.getModelDef(),in,new float[]{x,y,z},new float[]{0,24,0},0,file.getScale().floatValue());
        if(file.getAnimations().size()>0){
            for(String key : file.getAnimations().keySet()){
                if(!key.contains(".")){
                    throw new InvalidParameterException("Invalid Syntax: " + key);
                }
            }
        }
    }
    cemModelEntry(jemModel file, jemFile jemFile){
        this(file.getPart(),file.getModelDef(),null,new float[]{0,0,0},new float[]{0,24,0},0,file.getScale().floatValue(),jemFile.getTextureSize().get(0).intValue(),jemFile.getTextureSize().get(1).intValue());
    }
    private cemModelEntry(String part, jpmFile data, Model in, float[] debugpos, float[] parents, int gen, float scale){
        this(part,data,in,debugpos,parents,gen,scale,0,0);
    }
    
    private cemModelEntry(String part, jpmFile data, Model in, float[] debugpos, float[] parents, int gen, float scale, int textureWidth, int textureHeight){
        this.id = data.getId();
        this.part = part;
        this.children = new HashMap<>();
        this.translates = new float[]{
                (data.getTranslate().get(0).floatValue()) * (data.getInvertAxis()[0]?-1:1) + debugpos[0],
                (data.getTranslate().get(1).floatValue()) * (data.getInvertAxis()[1]?-1:1) + debugpos[1],
                (data.getTranslate().get(2).floatValue()) * (data.getInvertAxis()[2]?-1:1) + debugpos[2],
        };
        this.rotates = new float[]{
                data.getRotate().get(0).floatValue() * ((data.getInvertAxis()[0])?1:-1),
                data.getRotate().get(1).floatValue() * ((data.getInvertAxis()[1])?1:-1),
                data.getRotate().get(2).floatValue() * ((data.getInvertAxis()[2])?1:-1),
        };
        if(in!=null){
            this.model = new cemModelPart(in);
            
        } else {
            this.model = new cemModelPart(textureWidth,textureHeight);
        }
        this.initmodel(data, debugpos, parents, gen, scale);
        //CHILD INIT
        if(data.getSubmodels()!=null){
            for(jpmFile submodel : data.getSubmodels()){
                float childZ = (gen==0) ? ((data.getTranslate().get(2).floatValue()) * (data.getInvertAxis()[2] ? -1 : 1)) : 0;
                float childY = (gen==0) ? ((data.getTranslate().get(1).floatValue()) * (data.getInvertAxis()[1] ? -1 : 1)) : 0;
                float childX = (gen==0) ? ((data.getTranslate().get(0).floatValue()) * (data.getInvertAxis()[0] ? -1 : 1)) : 0;
                this.addChild(new cemModelEntry(null,submodel,in,new float[]{0,0,0},new float[]{childX,childY,childZ},gen+1,1));
            }
        }
        //END CHILD INIT
    }
    
    String getId(){ return id; }
    String getPart(){ return part; }
    cemModelPart getModel(){ return model; }
    
    float getTranslate(char axis){
        switch(axis){
            case 'x': return this.translates[0];
            case 'y': return this.translates[1];
            case 'z': return this.translates[2];
            default: throw new IllegalStateException("Unknown axis \""+axis+"\"");
        }
    }
    
    /** this is done because there can be a discrepancy between the translate value in the .jem and the actual part*/
    void setTranslate(char axis, float translate){
        float oldTranslate;
        switch(axis){
            case 'x':
                oldTranslate = this.translates[0];
                this.translates[0] = translate;
                this.getModel().pivotX += (this.translates[0] - oldTranslate);
                break;
            case 'y':
                oldTranslate = this.translates[1];
                this.translates[1] = translate;
                this.getModel().pivotY += (this.translates[1] - oldTranslate);
                break;
            case 'z':
                oldTranslate = this.translates[2];
                this.translates[2] = translate;
                this.getModel().pivotZ += (this.translates[2] - oldTranslate);
                break;
            default: throw new IllegalStateException("Unknown axis \""+axis+"\"");
        }
    }
    
    HashMap<ArrayList<String>, cemModelEntry> getChildren(){
        return children;
    }
    
    private void initmodel(jpmFile data, float[] debugpos, float[] parents, int gen, float scale){
        float[] pivot= new float[]{
                (gen==1)?(parents[0]+(data.getTranslate().get(0).floatValue()) * (data.getInvertAxis()[0] ?-1 : 1)):((data.getTranslate().get(0).floatValue()) * (data.getInvertAxis()[0] ? (gen==0)?1:-1 : 1)) + debugpos[0],
                (
                        (gen==0)
                                ?(parents[1] - (data.getTranslate().get(1).floatValue() * (data.getInvertAxis()[1] ? -1 : 1)))
                                :(
                                        (gen==1)
                                                ?(parents[1] + (data.getTranslate().get(1).floatValue() * (data.getInvertAxis()[1] ? -1 : 1)))
                                                :data.getTranslate().get(1).floatValue() * (data.getInvertAxis()[1] ? -1 : 1)
                                 )
                ) + debugpos[1],
                (gen==1)?(parents[2]+(data.getTranslate().get(2).floatValue()) * (data.getInvertAxis()[2] ?-1 : 1)):((data.getTranslate().get(2).floatValue()) * (data.getInvertAxis()[2] ? (gen==0)?1:-1 : 1)) + debugpos[2],
        };
        ///MUST SUBTRACT FROM PARENT FOR GEN1
        float[] translate= new float[]{
                (data.getTranslate().get(0).floatValue())/* * (data.getInvertAxis()[0] ? -1 : 1)*/ + debugpos[0],
                (data.getTranslate().get(1).floatValue())/* * (data.getInvertAxis()[1] ? -1 : 1)*/ + debugpos[1],
                (data.getTranslate().get(2).floatValue())/* * (data.getInvertAxis()[2] ? -1 : 1)*/ + debugpos[2],
        };
        if(data.getBoxes()!=null){
            for(jpmBox box : data.getBoxes()){
                //apply translates first, then ?invert pos, then ?subtract pos by size so that it is drawn correctly
                //top level model pivots need to translated up by 24, then 1st gen children need to work off of that rather than the translate values provided by the jpmFile
                //only top level models need translates applied, others are relative to parent (even gen1)
                this.model.addCemCuboid(
                        ((box.getCoordinates().get(0).floatValue() + ((gen==0)?translate[0]:0)) * ((data.getInvertAxis()[0])?-1:1)) - ((data.getInvertAxis()[0])?box.getCoordinates().get(3).floatValue():0),
        
                        ((box.getCoordinates().get(1).floatValue() + ((gen==0)?translate[1]:0)) * ((data.getInvertAxis()[1])?-1:1)) - ((data.getInvertAxis()[1])?box.getCoordinates().get(4).floatValue():0),
        
                        ((box.getCoordinates().get(2).floatValue() + ((gen==0)?translate[2]:0)) * ((data.getInvertAxis()[2])?-1:1)) - ((data.getInvertAxis()[2])?box.getCoordinates().get(5).floatValue():0),
                        box.getCoordinates().get(3).intValue(),
                        box.getCoordinates().get(4).intValue(),
                        box.getCoordinates().get(5).intValue(),
                        box.getSizeAdd().floatValue(),
                        box.getTextureOffset().get(0).intValue(),
                        box.getTextureOffset().get(1).intValue(),
                        data.getMirrorTexture()
                );
            }
        }
        //pivot point is relative to parent, so 0,0,0 means "same as parent"
        //remember to invert them okay
        //pivot points are given to me in perfect form, i think
        this.model.setPivot(
                pivot[0],
                pivot[1],
                pivot[2]
        );
        this.model.mirror = data.getMirrorTexture()[0];
        this.model.setRotation(rotates[0],rotates[1],rotates[2]);
        this.model.setScale(scale,scale,scale);
    }
    
    private void addChild(cemModelEntry child){
        ArrayList<String> key = new ArrayList<>(Collections.singletonList(child.getId()));
        if(this.children.containsKey(key)){
            throw new InvalidParameterException("Child "+ key +" already exists for parent "+this.getId());
        }
        this.children.put(key, child);
        for(ArrayList<String> refs : child.children.keySet()){
            cemModelEntry val = child.children.get(refs);
            refs.add(0, child.getId());
            this.children.put(refs,val);
        }
        this.model.addChild(child.getModel());
    }
    
    
    static class cemModelPart extends ModelPart{
        private final float[] scale;
        private final float[] rotation;
        
        private cemModelPart(Model model){
            super(model);
            this.scale = new float[]{0,0,0};
            this.rotation = new float[]{0,0,0};
        }
        
        private cemModelPart(int textureWidth, int texureHeight){
            super(textureWidth,texureHeight,0,0);
            this.scale = new float[]{0,0,0};
            this.rotation = new float[]{0,0,0};
        }
        
        private void addCemCuboid(float x, float y, float z, int sizeX, int sizeY, int sizeZ, float extra, int textureOffsetU, int textureOffsetV, Boolean[] mirror){
            this.setTextureOffset(textureOffsetU, textureOffsetV);
            this.addCemCuboid(textureOffsetU, textureOffsetV, x, y, z, (float)sizeX, (float)sizeY, (float)sizeZ, extra, extra, extra, mirror[0]);
        }
        private void addCemCuboid(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror) {
            this.cuboids.add(new Cuboid(u, v, x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirror, this.textureWidth, this.textureHeight));
        }
        
        private void setScale(float scaleX, float scaleY, float scaleZ){
            this.scale[0] = scaleX;
            this.scale[1] = scaleY;
            this.scale[2] = scaleZ;
        }
        
        private void setRotation(float rotX, float rotY, float rotZ){
            this.rotation[0] = rotX;
            this.rotation[1] = rotY;
            this.rotation[2] = rotZ;
        }
        
        void setScale(char axis, float scale){
            switch(axis){
                case 'x': this.scale[0] = scale; break;
                case 'y': this.scale[1] = scale; break;
                case 'z': this.scale[2] = scale; break;
                default: throw new IllegalStateException("Unknown axis \""+axis+"\"");
            }
        }
    
        void setRotation(char axis, float rot){
            switch(axis){
                case 'x': this.rotation[0] = rot; break;
                case 'y': this.rotation[1] = rot; break;
                case 'z': this.rotation[2] = rot; break;
                default: throw new IllegalStateException("Unknown axis \""+axis+"\"");
            }
        }
        
        float getScale(char axis){
            switch(axis){
                case 'x': return scale[0];
                case 'y': return scale[1];
                case 'z': return scale[2];
                default: throw new IllegalStateException("Unknown axis \""+axis+"\"");
            }
        }
    
        float getRotation(char axis){
            switch(axis){
                case 'x': return this.pitch + this.rotation[0];
                case 'y': return this.yaw + this.rotation[1];
                case 'z': return this.roll + this.rotation[2];
                default: throw new IllegalStateException("Unknown axis \""+axis+"\"");
            }
        }
    
        @Override
        public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha){
            matrices.scale(scale[0],scale[1],scale[2]);
            super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    
        @Override
        public void rotate(MatrixStack matrix){
            this.pitch += this.rotation[0];
            this.yaw += this.rotation[1];
            this.roll += this.rotation[2];
            super.rotate(matrix);
            this.pitch -= this.rotation[0];
            this.yaw -= this.rotation[1];
            this.roll -= this.rotation[2];
        }
    }
}