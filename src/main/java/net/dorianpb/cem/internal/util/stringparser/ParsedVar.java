package net.dorianpb.cem.internal.util.stringparser;

import net.dorianpb.cem.internal.models.CemModelEntry;
import net.dorianpb.cem.internal.models.CemModelRegistry;

import java.util.ArrayList;
import java.util.regex.Pattern;

class ParsedVar implements ParsedFunctionFloat{
	private static final Pattern       PATTERN = Pattern.compile("(\\w\\d?:?)+[.][trs][xyz]");
	private final        CemModelEntry entry;
	private final        char          val;
	private final        char          axis;
	
	ParsedVar(Token token, CemModelRegistry registry, CemModelEntry parent){
		if(!PATTERN.matcher(token.getName()).find()){
			throw new IllegalArgumentException("\"" + token.getName() + "\" isn't a reference to a model part");
		}
		this.entry = registry.findChild(token.getName().substring(0, token.getName().indexOf('.')), parent);
		this.val = token.getName().charAt(token.getName().indexOf('.') + 1);
		this.axis = token.getName().charAt(token.getName().indexOf('.') + 2);
	}
	
	@Override
	public int getArgNumber(){
		return -2;
	}
	
	@Override
	public float eval(ArrayList<ParsedExpression> args, Environment env){
		return switch(this.val){
			case 't' -> this.entry.getTranslate(this.axis);
			case 'r' -> this.entry.getModel().getRotation(this.axis);
			case 's' -> this.entry.getModel().getScale(this.axis);
			default -> throw new IllegalStateException("Unknown operation \"" + this.val + "\"");
		};
	}
}