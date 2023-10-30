import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/*
 * shell "applet"
 * frontend for the Commands class
 * contains input and output
 */
public class Shell extends JPanel {
    private Parser parser;
    
    public Shell() {
        super();
        this.setLayout(new BorderLayout());
        JTextField inp = new JTextField();
        JTextArea out = new JTextArea();
        
        this.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(240, 240, 240), 5),
            BorderFactory.createLineBorder(new Color(102, 102, 102), 2)
        ));
        
        out.setBackground(new Color(230, 230, 230));
        out.setEditable(false);
        out.setLineWrap(true);
        out.setWrapStyleWord(true);
        
        JScrollPane scroll = new JScrollPane(out);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        this.add(scroll, BorderLayout.CENTER);
        this.add(inp, BorderLayout.SOUTH);
        
        //Commands commands = new Commands();
        
        inp.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inp.getText();
                out.append(">>> " + input + "\n");
                out.append("" + Commands.execute(input) + "\n");
                inp.setText("");
            }
        });
        
        this.add(scroll, BorderLayout.CENTER);
        this.add(inp, BorderLayout.SOUTH);
        
        this.setVisible(true);
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Shell");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Shell());
        frame.setVisible(true);
    }
}
