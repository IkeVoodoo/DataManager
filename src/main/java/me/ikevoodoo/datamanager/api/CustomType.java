package me.ikevoodoo.datamanager.api;

import me.ikevoodoo.datamanager.load.expressions.ExpressionCalculator;

public interface CustomType extends ExpressionCalculator {

    Object handle(String typeName, String[] args, String combinedArgs);
    Object merge(Object dest, Object src);

}
