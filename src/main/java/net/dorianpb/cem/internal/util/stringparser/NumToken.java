package net.dorianpb.cem.internal.util.stringparser;

class NumToken extends Token {
    private final float num;

    NumToken(float num) {
        super("NUM", null);
        this.num = num;
    }

    float getNum() {
        return this.num;
    }
}