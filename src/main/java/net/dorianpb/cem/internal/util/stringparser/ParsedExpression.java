package net.dorianpb.cem.internal.util.stringparser;

import java.util.List;

public interface ParsedExpression {
    ParsedFunctionType getType();

    default void checkArgs(List<ParsedExpression> args, int paramNum) {
        if(args == null && paramNum != -2) {
            throw new IllegalArgumentException("Function \"" +
                                               this.getName().toLowerCase() +
                                               "\" should be be followed with \"()\", as it is not a " +
                                               "parameter!");
        } else if(args != null && paramNum == -2) {
            throw new IllegalArgumentException("Parameter \"" +
                                               this.getName().toLowerCase() +
                                               "\" does not take arguments and should not have any \"()" +
                                               "\"!");
        } else if(paramNum > -1 && args.size() != paramNum) {
            throw new IllegalArgumentException("Function \"" +
                                               this.getName().toLowerCase() +
                                               "\" needs exactly " +
                                               paramNum +
                                               " parameters, but " +
                                               args.size() +
                                               " " +
                                               ((args.size() == 1)? "was" : "were") +
                                               " " +
                                               "given!");
        }
    }

    String getName();
}