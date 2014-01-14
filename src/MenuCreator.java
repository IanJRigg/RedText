/* Class summary:
 * Import Statements
 * Method Declarations:
 *  	public MenuCreator
 *  	public makeMenuItems
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
import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

public class MenuCreator {
	
	private JTextPane pane;
	private JFrame guiFrame;
	private File target;
	private File directory;
	private JMenuBar menuBar;
	private JPopupMenu popMenu;
	private JMenu fileMenu;
	private JMenu editMenu;
	private boolean documentHasChanged;
		
	public MenuCreator(JTextPane text, JFrame frame){
		menuBar = new JMenuBar();
		popMenu = new JPopupMenu();
		fileMenu = new JMenu("File        ");
		editMenu = new JMenu("Edit        ");
		pane = text;
		pane.getDocument().addDocumentListener(new RedTextDocumentListener());
		documentHasChanged = false;
		guiFrame = frame;
	}
	public JMenuBar makeMenu(){
		LinkedList<JMenuItem> items = makeMenuItems();
		Iterator<JMenuItem> it = items.listIterator();
		int itemsCounter = 0;
		while(itemsCounter < 4){
			fileMenu.add(it.next());
			itemsCounter++;
		}
		fileMenu.add(new JSeparator());
		fileMenu.add(it.next());
		while(itemsCounter < 7){
			editMenu.add(it.next());
			itemsCounter++;
		}
		editMenu.add(new JSeparator());
		editMenu.add(it.next());
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		return menuBar;
	}
	public JPopupMenu makePopupMenu(){
		return popMenu;
	}
	private LinkedList<JMenuItem> makeMenuItems(){
		LinkedList<JMenuItem> menuItems = new LinkedList<JMenuItem>();
		NewFileAction newAction = new NewFileAction();
		OpenAction openAction = new OpenAction();
		SaveAction saveAction = new SaveAction();
		SaveAsAction saveAsAction = new SaveAsAction();
		CloseAction closeAction = new CloseAction();
		Action cutAction = new DefaultEditorKit.CutAction();
		Action copyAction = new DefaultEditorKit.CopyAction();
		Action pasteAction = new DefaultEditorKit.PasteAction();
		SelectAllAction selectAction = new SelectAllAction();
		
		newAction.putValue(Action.NAME, "New");
		openAction.putValue(Action.NAME, "Open");
		saveAction.putValue(Action.NAME, "Save");
		saveAsAction.putValue(Action.NAME, "Save As");
		closeAction.putValue(Action.NAME, "Close");
		cutAction.putValue(Action.NAME, "Cut");
		copyAction.putValue(Action.NAME, "Copy");
		pasteAction.putValue(Action.NAME, "Paste");
		selectAction.putValue(Action.NAME, "Select All");
		
		JMenuItem newItem = new JMenuItem(newAction);
		JMenuItem openItem = new JMenuItem(openAction);
		JMenuItem saveItem = new JMenuItem(saveAction);
		JMenuItem saveAsItem = new JMenuItem(saveAsAction);
		JMenuItem closeItem = new JMenuItem(closeAction);
		JMenuItem cutItem = new JMenuItem(cutAction);
		JMenuItem copyItem = new JMenuItem(copyAction);
		JMenuItem pasteItem = new JMenuItem(pasteAction);
		JMenuItem selectAllItem = new JMenuItem(selectAction);
		
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
		cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
		pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));
		selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
		
		menuItems.add(newItem);
		menuItems.add(openItem);
		menuItems.add(saveItem);
		menuItems.add(saveAsItem);
		menuItems.add(closeItem);
		menuItems.add(cutItem);
		menuItems.add(copyItem);
		menuItems.add(pasteItem);
		menuItems.add(selectAllItem);
		return menuItems;
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
	private void makeSaveWarning(ActionEvent e){
		if(documentHasChanged){
			JOptionPane warningPane = new JOptionPane();
			Object[] options = {"Save", "Don't Save", "Cancel"};
			int warningOption = JOptionPane.showOptionDialog(guiFrame,
				"There are unsaved changes to your work. Do you wish to save?", "Warning",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]); 
			if(warningOption == JOptionPane.YES_OPTION)
				new SaveAction().actionPerformed(e);
			else if(warningOption == JOptionPane.NO_OPTION)
				documentHasChanged = false;
			else
				documentHasChanged = true;
		}
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	class NewFileAction extends AbstractAction{	
		public void actionPerformed(ActionEvent e) {
			makeSaveWarning(e);
			if(!documentHasChanged)	
				pane.setText("");
				guiFrame.setTitle("Untitled");
				documentHasChanged = false;
		}
	}
	class OpenAction extends AbstractAction{
		public void actionPerformed(ActionEvent e){
			makeSaveWarning(e);
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
					guiFrame.setTitle(target.getName());
				}
			}else
				saveFile(target, directory);
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
			makeSaveWarning(e);
			if(!documentHasChanged)
				System.exit(0);
		}
	}
	class SelectAllAction extends AbstractAction{
		public void actionPerformed(ActionEvent e){
			pane.selectAll();
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