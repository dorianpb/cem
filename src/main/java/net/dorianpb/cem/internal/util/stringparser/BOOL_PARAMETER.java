package net.dorianpb.cem.internal.util.stringparser;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

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
			case IS_ALIVE -> env.entity().isAlive();
			case IS_BURNING -> env.entity().isOnFire();
			case IS_CHILD -> env.getLivingEntity().isBaby();
			case IS_GLOWING -> env.entity().isGlowing();
			case IS_HURT -> env.getLivingEntity().hurtTime != 0;
			case IS_IN_LAVA -> env.entity().isInLava();
			case IS_IN_WATER -> env.entity().isSubmergedInWater();
			case IS_INVISIBLE -> env.entity().isInvisible();
			case IS_ON_GROUND -> env.entity().isOnGround();
			case IS_RIDDEN -> env.entity().hasPassengers();
			case IS_RIDING -> env.entity().hasVehicle();
			case IS_SNEAKING -> env.entity().isSneaking();
			case IS_SPRINTING -> env.entity().isSprinting();
			case IS_WET -> env.entity().isWet();
			case TRUE -> true;
			case FALSE -> false;
		};
	}
	
	@Override
	public @Nullable ParsedFunctionType getArgType(){
		return null;
	}
	
	@Override
	public int getArgNumber(){
		return -2;
	}
}