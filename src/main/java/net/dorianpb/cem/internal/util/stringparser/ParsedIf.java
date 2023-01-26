package net.dorianpb.cem.internal.util.stringparser;

import net.dorianpb.cem.internal.models.CemModelEntry;
import net.dorianpb.cem.internal.models.CemModelRegistry;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * The "if" operator is the only operator that actually steals arguments from its container class, the ParsedExpressionFloat
 */
class ParsedIf implements ParsedFunctionFloat{
	private final List<ParsedExpressionBool>  conditions;
	private final List<ParsedExpressionFloat> expressions;
	
	ParsedIf(Token token, CemModelRegistry registry, CemModelEntry parent){
		if(token.getArgs() == null){
			throw new IllegalArgumentException("\"" + token.getName() + "\" requires arguments!");
		}
		else{
			this.conditions = new ArrayList<>();
			this.expressions = new ArrayList<>();
			for(int i = 0; i < token.getArgs().size(); i++){
				ParsedFunctionType wantedType = (i % 2 == 1 || i == token.getArgs().size() - 1)? ParsedFunctionType.FLOAT : ParsedFunctionType.BOOL;
				if(wantedType == ParsedFunctionType.BOOL){
					try{
						this.conditions.add(new ParsedExpressionBool(token.getArgs().get(i), registry, parent));
					} catch(InvalidParameterException ignored){
						throw new IllegalArgumentException("\"" + token.getName() + "\" requires a bool for argument #" + (i + 1) + ", but a " + "number was provided");
					}
				}
				else{
					try{
						this.expressions.add(new ParsedExpressionFloat(token.getArgs().get(i), registry, parent));
					} catch(InvalidParameterException ignored){
						throw new IllegalArgumentException("\"" + token.getName() + "\" requires a number for argument #" + (i + 1) + ", but a " + "bool was provided");
					}
				}
			}
			if(this.conditions.isEmpty()){
				throw new IllegalArgumentException("\"" + token.getName() + "\" requires at least one condition!");
				
			}
			else if(this.conditions.size() + 1 != this.expressions.size()){
				throw new IllegalArgumentException("\"" + token.getName() + "\" is missing an \"val_else\" value, please add a number at the " + "end" + ".");
			}
		}
	}
	
	@Override
	public float eval(ArrayList<ParsedExpression> args, Environment env){
		for(int i = 0; i < this.conditions.size(); i++){
			if(this.conditions.get(i).eval(env)){
				return this.expressions.get(i).eval(env);
			}
		}
		return this.expressions.get(this.expressions.size() - 1).eval(env);
	}
	
	@Override
	public int getArgNumber(){
		return -1;
	}
	
}