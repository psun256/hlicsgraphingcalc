import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/*
 * token class, used to store the type and value of a token
 * made to simplify the parser, and provide a way to easily add new functions and operators
 */
public class Token {
    private static final HashSet<String> functions = new HashSet<String>(Arrays.asList(
        "sin",
        "cos",
        "tan",
        "asin",
        "acos",
        "atan",
        "log",
        "ln",
        "sqrt",
        "abs"
    ));
    private static final Map<String, Integer> operators = new HashMap<String, Integer>() {{
        put("+", 1);
        put("-", 1);
        put("*", 2);
        put("/", 2);
        put("^", 3);
    }};
    private final Type type;
    private final String value;
    public Token(String value, Type type) {
        this.value = value;
        this.type = type;
    }
    
    public static Boolean isOperator(String value) {
        return operators.containsKey(value);
    }
    
    public static int getPrecedence(String value) {
        return operators.get(value);
    }
    
    public static Boolean isFunction(String s) {
        return functions.contains(s);
    }
    
    public Type getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
    public enum Type {
        NUMBER, VARIABLE, OPERATOR, FUNCTION, NULL
    }
}
