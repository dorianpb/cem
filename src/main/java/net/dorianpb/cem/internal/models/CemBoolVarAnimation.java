package net.dorianpb.cem.internal.models;

import net.dorianpb.cem.internal.util.stringparser.CemStringParser;
import net.dorianpb.cem.internal.util.stringparser.ParsedExpression;
import net.dorianpb.cem.internal.util.stringparser.ParsedExpressionBool;
import net.dorianpb.cem.internal.util.stringparser.ParsedFunctionType;
import net.minecraft.entity.Entity;

import java.lang.invoke.WrongMethodTypeException;
import java.util.Map;

class CemBoolVarAnimation implements CemAnimation{
	private final String               target;
	private final ParsedExpressionBool expression;
	private final Map<String, Boolean> boolanimvars;
	private final Map<String, Float>   floatanimvars;
	
	
	CemBoolVarAnimation(String target, String expr, CemModelRegistry registry){
		this.target = target;
		
		ParsedExpression parsedExpression = CemStringParser.parse(expr, registry, null);
		if(parsedExpression.getType() == ParsedFunctionType.BOOL){
			this.expression = (ParsedExpressionBool) parsedExpression;
		}
		else{
			throw new WrongMethodTypeException("\"" + parsedExpression.getName() + " must evaluate to a boolean, not a number!");
		}
		
		this.boolanimvars = registry.getBoolanimvars();
		this.floatanimvars = registry.getFloatanimvars();
	}
	
	@Override
	public void apply(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, Entity entity){
		boolean val = this.expression.eval(limbAngle, limbDistance, age, head_yaw, head_pitch, entity, this.boolanimvars, this.floatanimvars);
		this.boolanimvars.put(this.target, val);
	}
}