package net.dorianpb.cem.internal.util.stringparser;

import java.util.ArrayList;

class Token {
    private final String           name;
    private final ArrayList<Token> args;

    Token(String name, ArrayList<Token> args) {
        this.name = name;
        this.args = args;
    }

    Token(String name) {
        this.name = name;
        this.args = null;
    }

    String getName() {
        return this.name;
    }

    ArrayList<Token> getArgs() {
        return this.args;
    }
}