import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

/* Class summary:
 * Method Declarations:
 *  	public MenuCreator
 *  	public makeMenu
 *  	public makePopupMenu
 *  	private openFile
 *  	private saveFile
 * 
 * Inner Classes:
 * 		NewAction
 * 		OpenAction
 * 		SaveAction
 * 		CloseAction
 * 
 */
public class MenuCreator {
	
	JTextPane pane;
	JFrame guiFrame;
	File target;
	File directory;
	private JMenuBar menuBar;
	private JPopupMenu popMenu;
	private JMenu fileMenu;
	private JMenu editMenu;
	private boolean documentHasChanged;
		
	public MenuCreator(JTextPane text, JFrame frame){
		menuBar = new JMenuBar();
		popMenu = new JPopupMenu();
		fileMenu = new JMenu("File    ");
		editMenu = new JMenu("Edit    ");
		pane = text;
		pane.getDocument().addDocumentListener(new RedTextDocumentListener());
		documentHasChanged = false;
		guiFrame = frame;
	}
	public JMenuBar makeMenu(){
		NFileAction nAction = new NFileAction();
		nAction.putValue(Action.NAME, "New");
		
		OpenAction oAction = new OpenAction();
		oAction.putValue(Action.NAME, "Open");
		
		SaveAction sAction = new SaveAction();
		sAction.putValue(Action.NAME, "Save");
		
		SaveAsAction sAAction = new SaveAsAction();
		sAAction.putValue(Action.NAME, "Save As");
		
		CloseAction cAction = new CloseAction();
		cAction.putValue(Action.NAME, "Close");
		
		Action cutAction = new DefaultEditorKit.CutAction();
		cutAction.putValue(Action.NAME, "Cut");
		
		Action copyAction = new DefaultEditorKit.CopyAction();
		copyAction.putValue(Action.NAME, "Copy");
		
		Action pasteAction = new DefaultEditorKit.PasteAction();
		pasteAction.putValue(Action.NAME, "Paste");
		
		JMenuItem newItem = new JMenuItem(nAction);
		JMenuItem openItem = new JMenuItem(oAction);
		JMenuItem saveItem = new JMenuItem(sAction);
		JMenuItem saveAsItem = new JMenuItem(sAAction);
		JMenuItem closeItem = new JMenuItem(cAction);
		JMenuItem cutItem = new JMenuItem(cutAction);
		JMenuItem copyItem = new JMenuItem(copyAction);
		JMenuItem pasteItem = new JMenuItem(pasteAction);
		
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(new JSeparator());
		fileMenu.add(closeItem);
		
		editMenu.add(cutItem);
		editMenu.add(copyItem);
		editMenu.add(pasteItem);
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		return menuBar;
	}
	public JPopupMenu makePopupMenu(){
		return popMenu;
	}
	private void openFile(File target, File directory){
		try{
			FileReader reader = new FileReader(directory.getCanonicalPath() + "\\" + target.getName());
			BufferedReader bReader = new BufferedReader(reader);
			String nextLine = null;
			while((nextLine = bReader.readLine()) != null){
				try {
				      Document doc = pane.getDocument();
				      doc.insertString(doc.getLength(), nextLine + "\n", null);
				} catch(BadLocationException e) {
				      e.printStackTrace();
				}
			}
			bReader.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	private void saveFile(File target, File directory){
		try{
			FileWriter writer = new FileWriter(directory.getCanonicalPath() + "\\" + target.getName());
			writer.write(pane.getText());
			writer.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	private boolean checkTitle(){	// Returns true if the title of the file and therefore the JFrame is "Untitled"
		return (guiFrame.getTitle().compareTo("Untitled") == 0);
	}
	private JDialog createWarning(String warning){
		JDialog tempDialog = new JDialog();
		JPanel tempPanel = new JPanel();
		JLabel tempLabel = new JLabel(warning);
		tempPanel.add(tempLabel);
		tempDialog.getContentPane().add(BorderLayout.NORTH, tempPanel);
		tempDialog.setSize(300, 100);
		tempDialog.setLocation(500, 275);
		tempDialog.setVisible(true);
		tempDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		return tempDialog;
	}
	private void makeSaveWarning(){
		JDialog tempDialog = new JDialog();
		JPanel tempPanel = new JPanel();
		JLabel tempLabel = new JLabel("Do you want to save you changes to " + guiFrame.getTitle());
		JButton save = new JButton("Save");
		save.addActionListener(new SaveAction());
		JButton dontSave = new JButton("Don't Save");
		dontSave.addActionListener();
		JButton cancel = new JButton("Cancel");
		tempPanel.add(tempLabel);
		tempDialog.getContentPane().add(BorderLayout.NORTH, tempPanel);
		tempDialog.setSize(300, 100);
		tempDialog.setLocation(500, 275);
		tempDialog.setVisible(true);
		tempDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	class NFileAction extends AbstractAction{	
		public void actionPerformed(ActionEvent e) {
			final JDialog saveDialog = createWarning("Do you wish to save before creating a new file?");
			JButton save = new JButton("Save");
			JButton dontSave = new JButton("Don't Save");
			JPanel savePanel = new JPanel();
			save.addActionListener(new SaveAsAction());
			dontSave.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					saveDialog.dispose();
				}
			});
			savePanel.add(save);
			savePanel.add(dontSave);
			saveDialog.add(BorderLayout.CENTER, savePanel);
			pane.setText("");
		}
	}
	class OpenAction extends AbstractAction{
		public void actionPerformed(ActionEvent e){
			JFileChooser fC = new JFileChooser();
			int option = fC.showOpenDialog(null);
			if(option == JFileChooser.APPROVE_OPTION){
				File target = fC.getSelectedFile();
				File directory = fC.getCurrentDirectory();
				pane.setText("");
				openFile(target, directory);
				guiFrame.setTitle(target.getName());
			}
		}
	}// End OpenAction
	class SaveAction extends AbstractAction{
		public void actionPerformed(ActionEvent e){
			if(checkTitle() || target == null || directory == null){
				JFileChooser fileChooser = new JFileChooser();
				int option = fileChooser.showSaveDialog(null);
				if(option == JFileChooser.APPROVE_OPTION){
					target = fileChooser.getSelectedFile();
					directory = fileChooser.getCurrentDirectory();
					saveFile(target, directory);
				}
			}else{
				saveFile(target, directory);
			}
		}
	}
	class SaveAsAction extends SaveAction{
		public void actionPerformed(ActionEvent e){
			target = directory = null;
			super.actionPerformed(e);
			guiFrame.setTitle(target.getName());
		}
	}
	class CloseAction extends AbstractAction{ // Close Action is to be unextendable
		public void actionPerformed(ActionEvent e){
			final JDialog exitDialog = new JDialog();
			JPanel exitPanel = new JPanel();
			JLabel exitLabel = new JLabel("Are you sure you wish to exit RedText?");
			JButton exit = new JButton("Yes");
			JButton dontExit = new JButton("No");
			exit.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						System.exit(0);
					}
				}
			);
			dontExit.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						exitDialog.dispose();
					}
				}
			);
			exitPanel.add(exitLabel);
			exitPanel.add(exit);
			exitPanel.add(dontExit);
			exitDialog.getContentPane().add(exitPanel);
			exitDialog.setSize(300, 100);
			exitDialog.setLocation(500, 275);
			exitDialog.setVisible(true);
			exitDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
	}
	class RedTextDocumentListener implements DocumentListener{
		public void insertUpdate(DocumentEvent e) {
            documentHasChanged = true;
        }
        public void removeUpdate(DocumentEvent e) {
            documentHasChanged = true;
        }
        public void changedUpdate(DocumentEvent e) {
        	documentHasChanged = true;
        }
	}
}