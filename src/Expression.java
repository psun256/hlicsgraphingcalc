import java.util.Queue;
import java.util.Stack;

/*
 * class for storing and evaluating expressions
 * uses the Parser class to obtain the postfix form of the expression
 */
public class Expression {
    private final double eps = 1e-6;
    private final Queue<Token> postfix;
    private final String expression;
    
    public Expression(String s) {
        this.expression = s.replace(" ", "").replace("[", "(").replace("]", ")");
        this.postfix = Parser.toPostFix(s);
    }
    
    public static void main(String[] args) {
        // test
        String test = "sin(x) + cos(y) + tan(z)";
        Expression e = new Expression(test);
        System.out.println(e.evaluate(1, 2, 3));
    }
    
    public double evaluate(double x) {
        // if there is y or z in the postfix, then it is invalid
        for (Token t : postfix) {
            if (t.getType() == Token.Type.VARIABLE) {
                if (t.getValue().equals("y") || t.getValue().equals("z")) {
                    throw new IllegalArgumentException("Invalid expression: " + expression);
                }
            }
        }
        
        return evaluate(x, 0.0, 0.0);
    }
    
    public double evaluate(double x, double y) {
        // if there is z in the postfix, then it is invalid
        for (Token t : postfix) {
            if (t.getType() == Token.Type.VARIABLE) {
                if (t.getValue().equals("z")) {
                    throw new IllegalArgumentException("Invalid expression: " + expression);
                }
            }
        }
        
        return evaluate(x, y, 0.0);
    }
    
    public double evaluate(double x, double y, double z) {
        // evaluate the postfix
        Stack<Double> stack = new Stack<Double>();
        for (Token t : postfix) {
            switch (t.getType()) {
                case NUMBER:
                    stack.push(Double.parseDouble(t.getValue()));
                    break;
                case VARIABLE:
                    if (t.getValue().equals("x")) {
                        stack.push(x);
                    } else if (t.getValue().equals("y")) {
                        stack.push(y);
                    } else {
                        stack.push(z);
                    }
                    break;
                case OPERATOR:
                    double num1 = stack.pop();
                    double num2 = stack.pop();
                    switch (t.getValue()) {
                        case "+":
                            stack.push(num2 + num1);
                            break;
                        case "-":
                            stack.push(num2 - num1);
                            break;
                        case "*":
                            stack.push(num2 * num1);
                            break;
                        case "/":
                            stack.push(num2 / num1);
                            break;
                        case "^":
                            stack.push(Math.pow(num2, num1));
                            break;
                    }
                    break;
                case FUNCTION:
                    double num = stack.pop();
                    switch (t.getValue()) {
                        case "sin":
                            stack.push(Math.sin(num));
                            break;
                        case "cos":
                            stack.push(Math.cos(num));
                            break;
                        case "tan":
                            stack.push(Math.tan(num));
                            break;
                        case "asin":
                            stack.push(Math.asin(num));
                            break;
                        case "acos":
                            stack.push(Math.acos(num));
                            break;
                        case "atan":
                            stack.push(Math.atan(num));
                            break;
                        case "log":
                            stack.push(Math.log10(num));
                            break;
                        case "ln":
                            stack.push(Math.log(num));
                            break;
                        case "sqrt":
                            stack.push(Math.sqrt(num));
                            break;
                        case "abs":
                            stack.push(Math.abs(num));
                            break;
                    }
                    break;
            }
        }
        return stack.pop();
    }
    
    public Double diffx(double x) {
        // if y or z is in the postfix, then it is invalid
        for (Token t : postfix) {
            if (t.getType() == Token.Type.VARIABLE) {
                if (t.getValue().equals("y") || t.getValue().equals("z")) {
                    throw new IllegalArgumentException("Invalid expression: " + expression);
                }
            }
        }
        // numerically compute gradient
        Double grad = (evaluate(x + eps) - evaluate(x - eps)) / (2 * eps);
        return grad;
    }
    
    public vec2 diffxy(double x, double y) {
        // if z is in the postfix, then it is invalid
        for (Token t : postfix) {
            if (t.getType() == Token.Type.VARIABLE) {
                if (t.getValue().equals("z")) {
                    throw new IllegalArgumentException("Invalid expression: " + expression);
                }
            }
        }
        // numerically compute gradient
        Double gradx = (evaluate(x + eps, y) - evaluate(x - eps, y)) / (2 * eps);
        Double grady = (evaluate(x, y + eps) - evaluate(x, y - eps)) / (2 * eps);
        return new vec2(gradx, grady);
    }
    
    public vec3 diffxyz(double x, double y, double z) {
        // numerically compute gradient
        Double gradx = (evaluate(x + eps, y, z) - evaluate(x - eps, y, z)) / (2 * eps);
        Double grady = (evaluate(x, y + eps, z) - evaluate(x, y - eps, z)) / (2 * eps);
        Double gradz = (evaluate(x, y, z + eps) - evaluate(x, y, z - eps)) / (2 * eps);
        return new vec3(gradx, grady, gradz);
    }
}