package net.dorianpb.cem.internal.util.stringparser;

import java.util.ArrayList;

interface ParsedFunctionBool extends ParsedFunction {
    Boolean eval(ArrayList<ParsedExpression> args, Environment env);

    ParsedFunctionType getArgType();

    @Override
    default ParsedFunctionType getType() {
        return ParsedFunctionType.BOOL;
    }
}