package calculator;

public class Helper {


    public static double performOperation(double operand1, double operand2, String operator) {
        return switch (operator) {
            case "+" -> operand1 + operand2;
            case "-" -> operand1 - operand2;
            case "×" -> operand1 * operand2;
            case "÷" -> operand1 / operand2;
            case "^" -> Math.pow(operand1, operand2);
            default -> 0;
        };
    }

    public static boolean hasHigherPrecedence(String operator1, String operator2) {
        int precedence1 = getPrecedence(operator1);
        int precedence2 = getPrecedence(operator2);
        return precedence1 <= precedence2;
    }

    public static int getPrecedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "×", "÷" -> 2;
            case "^" -> 3;
            default -> 0;
        };
    }

    public static String formatResult(double result) {
        if (result % 1 == 0) {
            return Integer.toString((int) result);
        } else {
            return Double.toString(result);
        }
    }

    public static boolean isNumber(String token) {
        return token.matches("-?\\d+(\\.\\d+)?");
    }

    public static boolean isOperator(String token) {
        return token.equals("+") || token.equals("×") || token.equals("÷") || token.equals("-") || token.equals("^");
    }
}
