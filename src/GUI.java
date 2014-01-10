import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GUI {
	JTextPane text;
	JTabbedPane workArea;
	private JFrame frame;
	private MenuCreator creator;
	private JScrollPane scroller;
	
	public GUI(){
		frame = new JFrame("Untitled");
		text = new JTextPane();
		scroller = new JScrollPane(text);
		creator = new MenuCreator(text, frame);
	}
	public void constructGUI(){
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		frame.getContentPane().add(BorderLayout.CENTER, scroller);
		frame.setJMenuBar(creator.makeMenu()); 
		frame.setSize(800, 600);
		frame.setLocation(250, 50);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);	
	}	
}