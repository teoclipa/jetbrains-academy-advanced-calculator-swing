package calculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Stack;

public class Calculator extends JFrame {
    private final JLabel equationLabel;

    private final JLabel resultLabel;
    private final StringBuilder equationBuilder;

    public Calculator() {
        super("Calculator");
        setSize(350, 510);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.LIGHT_GRAY);

        setLayout(new BorderLayout(80, 50));

        JPanel calculationPanel = new JPanel();
        calculationPanel.setBackground(Color.LIGHT_GRAY);
        calculationPanel.setLayout(new BoxLayout(calculationPanel, BoxLayout.Y_AXIS));
        calculationPanel.setBorder(new EmptyBorder(20, 20, 0, 20));

        resultLabel = new JLabel("0");
        resultLabel.setName("ResultLabel");
        resultLabel.setFont(resultLabel.getFont().deriveFont(42f));
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        equationLabel = new JLabel(" ");
        equationLabel.setName("EquationLabel");
        equationLabel.setFont(equationLabel.getFont().deriveFont(15f));
        equationLabel.setForeground(Color.GREEN.darker());
        equationLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.add(resultLabel, BorderLayout.NORTH);
        labelPanel.add(Box.createVerticalStrut(21), BorderLayout.CENTER);
        labelPanel.add(equationLabel, BorderLayout.SOUTH);
        labelPanel.setBackground(Color.LIGHT_GRAY);

        calculationPanel.add(labelPanel);

        Font buttonFont = new Font("ComicSans", Font.PLAIN, 20);


        JPanel allButtonsPanel = new JPanel(new GridBagLayout());
        allButtonsPanel.setBorder(new EmptyBorder(0, 5, 5, 5));

        JButton parenthesesButton = createButton("( )", "Parentheses", buttonFont);
        parenthesesButton.addActionListener(e -> insertParentheses());
        addToGridBagPanel(allButtonsPanel, parenthesesButton, 0, 0);

        JButton clearEntryButton = createButton("CE", "ClearEntry", buttonFont);
        clearEntryButton.addActionListener(e -> clearEntry());
        addToGridBagPanel(allButtonsPanel, clearEntryButton, 1, 0);

        JButton clearButton = createButton("C", "Clear", buttonFont);
        clearButton.addActionListener(e -> clearEquation());
        addToGridBagPanel(allButtonsPanel, clearButton, 2, 0);

        JButton deleteButton = createButton("Del", "Delete", buttonFont);
        deleteButton.addActionListener(e -> deleteLastCharacter());
        addToGridBagPanel(allButtonsPanel, deleteButton, 3, 0);

        JButton powTwoButton = createButton("x²", "PowerTwo", buttonFont);
        powTwoButton.addActionListener(e -> insertPowerTwo());
        addToGridBagPanel(allButtonsPanel, powTwoButton, 0, 1);

        JButton powYButton = createButton("xʸ", "PowerY", buttonFont);
        powYButton.addActionListener(e -> insertPowerY());
        addToGridBagPanel(allButtonsPanel, powYButton, 1, 1);

        JButton sqrtButton = createButton("√", "SquareRoot", buttonFont);
        sqrtButton.addActionListener(e -> insertSquareRoot());
        addToGridBagPanel(allButtonsPanel, sqrtButton, 2, 1);

        JButton divideButton = createButton("÷", "Divide", buttonFont);
        divideButton.setName("Divide");
        divideButton.addActionListener(e -> handleButtonInput(divideButton.getText()));
        addToGridBagPanel(allButtonsPanel, divideButton, 3, 1);

        String[] buttons = {"7", "8", "9", "×", "4", "5", "6", "-", "1", "2", "3", "+", "±", "0", ".", "="};
        String[] buttonNames = {"Seven", "Eight", "Nine", "Multiply", "Four", "Five", "Six", "Subtract", "One", "Two", "Three", "Add", "PlusMinus", "Zero", "Dot", "Equals"};

        int indexButtons = 0;
        int gridX = 0;
        int gridY = 2;
        for (String button : buttons) {
            JButton anotherButton = createButton(button, buttonNames[indexButtons++], buttonFont);
            anotherButton.addActionListener(e -> handleButtonInput(anotherButton.getText()));
            addToGridBagPanel(allButtonsPanel, anotherButton, gridX++, gridY);
            if (gridX == 4) {
                gridX = 0;
                gridY++;
            }
        }
        allButtonsPanel.setBackground(Color.LIGHT_GRAY);
        add(calculationPanel, BorderLayout.NORTH);
        add(allButtonsPanel, BorderLayout.CENTER);
        setVisible(true);

        equationBuilder = new StringBuilder();
    }

    private void addToGridBagPanel(JPanel panel, Component component, int gridX, int gridY) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridX;
        constraints.gridy = gridY;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        // Add spacing
        int horizontalGap = 2; // Horizontal spacing between components
        int verticalGap = 2; // Vertical spacing between components
        constraints.insets = new Insets(verticalGap, horizontalGap, verticalGap, horizontalGap);

        panel.add(component, constraints);
    }

    private JButton createButton(String text, String name, Font font) {
        JButton button = new JButton(text);
        button.setName(name);
        button.setFont(font);
        button.setBorderPainted(false);
        // Add MouseListener to handle button click events
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Darken the button's background color when pressed
                button.setBackground(button.getBackground().darker());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Restore the button's original background color when released
                button.setBackground(button.getBackground().brighter());
            }
        });

        // Set the initial background color of the button
        button.setBackground(Color.WHITE);

        // Customize the background color of specific buttons
        if (name.equals("Parentheses") || name.equals("ClearEntry") || name.equals("Clear") || name.equals("Delete") || name.equals("Divide") || name.equals("Multiply") || name.equals("Subtract") || name.equals("Add") || name.equals("Equals") || name.equals("SquareRoot") || name.equals("PowerTwo") || name.equals("PowerY")) {
            button.setBackground(getContentPane().getBackground().darker());
        }

        return button;
    }

    private void handleButtonInput(String input) {
        if (equationBuilder.length() == 0 && Helper.isOperator(input)) {
            return;
        }
        if (Helper.isOperator(input) && Helper.isOperator(equationBuilder.substring(equationBuilder.length() - 1))) {
            equationBuilder.deleteCharAt(equationBuilder.length() - 1);
        }
        switch (input) {
            case "( )" -> insertParentheses();
            case "x²" -> insertPowerTwo();
            case "xʸ" -> insertPowerY();
            case "√" -> insertSquareRoot();
            case "±" -> insertNegation();
            case "." -> handleDotButtonInput();
            case "=" -> evaluateExpression();
            default -> {
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
    }

    private void insertParentheses() {
        int leftParenthesesCount = countOccurrences(equationBuilder.toString(), "(");
        int rightParenthesesCount = countOccurrences(equationBuilder.toString(), ")");

        if (leftParenthesesCount == rightParenthesesCount || getLastToken().equals("(") || Helper.isOperator(getLastToken())) {
            equationBuilder.append("(");
        } else {
            equationBuilder.append(")");
        }

        equationLabel.setText(equationBuilder.toString());
    }

    private int countOccurrences(String text, String target) {
        int count = 0;
        int index = -1;
        while ((index = text.indexOf(target, index + 1)) != -1) {
            count++;
        }
        return count;
    }

    private void insertPowerTwo() {
        if (equationBuilder.length() > 0) {
            String lastToken = getLastToken();
            if (Helper.isNumber(lastToken)) {
                equationBuilder.append("^(2)");
                equationLabel.setText(equationBuilder.toString());
            }
        }
    }

    private void insertPowerY() {
        if (equationBuilder.length() > 0) {
            String lastToken = getLastToken();
            if (Helper.isNumber(lastToken)) {
                equationBuilder.append("^").append("(");
                equationLabel.setText(equationBuilder.toString());
            }
        }
    }

    private String getLastToken() {
        String[] tokens = equationBuilder.toString().split("(?<=\\D)|(?=\\D)");
        if (tokens.length > 0) {
            return tokens[tokens.length - 1];
        } else {
            return "";
        }
    }

    private void insertSquareRoot() {
        equationBuilder.append("√(");
        equationLabel.setText(equationBuilder.toString());
    }


    private void insertNegation() {
        if (equationBuilder.length() > 0) {
            String[] tokens = equationBuilder.toString().split("(?<=[+×÷\\-^√()])|(?=[+×÷\\-^√()])");
            String lastToken = tokens[tokens.length - 1];

            if (Helper.isNumber(lastToken)) {
                // Check if the number is already negated
                int lastIndex = equationBuilder.lastIndexOf(lastToken);

                if (lastIndex >= 2 && equationBuilder.substring(lastIndex - 2, lastIndex).equals("(-")) {
                    // Remove the negation
                    equationBuilder.delete(lastIndex - 2, lastIndex);
                } else {
                    // Negate the number
                    equationBuilder.insert(lastIndex, "(-");
                }
            } else if (lastToken.equals("(")) {
                // Add a minus sign after the opening bracket
                equationBuilder.append("-");
            } else {
                // Add or remove "(-" prefix
                if (equationBuilder.substring(equationBuilder.length() - 1).equals("(")) {
                    // Remove "(" if it's the last token
                    equationBuilder.deleteCharAt(equationBuilder.length() - 1);
                } else if (equationBuilder.substring(equationBuilder.length() - 2).equals("(-")) {
                    // Remove "(-" if it's the last token
                    equationBuilder.delete(equationBuilder.length() - 2, equationBuilder.length());
                } else {
                    // Add "(-" prefix
                    equationBuilder.append("(-");
                }
            }
        } else {
            equationBuilder.append("(-");
        }
        if(equationBuilder.length() > 0) {
            equationLabel.setText(equationBuilder.toString());
        }
        else {
            equationLabel.setText(" ");
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
        equationLabel.setForeground(Color.GREEN.darker());
        equationLabel.setText(" ");
        resultLabel.setText("0");
    }

    private void clearEntry() {
        if (equationBuilder.length() > 0) {
            // Check if the last character is a space (indicating no equation)
            if (equationBuilder.charAt(equationBuilder.length() - 1) == ' ') {
                equationBuilder.setLength(0);// Clear the entire equation
            } else {
                // Remove the last entry from the equation
                String[] tokens = equationBuilder.toString().split("(?<=\\D)|(?=\\D)");
                if (tokens.length > 1) {
                    // Remove the last token
                    equationBuilder.setLength(equationBuilder.length() - tokens[tokens.length - 1].length());
                } else {
                    equationBuilder.setLength(0);// Clear the entire equation if no token found
                }
            }

            equationLabel.setForeground(Color.GREEN.darker());
            if (equationBuilder.length() == 0) {
                equationLabel.setText(" ");
            } else {
                equationLabel.setText(equationBuilder.toString());
            }
            if (equationBuilder.length() == 0) {
                equationLabel.setText(" ");
                resultLabel.setText("0");
            }
        }
    }

    private void deleteLastCharacter() {
        if (equationBuilder.length() > 0) {
            equationBuilder.deleteCharAt(equationBuilder.length() - 1);
            equationLabel.setForeground(Color.GREEN.darker());
            if (equationBuilder.length() == 0) {
                equationLabel.setText(" ");
            } else {
                equationLabel.setText(equationBuilder.toString());
            }
        }
    }

    private void evaluateExpression() {
        if (!equationBuilder.toString().isEmpty() && Helper.isOperator(equationBuilder.substring(equationBuilder.length() - 1))) {
            equationLabel.setForeground(Color.RED.darker());
        }

        //if equation ends in an open parenthesis
        else if (equationBuilder.toString().endsWith("(")) {
            equationLabel.setForeground(Color.RED.darker());
        } else {
            equationLabel.setForeground(Color.GREEN.darker());
        }


        if (equationBuilder.length() > 0) {
            String[] tokens = equationBuilder.toString().split("(?<=[+×÷\\-^()])|(?=[+×÷\\-^()])");
            Stack<Double> operandStack = new Stack<>();
            Stack<String> operatorStack = new Stack<>();
            boolean evaluateUnderSqrt = false;

            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                if (Helper.isNumber(token)) {
                    double operand = Double.parseDouble(token);
                    if (evaluateUnderSqrt) {
                        evaluateUnderSqrt = false;
                    }
                    operandStack.push(operand);
                } else if (Helper.isOperator(token) && !token.equals("(") && !token.equals(")")) {
                    while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(") && Helper.hasHigherPrecedence(token, operatorStack.peek())) {
                        String operator = operatorStack.pop();
                        double operand2 = operandStack.pop();
                        double operand1 = operandStack.pop();
                        double result = Helper.performOperation(operand1, operand2, operator);
                        operandStack.push(result);
                    }
                    operatorStack.push(token);
                } else if (token.equals("(")) {
                    if (i + 1 < tokens.length && tokens[i + 1].equals("-")) {
                        operatorStack.push(token); // Push "(" to the operator stack
                        i++; // Skip the next token "-"
                        double operand = Double.parseDouble(tokens[i + 1]); // Get the negative operand
                        operand *= -1; // Negate the operand
                        operandStack.push(operand);
                        i++; // Skip the negated operand
                    } else {
                        operatorStack.push(token);
                    }
                } else if (token.equals(")")) {
                    while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                        String operator = operatorStack.pop();
                        double operand2 = operandStack.pop();
                        double operand1 = operandStack.pop();
                        double result = Helper.performOperation(operand1, operand2, operator);
                        operandStack.push(result);
                    }
                    operatorStack.pop(); // Discard the opening parenthesis

                    // Evaluate square root if the next token is a square root operator
                    if (!operatorStack.isEmpty() && operatorStack.peek().equals("√")) {
                        operatorStack.pop();
                        double operand = operandStack.pop();
                        double result = Math.sqrt(operand);
                        operandStack.push(result);
                    }
                } else if (token.equals("√")) {
                    operatorStack.push(token);
                    evaluateUnderSqrt = true;
                }
            }

            while (!operatorStack.isEmpty()) {
                String operator = operatorStack.pop();
                double operand2 = operandStack.pop();
                double operand1 = operandStack.pop();
                double result = Helper.performOperation(operand1, operand2, operator);
                operandStack.push(result);
            }

            double finalResult = operandStack.pop();

            if (Double.isInfinite(finalResult) || Double.isNaN(finalResult)) {
                equationLabel.setForeground(Color.RED.darker());
            } else {
                resultLabel.setText(Helper.formatResult(finalResult));
            }
        }
    }
}

