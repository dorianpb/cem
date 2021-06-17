package net.dorianpb.cem.internal.api;

import net.dorianpb.cem.internal.util.CemFairy;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;

import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CemFactory{
	private final Class<? extends CemRenderer> renderer;
	private final List<Object>                 params;
	
	public CemFactory(Class<? extends CemRenderer> renderer, Object... params){
		this.renderer = renderer;
		this.params = Arrays.asList(params);
	}
	
	private CemRenderer createRenderer(Object ctx) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException{
		ArrayList<Class<?>> classes = new ArrayList<>();
		classes.add(ctx.getClass());
		
		for(Object param : params){
			classes.add(param.getClass());
		}
		
		ArrayList<Object> tempparams = new ArrayList<>();
		tempparams.add(ctx);
		tempparams.addAll(this.params);
		return renderer.getDeclaredConstructor(classes.toArray(new Class[]{})).newInstance(tempparams.toArray());
	}
	
	@SuppressWarnings("rawtypes")
	public EntityRenderer create(EntityRendererFactory.Context ctx){
		try{
			CemRenderer hello = this.createRenderer(ctx);
			if(EntityRenderer.class.isAssignableFrom(hello.getClass())){
				return (EntityRenderer) hello;
			}
			else{
				throw new InvalidParameterException("Provided renderer " + hello.getClass() + " isn't an entity renderer!");
			}
		} catch(Exception e){
			CemFairy.getLogger().error(e);
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public BlockEntityRenderer create(BlockEntityRendererFactory.Context ctx){
		try{
			CemRenderer hello = this.createRenderer(ctx);
			if(BlockEntityRenderer.class.isAssignableFrom(hello.getClass())){
				return (BlockEntityRenderer) hello;
			}
			else{
				throw new InvalidParameterException("Provided renderer " + hello.getClass() + " isn't a block entity renderer!");
			}
		} catch(Exception e){
			CemFairy.getLogger().error(e);
			return null;
		}
	}
}