package net.dorianpb.cem.internal.models;

import net.minecraft.entity.Entity;

interface CemAnimation {
    void apply(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, Entity entity);
}