package net.dorianpb.cem.internal.util.stringparser;

import java.util.ArrayList;

interface ParsedFunctionFloat extends ParsedFunction{
	float eval(ArrayList<ParsedExpression> args, Environment env);
	
	@Override
	default ParsedFunctionType getType(){
		return ParsedFunctionType.FLOAT;
	}
}