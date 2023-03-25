package net.dorianpb.cem.internal.models;

import net.dorianpb.cem.internal.util.stringparser.CemStringParser;
import net.dorianpb.cem.internal.util.stringparser.ParsedExpression;
import net.dorianpb.cem.internal.util.stringparser.ParsedExpressionFloat;
import net.dorianpb.cem.internal.util.stringparser.ParsedFunctionType;
import net.minecraft.entity.Entity;

import java.lang.invoke.WrongMethodTypeException;
import java.util.Map;

class CemModelAnimation implements CemAnimation {
    private final CemModelEntry         target;
    private final ParsedExpressionFloat expression;
    private final char                  operation;
    private final char                  axis;

    private final Map<String, Boolean> boolanimvars;
    private final Map<String, Float>   floatanimvars;

    @SuppressWarnings("QuestionableName")
    CemModelAnimation(CemModelEntry target, String expr, String var, CemModelRegistry registry) {
        this.target = target;
        this.floatanimvars = registry.getFloatanimvars();
        this.boolanimvars = registry.getBoolanimvars();

        ParsedExpression parsedExpression = CemStringParser.parse(expr, registry, this.target);
        if(parsedExpression.getType() == ParsedFunctionType.FLOAT) {
            this.expression = (ParsedExpressionFloat) parsedExpression;
        } else {
            throw new WrongMethodTypeException("\"" + parsedExpression.getName() + " must evaluate to a number, not a boolean!");
        }

        this.operation = var.charAt(0);
        this.axis = var.charAt(1);
    }

    @Override
    public void apply(float limbAngle, float limbDistance, float age, float head_yaw, float head_pitch, Entity entity) {
        float val = this.expression.eval(limbAngle, limbDistance, age, head_yaw, head_pitch, entity, this.boolanimvars, this.floatanimvars);
        if(Float.isNaN(val)) {
            this.target.getModel().visible = false;/*.setTranslate(this.axis, Float.MAX_VALUE);*/
        } else {
            this.target.getModel().visible = true;
            switch(this.operation) {
                case 't' -> this.target.setTranslate(this.axis, val);
                case 'r' -> this.target.setRotate(this.axis, val);
                case 's' -> this.target.getModel().setScale(this.axis, val);
                default -> throw new IllegalStateException("Unknown operation \"" + this.operation + "\"");
            }
        }
    }
}