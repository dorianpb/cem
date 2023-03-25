package net.dorianpb.cem.internal.util.stringparser;

import java.util.ArrayList;

enum BOOL_FUNCTION_BOOL implements ParsedFunctionBool {
    NOT, AND, OR,
    ;

    @Override
    public Boolean eval(ArrayList<ParsedExpression> args, Environment env) {
        return switch(this) {
            case NOT -> !((ParsedExpressionBool) args.get(0)).eval(env);
            case AND -> ((ParsedExpressionBool) args.get(0)).eval(env) && ((ParsedExpressionBool) args.get(1)).eval(env);
            case OR -> ((ParsedExpressionBool) args.get(0)).eval(env) || ((ParsedExpressionBool) args.get(1)).eval(env);
        };
    }

    @Override
    public ParsedFunctionType getArgType() {
        return ParsedFunctionType.BOOL;
    }

    @Override
    public int getArgNumber() {
        return (this == NOT)? 1 : 2;
    }
}