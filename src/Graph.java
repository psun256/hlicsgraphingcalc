import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.ArrayList;

/*
 * graphing "applet"
 * contains equation input, boundary input, and the renderer itself
 * acts as a frontend for the Renderer class, which itself is a frontend for the MarchingCubes class
 */
public class Graph extends JPanel {
    private final JTextField ix1;
    private final JTextField ix2;
    private final JTextField iy1;
    private final JTextField iy2;
    private final JTextField iz1;
    private final JTextField iz2;
    private final JTextArea equation;
    private final JButton render;
    private final JPanel boundaries;
    private final Insets inset = new Insets(5, 5, 5, 5);
    private Renderer r;
    private JPanel input;
    
    public Graph() {
        super();
        // Renderer on the right, equation input, boundaries on the left
        this.setLayout(new BorderLayout());
        
        equation = new JTextArea();
        ix1 = new JTextField();
        ix2 = new JTextField();
        iy1 = new JTextField();
        iy2 = new JTextField();
        iz1 = new JTextField();
        iz2 = new JTextField();
        render = new JButton("Render");
        r = new Renderer();
       
        /*
        equation:
        <input>
        ix1: <input> ix2: <input>
        iy1: <input> iy2: <input>
        iz1: <input> iz2: <input>
        <render button>
        
        top level: gridlayout with 5 rows (1 blank row at bottom)
        1st row: equation label
        2nd row: equation input
        3rd row: boundaries
        4th row: render button
        5th row: blank, expands to fill space
         */
        
        JPanel input = new JPanel();
        input.setLayout(new GridBagLayout());
        input.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(240, 240, 240), 5),
            BorderFactory.createLineBorder(new Color(102, 102, 102), 2)
        ));
        input.setBackground(new Color(230, 230, 230));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = inset;
        
        /*
        JLabel equationLabel = new JLabel("Equation:");
        equationLabel.setPreferredSize(new Dimension(200, 10));
        input.add(equationLabel);
        equation.setPreferredSize(new Dimension(200, 10));
        input.add(equation);
        JPanel boundaries = new JPanel();
        boundaries.setLayout(new GridLayout(3, 4));
        boundaries.add(new JLabel("x1:"));
        boundaries.add(ix1);
        boundaries.add(new JLabel("x2:"));
        boundaries.add(ix2);
        boundaries.add(new JLabel("y1:"));
        boundaries.add(iy1);
        boundaries.add(new JLabel("y2:"));
        boundaries.add(iy2);
        boundaries.add(new JLabel("z1:"));
        boundaries.add(iz1);
        boundaries.add(new JLabel("z2:"));
        boundaries.add(iz2);
        input.add(boundaries);
        input.add(render);
        input.add(new JLabel());
        this.add(input, BorderLayout.WEST);
        this.add(r, BorderLayout.CENTER);
        this.setVisible(true);
         */
        
        c.weightx = 1;
        c.weighty = 0.1;
        c.gridx = 0;
        c.gridy = 0;
        input.add(new JLabel("Equation:"), c);
        c.gridy = 1;
        c.weighty = 0.25;
        equation.setPreferredSize(new Dimension(200, 10));
        equation.setLineWrap(true);
        equation.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        input.add(equation, c);
        c.gridy = 2;
        boundaries = new JPanel();
        boundaries.setLayout(new GridLayout(3, 4));
        boundaries.add(new JLabel("x1:"));
        boundaries.add(ix1);
        boundaries.add(new JLabel("x2:"));
        boundaries.add(ix2);
        boundaries.add(new JLabel("y1:"));
        boundaries.add(iy1);
        boundaries.add(new JLabel("y2:"));
        boundaries.add(iy2);
        boundaries.add(new JLabel("z1:"));
        boundaries.add(iz1);
        boundaries.add(new JLabel("z2:"));
        boundaries.add(iz2);
        c.weighty = 0.3;
        input.add(boundaries, c);
        c.gridy = 3;
        c.weighty = 0.1;
        render.addActionListener(e -> render());
        input.add(render, c);
        c.gridy = 4;
        c.weighty = 2.5;
        input.add(new JLabel(), c);
        this.add(input, BorderLayout.WEST);
        this.add(r, BorderLayout.CENTER);
        this.setVisible(true);
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.getContentPane().add(new Graph());
        frame.setVisible(true);
    }
    
    private void render() {
        Expression ex1, ex2, ey1, ey2, ez1, ez2, equation;
        try {
            ex1 = new Expression(ix1.getText());
            ex2 = new Expression(ix2.getText());
            ey1 = new Expression(iy1.getText());
            ey2 = new Expression(iy2.getText());
            ez1 = new Expression(iz1.getText());
            ez2 = new Expression(iz2.getText());
        } catch (Exception e) {
            // joptionpane
            JOptionPane.showMessageDialog(this, "Invalid boundaries");
            return;
        }
        try {
            String expr = this.equation.getText();
            // if there is 2 or more equal signs, invalid
            if (expr.split("=").length > 2) {
                JOptionPane.showMessageDialog(this, "Invalid equation or expression");
                return;
            }
            // if there is 1 equal sign, move everything to the left side
            if (expr.split("=").length == 2) {
                expr = expr.split("=")[0] + "-(" + expr.split("=")[1] + ")";
            }
            equation = new Expression(expr);
            float x1 = (float) ex1.evaluate(0);
            float x2 = (float) ex2.evaluate(0);
            float y1 = (float) ey1.evaluate(0);
            float y2 = (float) ey2.evaluate(0);
            float z1 = (float) ez1.evaluate(0);
            float z2 = (float) ez2.evaluate(0);
            
            // generate the vertices of the mesh extracted from the function with marching cubes
            MarchingCubes mc = new MarchingCubes(equation, x1, y1, z1, x2, y2, z2);
            mc.generateScalarField();
            ArrayList<Float> vertices = mc.extractMesh();
            
            // add new renderer
            this.remove(r);
            this.setVisible(false);
            r = new Renderer(x1, y1, z1, x2, y2, z2, vertices);
            r.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 240, 240), 5),
                BorderFactory.createLineBorder(new Color(102, 102, 102), 2)
            ));
            this.add(r, BorderLayout.CENTER);
            r.startAnimation();
            r.setVisible(true);
            this.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid equation or expression");
        }
    }
}
