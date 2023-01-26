package net.dorianpb.cem.internal.util.stringparser;

import java.util.ArrayList;

enum BOOL_FUNCTION_FLOAT implements ParsedFunctionBool{
	BETWEEN, EQUALS, IN, GREATER, GREATEREQ, LESS, LESSEQ, EQ, NOTEQ,
	;
	
	@Override
	public Boolean eval(ArrayList<ParsedExpression> args, Environment env){
		switch(this){
			case BETWEEN -> {
				return ((ParsedExpressionFloat) args.get(1)).eval(env) <= ((ParsedExpressionFloat) args.get(0)).eval(env) &&
				       ((ParsedExpressionFloat) args.get(0)).eval(env) <= ((ParsedExpressionFloat) args.get(2)).eval(env);
			}
			case EQUALS -> {
				return ((ParsedExpressionFloat) args.get(1)).eval(env) - ((ParsedExpressionFloat) args.get(2)).eval(env) <= ((ParsedExpressionFloat) args.get(0)).eval(env) &&
				       ((ParsedExpressionFloat) args.get(0)).eval(env) <= ((ParsedExpressionFloat) args.get(1)).eval(env) + ((ParsedExpressionFloat) args.get(2)).eval(env);
			}
			case IN -> {
				boolean x = false;
				for(int i = 1; i < args.size(); i++){
					x = (x || (Math.abs(((ParsedExpressionFloat) args.get(0)).eval(env) - ((ParsedExpressionFloat) args.get(i)).eval(env))) < 0.01);
				}
				return x;
			}
			case GREATER -> {
				return ((ParsedExpressionFloat) args.get(0)).eval(env) > ((ParsedExpressionFloat) args.get(1)).eval(env);
			}
			case GREATEREQ -> {
				return ((ParsedExpressionFloat) args.get(0)).eval(env) >= ((ParsedExpressionFloat) args.get(1)).eval(env);
			}
			case LESS -> {
				return ((ParsedExpressionFloat) args.get(0)).eval(env) < ((ParsedExpressionFloat) args.get(1)).eval(env);
			}
			case LESSEQ -> {
				return ((ParsedExpressionFloat) args.get(0)).eval(env) <= ((ParsedExpressionFloat) args.get(1)).eval(env);
			}
			case EQ -> {
				return Math.abs(((ParsedExpressionFloat) args.get(0)).eval(env) - ((ParsedExpressionFloat) args.get(1)).eval(env)) < 0.01;
			}
			case NOTEQ -> {
				return !(Math.abs(((ParsedExpressionFloat) args.get(0)).eval(env) - ((ParsedExpressionFloat) args.get(1)).eval(env)) < 0.01);
			}
		}
		throw new NullPointerException("my brain ... TREMBLES!");
	}
	
	@Override
	public ParsedFunctionType getArgType(){
		return ParsedFunctionType.FLOAT;
	}
	
	@Override
	public int getArgNumber(){
		return switch(this){
			case BETWEEN, EQUALS -> 3;
			case IN -> -1;
			case GREATER, GREATEREQ, LESS, EQ, LESSEQ, NOTEQ -> 2;
		};
	}
}