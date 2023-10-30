import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/*
 * parser class, very important for the entire program
 * used by the grapher to read the equations
 * used by the shell to parse the given equations and turn into Expression objects
 * converts a human input function into a postfix form
 * done in a 2 step process:
 * - de-condenses the expression (5x -> 5*x, (-1) -> (0-1)) by applying implicit multiplication, and adding 0 in front of subtraction
 * - converts the expression into postfix form
 */
public class Parser {
    // add implicit multiplication, and subtraction with nothing in front of it (like -1 + x)
    // abuses regex to turn something that was originally about 100 lines into 1 line
    public static String preprocessExpression(String s) {
        s = s.replace(" ", "").replace("[", "(").replace("]", ")");
        
        // abs
        s = s.replaceAll("(\\|)[^|]*(\\|)", "abs($0)");
        s = s.replace("|", "abs(");
        
        // put a 0 in front of subtraction if there is nothing in front of it
        s = s.replaceAll("(^|\\()\\s*-(\\s*[\\d.]+|[a-zA-Z])", "$10-$2");
        
        // numbers before letters or opening parentheses (5x, 5(x))
        while (s.matches(".*([\\d.]+)\\s*([a-zA-Z\\(]).*")) {
            s = s.replaceAll("([\\d.]+)\\s*([a-zA-Z\\(])", "$1*$2");
        }
        
        // constants
        s = s.replace("pi", Double.toString(Math.PI));
        s = s.replace("e", Double.toString(Math.E));
        
        // numbers before letters or opening parentheses (5x, 5(x))
        while (s.matches(".*([\\d.]+)\\s*([a-zA-Z\\(]).*")) {
            s = s.replaceAll("([\\d.]+)\\s*([a-zA-Z\\(])", "$1*$2");
        }
        
        // variables (xyz) before numbers or letters or opening parentheses
        while (s.matches(".*([xyz])\\s*([a-zA-Z\\(\\d]).*")) {
            s = s.replaceAll("([xyz])\\s*([a-zA-Z\\(\\d])", "$1*$2");
        }
        // closing parentheses before numbers or letters or opening parentheses
        while (s.matches(".*(\\))\\s*([a-zA-Z\\(\\d]).*")) {
            s = s.replaceAll("(\\))\\s*([a-zA-Z\\(\\d])", "$1*$2");
        }
        
        return s;
    }
    
    // converts the expression into postfix form
    public static Queue<Token> toPostFix(String s) {
        s = preprocessExpression(s);
        Queue<Token> ret = new LinkedList<Token>();
        Stack<Token> stack = new Stack<Token>();
        for (int i = 0; i < s.length(); ) {
            StringBuilder token = new StringBuilder();
            while (i < s.length() && Character.toString(s.charAt(i)).matches("[a-zA-Z\\d\\.]")) {
                token.append(s.charAt(i));
                i++;
            }
            if (token == null || token.length() == 0) {
                token.append(s.charAt(i));
                i++;
            }
            String tkn = token.toString();
            // number (float or int, either works)
            if (tkn.matches("[\\d.]+")) {
                ret.add(new Token(tkn, Token.Type.NUMBER));
            }
            // variable
            else if (tkn.matches("[xyz]")) {
                ret.add(new Token(tkn, Token.Type.VARIABLE));
            }
            // function
            else if (tkn.matches("[a-zA-Z]+")) {
                stack.push(new Token(tkn, Token.Type.FUNCTION));
            }
            // operator
            else if (tkn.matches("[+\\-*/^]")) {
                // use Token.getPrecedence() to determine precedence
                while (!stack.isEmpty() && stack.peek().getType() == Token.Type.OPERATOR && Token.getPrecedence(tkn) <= Token.getPrecedence(stack.peek().getValue())) {
                    ret.add(stack.pop());
                }
                stack.push(new Token(tkn, Token.Type.OPERATOR));
            }
            // opening parentheses
            else if (tkn.matches("\\(")) {
                stack.push(new Token(tkn, Token.Type.NULL));
            }
            // closing parentheses
            else if (tkn.matches("\\)")) {
                while (!stack.isEmpty() && stack.peek().getType() != Token.Type.NULL) {
                    ret.add(stack.pop());
                }
                stack.pop();
                // parentheses belonged to a function, add to output
                if (!stack.isEmpty() && stack.peek().getType() == Token.Type.FUNCTION) {
                    ret.add(stack.pop());
                }
            } else {
                throw new IllegalArgumentException("Invalid token: " + tkn);
            }
        }
        while (!stack.isEmpty()) {
            ret.add(stack.pop());
        }
        return ret;
    }
    
    // testing
    public static void main(String[] args) {
        System.out.println(preprocessExpression("x^2y^2+y^2z^2+z^2x^2-r^2xyz+cos(ln(5))=0"));
        System.out.println(preprocessExpression("abs(1sin(x))"));
        System.out.println(preprocessExpression("1    sin(xabs(-  x)"));
        Queue<Token> x = toPostFix("x^2y^2cos(ln(-5))-5.7");
        System.out.println("x^2y^2cos(ln(-5))-5.7");
        System.out.println(preprocessExpression("x^2y^2cos(ln(-5))-5.7"));
        while (!x.isEmpty()) {
            Token t = x.poll();
            System.out.print(t.getValue() + " ");
        }
    }
}