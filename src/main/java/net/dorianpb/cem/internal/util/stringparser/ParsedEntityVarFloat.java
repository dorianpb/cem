package net.dorianpb.cem.internal.util.stringparser;

import java.util.ArrayList;

class ParsedEntityVarFloat implements ParsedFunctionFloat {
    private final String name;

    ParsedEntityVarFloat(Token token) {
        this.name = token.getName().substring("var.".length());
    }

    @Override
    public int getArgNumber() {
        return -2;
    }

    @Override
    public float eval(ArrayList<ParsedExpression> args, Environment env) {
        return env.floatanimvars().getOrDefault(this.name, Float.NaN);
    }
}