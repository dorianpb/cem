package net.dorianpb.cem.internal.util.stringparser;

import net.dorianpb.cem.internal.models.CemModelEntry;
import net.dorianpb.cem.internal.models.CemModelRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public enum CemStringParser{
	;
	
	private static final Pattern ensure_whitespace_between_operators    = Pattern.compile("\\s*(\\+|-|\\*|/|%|!=|\\|\\||&&|>=|<=|==|>|<)\\s*");
	private static final Pattern keep_exclamation_point_with_expression = Pattern.compile("!\\s*(\\w)");
	private static final Pattern ensure_paran_whitespace                = Pattern.compile("\\s*([(),])\\s*");
	private static final Pattern merge_whitespace                       = Pattern.compile("(\\s)+");
	private static final Pattern remove_sectionsign                     = Pattern.compile("§");
	
	public static ParsedExpression parse(String expr, CemModelRegistry registry, CemModelEntry parent){
		Token token = initParseLoop(expr);
		ParsedFunction matched = matchToken(token, registry, parent);
		if(matched.getType() == ParsedFunctionType.FLOAT){
			return new ParsedExpressionFloat(token, registry, parent);
		}
		else{
			return new ParsedExpressionBool(token, registry, parent);
		}
	}
	
	static ParsedFunction matchToken(Token token, CemModelRegistry registry, CemModelEntry parent){
		if(token.getName().equals("NUM")){
			try{
				return new ParsedNumber((NumToken) token);
			} catch(Exception ignored){
				throw new IllegalArgumentException("Why is there a token named \"NUM\" that is not a NumToken?");
			}
		}
		else if(token.getName().contains(".")){
			return new ParsedVar(token, registry, parent);
		}
		else if(token.getName().equalsIgnoreCase("if")){
			return new ParsedIf(token, registry, parent);
		}
		else{
			try{
				return FLOAT_PARAMETER.valueOf(token.getName().toUpperCase());
			} catch(Exception ignored){
			}
			try{
				return FLOAT_FUNCTION.valueOf(token.getName().toUpperCase());
			} catch(Exception ignored){
			}
			try{
				return BOOL_PARAMETER.valueOf(token.getName().toUpperCase());
			} catch(Exception ignored){
			}
			try{
				return BOOL_FUNCTION_FLOAT.valueOf(token.getName().toUpperCase());
			} catch(Exception ignored){
			}
			try{
				return BOOL_FUNCTION_BOOL.valueOf(token.getName().toUpperCase());
			} catch(Exception ignored){
			}
		}
		throw new IllegalArgumentException("Unknown symbol \"" + token.getName() + "\"");
	}
	
	/**
	 * This runs first to prepare the string, then sends it to the parseLoop to be turned into a giant Token
	 */
	private static Token initParseLoop(String input){
		//ensure there is whitespace between operators
		//keep ! with expression
		//ensure correct whitespace for ( and )
		//remove duplicate whitespace
		ArrayList<String> work = new ArrayList<>(Arrays.asList(remove_sectionsign.matcher(merge_whitespace.matcher(ensure_paran_whitespace.matcher(
				                                                                                                  keep_exclamation_point_with_expression.matcher(ensure_whitespace_between_operators.matcher(input).replaceAll(" $1 ")).replaceAll(" ! $1")).replaceAll(" $1 "))
		                                                                                                  .replaceAll(" ")).replaceAll("") //just to be safe
		                                                                         .trim().split(" ")));
		//try to eliminate garbage
		Pattern garbagePattern = Pattern.compile("^[+\\-*/%!=|&><\\w(),].*$");
		for(String badboi : work){
			if(!garbagePattern.matcher(badboi).find()){
				throw new IllegalArgumentException("Garbage symbol \"" + badboi + "\"");
			}
		}
		//find functions, turn parentheses into curly braces so that we don't parse them as grouped expressions, and we correctly parse them later
		Pattern functionPattern = Pattern.compile("^(\\w\\d?)+$");
		int j = 0;
		while(true){
			int i = regIndexOf(work, "^\\($", j);
			if(i >= 0){
				j = i + 1;
				if(i > 0 && functionPattern.matcher(work.get(i - 1)).find()){
					work.set(i + takeParen(work, i).size() + 1, "}");
					work.set(i, "{");
				}
			}
			else{
				break;
			}
		}
		return parseLoop(work, new ArrayList<>());
	}
	
	/**
	 * AKA the Token grinder
	 */
	private static Token parseLoop(ArrayList<String> input, ArrayList<Token> tokens){
		//REMEMBER P E M D A S
		ArrayList<String> work = new ArrayList<>(input);
		int i = -1; //used to remember position in case of failure
		try{
			//convert functions to tokens
			while(true){
				i = regIndexOf(work, "^\\{$");
				if(i >= 0){
					int k = indexOfEndOfArgs(work, i);
					tokens.add(new Token(work.get(i - 1), parseArgs(work, tokens, i, k)));
					for(int j = (k - i + 2); j > 0; j--){
						work.remove(i - 1);
					}
					work.add(i - 1, "§" + (tokens.size() - 1));
					
				}
				else{
					break;
				}
			}
			//parentheses
			while(true){
				i = regIndexOf(work, "^\\($");
				if(i >= 0){
					ArrayList<String> sub = takeParen(work, i);
					if(sub.isEmpty()){
						throw new IllegalArgumentException("Invalid Syntax: " + (i > 0? work.get(i - 1) : "") + work.get(i) + (i < work.size() - 1? work.get(i + 1) : ""));
					}
					else{ //otherwise, treat it normally
						for(int j = sub.size() + 2; j > 0; j--){
							work.remove(i);
						}
						tokens.add(parseLoop(sub, tokens));
						work.add(i, "§" + (tokens.size() - 1)); //placeholder for evaluated parentheses expression
						
					}
				}
				else{
					break;
				}
			}
			//convert raw numbers to tokens
			while(true){
				i = regIndexOf(work, "^(\\d+)([.]\\d+)?$");
				if(i >= 0){
					tokens.add(new NumToken(Float.parseFloat(work.set(i, "§" + tokens.size()))));
				}
				else{
					break;
				}
			}
			//convert variable names to tokens
			while(true){
				i = regIndexOf(work, "^\\w(\\w\\d?:?)+([.]\\w\\w)?$");
				if(i >= 0){
					tokens.add(new Token(work.set(i, "§" + tokens.size())));
				}
				else{
					break;
				}
			}
			//handle negative numbers and extraneous plus signs here
			i = 0;
			while(true){
				i = regIndexOf(work, "^[-+]$", i);
				if(i >= 0 && (i == 0 || !work.get(i - 1).startsWith("§"))){
					if(work.get(i).equals("-")){
						tokens.add(new NumToken(0));
						work.add(i, "§" + (tokens.size() - 1));
						ArrayList<Token> args = new ArrayList<>();
						i++;
						args.add(getToken(work.get(i - 1), tokens));
						args.add(getToken(work.get(i + 1), tokens));
						tokens.add(new Token("SUB", args));
						work.remove(i);
						work.remove(i);
						work.set(i - 1, "§" + (tokens.size() - 1));
					}
					else{
						work.remove(i);
					}
				}
				else{
					if(i == -1){
						break;
					}
					else{
						i++;
					}
				}
			}
			//exponents aren't a thing, so we go to multiplication and division(including modulo)
			while(true){
				i = regIndexOf(work, "^[*/%]$");
				if(i >= 0){
					ArrayList<Token> args = new ArrayList<>();
					args.add(getToken(work.get(i - 1), tokens));
					args.add(getToken(work.get(i + 1), tokens));
					String name = switch(work.get(i)){
						case "*" -> "MULT";
						case "/" -> "DIV";
						case "%" -> "MOD";
						default -> throw new IllegalStateException("Unexpected value: " + work.get(i));
					};
					tokens.add(new Token(name, args));
					work.remove(i);
					work.remove(i);
					work.set(i - 1, "§" + (tokens.size() - 1));
				}
				else{
					break;
				}
			}
			//addition & subtraction
			while(true){
				i = regIndexOf(work, "^[+-]$");
				if(i >= 0){
					ArrayList<Token> args = new ArrayList<>();
					args.add(getToken(work.get(i - 1), tokens));
					args.add(getToken(work.get(i + 1), tokens));
					tokens.add(new Token(work.get(i).equals("+")? "ADD" : "SUB", args));
					work.remove(i);
					work.remove(i);
					work.set(i - 1, "§" + (tokens.size() - 1));
				}
				else{
					break;
				}
			}
			//not
			while(true){
				i = regIndexOf(work, "^!$");
				if(i >= 0){
					ArrayList<Token> args = new ArrayList<>();
					args.add(getToken(work.get(i + 1), tokens));
					work.remove(i + 1);
					tokens.add(new Token("NOT", args));
					work.set(i, "§" + (tokens.size() - 1));
				}
				else{
					break;
				}
			}
			//equality
			while(true){
				i = regIndexOf(work, "^==|!=|<=|>=|<|>$");
				if(i >= 0){
					ArrayList<Token> args = new ArrayList<>();
					args.add(getToken(work.get(i - 1), tokens));
					args.add(getToken(work.get(i + 1), tokens));
					String name = switch(work.get(i)){
						case "==" -> "EQ";
						case "!=" -> "NOTEQ";
						case "<=" -> "LESSEQ";
						case ">=" -> "GREATEREQ";
						case "<" -> "LESS";
						case ">" -> "GREATER";
						default -> throw new IllegalStateException("Unexpected value: " + work.get(i));
					};
					tokens.add(new Token(name, args));
					work.remove(i);
					work.remove(i);
					work.set(i - 1, "§" + (tokens.size() - 1));
				}
				else{
					break;
				}
			}
			//AND/OR
			while(true){
				i = regIndexOf(work, "^&&|\\|\\|$");
				if(i >= 0){
					ArrayList<Token> args = new ArrayList<>();
					args.add(getToken(work.get(i - 1), tokens));
					args.add(getToken(work.get(i + 1), tokens));
					tokens.add(new Token(work.get(i).equals("&&")? "AND" : "OR", args));
					work.remove(i);
					work.remove(i);
					work.set(i - 1, "§" + (tokens.size() - 1));
				}
				else{
					break;
				}
			}
		} catch(Exception e){
			throw new IllegalArgumentException("\"" + e + "\" occurred when trying to parse animation at index " + i + "!");
		}
		if(work.size() != 1){
			//attempt to find problem symbol
			for(String badboi : work){
				if(badboi.charAt(0) != '§'){
					throw new IllegalArgumentException("Unknown symbol \"" + badboi + "\"");
				}
			}
			throw new IllegalArgumentException("Error parsing " + work);
		}
		return getToken(work.get(0), tokens);
	}
	
	/**
	 * Returns the sub array of values inside parentheses
	 */
	private static ArrayList<String> takeParen(ArrayList<String> strings, int start){
		int lvl = 0;
		if(!strings.get(start).equals("(")){
			throw new IllegalArgumentException("Expecting \"(\", received \"" + strings.get(start) + "\"");
		}
		for(int w = start; w < strings.size(); w++){
			if(strings.get(w).equals("(")){
				lvl++;
			}
			if(strings.get(w).equals(")")){
				lvl--;
				if(lvl == 0){
					return new ArrayList<>(strings.subList(start + 1, w));
				}
			}
		}
		throw new NullPointerException("expected \")\"");
	}
	
	/**
	 * Find index of the closing "}" to an opening "{"
	 */
	private static int indexOfEndOfArgs(ArrayList<String> strings, int start){
		int lvl = 0;
		if(!strings.get(start).equals("{")){
			throw new IllegalArgumentException("Expecting \"{\", received \"" + strings.get(start) + "\"");
		}
		//find correct area where arguments are
		for(int w = start; w < strings.size(); w++){
			if(strings.get(w).equals("{")){
				lvl++;
			}
			if(strings.get(w).equals("}")){
				lvl--;
				if(lvl == 0){
					return w;
				}
			}
		}
		throw new NullPointerException("expected \"}\"");
	}
	
	private static ArrayList<Token> parseArgs(ArrayList<String> strings, ArrayList<Token> tokens, int start, int end){
		//count number of arguments and split them into their own ArrayLists
		int count = 0;
		int lvl = 0;
		List<ArrayList<String>> args = new ArrayList<>();
		ArrayList<Token> tokenArgs = new ArrayList<>();
		for(int w = start + 1; w < end; w++){
			if(strings.get(w).equals(",") && lvl == 0){
				count++;
			}
			else{
				if(strings.get(w).equals("{")){
					lvl++;
				}
				if(strings.get(w).equals("}")){
					lvl--;
				}
				if(args.size() == count){
					args.add(new ArrayList<>());
				}
				args.get(count).add(strings.get(w));
			}
		}
		for(ArrayList<String> arg : args){
			tokenArgs.add(parseLoop(arg, tokens));
		}
		return tokenArgs;
	}
	
	private static int regIndexOf(ArrayList<String> input, String regex, int start){
		Pattern pattern = Pattern.compile(regex);
		return regIndexOf(input, pattern, start);
	}
	
	private static int regIndexOf(ArrayList<String> input, Pattern pattern, int start){
		for(int i = start; i < input.size(); i++){
			if(pattern.matcher(input.get(i)).find()){
				return i;
			}
		}
		return -1;
	}
	
	private static int regIndexOf(ArrayList<String> input, String regex){
		return regIndexOf(input, regex, 0);
	}
	
	private static Token getToken(String expression, ArrayList<Token> temp){
		if(expression.charAt(0) == '§'){
			return temp.get(Integer.parseInt(expression.substring(1)));
		}
		throw new IllegalArgumentException("Invalid token reference " + expression);
	}
	
}