import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

/*
 * the "backend" for the shell.
 *
 * this class handles all of the commands parsing and execution, as well as the help command.
 *
 * the commands are stored in a hashmap, with the command name as the key and the number of arguments as the value.
 */
public class Commands {
    // eval(expr), evalx(fn, x), evalxy(fn, x, y), evalxyz(fn, xyz), diffx(fn, x), diffxy(fn, x, y), diffxyz(fn, x, y, z)
    // maybe todo if i have time: plot2d(fn, x1, y1, x2, y2), plot3d(fn, x1, y1, z1, x2, y2, z2)
    public static HashMap<String, Integer> commands = new HashMap<>() {{
        put("eval", 1);
        put("evalx", 2);
        put("evalxy", 3);
        put("evalxyz", 4);
        put("diffx", 2);
        put("diffxy", 3);
        put("diffxyz", 4);
    }};
    
    public Commands() {
    }
    
    /*
     * parses the input and executes the command
     */
    public static String execute(String input) {
        // determine if it's a help command, since it's a special case
        // check if start of string is "help"
        if (input.startsWith("help")) {
            // if it's just "help", return the list of commands
            if (input.trim().length() == 4) {
                return help();
            } else {
                // otherwise, return the help for the command
                String command = input.substring(5);
                return help(command.trim());
            }
        } else {
            // commands are like functions, with the arguments separated by spaces
            StringBuilder command = new StringBuilder();
            // read until the first non alphabet character, regex
            for (int i = 0; i < input.length(); i++) {
                if (Character.toString(input.charAt(i)).matches("[a-zA-Z]")) {
                    command.append(input.charAt(i));
                } else {
                    if (i == 0) {
                        // if the first character isn't a letter, it's not a valid command
                        return "Invalid command";
                    } else if (input.charAt(i) != '(') {
                        // if the first non-alphabet character isn't a (
                        return "Invalid command";
                    }
                    break;
                }
            }
            if (!commands.containsKey(command.toString())) {
                return "Invalid command";
            }
            String cmd = command.toString();
            String argsString = input.substring(cmd.length()).replace(" ", "");
            if (argsString.length() == 0) {
                return "Invalid command";
            }
            argsString = argsString.substring(1);
            // lets the user use the command without closing parentheses
            if (argsString.charAt(argsString.length() - 1) == ')') {
                argsString = argsString.substring(0, argsString.length() - 1);
            }
            // check if the number of arguments is correct
            String[] args = argsString.split(",");
            if (args.length != commands.get(cmd)) {
                return "Invalid number of arguments";
            }
            // now we have the command and the arguments
            // we can execute the command
            if (cmd.equals("eval")) {
                Expression expr = new Expression(args[0].trim());
                return Double.toString(round(expr.evaluate(0), 6));
            } else if (cmd.equals("evalx")) {
                Expression expr = new Expression(args[0].trim());
                return Double.toString(round(expr.evaluate(Double.parseDouble(args[1].trim())), 6));
            } else if (cmd.equals("evalxy")) {
                Expression expr = new Expression(args[0].trim());
                return Double.toString(round(expr.evaluate(Double.parseDouble(args[1].trim()), Double.parseDouble(args[2].trim())), 6));
            } else if (cmd.equals("evalxyz")) {
                Expression expr = new Expression(args[0].trim());
                return Double.toString(round(expr.evaluate(Double.parseDouble(args[1].trim()), Double.parseDouble(args[2].trim()), Double.parseDouble(args[3].trim())), 6));
            } else if (cmd.equals("diffx")) {
                Expression expr = new Expression(args[0].trim());
                return Double.toString(round(expr.diffx(Double.parseDouble(args[1].trim())), 6));
            } else if (cmd.equals("diffxy")) {
                Expression expr = new Expression(args[0].trim());
                vec2 gradient = expr.diffxy(Double.parseDouble(args[1].trim()), Double.parseDouble(args[2].trim()));
                return "{" + round(gradient.x, 6) + ", " + round(gradient.y, 6) + "}";
            } else { // diffxyz
                Expression expr = new Expression(args[0].trim());
                vec3 gradient = expr.diffxyz(Double.parseDouble(args[1].trim()), Double.parseDouble(args[2].trim()), Double.parseDouble(args[3].trim()));
                return "{" + round(gradient.x, 6) + ", " + round(gradient.y, 6) + ", " + round(gradient.z, 6) + "}";
            }
        }
    }
    
    public static String help() {
        // prints out all the commands
        // iterate through commands
        StringBuilder ret = new StringBuilder();
        ret.append("Commands:\n");
        for (String key : commands.keySet()) {
            ret.append(key).append("\n");
        }
        return ret.toString();
    }
    
    public static String help(String command) {
        // prints out the help for a specific command
        // check if command exists
        if (commands.containsKey(command)) {
            // return the help for the command
            if (command.equals("eval")) {
                String ret = "Evaluates an expression.\n" + "Expression must not contain any equal signs, nor variables.\n" + "Example:\n >>> eval(2+2)\n 4.0\n";
                return ret;
            } else if (command.equals("evalx")) {
                String ret = "Evaluates an expression with a single variable x.\n" + "Expression must not contain any equal signs, nor variables other than x.\n" + "Example:\n >>> evalx(2x+2, 2)\n 6.0\n";
                return ret;
            } else if (command.equals("evalxy")) {
                String ret = "Evaluates an expression with two variables x and y.\n" + "Expression must not contain any equal signs, nor variables other than x and y.\n" + "Example:\n >>> evalxy(2x+2y+2, 2, 3)\n 10.0\n";
                return ret;
            } else if (command.equals("evalxyz")) {
                String ret = "Evaluates an expression with three variables x, y, and z.\n" + "Expression must not contain any equal signs, nor variables other than x, y, and z.\n" + "Example:\n >>> evalxyz(2x+2y+2z+2, 2, 3, 4)\n 18.0\n";
                return ret;
            } else if (command.equals("diffx")) {
                String ret = "Numerically calculates the derivative of the function at the given x value." + "Expression must not contain any equal signs, nor variables other than x.\n" + "Example:\n >>> diffx(2x^2, 2)\n 8.0\n";
                return ret;
            } else if (command.equals("diffxy")) {
                String ret = "Numerically calculates the partial derivatives of the function at the given x and y values." + "Expression must not contain any equal signs, nor variables other than x and y.\n" + "Example:\n >>> diffxy(2x^2+2y^2, 2, 3)\n {8.0, 12.0}\n";
                return ret;
            } else if (command.equals("diffxyz")) {
                String ret = "Numerically calculates the partial derivatives of the function at the given x, y, and z values." + "Expression must not contain any equal signs, nor variables other than x, y, and z.\n" + "Example:\n >>> diffxyz(2x^2+2y^2+2z^2, 2, 3, 4)\n {8.0, 12.0, 16.0}\n";
                return ret;
            }
        }
        return "Error: No such command found";
    }
    
    public static void main(String[] args) {
        Commands c = new Commands();
        System.out.println(execute("diffxyz(2x^2+2y^2+2z^2, 2, 3, 4)"));
    }
    
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}