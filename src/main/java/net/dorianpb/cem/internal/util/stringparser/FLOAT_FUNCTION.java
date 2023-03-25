package net.dorianpb.cem.internal.util.stringparser;

import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings({"UnsecureRandomNumberGeneration", "NumericCastThatLosesPrecision"})
enum FLOAT_FUNCTION implements ParsedFunctionFloat {
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
    LERP;

    @Override
    public float eval(ArrayList<ParsedExpression> args, Environment env) {
        return switch(this) {
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
            case FLOOR -> MathHelper.floor(((ParsedExpressionFloat) args.get(0)).eval(env));
            case CEIL -> MathHelper.ceil(((ParsedExpressionFloat) args.get(0)).eval(env));
            case EXP -> (float) Math.exp(((ParsedExpressionFloat) args.get(0)).eval(env));
            case FRAC -> MathHelper.fractionalPart(((ParsedExpressionFloat) args.get(0)).eval(env));
            case LOG -> (float) Math.log(((ParsedExpressionFloat) args.get(0)).eval(env));
            case POW -> (float) Math.pow(((ParsedExpressionFloat) args.get(0)).eval(env), ((ParsedExpressionFloat) args.get(1)).eval(env));
            case RANDOM -> (float) Math.random();
            case ROUND -> Math.round(((ParsedExpressionFloat) args.get(0)).eval(env));
            case SIGNUM -> Math.signum(((ParsedExpressionFloat) args.get(0)).eval(env));
            case SQRT -> MathHelper.sqrt(((ParsedExpressionFloat) args.get(0)).eval(env));
            case FMOD -> MathHelper.floorMod(((ParsedExpressionFloat) args.get(0)).eval(env), ((ParsedExpressionFloat) args.get(1)).eval(env));
            case LERP -> MathHelper.lerp(((ParsedExpressionFloat) args.get(0)).eval(env),
                                         ((ParsedExpressionFloat) args.get(1)).eval(env),
                                         ((ParsedExpressionFloat) args.get(2)).eval(env)
                                        );
            case ADD -> ((ParsedExpressionFloat) args.get(0)).eval(env) + ((ParsedExpressionFloat) args.get(1)).eval(env);
            case SUB -> ((ParsedExpressionFloat) args.get(0)).eval(env) - ((ParsedExpressionFloat) args.get(1)).eval(env);
            case MULT -> ((ParsedExpressionFloat) args.get(0)).eval(env) * ((ParsedExpressionFloat) args.get(1)).eval(env);
            case DIV -> ((ParsedExpressionFloat) args.get(0)).eval(env) / ((ParsedExpressionFloat) args.get(1)).eval(env);
            case MOD -> ((ParsedExpressionFloat) args.get(0)).eval(env) % ((ParsedExpressionFloat) args.get(1)).eval(env);
        };
    }

    private static float findExtreme(ArrayList<ParsedExpression> args, Environment env, boolean big) {
        float[] nums = new float[args.size()];
        for(int i = 0; i < args.size(); i++) {
            nums[i] = ((ParsedExpressionFloat) args.get(i)).eval(env);
        }
        Arrays.sort(nums);
        return big? nums[nums.length - 1] : nums[0];
    }

    @Override
    public int getArgNumber() {
        return switch(this) {
            case MIN, MAX -> -1;
            case RANDOM -> 0;
            case SIN, COS, ASIN, ACOS, TAN, ATAN, TORAD, TODEG, ABS, FLOOR, CEIL, EXP, FRAC, LOG, ROUND, SIGNUM, SQRT -> 1;
            case ATAN2, POW, FMOD, ADD, SUB, MULT, DIV, MOD -> 2;
            case CLAMP, LERP -> 3;
        };
    }
}