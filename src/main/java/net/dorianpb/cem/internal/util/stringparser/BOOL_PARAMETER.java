package net.dorianpb.cem.internal.util.stringparser;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

enum BOOL_PARAMETER implements ParsedFunctionBool {
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
    IS_SLEEPING,
    IS_SITTING,
    IS_DANCING,
    IS_IN_POWDER_SNOW,
    WAS_ON_FIRE,
    WAS_IN_POWDER_SNOW,
    TRUE,
    FALSE,
    ;

    @Override
    public Boolean eval(ArrayList<ParsedExpression> args, Environment env) {
        return switch(this) {
            case IS_ALIVE -> env.entity().isAlive();
            case IS_BURNING -> env.entity().isOnFire();
            case IS_CHILD -> env.livingEntity().isBaby();
            case IS_GLOWING -> env.entity().isGlowing();
            case IS_HURT -> env.livingEntity().hurtTime != 0;
            case IS_IN_LAVA -> env.entity().isInLava();
            case IS_IN_WATER -> env.entity().isSubmergedInWater();
            case IS_INVISIBLE -> env.entity().isInvisible();
            case IS_ON_GROUND -> env.entity().isOnGround();
            case IS_RIDDEN -> env.entity().hasPassengers();
            case IS_RIDING -> env.entity().hasVehicle();
            case IS_SNEAKING -> env.entity().isSneaking();
            case IS_SPRINTING -> env.entity().isSprinting();
            case IS_WET -> env.entity().isWet();
            case IS_SLEEPING -> env.tameableEntity().isSleeping();
            case IS_SITTING -> env.tameableEntity().isSitting();
            case IS_DANCING -> env.piglinEntity().isDancing();
            case IS_IN_POWDER_SNOW -> env.entity().inPowderSnow;
            case WAS_ON_FIRE -> env.entity().wasOnFire;
            case WAS_IN_POWDER_SNOW -> env.entity().wasInPowderSnow;
            case TRUE -> true;
            case FALSE -> false;
        };
    }

    @Override
    public @Nullable ParsedFunctionType getArgType() {
        return null;
    }

    @Override
    public int getArgNumber() {
        return -2;
    }
}