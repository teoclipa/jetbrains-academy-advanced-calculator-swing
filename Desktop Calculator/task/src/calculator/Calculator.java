package calculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Stack;

public class Calculator extends JFrame {

    private final JLabel equationLabel;
    private final JLabel resultLabel;
    private final StringBuilder equationBuilder;

    public Calculator() {
        super("Calculator");
        setSize(400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));

        JPanel calculationPanel = new JPanel(new BorderLayout());
        calculationPanel.setBorder(new EmptyBorder(20, 20, 0, 20));

        resultLabel = new JLabel("0");
        resultLabel.setName("ResultLabel");
        resultLabel.setFont(resultLabel.getFont().deriveFont(40f));
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        equationLabel = new JLabel(" ");
        equationLabel.setName("EquationLabel");
        equationLabel.setFont(equationLabel.getFont().deriveFont(20f));
        equationLabel.setForeground(Color.GREEN);
        equationLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        calculationPanel.add(resultLabel, BorderLayout.NORTH);
        calculationPanel.add(equationLabel, BorderLayout.CENTER);

        JPanel allButtonsPanel = new JPanel(new GridLayout(5, 4, 20, 20));
        allButtonsPanel.setName("NumbersPanel");
        allButtonsPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        allButtonsPanel.add(new JLabel());
        allButtonsPanel.add(new JLabel());

        JButton clearButton = new JButton("C");
        clearButton.setName("Clear");
        clearButton.addActionListener(e -> clearEquation());
        allButtonsPanel.add(clearButton);

        JButton deleteButton = new JButton("Del");
        deleteButton.setName("Delete");
        deleteButton.addActionListener(e -> deleteLastCharacter());
        allButtonsPanel.add(deleteButton);

        String[] buttons = {"7", "8", "9", "÷", "4", "5", "6", "×", "1", "2", "3", "+", ".", "0", "=", "-"};
        String[] buttonNames = {"Seven", "Eight", "Nine", "Divide", "Four", "Five", "Six", "Multiply", "One", "Two", "Three", "Add", "Dot", "Zero", "Equals", "Subtract"};

        int indexButtons = 0;
        for (String button : buttons) {
            JButton numberButton = new JButton(button);
            numberButton.setName(buttonNames[indexButtons++]);
            numberButton.addActionListener(e -> handleButtonInput(numberButton.getText()));
            allButtonsPanel.add(numberButton);
        }

        add(calculationPanel, BorderLayout.NORTH);
        add(allButtonsPanel, BorderLayout.CENTER);
        setVisible(true);

        equationBuilder = new StringBuilder();
    }

    private void handleButtonInput(String input) {
        if (equationBuilder.length() == 0 && isOperator(input)) {
            return;
        }
        if (isOperator(input) && isOperator(equationBuilder.substring(equationBuilder.length() - 1))) {
            equationBuilder.deleteCharAt(equationBuilder.length() - 1);
        }
        if (input.equals(".")) {
            handleDotButtonInput();
        } else if (input.equals("=")) {
            evaluateExpression();
        } else {
            if (input.matches("\\.\\d")) {
                equationBuilder.append("0");
            } else if (input.matches("\\d\\.")) {
                equationBuilder.append(input);
            } else {
                String[] tokens = equationBuilder.toString().split("(?<=\\D)|(?=\\D)");
                String lastToken = tokens[tokens.length - 1];

                if (tokens.length > 1) {

                    if (lastToken.endsWith(".")) {
                        if (!(tokens[tokens.length - 2].matches("\\d+"))) {
                            equationBuilder.insert(equationBuilder.length() - 1, "0");
                        }
                        if (tokens[tokens.length - 2].matches("\\d+") && !(input.matches("\\d+"))) {
                            equationBuilder.append("0");
                        }
                    }
                } else if (lastToken.endsWith(".")) {
                    equationBuilder.insert(equationBuilder.length() - 1, "0");
                }
            }
            equationBuilder.append(input);
            equationLabel.setText(equationBuilder.toString());
        }
    }

    private void handleDotButtonInput() {
        if (equationBuilder.length() == 0) {
            equationBuilder.append(".");
        } else {

            // Check if the last token is a number
            String[] tokens = equationBuilder.toString().split("(?<=\\D)|(?=\\D)");
            String lastToken = tokens[tokens.length - 1];
            if (!lastToken.contains(".")) {
                equationBuilder.append(".");
            }
        }
        equationLabel.setText(equationBuilder.toString());
    }


    private void clearEquation() {
        equationBuilder.setLength(0);
        equationLabel.setForeground(Color.GREEN);
        equationLabel.setText(" ");
        resultLabel.setText("0");
    }

    private void deleteLastCharacter() {
        if (equationBuilder.length() > 0) {
            equationBuilder.deleteCharAt(equationBuilder.length() - 1);
            equationLabel.setForeground(Color.GREEN);
            equationLabel.setText(equationBuilder.toString());
        }
    }

    private void evaluateExpression() {
        //Handle equation with just dot
        if (equationBuilder.toString().equals(".")) {
            resultLabel.setText(".");
            return;
        }

        if (equationBuilder.length() > 0) {
            String[] tokens = equationBuilder.toString().split("(?<=[+×÷\\-])|(?=[+×÷\\-])");
            Stack<Double> operandStack = new Stack<>();
            Stack<String> operatorStack = new Stack<>();

            for (String token : tokens) {
                if (isNumber(token)) {
                    operandStack.push(Double.parseDouble(token));
                } else if (isOperator(token)) {
                    while (!operatorStack.isEmpty() && hasHigherPrecedence(token, operatorStack.peek())) {
                        String operator = operatorStack.pop();
                        double operand2 = operandStack.pop();
                        double operand1 = operandStack.pop();
                        double result = performOperation(operand1, operand2, operator);
                        operandStack.push(result);
                    }
                    operatorStack.push(token);
                }
            }
            if (!equationBuilder.toString().isEmpty() && isOperator(equationBuilder.substring(equationBuilder.length() - 1))) {
                equationLabel.setForeground(Color.RED.darker());
            } else {
                equationLabel.setForeground(Color.GREEN);
            }
            while (!operatorStack.isEmpty()) {
                String operator = operatorStack.pop();
                double operand2 = operandStack.pop();
                double operand1 = operandStack.pop();
                double result = performOperation(operand1, operand2, operator);
                operandStack.push(result);
            }

            if (!operandStack.isEmpty()) {
                double result = operandStack.pop();

                if (Double.isInfinite(result) || Double.isNaN(result)) {
                    equationLabel.setForeground(Color.RED.darker());
                } else {
                    resultLabel.setText(formatResult(result));
                }

            }

        }
    }

    private boolean hasHigherPrecedence(String operator1, String operator2) {
        int precedence1 = getPrecedence(operator1);
        int precedence2 = getPrecedence(operator2);
        return precedence1 <= precedence2;
    }

    private int getPrecedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "×", "÷" -> 2;
            default -> 0;
        };
    }

    private String formatResult(double result) {
        if (result % 1 == 0) {
            return Integer.toString((int) result);
        } else {
            return Double.toString(result);
        }
    }

    private boolean isNumber(String token) {
        return token.matches("-?\\d+(\\.\\d+)?");
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("×") || token.equals("÷") || token.equals("-");
    }

    private double performOperation(double operand1, double operand2, String operator) {
        return switch (operator) {
            case "+" -> operand1 + operand2;
            case "-" -> operand1 - operand2;
            case "×" -> operand1 * operand2;
            case "÷" -> operand1 / operand2;
            default -> 0;
        };
    }
}

