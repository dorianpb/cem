package net.dorianpb.cem.internal.models;

import net.dorianpb.cem.internal.util.stringparser.CemStringParser;
import net.dorianpb.cem.internal.util.stringparser.ParsedExpression;
import net.dorianpb.cem.internal.util.stringparser.ParsedExpressionFloat;
import net.dorianpb.cem.internal.util.stringparser.ParsedFunctionType;
import net.minecraft.entity.Entity;

import java.lang.invoke.WrongMethodTypeException;
import java.util.Map;

class CemFloatVarAnimation implements CemAnimation{
	private final String                target;
	private final ParsedExpressionFloat expression;
	private final Map<String, Boolean>  boolanimvars;
	private final Map<String, Float>    floatanimvars;
	
	
	CemFloatVarAnimation(String target, String expr, CemModelRegistry registry){
		this.target = target;
		
		ParsedExpression parsedExpression = CemStringParser.parse(expr, registry, null);
		if(parsedExpression.getType() == ParsedFunctionType.FLOAT){
			this.expression = (ParsedExpressionFloat) parsedExpression;
		}
		else{
			throw new WrongMethodTypeException("\"" + parsedExpression.getName() + " must evaluate to a number, not a boolean!");
		}
		
		this.boolanimvars = registry.getBoolanimvars();
		this.floatanimvars = registry.getFloatanimvars();
	}
	
	@Override
	public void apply(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, Entity entity){
		float val = this.expression.eval(limbAngle, limbDistance, age, head_yaw, head_pitch, entity, this.boolanimvars, this.floatanimvars);
		this.floatanimvars.put(this.target, val);
	}
}