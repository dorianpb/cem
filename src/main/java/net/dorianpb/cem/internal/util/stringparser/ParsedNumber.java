package net.dorianpb.cem.internal.util.stringparser;

import java.util.ArrayList;

class ParsedNumber implements ParsedFunctionFloat{
	private final float num;
	
	ParsedNumber(NumToken token){
		this.num = token.getNum();
	}
	
	@Override
	public int getArgNumber(){
		return -2;
	}
	
	@Override
	public float eval(ArrayList<ParsedExpression> args, Environment env){
		return this.num;
	}
}