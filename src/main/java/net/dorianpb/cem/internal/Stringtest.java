package net.dorianpb.cem.internal;

import net.dorianpb.cem.internal.util.CemStringParser;
import net.dorianpb.cem.internal.util.CemStringParser.ParsedExpression;


/***/
public class Stringtest{
	public static void main(String[] args){
		String yeet = "clamp(-0.5 * alex.rx, 0, 90)";
		ParsedExpression betelgeuse = CemStringParser.parse(yeet, null, null);
		System.out.println(betelgeuse.eval(0, 0, 0, 0, 0, null, null));
	}
}