package net.dorianpb.cem.internal.util.stringparser;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

record Environment(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, Entity entity){
	
	LivingEntity getLivingEntity(){
		return (LivingEntity) this.entity;
	}
}