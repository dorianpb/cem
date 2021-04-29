package net.dorianpb.cem.internal;

import net.dorianpb.cem.internal.cemStringParser.ParsedFunction.ParsedFunctionBool;
import net.dorianpb.cem.internal.cemStringParser.ParsedFunction.ParsedFunctionFloat;
import net.dorianpb.cem.internal.cemStringParser.ParsedFunction.ParsedFunctionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.lang.invoke.WrongMethodTypeException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

class cemStringParser{
    /* EVALUATION, SPECIFIC TO CEM BUT COULD BE USED AS REFERENCE FOR THE PARSER BELOW */
    
    private static class DexEnvironment{
        private static float limbAngle;
        private static float limbDistance;
        private static float age;
        private static float head_yaw;
        private static float head_pitch;
        private static LivingEntity livingEntity;
        private static cemModelRegistry registry;
        
        static void setEnv(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, LivingEntity livingEntity, cemModelRegistry registry){
            DexEnvironment.limbAngle = limbAngle;
            DexEnvironment.limbDistance = limbDistance;
            DexEnvironment.age = age;
            DexEnvironment.head_yaw = head_yaw;
            DexEnvironment.head_pitch = head_pitch;
            DexEnvironment.livingEntity = livingEntity;
            DexEnvironment.registry = registry;
        }
        
        static void setRegistry(cemModelRegistry registry){
            DexEnvironment.registry = registry;
        }
        
        static float getLimbAngle(){
            return limbAngle;
        }
        
        static float getLimbDistance(){
            return limbDistance;
        }
        
        static float getAge(){
            return age;
        }
        
        static float getHead_yaw(){
            return head_yaw;
        }
        
        static float getHead_pitch(){
            return head_pitch;
        }
        
        static LivingEntity getLivingEntity(){
            return livingEntity;
        }
        
        static cemModelRegistry getRegistry(){
            return DexEnvironment.registry;
        }
        
    }
    
    static ParsedExpression parse(String expr, cemModelRegistry registry, cemModelEntry parent){
        Token token = initParseLoop(expr);
        ParsedFunction matched = matchToken(token);
        DexEnvironment.setRegistry(registry);
        ParsedVar.parent = parent;
        if(matched.getType() == ParsedFunctionType.FLOAT){
            return new ParsedExpressionFloat(token);
        } else{
            return new ParsedExpressionBool(token);
        }
    }
    
    static ParsedFunction matchToken(Token token){
        if(token.getName().equals("NUM")){
            try{
                return new ParsedNumber((NumToken) token);
            } catch(Exception ignored){
                throw new IllegalArgumentException("Why is there a token named \"NUM\" that is not a NumToken?");
            }
        } else if(token.getName().contains(".")){
            return new ParsedVar(token);
        } else if(token.getName().equalsIgnoreCase("if")){
            return new ParsedIf(token);
        } else{
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
    
    interface ParsedFunction{
        ParsedFunctionType getType();
        
        int getArgNumber();
        
        enum ParsedFunctionType{
            FLOAT,
            BOOL,
        }
        
        interface ParsedFunctionFloat extends ParsedFunction{
            @Override
            default ParsedFunctionType getType(){
                return ParsedFunctionType.FLOAT;
            }
            
            Float eval(ArrayList<ParsedExpression> args);
        }
        
        interface ParsedFunctionBool extends ParsedFunction{
            @Override
            default ParsedFunctionType getType(){
                return ParsedFunctionType.BOOL;
            }
            
            Boolean eval(ArrayList<ParsedExpression> args);
            
            ParsedFunctionType getArgType();
        }
    }
    
    interface ParsedExpression{
        String getName();
        
        default void checkArgs(ArrayList<ParsedExpression> args, int paramNum){
            if(args == null && paramNum != -2){
                throw new IllegalArgumentException("Function \"" + this.getName().toLowerCase() + "\" should be be followed with \"()\", as it is not a parameter!");
            } else if(args != null && paramNum == -2){
                throw new IllegalArgumentException("Parameter \"" + this.getName().toLowerCase() + "\" does not take arguments and should not have any \"()\"!");
            } else if(paramNum > -1 && args.size() != paramNum){
                throw new IllegalArgumentException("Function \"" + this.getName().toLowerCase() + "\" needs exactly " + paramNum + " parameters, but " + args.size() + " " + ((args.size() == 1) ? "was" : "were") + " given!");
            }
        }
        
        default float eval(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, LivingEntity livingEntity, cemModelRegistry registry){
            if(this.getClass() == ParsedExpressionBool.class){
                throw new WrongMethodTypeException("\"" + this.getName() + " must evaluate to a number, not a boolean!");
            } else{
                DexEnvironment.setEnv(limbAngle, limbDistance, age, head_yaw, head_pitch, livingEntity, registry);
                return ((ParsedExpressionFloat) this).eval();
            }
        }
    }
    
    static class ParsedExpressionFloat implements ParsedExpression{
        private final ParsedFunctionFloat operation;
        private final ArrayList<ParsedExpression> arguments;
        
        ParsedExpressionFloat(Token token){
            ParsedFunction temp = matchToken(token);
            if(temp.getType() == ParsedFunctionType.FLOAT){
                this.operation = (ParsedFunctionFloat) temp;
            } else{
                throw new InvalidParameterException("\"" + token.getName() + "\" is not a number and will not return a number!");
            }
            if(token.getArgs() != null && this.operation.getClass() != ParsedIf.class){
                this.arguments = new ArrayList<>();
                for(Token arg : token.getArgs()){
                    try{
                        this.arguments.add(new ParsedExpressionFloat(arg));
                    } catch(InvalidParameterException ignored){
                        throw new IllegalArgumentException("\"" + token.getName() + "\" requires numbers as arguments and \"" + arg.getName() + "\" is not a number!");
                    }
                    
                }
            } else if(token.getArgs() != null){
                this.arguments = new ArrayList<>();
            } else{
                this.arguments = null;
            }
            checkArgs(this.arguments, this.operation.getArgNumber());
        }
        
        float eval(){
            return operation.eval(arguments);
        }
        
        @Override
        public String getName(){
            return this.operation.toString();
        }
        
    }
    
    /**
     * The "if" operator is the only operator that actually steals arguments from it's container class, the ParsedExpressionFloat
     */
    static class ParsedIf implements ParsedFunctionFloat{
        private final ArrayList<ParsedExpressionBool> conditions;
        private final ArrayList<ParsedExpressionFloat> expressions;
        
        ParsedIf(Token token){
            if(token.getArgs() == null){
                throw new IllegalArgumentException("\"" + token.getName() + "\" requires arguments!");
            } else{
                this.conditions = new ArrayList<>();
                this.expressions = new ArrayList<>();
                for(int i = 0; i < token.getArgs().size(); i++){
                    ParsedFunctionType wantedType = (i % 2 == 1 || i == token.getArgs().size() - 1) ? ParsedFunctionType.FLOAT : ParsedFunctionType.BOOL;
                    if(wantedType == ParsedFunctionType.BOOL){
                        try{
                            this.conditions.add(new ParsedExpressionBool(token.getArgs().get(i)));
                        } catch(InvalidParameterException ignored){
                            throw new IllegalArgumentException("\"" + token.getName() + "\" requires a bool for argument #" + (i + 1) + ", but a number was provided");
                        }
                    } else{
                        try{
                            this.expressions.add(new ParsedExpressionFloat(token.getArgs().get(i)));
                        } catch(InvalidParameterException ignored){
                            throw new IllegalArgumentException("\"" + token.getName() + "\" requires a number for argument #" + (i + 1) + ", but a bool was provided");
                        }
                    }
                }
                if(this.conditions.size() == 0){
                    throw new IllegalArgumentException("\"" + token.getName() + "\" requires at least one condition!");
                    
                } else if(this.conditions.size() + 1 != this.expressions.size()){
                    throw new IllegalArgumentException("\"" + token.getName() + "\" is missing an \"val_else\" value, please add a number at the end.");
                }
            }
        }
        
        @Override
        public ParsedFunctionType getType(){
            return ParsedFunctionType.FLOAT;
        }
        
        @Override
        public Float eval(ArrayList<ParsedExpression> args){
            for(int i = 0; i < this.conditions.size(); i++){
                if(this.conditions.get(i).eval()){
                    return this.expressions.get(i).eval();
                }
            }
            return this.expressions.get(this.expressions.size() - 1).eval();
        }
        
        @Override
        public int getArgNumber(){
            return -1;
        }
        
        
    }
    
    static class ParsedExpressionBool implements ParsedExpression{
        private final ParsedFunctionBool operation;
        private final ArrayList<ParsedExpression> arguments;
        
        ParsedExpressionBool(Token token){
            ParsedFunction temp = matchToken(token);
            if(temp.getType() == ParsedFunctionType.BOOL){
                this.operation = (ParsedFunctionBool) temp;
            } else{
                throw new InvalidParameterException("\"" + token.getName() + "\" is not a number and will not return a number!");
            }
            if(token.getArgs() != null){
                this.arguments = new ArrayList<>();
                for(Token arg : token.getArgs()){
                    if(this.operation.getArgType() == ParsedFunctionType.FLOAT){
                        try{
                            this.arguments.add(new ParsedExpressionFloat(arg));
                        } catch(InvalidParameterException ignored){
                            throw new IllegalArgumentException("\"" + token.getName() + "\" requires numbers as arguments and \"" + arg.getName() + "\" is not a number!");
                        }
                    } else{
                        try{
                            this.arguments.add(new ParsedExpressionBool(arg));
                        } catch(InvalidParameterException ignored){
                            throw new IllegalArgumentException("\"" + token.getName() + "\" requires bools as arguments and \"" + arg.getName() + "\" is not a bool!");
                        }
                    }
                }
            } else{
                this.arguments = null;
            }
            checkArgs(this.arguments, this.operation.getArgNumber());
        }
        
        boolean eval(){
            return this.operation.eval(arguments);
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
        public Float eval(ArrayList<ParsedExpression> args){
            return this.num;
        }
    }
    
    static class ParsedVar implements ParsedFunctionFloat{
        private static final Pattern PATTERN = Pattern.compile("(\\w\\d?:?)+[.][trs][xyz]");
        static cemModelEntry parent;
        private final cemModelEntry entry;
        private final char val;
        private final char axis;
        
        ParsedVar(Token token){
            if(!PATTERN.matcher(token.getName()).find()){
                throw new IllegalArgumentException("\"" + token.getName() + "\" isn't a reference to a model part");
            }
            this.entry = DexEnvironment.getRegistry().findChild(token.getName().substring(0, token.getName().indexOf(".")), parent);
            this.val = token.getName().charAt(token.getName().indexOf(".") + 1);
            this.axis = token.getName().charAt(token.getName().indexOf(".") + 2);
        }
        
        @Override
        public int getArgNumber(){
            return -2;
        }
        
        @Override
        public Float eval(ArrayList<ParsedExpression> args){
            switch(val){
                case 't':
                    return entry.getTranslate(axis);
                case 'r':
                    return entry.getModel().getRotation(axis);
                case 's':
                    return entry.getModel().getScale(axis);
                default:
                    throw new IllegalStateException("Unknown operation \"" + val + "\"");
            }
        }
    }
    
    
    enum FLOAT_PARAMETER implements ParsedFunctionFloat{
        //render parameters
        LIMB_SWING,
        LIMB_SPEED,
        AGE,
        HEAD_YAW,
        HEAD_PITCH,
        //entity parameters
        HEALTH,
        HURT_TIME,
        IDLE_TIME,
        MAX_HEALTH,
        MOVE_FORWARD,
        MOVE_STRAFING,
        POS_X,
        POS_Y,
        POS_Z,
        REVENGE_TIME,
        SWING_PROGRESS,
        //other
        TIME,
        PI,
        ;
        
        @Override
        public ParsedFunctionType getType(){
            return ParsedFunctionType.FLOAT;
        }
        
        @Override
        public int getArgNumber(){
            return -2;
        }
        
        @Override
        public Float eval(ArrayList<ParsedExpression> args){
            switch(this){
                //render parameters
                case AGE:
                    return DexEnvironment.getAge();
                case HEAD_YAW:
                    return DexEnvironment.getHead_yaw();
                case HEAD_PITCH:
                    return DexEnvironment.getHead_pitch();
                case LIMB_SPEED:
                    return DexEnvironment.getLimbDistance();
                case LIMB_SWING:
                    return DexEnvironment.getLimbAngle();
                case TIME:
                    MinecraftClient minecraft = MinecraftClient.getInstance();
                    World world = minecraft.world;
                    return (world == null) ? 0F : (float) (world.getTime() % 24000L) + minecraft.getTickDelta();
                case PI:
                    return 3.1415926F;
                //entity parameters
                case HEALTH:
                    return DexEnvironment.getLivingEntity().getHealth();
                case HURT_TIME:
                    return (float) DexEnvironment.getLivingEntity().hurtTime;
                case IDLE_TIME:
                    return (float) DexEnvironment.getLivingEntity().getLastAttackTime();
                case MAX_HEALTH:
                    return DexEnvironment.getLivingEntity().getMaxHealth();
                case MOVE_FORWARD:
                    return DexEnvironment.getLivingEntity().forwardSpeed;
                case MOVE_STRAFING:
                    return DexEnvironment.getLivingEntity().sidewaysSpeed;
                case POS_X:
                    return (float) DexEnvironment.getLivingEntity().getX();
                case POS_Y:
                    return (float) DexEnvironment.getLivingEntity().getY();
                case POS_Z:
                    return (float) DexEnvironment.getLivingEntity().getZ();
                case REVENGE_TIME:
                    return (float) DexEnvironment.getLivingEntity().getLastAttackedTime();
                case SWING_PROGRESS:
                    return DexEnvironment.getLivingEntity().handSwingProgress;
            }
            throw new NullPointerException("uwu");
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
        
        private float findExtreme(ArrayList<ParsedExpression> args, boolean big){
            float[] nums = new float[args.size()];
            for(int i = 0; i < args.size(); i++){
                nums[i] = ((ParsedExpressionFloat) args.get(i)).eval();
            }
            Arrays.sort(nums);
            return big ? nums[nums.length - 1] : nums[0];
        }
        
        @Override
        public ParsedFunctionType getType(){
            return ParsedFunctionType.FLOAT;
        }
        
        @Override
        public int getArgNumber(){
            int paramNum;
            switch(this){
                case SIN:
                case COS:
                case ASIN:
                case ACOS:
                case TAN:
                case ATAN:
                case TORAD:
                case TODEG:
                case ABS:
                case FLOOR:
                case CEIL:
                case EXP:
                case FRAC:
                case LOG:
                case ROUND:
                case SIGNUM:
                case SQRT:
                    paramNum = 1;
                    break;
                case ATAN2:
                case POW:
                case FMOD:
                case ADD:
                case SUB:
                case MULT:
                case DIV:
                case MOD:
                    paramNum = 2;
                    break;
                case MIN:
                case MAX:
                    paramNum = -1;
                    break;
                case CLAMP:
                    paramNum = 3;
                    break;
                case RANDOM:
                    paramNum = 0;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + this);
            }
            return paramNum;
        }
        
        @Override
        public Float eval(ArrayList<ParsedExpression> args){
            switch(this){
                case SIN:
                    return MathHelper.sin(((ParsedExpressionFloat) args.get(0)).eval());
                case COS:
                    return MathHelper.cos(((ParsedExpressionFloat) args.get(0)).eval());
                case ASIN:
                    return (float) Math.asin(((ParsedExpressionFloat) args.get(0)).eval());
                case ACOS:
                    return (float) Math.acos(((ParsedExpressionFloat) args.get(0)).eval());
                case TAN:
                    return (float) Math.tan(((ParsedExpressionFloat) args.get(0)).eval());
                case ATAN:
                    return (float) Math.atan(((ParsedExpressionFloat) args.get(0)).eval());
                case ATAN2:
                    return (float) MathHelper.atan2(((ParsedExpressionFloat) args.get(0)).eval(), ((ParsedExpressionFloat) args.get(1)).eval());
                case TORAD:
                    return (float) Math.toRadians(((ParsedExpressionFloat) args.get(0)).eval());
                case TODEG:
                    return (float) Math.toDegrees(((ParsedExpressionFloat) args.get(0)).eval());
                case MIN:
                    return findExtreme(args, false);
                case MAX:
                    return findExtreme(args, true);
                case CLAMP:
                    return MathHelper.clamp(((ParsedExpressionFloat) args.get(0)).eval(), ((ParsedExpressionFloat) args.get(1)).eval(), ((ParsedExpressionFloat) args.get(2)).eval());
                case ABS:
                    return MathHelper.abs(((ParsedExpressionFloat) args.get(0)).eval());
                case FLOOR:
                    return (float) MathHelper.fastFloor(((ParsedExpressionFloat) args.get(0)).eval());
                case CEIL:
                    return (float) MathHelper.ceil(((ParsedExpressionFloat) args.get(0)).eval());
                case EXP:
                    return (float) Math.exp(((ParsedExpressionFloat) args.get(0)).eval());
                case FRAC:
                    return MathHelper.fractionalPart(((ParsedExpressionFloat) args.get(0)).eval());
                case LOG:
                    return (float) Math.log(((ParsedExpressionFloat) args.get(0)).eval());
                case POW:
                    return (float) Math.pow(((ParsedExpressionFloat) args.get(0)).eval(), ((ParsedExpressionFloat) args.get(1)).eval());
                case RANDOM:
                    return (float) Math.random();
                case ROUND:
                    return (float) Math.round(((ParsedExpressionFloat) args.get(0)).eval());
                case SIGNUM:
                    return Math.signum(((ParsedExpressionFloat) args.get(0)).eval());
                case SQRT:
                    return MathHelper.sqrt(((ParsedExpressionFloat) args.get(0)).eval());
                case FMOD:
                    return MathHelper.floorMod(((ParsedExpressionFloat) args.get(0)).eval(), ((ParsedExpressionFloat) args.get(1)).eval());
                case ADD:
                    return ((ParsedExpressionFloat) args.get(0)).eval() + ((ParsedExpressionFloat) args.get(1)).eval();
                case SUB:
                    return ((ParsedExpressionFloat) args.get(0)).eval() - ((ParsedExpressionFloat) args.get(1)).eval();
                case MULT:
                    return ((ParsedExpressionFloat) args.get(0)).eval() * ((ParsedExpressionFloat) args.get(1)).eval();
                case DIV:
                    return ((ParsedExpressionFloat) args.get(0)).eval() / ((ParsedExpressionFloat) args.get(1)).eval();
                case MOD:
                    return ((ParsedExpressionFloat) args.get(0)).eval() % ((ParsedExpressionFloat) args.get(1)).eval();
            }
            throw new NullPointerException("onii-chan?");
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
        public ParsedFunctionType getType(){
            return ParsedFunctionType.BOOL;
        }
        
        @Override
        public Boolean eval(ArrayList<ParsedExpression> args){
            switch(this){
                case IS_ALIVE:
                    return DexEnvironment.getLivingEntity().isAlive();
                case IS_BURNING:
                    return DexEnvironment.getLivingEntity().isOnFire();
                case IS_CHILD:
                    return DexEnvironment.getLivingEntity().isBaby();
                case IS_GLOWING:
                    return DexEnvironment.getLivingEntity().isGlowing();
                case IS_HURT:
                    return DexEnvironment.getLivingEntity().hurtTime != 0;
                case IS_IN_LAVA:
                    return DexEnvironment.getLivingEntity().isInLava();
                case IS_IN_WATER:
                    return DexEnvironment.getLivingEntity().isSubmergedInWater();
                case IS_INVISIBLE:
                    return DexEnvironment.getLivingEntity().isInvisible();
                case IS_ON_GROUND:
                    return DexEnvironment.getLivingEntity().isOnGround();
                case IS_RIDDEN:
                    return DexEnvironment.getLivingEntity().hasPassengers();
                case IS_RIDING:
                    return DexEnvironment.getLivingEntity().hasVehicle();
                case IS_SNEAKING:
                    return DexEnvironment.getLivingEntity().isSneaking();
                case IS_SPRINTING:
                    return DexEnvironment.getLivingEntity().isSprinting();
                case IS_WET:
                    return DexEnvironment.getLivingEntity().isWet();
                case TRUE:
                    return true;
                case FALSE:
                    return false;
                
            }
            throw new NullPointerException("owo");
        }
        
        @Override
        public ParsedFunctionType getArgType(){
            return null;
        }
        
        @Override
        public int getArgNumber(){
            return -2;
        }
    }
    
    enum BOOL_FUNCTION_FLOAT implements ParsedFunctionBool{
        BETWEEN,
        EQUALS,
        IN,
        GREATER,
        GREATEREQ,
        LESS,
        LESSEQ,
        EQ,
        NOTEQ,
        ;
        
        @Override
        public ParsedFunctionType getType(){
            return ParsedFunctionType.BOOL;
        }
        
        @Override
        public Boolean eval(ArrayList<ParsedExpression> args){
            switch(this){
                case BETWEEN:
                    return ((ParsedExpressionFloat) args.get(1)).eval() <= ((ParsedExpressionFloat) args.get(0)).eval() && ((ParsedExpressionFloat) args.get(0)).eval() <= ((ParsedExpressionFloat) args.get(2)).eval();
                case EQUALS:
                    return ((ParsedExpressionFloat) args.get(1)).eval() - ((ParsedExpressionFloat) args.get(2)).eval() <= ((ParsedExpressionFloat) args.get(0)).eval() && ((ParsedExpressionFloat) args.get(0)).eval() <= ((ParsedExpressionFloat) args.get(1)).eval() + ((ParsedExpressionFloat) args.get(2)).eval();
                case IN:
                    boolean x = false;
                    for(int i = 1; i < args.size(); i++){
                        x = (x || ((ParsedExpressionFloat) args.get(0)).eval() == ((ParsedExpressionFloat) args.get(i)).eval());
                    }
                    return x;
                case GREATER:
                    return ((ParsedExpressionFloat) args.get(0)).eval() > ((ParsedExpressionFloat) args.get(1)).eval();
                case GREATEREQ:
                    return ((ParsedExpressionFloat) args.get(0)).eval() >= ((ParsedExpressionFloat) args.get(1)).eval();
                case LESS:
                    return ((ParsedExpressionFloat) args.get(0)).eval() < ((ParsedExpressionFloat) args.get(1)).eval();
                case LESSEQ:
                    return ((ParsedExpressionFloat) args.get(0)).eval() <= ((ParsedExpressionFloat) args.get(1)).eval();
                case EQ:
                    return ((ParsedExpressionFloat) args.get(0)).eval() == ((ParsedExpressionFloat) args.get(1)).eval();
                case NOTEQ:
                    return ((ParsedExpressionFloat) args.get(0)).eval() != ((ParsedExpressionFloat) args.get(1)).eval();
            }
            throw new NullPointerException("my brain ... TREMBLES!");
        }
        
        @Override
        public ParsedFunctionType getArgType(){
            return ParsedFunctionType.FLOAT;
        }
        
        @Override
        public int getArgNumber(){
            int paramNum;
            switch(this){
                case BETWEEN:
                case EQUALS:
                    paramNum = 3;
                    break;
                case IN:
                    paramNum = -1;
                    break;
                case GREATER:
                case GREATEREQ:
                case LESS:
                case EQ:
                case LESSEQ:
                case NOTEQ:
                    paramNum = 2;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + this);
            }
            return paramNum;
        }
    }
    
    enum BOOL_FUNCTION_BOOL implements ParsedFunctionBool{
        NOT,
        AND,
        OR,
        ;
        
        @Override
        public ParsedFunctionType getType(){
            return ParsedFunctionType.BOOL;
        }
        
        @Override
        public Boolean eval(ArrayList<ParsedExpression> args){
            switch(this){
                case NOT:
                    return !((ParsedExpressionBool) args.get(0)).eval();
                case AND:
                    return ((ParsedExpressionBool) args.get(0)).eval() && ((ParsedExpressionBool) args.get(1)).eval();
                case OR:
                    return ((ParsedExpressionBool) args.get(0)).eval() || ((ParsedExpressionBool) args.get(1)).eval();
            }
            throw new NullPointerException("sussy");
        }
        
        @Override
        public ParsedFunctionType getArgType(){
            return ParsedFunctionType.BOOL;
        }
        
        @Override
        public int getArgNumber(){
            return (this == NOT) ? 1 : 2;
        }
    }
    
    /* CREATION OF TOKENS; THIS COULD POSSIBLY BE USED ELSEWHERE, maybe put it into its own class? */
    
    
    /**
     * This runs first to prepare the string, then sends it to the parseLoop to be turned into a giant Token
     */
    private static Token initParseLoop(String input){
        ArrayList<String> work = new ArrayList<>(
                Arrays.asList(
                        input
                                .replaceAll("\\s*(\\+|-|\\*|/|%|!=|\\|\\||&&|>=|<=|==|>|<)\\s*", " $1 ") //ensure there is whitespace between operators
                                .replaceAll("!\\s*(\\w)", " ! $1") //keep ! with expression
                                .replaceAll("\\s*([(),])\\s*", " $1 ")//ensure correct whitespace for ( and )
                                .replaceAll("(\\s)+", " ") //remove duplicate whitespace
                                .replaceAll("§", "") //just to be safe
                                .trim()
                                .split(" ")
                )
        );
        //try to eliminate garbage
        Pattern garbagePattern = Pattern.compile("^[+\\-*/%!=|&><\\w(),].*$");
        for(String badboi : work){
            if(!garbagePattern.matcher(badboi).find()){
                throw new IllegalArgumentException("Garbage symbol \"" + badboi + "\"");
            }
        }
        //find functions, turn parentheses into curly braces so that we don't parse them as grouped expressions and we correctly parse them later
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
            } else{
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
        //convert functions to tokens
        while(true){
            int i = regIndexOf(work, "^\\{$");
            if(i >= 0){
                int k = indexOfEndOfArgs(work, i);
                tokens.add(new Token(work.get(i - 1), parseArgs(work, tokens, i, k)));
                for(int j = (k - i + 2); j > 0; j--){
                    work.remove(i - 1);
                }
                work.add(i - 1, "§" + (tokens.size() - 1));
                
            } else{
                break;
            }
        }
        //parentheses
        while(true){
            int i = regIndexOf(work, "^\\($");
            if(i >= 0){
                ArrayList<String> sub = takeParen(work, i);
                if(sub.size() == 0){
                    throw new IllegalArgumentException("Invalid Syntax: " + (i > 0 ? work.get(i - 1) : "") + work.get(i) + (i < work.size() - 1 ? work.get(i + 1) : ""));
                } else{ //otherwise treat it normally
                    for(int j = sub.size() + 2; j > 0; j--){
                        work.remove(i);
                    }
                    tokens.add(parseLoop(sub, tokens));
                    work.add(i, "§" + (tokens.size() - 1)); //placeholder for evaluated parentheses expression
                    
                }
            } else{
                break;
            }
        }
        //convert raw numbers to tokens
        while(true){
            int i = regIndexOf(work, "^(\\d+)([.]\\d+)?$");
            if(i >= 0){
                tokens.add(new NumToken(Float.parseFloat(work.set(i, "§" + tokens.size()))));
            } else{
                break;
            }
        }
        //convert variable names to tokens
        while(true){
            int i = regIndexOf(work, "^\\w(\\w\\d?:?)+([.]\\w\\w)?$");
            if(i >= 0){
                tokens.add(new Token(work.set(i, "§" + tokens.size())));
            } else{
                break;
            }
        }
        //exponents aren't a thing, so we go to multiplication and division(including modulo)
        while(true){
            int i = regIndexOf(work, "^[*/%]$");
            if(i >= 0){
                ArrayList<Token> args = new ArrayList<>();
                args.add(getToken(work.get(i - 1), tokens));
                args.add(getToken(work.get(i + 1), tokens));
                String name;
                switch(work.get(i)){
                    case "*":
                        name = "MULT";
                        break;
                    case "/":
                        name = "DIV";
                        break;
                    case "%":
                        name = "MOD";
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + work.get(i));
                }
                tokens.add(new Token(name, args));
                work.remove(i);
                work.remove(i);
                work.set(i - 1, "§" + (tokens.size() - 1));
            } else{
                break;
            }
        }
        //addition & subtraction
        while(true){
            int i = regIndexOf(work, "^[+-]$");
            if(i >= 0){
                if(i == 0 || !work.get(i - 1).startsWith("§")){
                    tokens.add(new NumToken(0));
                    work.add(i, "§" + (tokens.size() - 1));
                    i++;
                }
                ArrayList<Token> args = new ArrayList<>();
                args.add(getToken(work.get(i - 1), tokens));
                args.add(getToken(work.get(i + 1), tokens));
                tokens.add(new Token(work.get(i).equals("+") ? "ADD" : "SUB", args));
                work.remove(i);
                work.remove(i);
                work.set(i - 1, "§" + (tokens.size() - 1));
            } else{
                break;
            }
        }
        //not
        while(true){
            int i = regIndexOf(work, "^!$");
            if(i >= 0){
                ArrayList<Token> args = new ArrayList<>();
                args.add(getToken(work.get(i + 1), tokens));
                work.remove(i + 1);
                tokens.add(new Token("NOT", args));
                work.set(i, "§" + (tokens.size() - 1));
            } else{
                break;
            }
        }
        //equality
        while(true){
            int i = regIndexOf(work, "^==|!=|<=|>=|<|>$");
            if(i >= 0){
                ArrayList<Token> args = new ArrayList<>();
                args.add(getToken(work.get(i - 1), tokens));
                args.add(getToken(work.get(i + 1), tokens));
                String name;
                switch(work.get(i)){
                    case "==":
                        name = "EQ";
                        break;
                    case "!=":
                        name = "NOTEQ";
                        break;
                    case "<=":
                        name = "LESSEQ";
                        break;
                    case ">=":
                        name = "GREATEREQ";
                        break;
                    case "<":
                        name = "LESS";
                        break;
                    case ">":
                        name = "GREATER";
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + work.get(i));
                }
                tokens.add(new Token(name, args));
                work.remove(i);
                work.remove(i);
                work.set(i - 1, "§" + (tokens.size() - 1));
            } else{
                break;
            }
        }
        //AND/OR
        while(true){
            int i = regIndexOf(work, "^&&|\\|\\|$");
            if(i >= 0){
                ArrayList<Token> args = new ArrayList<>();
                args.add(getToken(work.get(i - 1), tokens));
                args.add(getToken(work.get(i + 1), tokens));
                tokens.add(new Token(work.get(i).equals("+") ? "ADD" : "SUB", args));
                work.remove(i);
                work.remove(i);
                work.set(i - 1, "§" + (tokens.size() - 1));
            } else{
                break;
            }
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
            } else{
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
    
    private static class Token{
        private final String name;
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
    
    
}