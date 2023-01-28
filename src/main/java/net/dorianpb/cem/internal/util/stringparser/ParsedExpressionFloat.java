package net.dorianpb.cem.internal.util.stringparser;

import net.dorianpb.cem.internal.models.CemModelEntry;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.entity.Entity;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Map;

public final class ParsedExpressionFloat implements ParsedExpression{
	private final ParsedFunctionFloat         operation;
	private final ArrayList<ParsedExpression> arguments;
	
	ParsedExpressionFloat(Token token, CemModelRegistry registry, CemModelEntry parent){
		ParsedFunction temp = CemStringParser.matchToken(token, registry, parent);
		if(temp.getType() == ParsedFunctionType.FLOAT){
			this.operation = (ParsedFunctionFloat) temp;
		}
		else{
			throw new InvalidParameterException("\"" + token.getName() + "\" is not a number and will not return a number!");
		}
		if(token.getArgs() != null && this.operation.getClass() != ParsedIf.class){
			this.arguments = new ArrayList<>();
			for(Token arg : token.getArgs()){
				try{
					this.arguments.add(new ParsedExpressionFloat(arg, registry, parent));
				} catch(InvalidParameterException ignored){
					throw new IllegalArgumentException("\"" + token.getName() + "\" requires numbers as arguments and \"" + arg.getName() + "\" " + "is not a number!");
				}
				
			}
		}
		else if(token.getArgs() != null){
			this.arguments = new ArrayList<>();
		}
		else{
			this.arguments = null;
		}
		this.checkArgs(this.arguments, this.operation.getArgNumber());
	}
	
	public float eval(float limbAngle,
	                  float limbDistance,
	                  float age,
	                  float head_yaw,
	                  float head_pitch,
	                  Entity entity,
	                  Map<String, Boolean> boolanimvars,
	                  Map<String, Float> floatanimvars){
		return this.eval(new Environment(limbAngle, limbDistance, age, head_yaw, head_pitch, entity, boolanimvars, floatanimvars));
	}
	
	float eval(Environment env){
		return this.operation.eval(this.arguments, env);
	}
	
	@Override
	public ParsedFunctionType getType(){
		return ParsedFunctionType.FLOAT;
	}
	
	@Override
	public String getName(){
		return this.operation.toString();
	}
	
}