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
 *  	private checkTitle
 *  	private makeSaveWarning
 * 
 * Inner Classes:
 * 		NewAction
 * 		OpenAction
 * 		SaveAction
 * 		CloseAction
 * 		RedTextDocumentListener
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
	private boolean checkTitle(){
		return (guiFrame.getTitle().compareTo("Untitled") == 0);
	}
	int makeSaveWarning(){
		JOptionPane warningPane = new JOptionPane();
		Object[] options = {"Save",
        "Don't Save", "Cancel"};
		return JOptionPane.showOptionDialog(guiFrame,
			"There are unsaved changes to your work. Do you wish to save?", "Warning",
			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]); 
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	class NFileAction extends AbstractAction{	
		public void actionPerformed(ActionEvent e) {
			int warningOption = makeSaveWarning();
			if(warningOption == JOptionPane.YES_OPTION)
				new SaveAction().actionPerformed(e);
			else if(warningOption == JOptionPane.NO_OPTION)
				documentHasChanged = false;
			else{
				documentHasChanged = true;
				return;
			}
			if(!documentHasChanged)	
				pane.setText("");
				documentHasChanged = false;
		}
	}
	class OpenAction extends AbstractAction{
		public void actionPerformed(ActionEvent e){
			int warningOption = makeSaveWarning();
			if(warningOption == JOptionPane.YES_OPTION)
				new SaveAction().actionPerformed(e);
			else if(warningOption == JOptionPane.NO_OPTION)
				documentHasChanged = false;
			else{
				documentHasChanged = true;
				return;
			}
			if(!documentHasChanged){
				JFileChooser fC = new JFileChooser();
				int option = fC.showOpenDialog(null);
				if(option == JFileChooser.APPROVE_OPTION){
					File target = fC.getSelectedFile();
					File directory = fC.getCurrentDirectory();
					pane.setText("");
					openFile(target, directory);
					guiFrame.setTitle(target.getName());
					documentHasChanged = false;
				}
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
			documentHasChanged = false;
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
			int warningOption = makeSaveWarning();
			if(warningOption == JOptionPane.YES_OPTION)
				new SaveAction().actionPerformed(e);
			else if(warningOption == JOptionPane.NO_OPTION)
				documentHasChanged = false;
			else{
				documentHasChanged = true;
				return;
			}
			if(!documentHasChanged)
				System.exit(0);
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