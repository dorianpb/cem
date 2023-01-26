package net.dorianpb.cem.internal.util.stringparser;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;

import java.util.ArrayList;

enum FLOAT_PARAMETER implements ParsedFunctionFloat{
	//render parameters
	LIMB_SWING, LIMB_SPEED, AGE, HEAD_YAW, HEAD_PITCH, //entity parameters
	HEALTH, HURT_TIME, IDLE_TIME, MAX_HEALTH, MOVE_FORWARD, MOVE_STRAFING, POS_X, POS_Y, POS_Z, REVENGE_TIME, SWING_PROGRESS, //other
	TIME, PI,
	;
	
	@SuppressWarnings("NumericCastThatLosesPrecision")
	@Override
	public float eval(ArrayList<ParsedExpression> args, Environment env){
		switch(this){
			//render parameters
			case AGE -> {
				return env.age();
			}
			case HEAD_YAW -> {
				return env.head_yaw();
			}
			case HEAD_PITCH -> {
				return env.head_pitch();
			}
			case LIMB_SPEED -> {
				return env.limbDistance();
			}
			case LIMB_SWING -> {
				return env.limbAngle();
			}
			case TIME -> {
				MinecraftClient minecraft = MinecraftClient.getInstance();
				World world = minecraft.world;
				return (world == null)? 0.0F : (world.getTime() % 24000L) + minecraft.getTickDelta();
			}
			case PI -> {
				return 3.1415926F;
			}
			//entity parameters
			case HEALTH -> {
				return env.livingEntity().getHealth();
			}
			case HURT_TIME -> {
				return env.livingEntity().hurtTime;
			}
			case IDLE_TIME -> {
				return env.livingEntity().getLastAttackTime();
			}
			case MAX_HEALTH -> {
				return env.livingEntity().getMaxHealth();
			}
			case MOVE_FORWARD -> {
				return env.livingEntity().forwardSpeed;
			}
			case MOVE_STRAFING -> {
				return env.livingEntity().sidewaysSpeed;
			}
			case POS_X -> {
				return (float) env.entity().getX();
			}
			case POS_Y -> {
				return (float) env.entity().getY();
			}
			case POS_Z -> {
				return (float) env.entity().getZ();
			}
			case REVENGE_TIME -> {
				return env.livingEntity().getLastAttackedTime();
			}
			case SWING_PROGRESS -> {
				return env.livingEntity().handSwingProgress;
			}
		}
		throw new NullPointerException("uwu");
	}
	
	@Override
	public int getArgNumber(){
		return -2;
	}
}