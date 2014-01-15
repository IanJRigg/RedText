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
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class MenuCreator {
	
	private JTextPane pane;
	private JFrame guiFrame;
	private File target;
	private File directory;
	private JMenuBar menuBar;
	private JPopupMenu popMenu;
	private boolean documentHasChanged;
	private UndoAction undoAction;
	private RedoAction redoAction;
	private UndoManager undo;
	private AbstractDocument doc;
		
	public MenuCreator(JTextPane text, JFrame frame){
		menuBar = new JMenuBar();
		popMenu = new JPopupMenu();
		pane = text;
		pane.getDocument().addDocumentListener(new RedTextDocumentListener());
		documentHasChanged = false;
		guiFrame = frame;
		undo = new UndoManager();
		undoAction = new UndoAction();
		redoAction = new RedoAction();
		Document styledDoc = pane.getDocument();
		if (styledDoc instanceof AbstractDocument)
            doc = (AbstractDocument)styledDoc;
		doc.addUndoableEditListener(new RedTextUndoableEditListener());
	}
	public JMenuBar makeMenu(){
		JMenu fileMenu = new JMenu("File        ");
		JMenu editMenu = new JMenu("Edit        ");
		JMenu spellMenu = new JMenu("Spelling        ");
		LinkedList<JComponent> items = makeMenuItems();
		Iterator<JComponent> it = items.listIterator();
		int itemsCounter = 0;
		while(itemsCounter < 4){
			fileMenu.add(it.next());
			itemsCounter++;
		}
		fileMenu.add(new JSeparator());
		fileMenu.add(it.next());
		while(itemsCounter < 9){
			editMenu.add(it.next());
			itemsCounter++;
		}
		editMenu.add(new JSeparator());
		editMenu.add(it.next());
		while(it.hasNext())
			spellMenu.add(it.next());
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(spellMenu);
		return menuBar;
	}
	public JPopupMenu makePopupMenu(){
		return popMenu;
	}
	private LinkedList<JComponent> makeMenuItems(){
		LinkedList<JComponent> menuItems = new LinkedList<JComponent>();
		NewFileAction newAction = new NewFileAction();
		OpenAction openAction = new OpenAction();
		SaveAction saveAction = new SaveAction();
		SaveAsAction saveAsAction = new SaveAsAction();
		CloseAction closeAction = new CloseAction();
		Action cutAction = new DefaultEditorKit.CutAction();
		Action copyAction = new DefaultEditorKit.CopyAction();
		Action pasteAction = new DefaultEditorKit.PasteAction();
		SelectAllAction selectAction = new SelectAllAction();
		CheckSpellingAction spellingAction = new CheckSpellingAction();
		ReplaceDictionaryAction replaceDictionaryAction = new ReplaceDictionaryAction();
		AddWordAction addWordAction = new AddWordAction();
		RemoveWordAction removeWordAction = new RemoveWordAction();
		
		newAction.putValue(Action.NAME, "New");
		openAction.putValue(Action.NAME, "Open");
		saveAction.putValue(Action.NAME, "Save");
		saveAsAction.putValue(Action.NAME, "Save As");
		closeAction.putValue(Action.NAME, "Close");
		cutAction.putValue(Action.NAME, "Cut");
		copyAction.putValue(Action.NAME, "Copy");
		pasteAction.putValue(Action.NAME, "Paste");
		selectAction.putValue(Action.NAME, "Select All");
		undoAction.putValue(Action.NAME, "Undo");
		redoAction.putValue(Action.NAME, "Redo");
		spellingAction.putValue(Action.NAME, "Check Spelling");
		replaceDictionaryAction.putValue(Action.NAME, "Replace Dictionary");
		addWordAction.putValue(Action.NAME, "Add Word to Dictionary");
		removeWordAction.putValue(Action.NAME, "Remove Word From Dictionary");
		
		JMenuItem newItem = new JMenuItem(newAction);
		JMenuItem openItem = new JMenuItem(openAction);
		JMenuItem saveItem = new JMenuItem(saveAction);
		JMenuItem saveAsItem = new JMenuItem(saveAsAction);
		JMenuItem closeItem = new JMenuItem(closeAction);
		JMenuItem cutItem = new JMenuItem(cutAction);
		JMenuItem copyItem = new JMenuItem(copyAction);
		JMenuItem pasteItem = new JMenuItem(pasteAction);
		JMenuItem selectAllItem = new JMenuItem(selectAction);
		JMenuItem undoItem = new JMenuItem(undoAction);
		JMenuItem redoItem = new JMenuItem(redoAction);
		
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
		cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
		pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));
		selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK));
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_MASK));
		
		menuItems.add(newItem);
		menuItems.add(openItem);
		menuItems.add(saveItem);
		menuItems.add(saveAsItem);
		menuItems.add(closeItem);
		menuItems.add(undoItem);
		menuItems.add(redoItem);
		menuItems.add(cutItem);
		menuItems.add(copyItem);
		menuItems.add(pasteItem);
		menuItems.add(selectAllItem);
		menuItems.add(new JMenuItem(spellingAction));
		menuItems.add(new JMenuItem(addWordAction));
		menuItems.add(new JMenuItem(removeWordAction));
		menuItems.add(new JSeparator());
		menuItems.add(new JMenuItem(replaceDictionaryAction));
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
	class PrintAction extends AbstractAction{
		public void actionPerformed(ActionEvent e){
			
		}
	}
	class CloseAction extends AbstractAction{ // Close Action is to be unextendable
		public void actionPerformed(ActionEvent e){
			makeSaveWarning(e);
			if(!documentHasChanged)
				System.exit(0);
		}
	}
	class RedTextUndoableEditListener implements UndoableEditListener {
		
		public void undoableEditHappened(UndoableEditEvent e) {
			undo.addEdit(e.getEdit());
			undoAction.updateUndoState();
			redoAction.updateRedoState();
		}
	}  
	class UndoAction extends AbstractAction{
		 public UndoAction() {
	            super("Undo");
	            setEnabled(false);
	     }	
		public void actionPerformed(ActionEvent ev){
			try{
				undo.undo();
			}catch(CannotUndoException ex){}
			
			updateUndoState();
			redoAction.updateRedoState();
		}
		protected void updateUndoState() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
	}
	class RedoAction extends AbstractAction{
		public RedoAction() {
            super("Redo");
            setEnabled(false);
        }
		public void actionPerformed(ActionEvent ev){
			try{
				undo.redo();
			}catch(CannotRedoException ex){}	
			updateRedoState();
			undoAction.updateUndoState();
		}
		public void updateRedoState() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }	
	}
	class SelectAllAction extends AbstractAction{
		public void actionPerformed(ActionEvent e){
			pane.selectAll();
		}
	}
	class CheckSpellingAction extends AbstractAction{
		public void actionPerformed(ActionEvent e){
			
		}
	}
	class ReplaceDictionaryAction extends AbstractAction{
		public void actionPerformed(ActionEvent e){
			
		}
	}
	class AddWordAction extends AbstractAction{
		public void actionPerformed(ActionEvent e){
			
		}
	}
	class RemoveWordAction extends AbstractAction{
		public void actionPerformed(ActionEvent e){
			
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