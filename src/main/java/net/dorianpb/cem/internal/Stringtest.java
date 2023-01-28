package net.dorianpb.cem.internal;

import net.dorianpb.cem.internal.util.stringparser.CemStringParser;
import net.dorianpb.cem.internal.util.stringparser.ParsedExpressionFloat;

import java.util.HashMap;

/***/
public enum Stringtest{
	;
	
	public static void main(String[] args){
		String yeet = "clamp(-0.5 * alex.rx, 0, 90)";
		ParsedExpressionFloat betelgeuse = (ParsedExpressionFloat) CemStringParser.parse(yeet, null, null);
		System.out.println(betelgeuse.eval(0, 0, 0, 0, 0, null, new HashMap<>(), new HashMap<>()));
	}
}