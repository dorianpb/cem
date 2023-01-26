package net.dorianpb.cem.internal.util.stringparser;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.TameableEntity;

record Environment(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, Entity entity){
	
	LivingEntity livingEntity(){
		return (LivingEntity) this.entity;
	}
	
	TameableEntity tameableEntity(){
		return (TameableEntity) this.entity;
	}
	
	PiglinEntity piglinEntity(){
		return (PiglinEntity) this.entity;
	}
}