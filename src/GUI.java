import javax.swing.*;
import java.awt.*;

// creates actual calculator window
public class GUI extends JFrame {
    public GUI(String name) {
        super(name);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        this.setSize(screenWidth * 3 / 4, screenHeight * 3 / 4);
        this.setLocation(screenWidth / 8, screenHeight / 8);
        this.setBackground(new Color(240, 240, 240));
        
        JPanel upper = new JPanel();
        upper.setLayout(new BorderLayout());
        
        this.setLayout(new BorderLayout());
        
        Graph graph = new Graph();
        graph.setDoubleBuffered(true);
        Calc calc = new Calc();
        calc.setDoubleBuffered(true);
        Shell shell = new Shell();
        shell.setDoubleBuffered(true);
        
        upper.add(graph, BorderLayout.CENTER);
        calc.setPreferredSize(new Dimension(380, 0));
        upper.add(calc, BorderLayout.EAST);
        
        this.add(upper, BorderLayout.CENTER);
        shell.setPreferredSize(new Dimension(0, 300));
        this.add(shell, BorderLayout.SOUTH);
        
        this.setVisible(true);
    }
    
    public static void main(String[] args) {
        GUI calc = new GUI("test");
    }
}
