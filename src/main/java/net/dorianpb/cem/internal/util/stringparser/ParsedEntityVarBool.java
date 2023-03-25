package net.dorianpb.cem.internal.util.stringparser;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

class ParsedEntityVarBool implements ParsedFunctionBool {
    private final String name;

    ParsedEntityVarBool(Token token) {
        this.name = token.getName().substring("varb.".length());
    }

    @Override
    public int getArgNumber() {
        return -2;
    }

    @Override
    public Boolean eval(ArrayList<ParsedExpression> args, Environment env) {
        return env.boolanimvars().getOrDefault(this.name, null);
    }

    @Override
    public @Nullable ParsedFunctionType getArgType() {
        return null;
    }
}