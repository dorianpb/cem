package net.dorianpb.cem.internal.util.stringparser;

import net.dorianpb.cem.internal.models.CemModelEntry;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.entity.Entity;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public final class ParsedExpressionBool implements ParsedExpression{
	private final ParsedFunctionBool          operation;
	private final ArrayList<ParsedExpression> arguments;
	
	ParsedExpressionBool(Token token, CemModelRegistry registry, CemModelEntry parent){
		ParsedFunction temp = CemStringParser.matchToken(token, registry, parent);
		if(temp.getType() == ParsedFunctionType.BOOL){
			this.operation = (ParsedFunctionBool) temp;
		}
		else{
			throw new InvalidParameterException("\"" + token.getName() + "\" is not a number and will not return a number!");
		}
		if(token.getArgs() != null){
			this.arguments = new ArrayList<>();
			for(Token arg : token.getArgs()){
				if(this.operation.getArgType() == ParsedFunctionType.FLOAT){
					try{
						this.arguments.add(new ParsedExpressionFloat(arg, registry, parent));
					} catch(InvalidParameterException ignored){
						throw new IllegalArgumentException("\"" + token.getName() + "\" requires numbers as arguments and \"" + arg.getName() + "\" is not a number!");
					}
				}
				else{
					try{
						this.arguments.add(new ParsedExpressionBool(arg, registry, parent));
					} catch(InvalidParameterException ignored){
						throw new IllegalArgumentException("\"" + token.getName() + "\" requires bools as arguments and \"" + arg.getName() + "\" is not a bool!");
					}
				}
			}
		}
		else{
			this.arguments = null;
		}
		this.checkArgs(this.arguments, this.operation.getArgNumber());
	}
	
	public boolean eval(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, Entity entity){
		return this.eval(new Environment(limbAngle, limbDistance, age, head_yaw, head_pitch, entity));
	}
	
	boolean eval(Environment env){
		return this.operation.eval(this.arguments, env);
	}
	
	@Override
	public ParsedFunctionType getType(){
		return ParsedFunctionType.BOOL;
	}
	
	@Override
	public String getName(){
		return this.operation.toString();
	}
}