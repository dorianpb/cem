package net.dorianpb.cem.internal.util;

import net.dorianpb.cem.internal.models.CemModelEntry;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.dorianpb.cem.internal.util.CemStringParser.ParsedFunction.ParsedFunctionBool;
import net.dorianpb.cem.internal.util.CemStringParser.ParsedFunction.ParsedFunctionFloat;
import net.dorianpb.cem.internal.util.CemStringParser.ParsedFunction.ParsedFunctionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.lang.invoke.WrongMethodTypeException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class CemStringParser{
	
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
		ArrayList<String> work = new ArrayList<>(Arrays.asList(input.replaceAll("\\s*(\\+|-|\\*|/|%|!=|\\|\\||&&|>=|<=|==|>|<)\\s*",
		                                                                        " $1 "
		                                                                       ) //ensure there is whitespace between operators
		                                                            .replaceAll("!\\s*(\\w)", " ! $1") //keep ! with expression
		                                                            .replaceAll("\\s*([(),])\\s*", " $1 ")//ensure correct whitespace for ( and )
		                                                            .replaceAll("(\\s)+", " ") //remove duplicate whitespace
		                                                            .replaceAll("§", "") //just to be safe
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
					if(sub.size() == 0){
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
		ArrayList<ArrayList<String>> args = new ArrayList<>();
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
	
	enum FLOAT_PARAMETER implements ParsedFunctionFloat{
		//render parameters
		LIMB_SWING, LIMB_SPEED, AGE, HEAD_YAW, HEAD_PITCH, //entity parameters
		HEALTH, HURT_TIME, IDLE_TIME, MAX_HEALTH, MOVE_FORWARD, MOVE_STRAFING, POS_X, POS_Y, POS_Z, REVENGE_TIME, SWING_PROGRESS, //other
		TIME, PI,
		;
		
		@Override
		public Float eval(ArrayList<ParsedExpression> args, Environment env){
			switch(this){
				//render parameters
				case AGE:
					return env.getAge();
				case HEAD_YAW:
					return env.getHead_yaw();
				case HEAD_PITCH:
					return env.getHead_pitch();
				case LIMB_SPEED:
					return env.getLimbDistance();
				case LIMB_SWING:
					return env.getLimbAngle();
				case TIME:
					MinecraftClient minecraft = MinecraftClient.getInstance();
					World world = minecraft.world;
					return (world == null)? 0F : (float) (world.getTime() % 24000L) + minecraft.getTickDelta();
				case PI:
					return 3.1415926F;
				//entity parameters
				case HEALTH:
					return env.getLivingEntity().getHealth();
				case HURT_TIME:
					return (float) env.getLivingEntity().hurtTime;
				case IDLE_TIME:
					return (float) env.getLivingEntity().getLastAttackTime();
				case MAX_HEALTH:
					return env.getLivingEntity().getMaxHealth();
				case MOVE_FORWARD:
					return env.getLivingEntity().forwardSpeed;
				case MOVE_STRAFING:
					return env.getLivingEntity().sidewaysSpeed;
				case POS_X:
					return (float) env.getEntity().getX();
				case POS_Y:
					return (float) env.getEntity().getY();
				case POS_Z:
					return (float) env.getEntity().getZ();
				case REVENGE_TIME:
					return (float) env.getLivingEntity().getLastAttackedTime();
				case SWING_PROGRESS:
					return env.getLivingEntity().handSwingProgress;
			}
			throw new NullPointerException("uwu");
		}
		
		@Override
		public ParsedFunctionType getType(){
			return ParsedFunctionType.FLOAT;
		}
		
		@Override
		public int getArgNumber(){
			return -2;
		}
	}
	
	enum FLOAT_FUNCTION implements ParsedFunctionFloat{
		SIN,
		COS,
		ASIN,
		ACOS,
		TAN,
		ATAN,
		ATAN2,
		TORAD,
		TODEG,
		MIN,
		MAX,
		CLAMP,
		ABS,
		FLOOR,
		CEIL,
		EXP,
		FRAC,
		LOG,
		POW,
		RANDOM,
		ROUND,
		SIGNUM,
		SQRT,
		FMOD,
		ADD,
		SUB,
		MULT,
		DIV,
		MOD,
		;
		
		@Override
		public Float eval(ArrayList<ParsedExpression> args, Environment env){
			return switch(this){
				case SIN -> MathHelper.sin(((ParsedExpressionFloat) args.get(0)).eval(env));
				case COS -> MathHelper.cos(((ParsedExpressionFloat) args.get(0)).eval(env));
				case ASIN -> (float) Math.asin(((ParsedExpressionFloat) args.get(0)).eval(env));
				case ACOS -> (float) Math.acos(((ParsedExpressionFloat) args.get(0)).eval(env));
				case TAN -> (float) Math.tan(((ParsedExpressionFloat) args.get(0)).eval(env));
				case ATAN -> (float) Math.atan(((ParsedExpressionFloat) args.get(0)).eval(env));
				case ATAN2 -> (float) MathHelper.atan2(((ParsedExpressionFloat) args.get(0)).eval(env), ((ParsedExpressionFloat) args.get(1)).eval(env));
				case TORAD -> (float) Math.toRadians(((ParsedExpressionFloat) args.get(0)).eval(env));
				case TODEG -> (float) Math.toDegrees(((ParsedExpressionFloat) args.get(0)).eval(env));
				case MIN -> findExtreme(args, env, false);
				case MAX -> findExtreme(args, env, true);
				case CLAMP -> MathHelper.clamp(((ParsedExpressionFloat) args.get(0)).eval(env),
				                               ((ParsedExpressionFloat) args.get(1)).eval(env),
				                               ((ParsedExpressionFloat) args.get(2)).eval(env)
				                              );
				case ABS -> MathHelper.abs(((ParsedExpressionFloat) args.get(0)).eval(env));
				case FLOOR -> (float) MathHelper.fastFloor(((ParsedExpressionFloat) args.get(0)).eval(env));
				case CEIL -> (float) MathHelper.ceil(((ParsedExpressionFloat) args.get(0)).eval(env));
				case EXP -> (float) Math.exp(((ParsedExpressionFloat) args.get(0)).eval(env));
				case FRAC -> MathHelper.fractionalPart(((ParsedExpressionFloat) args.get(0)).eval(env));
				case LOG -> (float) Math.log(((ParsedExpressionFloat) args.get(0)).eval(env));
				case POW -> (float) Math.pow(((ParsedExpressionFloat) args.get(0)).eval(env), ((ParsedExpressionFloat) args.get(1)).eval(env));
				case RANDOM -> (float) Math.random();
				case ROUND -> (float) Math.round(((ParsedExpressionFloat) args.get(0)).eval(env));
				case SIGNUM -> Math.signum(((ParsedExpressionFloat) args.get(0)).eval(env));
				case SQRT -> MathHelper.sqrt(((ParsedExpressionFloat) args.get(0)).eval(env));
				case FMOD -> MathHelper.floorMod(((ParsedExpressionFloat) args.get(0)).eval(env), ((ParsedExpressionFloat) args.get(1)).eval(env));
				case ADD -> ((ParsedExpressionFloat) args.get(0)).eval(env) + ((ParsedExpressionFloat) args.get(1)).eval(env);
				case SUB -> ((ParsedExpressionFloat) args.get(0)).eval(env) - ((ParsedExpressionFloat) args.get(1)).eval(env);
				case MULT -> ((ParsedExpressionFloat) args.get(0)).eval(env) * ((ParsedExpressionFloat) args.get(1)).eval(env);
				case DIV -> ((ParsedExpressionFloat) args.get(0)).eval(env) / ((ParsedExpressionFloat) args.get(1)).eval(env);
				case MOD -> ((ParsedExpressionFloat) args.get(0)).eval(env) % ((ParsedExpressionFloat) args.get(1)).eval(env);
			};
		}
		
		@Override
		public ParsedFunctionType getType(){
			return ParsedFunctionType.FLOAT;
		}
		
		private float findExtreme(ArrayList<ParsedExpression> args, Environment env, boolean big){
			float[] nums = new float[args.size()];
			for(int i = 0; i < args.size(); i++){
				nums[i] = ((ParsedExpressionFloat) args.get(i)).eval(env);
			}
			Arrays.sort(nums);
			return big? nums[nums.length - 1] : nums[0];
		}
		
		@Override
		public int getArgNumber(){
			return switch(this){
				case SIN, COS, ASIN, ACOS, TAN, ATAN, TORAD, TODEG, ABS, FLOOR, CEIL, EXP, FRAC, LOG, ROUND, SIGNUM, SQRT -> 1;
				case ATAN2, POW, FMOD, ADD, SUB, MULT, DIV, MOD -> 2;
				case MIN, MAX -> -1;
				case CLAMP -> 3;
				case RANDOM -> 0;
			};
		}
	}
	
	enum BOOL_PARAMETER implements ParsedFunctionBool{
		IS_ALIVE,
		IS_BURNING,
		IS_CHILD,
		IS_GLOWING,
		IS_HURT,
		IS_IN_LAVA,
		IS_IN_WATER,
		IS_INVISIBLE,
		IS_ON_GROUND,
		IS_RIDDEN,
		IS_RIDING,
		IS_SNEAKING,
		IS_SPRINTING,
		IS_WET,
		TRUE,
		FALSE,
		;
		
		@Override
		public Boolean eval(ArrayList<ParsedExpression> args, Environment env){
			return switch(this){
				case IS_ALIVE -> env.getEntity().isAlive();
				case IS_BURNING -> env.getEntity().isOnFire();
				case IS_CHILD -> env.getLivingEntity().isBaby();
				case IS_GLOWING -> env.getEntity().isGlowing();
				case IS_HURT -> env.getLivingEntity().hurtTime != 0;
				case IS_IN_LAVA -> env.getEntity().isInLava();
				case IS_IN_WATER -> env.getEntity().isSubmergedInWater();
				case IS_INVISIBLE -> env.getEntity().isInvisible();
				case IS_ON_GROUND -> env.getEntity().isOnGround();
				case IS_RIDDEN -> env.getEntity().hasPassengers();
				case IS_RIDING -> env.getEntity().hasVehicle();
				case IS_SNEAKING -> env.getEntity().isSneaking();
				case IS_SPRINTING -> env.getEntity().isSprinting();
				case IS_WET -> env.getEntity().isWet();
				case TRUE -> true;
				case FALSE -> false;
			};
		}
		
		@Override
		public ParsedFunctionType getArgType(){
			return null;
		}
		
		@Override
		public ParsedFunctionType getType(){
			return ParsedFunctionType.BOOL;
		}
		
		@Override
		public int getArgNumber(){
			return -2;
		}
	}
	
	enum BOOL_FUNCTION_FLOAT implements ParsedFunctionBool{
		BETWEEN, EQUALS, IN, GREATER, GREATEREQ, LESS, LESSEQ, EQ, NOTEQ,
		;
		
		@Override
		public Boolean eval(ArrayList<ParsedExpression> args, Environment env){
			switch(this){
				case BETWEEN:
					return ((ParsedExpressionFloat) args.get(1)).eval(env) <= ((ParsedExpressionFloat) args.get(0)).eval(env) &&
					       ((ParsedExpressionFloat) args.get(0)).eval(env) <= ((ParsedExpressionFloat) args.get(2)).eval(env);
				case EQUALS:
					return ((ParsedExpressionFloat) args.get(1)).eval(env) - ((ParsedExpressionFloat) args.get(2)).eval(env) <=
					       ((ParsedExpressionFloat) args.get(0)).eval(env) &&
					       ((ParsedExpressionFloat) args.get(0)).eval(env) <=
					       ((ParsedExpressionFloat) args.get(1)).eval(env) + ((ParsedExpressionFloat) args.get(2)).eval(env);
				case IN:
					boolean x = false;
					for(int i = 1; i < args.size(); i++){
						x = (x || ((ParsedExpressionFloat) args.get(0)).eval(env) == ((ParsedExpressionFloat) args.get(i)).eval(env));
					}
					return x;
				case GREATER:
					return ((ParsedExpressionFloat) args.get(0)).eval(env) > ((ParsedExpressionFloat) args.get(1)).eval(env);
				case GREATEREQ:
					return ((ParsedExpressionFloat) args.get(0)).eval(env) >= ((ParsedExpressionFloat) args.get(1)).eval(env);
				case LESS:
					return ((ParsedExpressionFloat) args.get(0)).eval(env) < ((ParsedExpressionFloat) args.get(1)).eval(env);
				case LESSEQ:
					return ((ParsedExpressionFloat) args.get(0)).eval(env) <= ((ParsedExpressionFloat) args.get(1)).eval(env);
				case EQ:
					return ((ParsedExpressionFloat) args.get(0)).eval(env) == ((ParsedExpressionFloat) args.get(1)).eval(env);
				case NOTEQ:
					return ((ParsedExpressionFloat) args.get(0)).eval(env) != ((ParsedExpressionFloat) args.get(1)).eval(env);
			}
			throw new NullPointerException("my brain ... TREMBLES!");
		}
		
		@Override
		public ParsedFunctionType getArgType(){
			return ParsedFunctionType.FLOAT;
		}
		
		@Override
		public ParsedFunctionType getType(){
			return ParsedFunctionType.BOOL;
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
	
	enum BOOL_FUNCTION_BOOL implements ParsedFunctionBool{
		NOT, AND, OR,
		;
		
		@Override
		public Boolean eval(ArrayList<ParsedExpression> args, Environment env){
			return switch(this){
				case NOT -> !((ParsedExpressionBool) args.get(0)).eval(env);
				case AND -> ((ParsedExpressionBool) args.get(0)).eval(env) && ((ParsedExpressionBool) args.get(1)).eval(env);
				case OR -> ((ParsedExpressionBool) args.get(0)).eval(env) || ((ParsedExpressionBool) args.get(1)).eval(env);
			};
		}
		
		@Override
		public ParsedFunctionType getArgType(){
			return ParsedFunctionType.BOOL;
		}
		
		@Override
		public ParsedFunctionType getType(){
			return ParsedFunctionType.BOOL;
		}
		
		@Override
		public int getArgNumber(){
			return (this == NOT)? 1 : 2;
		}
	}
	
	interface ParsedFunction{
		ParsedFunctionType getType();
		
		int getArgNumber();
		
		enum ParsedFunctionType{
			FLOAT, BOOL,
		}
		
		interface ParsedFunctionFloat extends ParsedFunction{
			Float eval(ArrayList<ParsedExpression> args, Environment env);
			
			@Override
			default ParsedFunctionType getType(){
				return ParsedFunctionType.FLOAT;
			}
		}
		
		interface ParsedFunctionBool extends ParsedFunction{
			Boolean eval(ArrayList<ParsedExpression> args, Environment env);
			
			ParsedFunctionType getArgType();
			
			@Override
			default ParsedFunctionType getType(){
				return ParsedFunctionType.BOOL;
			}
		}
	}
	
	public interface ParsedExpression{
		default void checkArgs(ArrayList<ParsedExpression> args, int paramNum){
			if(args == null && paramNum != -2){
				throw new IllegalArgumentException("Function \"" + this.getName().toLowerCase() + "\" should be be followed with \"()\", as it is not a " + "parameter!");
			}
			else if(args != null && paramNum == -2){
				throw new IllegalArgumentException("Parameter \"" + this.getName().toLowerCase() + "\" does not take arguments and should not have any \"()" + "\"!");
			}
			else if(paramNum > -1 && args.size() != paramNum){
				throw new IllegalArgumentException("Function \"" +
				                                   this.getName().toLowerCase() +
				                                   "\" needs exactly " +
				                                   paramNum +
				                                   " parameters, but " +
				                                   args.size() +
				                                   " " +
				                                   ((args.size() == 1)? "was" : "were") +
				                                   " " +
				                                   "given!");
			}
		}
		
		String getName();
		
		default float eval(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, Entity entity, CemModelRegistry registry){
			if(this.getClass() == ParsedExpressionBool.class){
				throw new WrongMethodTypeException("\"" + this.getName() + " must evaluate to a number, not a boolean!");
			}
			else{
				return ((ParsedExpressionFloat) this).eval(new Environment(limbAngle, limbDistance, age, head_yaw, head_pitch, entity));
			}
		}
	}
	
	static class ParsedExpressionFloat implements ParsedExpression{
		private final ParsedFunctionFloat         operation;
		private final ArrayList<ParsedExpression> arguments;
		
		ParsedExpressionFloat(Token token, CemModelRegistry registry, CemModelEntry parent){
			ParsedFunction temp = matchToken(token, registry, parent);
			if(temp.getType() == ParsedFunctionType.FLOAT){
				this.operation = (ParsedFunctionFloat) temp;
			}
			else{
				throw new InvalidParameterException("\"" + token.getName() + "\" is not a number and will not return a number!");
			}
			if(token.getArgs() != null && this.operation.getClass() != ParsedIf.class){
				this.arguments = new ArrayList<>();
				for(Token arg : token.getArgs()){
					try{
						this.arguments.add(new ParsedExpressionFloat(arg, registry, parent));
					} catch(InvalidParameterException ignored){
						throw new IllegalArgumentException("\"" + token.getName() + "\" requires numbers as arguments and \"" + arg.getName() + "\" " + "is not a number!");
					}
					
				}
			}
			else if(token.getArgs() != null){
				this.arguments = new ArrayList<>();
			}
			else{
				this.arguments = null;
			}
			checkArgs(this.arguments, this.operation.getArgNumber());
		}
		
		float eval(Environment env){
			return operation.eval(arguments, env);
		}
		
		@Override
		public String getName(){
			return this.operation.toString();
		}
		
	}
	
	/**
	 * The "if" operator is the only operator that actually steals arguments from its container class, the ParsedExpressionFloat
	 */
	static class ParsedIf implements ParsedFunctionFloat{
		private final ArrayList<ParsedExpressionBool>  conditions;
		private final ArrayList<ParsedExpressionFloat> expressions;
		
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
				if(this.conditions.size() == 0){
					throw new IllegalArgumentException("\"" + token.getName() + "\" requires at least one condition!");
					
				}
				else if(this.conditions.size() + 1 != this.expressions.size()){
					throw new IllegalArgumentException("\"" + token.getName() + "\" is missing an \"val_else\" value, please add a number at the " + "end" + ".");
				}
			}
		}
		
		@Override
		public Float eval(ArrayList<ParsedExpression> args, Environment env){
			for(int i = 0; i < this.conditions.size(); i++){
				if(this.conditions.get(i).eval(env)){
					return this.expressions.get(i).eval(env);
				}
			}
			return this.expressions.get(this.expressions.size() - 1).eval(env);
		}
		
		@Override
		public ParsedFunctionType getType(){
			return ParsedFunctionType.FLOAT;
		}
		
		@Override
		public int getArgNumber(){
			return -1;
		}
		
	}
	
	static class ParsedExpressionBool implements ParsedExpression{
		private final ParsedFunctionBool          operation;
		private final ArrayList<ParsedExpression> arguments;
		
		ParsedExpressionBool(Token token, CemModelRegistry registry, CemModelEntry parent){
			ParsedFunction temp = matchToken(token, registry, parent);
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
			checkArgs(this.arguments, this.operation.getArgNumber());
		}
		
		boolean eval(Environment env){
			return this.operation.eval(arguments, env);
		}
		
		@Override
		public String getName(){
			return this.operation.toString();
		}
	}
	
	static class ParsedNumber implements ParsedFunctionFloat{
		private final float num;
		
		ParsedNumber(NumToken token){
			this.num = token.getNum();
		}
		
		@Override
		public int getArgNumber(){
			return -2;
		}
		
		@Override
		public Float eval(ArrayList<ParsedExpression> args, Environment env){
			return this.num;
		}
	}
	
	static class ParsedVar implements ParsedFunctionFloat{
		private static final Pattern       PATTERN = Pattern.compile("(\\w\\d?:?)+[.][trs][xyz]");
		private final        CemModelEntry entry;
		private final        char          val;
		private final        char          axis;
		
		ParsedVar(Token token, CemModelRegistry registry, CemModelEntry parent){
			if(!PATTERN.matcher(token.getName()).find()){
				throw new IllegalArgumentException("\"" + token.getName() + "\" isn't a reference to a model part");
			}
			this.entry = registry.findChild(token.getName().substring(0, token.getName().indexOf(".")), parent);
			this.val = token.getName().charAt(token.getName().indexOf(".") + 1);
			this.axis = token.getName().charAt(token.getName().indexOf(".") + 2);
		}
		
		@Override
		public int getArgNumber(){
			return -2;
		}
		
		@Override
		public Float eval(ArrayList<ParsedExpression> args, Environment env){
			return switch(val){
				case 't' -> entry.getTranslate(axis);
				case 'r' -> entry.getModel().getRotation(axis);
				case 's' -> entry.getModel().getScale(axis);
				default -> throw new IllegalStateException("Unknown operation \"" + val + "\"");
			};
		}
	}
	
	private static class Token{
		private final String           name;
		private final ArrayList<Token> args;
		
		Token(String name, ArrayList<Token> args){
			this.name = name;
			this.args = args;
		}
		
		Token(String name){
			this.name = name;
			this.args = null;
		}
		
		String getName(){
			return name;
		}
		
		ArrayList<Token> getArgs(){
			return args;
		}
	}
	
	private static class NumToken extends Token{
		private final float num;
		
		NumToken(float num){
			super("NUM", null);
			this.num = num;
		}
		
		float getNum(){
			return num;
		}
	}
	
	private static class Environment{
		private final float limbAngle, limbDistance, age, head_yaw, head_pitch;
		private final Entity entity;
		
		
		private Environment(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, Entity entity){
			this.limbAngle = limbAngle;
			this.limbDistance = limbDistance;
			this.age = age;
			this.head_yaw = head_yaw;
			this.head_pitch = head_pitch;
			this.entity = entity;
		}
		
		private float getLimbAngle(){
			return limbAngle;
		}
		
		private float getLimbDistance(){
			return limbDistance;
		}
		
		private float getAge(){
			return age;
		}
		
		private float getHead_yaw(){
			return head_yaw;
		}
		
		private float getHead_pitch(){
			return head_pitch;
		}
		
		private Entity getEntity(){
			return entity;
		}
		
		private LivingEntity getLivingEntity(){
			return (LivingEntity) entity;
		}
	}
	
}