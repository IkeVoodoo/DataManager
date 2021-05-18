package me.ikevoodoo.datamanager.load.expressions;

import java.util.HashMap;

/**
 * A class only to be used by the ExpressionCalculator, provides no public methods.
 * @see ExpressionCalculator
 */
public class ExpressionUtils {

    private static final HashMap<Character, Integer> precedence = new HashMap<>();

    static {
        precedence.put('*', 3);
        precedence.put('/', 3);
        precedence.put('^', 3);
        precedence.put('%', 3);

        precedence.put('-', 2);
        precedence.put('+', 2);
    }

    protected static boolean hasPrecedence(char operator, char comparator) {
        if(comparator == '(' || comparator == ')')
            return false;
        return precedence.get(operator) < precedence.get(comparator);
    }

    protected static boolean isOperator(char c) {
        return c == '+' || c == '-'
                || c == '*' || c == '/' || c == '^' || c == '%';
    }

    protected static int calc(char operator, int a, int b) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new UnsupportedOperationException("Division by zero");
                return a / b;
            case '^':
                return (int)Math.pow(a, b);
            case '%':
                return b % a;
        }

        return 0;
    }

}
