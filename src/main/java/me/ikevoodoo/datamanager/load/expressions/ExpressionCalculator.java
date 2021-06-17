package me.ikevoodoo.datamanager.load.expressions;

import java.util.Stack;

import static me.ikevoodoo.datamanager.load.expressions.ExpressionUtils.*;

public interface ExpressionCalculator {

    /**
     * Evaluate a mathematical expression, such as<br>
     * 1+1, 2 * 4, 4^4 etc<br>
     * @return The result of the evaluation
     * @param expr The expression to evaluate
     */
    default int evaluate(String expr) {
        char[] characters = expr.trim().toCharArray();
        Stack<Integer> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < characters.length; i++) {
            if (isNumber(characters, i)) {
                if(characters[Math.max(i, 1) - 1] == '-') {
                    builder.append('-');
                    i++;
                }
                while (i < characters.length && isNumber(characters, i))
                    builder.append(characters[i++]);
                numbers.push(Integer.parseInt(builder.toString()));
                builder.delete(0, builder.length());
                i--;
            } else if (characters[i] == '{')
                operators.push(characters[i]);
            else if (characters[i] == '}') {
                while (operators.peek() != '{')
                    numbers.push(
                            calc(
                                    operators.pop(),
                                    numbers.pop(),
                                    numbers.pop()
                            )
                    );
                operators.pop();
            } else if (characters[i] == '[')
                operators.push(characters[i]);
            else if (characters[i] == ']') {
                while (operators.peek() != '[')
                    numbers.push(
                            calc(
                                    operators.pop(),
                                    numbers.pop(),
                                    numbers.pop()
                            )
                    );
                operators.pop();
            } else if (characters[i] == '(')
                operators.push(characters[i]);
            else if (characters[i] == ')') {
                while (operators.peek() != '(')
                    numbers.push(
                            calc(
                                    operators.pop(),
                                    numbers.pop(),
                                    numbers.pop()
                            )
                    );
                operators.pop();
            } else if (isOperator(characters[i])) {
                while (!operators.empty() && hasPrecedence(characters[i], operators.peek()))
                    numbers.push(
                            calc(
                                    operators.pop(),
                                    numbers.pop(),
                                    numbers.pop()
                            )
                    );

                operators.push(characters[i]);
            }
        }
        while (!operators.empty())
            numbers.push(
                    calc(
                            operators.pop(),
                            numbers.pop(),
                            numbers.pop()
                    )
            );
        return numbers.pop();
    }


}
