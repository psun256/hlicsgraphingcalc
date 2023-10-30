import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/*
 * simple calculator "applet" that does:
 * sqrt, sin, cos, tan, log, ln
 * arcsin, arccos, arctan
 * also has the 4 basic operations and exponentiation
 *
 * like a handheld calculator
 * shows the answer in the display
 *
 * meant to emulate a simple calculator for basic math for when you are using this program
 */
public class Calc extends JPanel {
    private Double firstNumber;
    private String currentOperation;
    private final JTextArea display;
    private final Insets inset = new Insets(5, 5, 5, 5);
    
    public Calc() {
        super();
        
        // + - * / ^ have to have a number on both sides, so
        // when you click the button, it will wait for you to
        // enter another number
        
        // the other functions will only take one number, so
        // they will be evaluated immediately on the current number
        
        // the display will show the current number
        
        // give the display a border
        // give the buttons a border
        
        this.setLayout(new BorderLayout());
        display = new JTextArea();
        display.setEditable(false);
        display.setPreferredSize(new Dimension(320, 200));
        display.setBackground(new Color(230, 230, 230));
        display.setFont(new Font("Arial", Font.PLAIN, 20));
        display.setText("0");
        
        JPanel viewport = new JPanel();
        viewport.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(240, 240, 240), 5),
            BorderFactory.createLineBorder(new Color(102, 102, 102), 2)
        ));
        viewport.setBackground(new Color(230, 230, 230));
        viewport.add(display);
        
        
        /*
        layout of buttons:
        sin cos tan Clear
        arcsin arccos arctan pi
        sqrt log ln ^
        7 8 9 /
        4 5 6 *
        1 2 3 -
        0 . = +
         */
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = inset;
        buttons.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(240, 240, 240), 5),
            BorderFactory.createLineBorder(new Color(102, 102, 102), 2)
        ));
        buttons.setBackground(new Color(230, 230, 230));
        
        JButton sin = new JButton("sin");
        sin.setPreferredSize(new Dimension(80, 50));
        JButton cos = new JButton("cos");
        cos.setPreferredSize(new Dimension(80, 50));
        JButton tan = new JButton("tan");
        tan.setPreferredSize(new Dimension(80, 50));
        JButton clear = new JButton("Clear");
        clear.setPreferredSize(new Dimension(80, 50));
        JButton arcsin = new JButton("arcsin");
        arcsin.setPreferredSize(new Dimension(80, 50));
        JButton arccos = new JButton("arccos");
        arccos.setPreferredSize(new Dimension(80, 50));
        JButton arctan = new JButton("arctan");
        arctan.setPreferredSize(new Dimension(80, 50));
        JButton pi = new JButton("π");
        pi.setPreferredSize(new Dimension(80, 50));
        JButton sqrt = new JButton("sqrt");
        sqrt.setPreferredSize(new Dimension(80, 50));
        JButton log = new JButton("log");
        log.setPreferredSize(new Dimension(80, 50));
        JButton ln = new JButton("ln");
        ln.setPreferredSize(new Dimension(80, 50));
        JButton exp = new JButton("^");
        exp.setPreferredSize(new Dimension(80, 50));
        JButton seven = new JButton("7");
        seven.setPreferredSize(new Dimension(80, 50));
        JButton eight = new JButton("8");
        eight.setPreferredSize(new Dimension(80, 50));
        JButton nine = new JButton("9");
        nine.setPreferredSize(new Dimension(80, 50));
        JButton div = new JButton("/");
        div.setPreferredSize(new Dimension(80, 50));
        JButton four = new JButton("4");
        four.setPreferredSize(new Dimension(80, 50));
        JButton five = new JButton("5");
        five.setPreferredSize(new Dimension(80, 50));
        JButton six = new JButton("6");
        six.setPreferredSize(new Dimension(80, 50));
        JButton mult = new JButton("*");
        mult.setPreferredSize(new Dimension(80, 50));
        JButton one = new JButton("1");
        one.setPreferredSize(new Dimension(80, 50));
        JButton two = new JButton("2");
        two.setPreferredSize(new Dimension(80, 50));
        JButton three = new JButton("3");
        three.setPreferredSize(new Dimension(80, 50));
        JButton sub = new JButton("-");
        sub.setPreferredSize(new Dimension(80, 50));
        JButton zero = new JButton("0");
        zero.setPreferredSize(new Dimension(80, 50));
        JButton dot = new JButton(".");
        dot.setPreferredSize(new Dimension(80, 50));
        JButton equals = new JButton("=");
        equals.setPreferredSize(new Dimension(80, 50));
        JButton add = new JButton("+");
        add.setPreferredSize(new Dimension(80, 50));
        
        sin.addActionListener(e -> sinBtn());
        cos.addActionListener(e -> cosBtn());
        tan.addActionListener(e -> tanBtn());
        clear.addActionListener(e -> clearBtn());
        arcsin.addActionListener(e -> arcsinBtn());
        arccos.addActionListener(e -> arccosBtn());
        arctan.addActionListener(e -> arctanBtn());
        pi.addActionListener(e -> piBtn());
        sqrt.addActionListener(e -> sqrtBtn());
        log.addActionListener(e -> logBtn());
        ln.addActionListener(e -> lnBtn());
        exp.addActionListener(e -> expBtn());
        seven.addActionListener(e -> sevenBtn());
        eight.addActionListener(e -> eightBtn());
        nine.addActionListener(e -> nineBtn());
        div.addActionListener(e -> divBtn());
        four.addActionListener(e -> fourBtn());
        five.addActionListener(e -> fiveBtn());
        six.addActionListener(e -> sixBtn());
        mult.addActionListener(e -> multBtn());
        one.addActionListener(e -> oneBtn());
        two.addActionListener(e -> twoBtn());
        three.addActionListener(e -> threeBtn());
        sub.addActionListener(e -> subBtn());
        zero.addActionListener(e -> zeroBtn());
        dot.addActionListener(e -> dotBtn());
        equals.addActionListener(e -> equalsBtn());
        add.addActionListener(e -> addBtn());
        
        /*
        buttons.add(sin);
        buttons.add(cos);
        buttons.add(tan);
        buttons.add(clear);
        buttons.add(arcsin);
        buttons.add(arccos);
        buttons.add(arctan);
        buttons.add(pi);
        buttons.add(sqrt);
        buttons.add(log);
        buttons.add(ln);
        buttons.add(exp);
        buttons.add(seven);
        buttons.add(eight);
        buttons.add(nine);
        buttons.add(div);
        buttons.add(four);
        buttons.add(five);
        buttons.add(six);
        buttons.add(mult);
        buttons.add(one);
        buttons.add(two);
        buttons.add(three);
        buttons.add(sub);
        buttons.add(zero);
        buttons.add(dot);
        buttons.add(equals);
        buttons.add(add);
         */
        
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        buttons.add(sin, c);
        c.gridx = 1;
        c.gridy = 0;
        buttons.add(cos, c);
        c.gridx = 2;
        c.gridy = 0;
        buttons.add(tan, c);
        c.gridx = 3;
        c.gridy = 0;
        buttons.add(clear, c);
        c.gridx = 0;
        c.gridy = 1;
        buttons.add(arcsin, c);
        c.gridx = 1;
        c.gridy = 1;
        buttons.add(arccos, c);
        c.gridx = 2;
        c.gridy = 1;
        buttons.add(arctan, c);
        c.gridx = 3;
        c.gridy = 1;
        buttons.add(pi, c);
        c.gridx = 0;
        c.gridy = 2;
        buttons.add(sqrt, c);
        c.gridx = 1;
        c.gridy = 2;
        buttons.add(log, c);
        c.gridx = 2;
        c.gridy = 2;
        buttons.add(ln, c);
        c.gridx = 3;
        c.gridy = 2;
        buttons.add(exp, c);
        c.gridx = 0;
        c.gridy = 3;
        buttons.add(seven, c);
        c.gridx = 1;
        c.gridy = 3;
        buttons.add(eight, c);
        c.gridx = 2;
        c.gridy = 3;
        buttons.add(nine, c);
        c.gridx = 3;
        c.gridy = 3;
        buttons.add(div, c);
        c.gridx = 0;
        c.gridy = 4;
        buttons.add(four, c);
        c.gridx = 1;
        c.gridy = 4;
        buttons.add(five, c);
        c.gridx = 2;
        c.gridy = 4;
        buttons.add(six, c);
        c.gridx = 3;
        c.gridy = 4;
        buttons.add(mult, c);
        c.gridx = 0;
        c.gridy = 5;
        buttons.add(one, c);
        c.gridx = 1;
        c.gridy = 5;
        buttons.add(two, c);
        c.gridx = 2;
        c.gridy = 5;
        buttons.add(three, c);
        c.gridx = 3;
        c.gridy = 5;
        buttons.add(sub, c);
        c.gridx = 0;
        c.gridy = 6;
        buttons.add(zero, c);
        c.gridx = 1;
        c.gridy = 6;
        buttons.add(dot, c);
        c.gridx = 2;
        c.gridy = 6;
        buttons.add(equals, c);
        c.gridx = 3;
        c.gridy = 6;
        buttons.add(add, c);
        c.gridwidth = 4;
        c.gridheight = 1;
        c.weighty = 3;
        c.gridx = 0;
        c.gridy = 7;
        buttons.add(new JLabel(), c);
        
        
        this.add(viewport, BorderLayout.NORTH);
        this.add(buttons, BorderLayout.CENTER);
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.add(new Calc());
        frame.setVisible(true);
    }
    
    private void sinBtn() {
        double num = Double.parseDouble(display.getText());
        display.setText(Double.toString(round(Math.sin(num), 6)));
    }
    
    private void cosBtn() {
        double num = Double.parseDouble(display.getText());
        display.setText(Double.toString(round(Math.cos(num), 6)));
    }
    
    private void tanBtn() {
        double num = Double.parseDouble(display.getText());
        display.setText(Double.toString(round(Math.tan(num), 6)));
    }
    
    private void clearBtn() {
        display.setText("0");
    }
    
    private void arcsinBtn() {
        double num = Double.parseDouble(display.getText());
        display.setText(Double.toString(round(Math.asin(num), 6)));
    }
    
    private void arccosBtn() {
        double num = Double.parseDouble(display.getText());
        display.setText(Double.toString(round(Math.acos(num), 6)));
    }
    
    private void arctanBtn() {
        double num = Double.parseDouble(display.getText());
        display.setText(Double.toString(round(Math.atan(num), 6)));
    }
    
    private void piBtn() {
        display.setText(Double.toString(Math.PI));
    }
    
    private void sqrtBtn() {
        double num = Double.parseDouble(display.getText());
        display.setText(Double.toString(round(Math.sqrt(num), 6)));
    }
    
    private void logBtn() {
        double num = Double.parseDouble(display.getText());
        display.setText(Double.toString(round(Math.log10(num), 6)));
    }
    
    private void lnBtn() {
        double num = Double.parseDouble(display.getText());
        display.setText(Double.toString(round(Math.log(num), 6)));
    }
    
    private void expBtn() {
        firstNumber = Double.parseDouble(display.getText());
        currentOperation = "^";
        display.setText("0");
    }
    
    private void sevenBtn() {
        if (display.getText().equals("0")) {
            display.setText("7");
        } else {
            display.setText(display.getText() + "7");
        }
    }
    
    private void eightBtn() {
        if (display.getText().equals("0")) {
            display.setText("8");
        } else {
            display.setText(display.getText() + "8");
        }
    }
    
    private void nineBtn() {
        if (display.getText().equals("0")) {
            display.setText("9");
        } else {
            display.setText(display.getText() + "9");
        }
    }
    
    private void divBtn() {
        firstNumber = Double.parseDouble(display.getText());
        currentOperation = "/";
        display.setText("0");
    }
    
    private void fourBtn() {
        if (display.getText().equals("0")) {
            display.setText("4");
        } else {
            display.setText(display.getText() + "4");
        }
    }
    
    private void fiveBtn() {
        if (display.getText().equals("0")) {
            display.setText("5");
        } else {
            display.setText(display.getText() + "5");
        }
    }
    
    private void sixBtn() {
        if (display.getText().equals("0")) {
            display.setText("6");
        } else {
            display.setText(display.getText() + "6");
        }
    }
    
    private void multBtn() {
        firstNumber = Double.parseDouble(display.getText());
        currentOperation = "*";
        display.setText("0");
    }
    
    private void oneBtn() {
        if (display.getText().equals("0")) {
            display.setText("1");
        } else {
            display.setText(display.getText() + "1");
        }
    }
    
    private void twoBtn() {
        if (display.getText().equals("0")) {
            display.setText("2");
        } else {
            display.setText(display.getText() + "2");
        }
    }
    
    private void threeBtn() {
        if (display.getText().equals("0")) {
            display.setText("3");
        } else {
            display.setText(display.getText() + "3");
        }
    }
    
    private void subBtn() {
        firstNumber = Double.parseDouble(display.getText());
        currentOperation = "-";
        display.setText("0");
    }
    
    private void zeroBtn() {
        if (display.getText().equals("0")) {
            display.setText("0");
        } else {
            display.setText(display.getText() + "0");
        }
    }
    
    private void dotBtn() {
        if (display.getText().contains(".")) {
            return;
        }
        if (display.getText().equals("0")) {
            display.setText("0.");
        } else {
            display.setText(display.getText() + ".");
        }
    }
    
    private void equalsBtn() {
        double secondNumber = Double.parseDouble(display.getText());
        double result = switch (currentOperation) {
            case "+" -> firstNumber + secondNumber;
            case "-" -> firstNumber - secondNumber;
            case "*" -> firstNumber * secondNumber;
            case "/" -> firstNumber / secondNumber;
            case "^" -> Math.pow(firstNumber, secondNumber);
            default -> 0;
        };
        display.setText(Double.toString(round(result, 6)));
    }
    
    private void addBtn() {
        firstNumber = Double.parseDouble(display.getText());
        currentOperation = "+";
        display.setText("0");
    }
    
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}