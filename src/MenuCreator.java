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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class MenuCreator {
	
	private final int buckets;
	private Hashtable<Integer, ArrayList<String>> wordList;
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
	private RedTextStyledDocument redTextSD;
		
	public MenuCreator(JTextPane text, JFrame frame){
		buckets = 50;
		menuBar = new JMenuBar();
		popMenu = new JPopupMenu();
	    redTextSD = new RedTextStyledDocument();
		pane = text;
		pane.setDocument(redTextSD);
		redTextSD.addDocumentListener(new RedTextDocumentListener());
		redTextSD.addUndoableEditListener(new RedTextUndoableEditListener());
		documentHasChanged = false;
		guiFrame = frame;
		undo = new UndoManager();
		undoAction = new UndoAction();
		redoAction = new RedoAction();
		wordList = new Hashtable<Integer, ArrayList<String>>();
		for(int i = 0; i < buckets; i++)
			wordList.put(i, new ArrayList<String>());
		loadWordList("WordList.ser");
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
		CheckSpellingAction spellingAction = new CheckSpellingAction();
        ReplaceDictionaryAction replaceDictionaryAction = new ReplaceDictionaryAction();
        AddWordAction addWordAction = new AddWordAction();
        RemoveWordAction removeWordAction = new RemoveWordAction();
		
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
		spellingAction.putValue(Action.NAME, "Check Spelling");
        replaceDictionaryAction.putValue(Action.NAME, "Replace Dictionary");
        addWordAction.putValue(Action.NAME, "Add Word to Dictionary");
        removeWordAction.putValue(Action.NAME, "Remove Word From Dictionary");
		
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
			Document doc = new DefaultStyledDocument();
			long start = System.currentTimeMillis();
			while((nextLine = bReader.readLine()) != null){
				try {
				      doc.insertString(doc.getLength(), nextLine + "\n", null);
				} catch(BadLocationException e) {
				      e.printStackTrace();
				}
			}
			bReader.close();
			pane.setDocument(doc);
			System.out.println(System.currentTimeMillis() - start);
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
	private int stringHash(String word){
		int counter = 0;
		for(int i = 0; i < word.length(); i++)
			counter += (int)word.charAt(i);
		return counter % buckets;
	}
	private void createWordList(String location) throws IOException{
		FileReader reader = new FileReader(location);
		BufferedReader bReader = new BufferedReader(reader);
		String nextLine = null;
		while((nextLine = bReader.readLine()) != null)
			wordList.get(stringHash(nextLine)).add(nextLine);
		bReader.close();
	}
	private void loadWordList(String location){
		try{
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(location));
			wordList = (Hashtable<Integer, ArrayList<String>>) is.readObject();
		} catch(IOException ex) {
			ex.printStackTrace();
		} catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}
	}
	private void saveWordList(){
		try{
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("WordList.ser"));
			os.writeObject(wordList);
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	private int binarySearch(String[] words, String target){
		if(words.length == 0)
			return -1;
		int mid, low, high, temp;
		low = 0;
		high = words.length - 1;
		mid = (low + high) / 2;
		temp = 0;
		while(low <= high){
			temp = words[mid].compareTo(target);
			if(temp < 0){
				low = mid;
				mid = (low + high) / 2;
			}
			else if(temp > 0){
				high = mid;
				mid = (low + high) / 2;
			}
			else
				return mid;
		}
		return -1;
	}
	private int inserBinarySearch(String[] words, String target){
		if(words.length == 0)
			return -1;
		int mid, low, high, temp;
		low = 0;
		high = words.length - 1;
		mid = (low + high) / 2;
		temp = 0;
		while(mid != low){
			temp = words[mid].compareTo(target);
			if(temp < 0){
				low = mid;
				mid = (low + high) / 2;
			}
			else if(temp > 0){
				high = mid;
				mid = (low + high) / 2;
			}
			else
				return -1;
		}
		return mid;
	}
	public void warningDialog(ActionEvent e, String message){
		String[] options = {"OK"};
		JPanel panel = new JPanel();
		JLabel lbl = new JLabel(message);
		panel.add(lbl);
		int selectedOption = JOptionPane.showOptionDialog(null, panel, "The Title",
				JOptionPane.NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options , options[0]);

	}
	private String getWord(ActionEvent e){
		String[] options = {"OK"};
		JPanel panel = new JPanel();
		JLabel lbl = new JLabel("Enter the word: ");
		JTextField txt = new JTextField(10);
		panel.add(lbl);
		panel.add(txt);
		int selectedOption = JOptionPane.showOptionDialog(null, panel, "The Title", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
		System.out.println(txt.getText());
		return txt.getText();
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
					target = fC.getSelectedFile();
					directory = fC.getCurrentDirectory();
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
	class PrintAction extends AbstractAction implements Printable{
		public void actionPerformed(ActionEvent e){
			PrinterJob job = PrinterJob.getPrinterJob();
	        job.setPrintable(this);
	        boolean toPrint = job.printDialog();
	        if(toPrint){
	            try {
	                 job.print();
	            } catch (PrinterException ex) {
	            	 
	            }
	        }
		}
		public int print(Graphics graphics, PageFormat format, int page) throws PrinterException{
			return 0;
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
            String fullText = pane.getText();
            String []text = fullText.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
            for(String word : text){
            	word = word.toLowerCase();
            	if(!((ArrayList<String>)wordList.get(stringHash(word))).contains(word))
            		System.out.println(word);
            }
        }
	}
	class ReplaceDictionaryAction extends AbstractAction{
	    public void actionPerformed(ActionEvent e){
	    	JFileChooser fC = new JFileChooser();
			int option = fC.showOpenDialog(null);
			if(option != JFileChooser.APPROVE_OPTION)
				return;			
			for(int i = 0; i < buckets; i++)
				wordList.get(new Integer(i)).clear();
			File replaceTarget = fC.getSelectedFile();
			File replaceDirectory = fC.getCurrentDirectory();
			try {
				createWordList(replaceDirectory.getCanonicalPath() + "//" + replaceTarget.getName());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
	    }
	}
	class AddWordAction extends AbstractAction{
	    public void actionPerformed(ActionEvent e){
	    	String toInsert = getWord(e);
	    	ArrayList<String> temp = wordList.get(stringHash(toInsert));
	        int index = inserBinarySearch(temp.toArray(new String[temp.size()]), toInsert);
	        if(index == -1){
	        	warningDialog(e, "The word you are looking for is already in the Dictionary");
	        	return;
	        }
	        if(temp.get(index).compareTo(toInsert) < 0)
	        	temp.add(index + 1, toInsert);
	        else
	        	temp.add(index, toInsert);
	    }
	}
	class RemoveWordAction extends AbstractAction{
	    public void actionPerformed(ActionEvent e){
	    	String wordToRemove = getWord(e);
	    	ArrayList<String> temp = wordList.get(stringHash(wordToRemove));
	        System.out.println(temp.toString());
	        int index = binarySearch(temp.toArray(new String[temp.size()]), wordToRemove);
	        if(index == -1){
	        	warningDialog(e, "The word entered is not in the dictionary");
	        	System.out.println(wordList.get(stringHash("a")).get(1));
	        	return;
	        }
	        temp.set(index, temp.get(index) + "!");
	        System.out.println(temp.get(index));
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
	class RedTextStyledDocument extends DefaultStyledDocument{
		final StyleContext cont;
        final AttributeSet attr;
        final AttributeSet attrBlack;
        
        public RedTextStyledDocument(){
        	cont = StyleContext.getDefaultStyleContext();
        	attr = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.RED);
        	attrBlack = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
        }
        public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
            super.insertString(offset, str, a);

            String text = getText(0, getLength());
            int before = findLastNonWordChar(text, offset);
            if (before < 0) before = 0;
            int after = findFirstNonWordChar(text, offset + str.length());
            int wordL = before;
            int wordR = before;
            String temp = text.substring(before, after);
            boolean incor;
            while (wordR <= after) {
            	incor = wordList.get(stringHash(temp)).contains(temp);
                if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
                    if (incor)
                        setCharacterAttributes(wordL, wordR - wordL, attr, false);
                    else
                        setCharacterAttributes(wordL, wordR - wordL, attrBlack, false);
                    wordL = wordR;
                }
                wordR++;
            }
        } 
        public void remove (int offs, int len) throws BadLocationException {
            super.remove(offs, len);
            String text = getText(0, getLength());
            int before = findLastNonWordChar(text, offs);
            if (before < 0) before = 0;
            int after = findFirstNonWordChar(text, offs); 
            String temp = text.substring(before, after);
            boolean incor = wordList.get(stringHash(temp)).contains(temp);
            if (incor) {
                setCharacterAttributes(before, after - before, attr, false);
            } else {
                setCharacterAttributes(before, after - before, attrBlack, false);
            }
        }
        private int findLastNonWordChar (String text, int index) {
            while (--index >= 0) {
                if (String.valueOf(text.charAt(index)).matches("\\W")) break;
            }
            // Iterate through the text to find the last character that isn't a-z,A-Z or 0-9
            return index;
        }

        private int findFirstNonWordChar (String text, int index) {
            while (index < text.length()) {
                if (String.valueOf(text.charAt(index)).matches("\\W")) break;
                index++;
                //starting from index, advance until you find a character that isn't a-z, A-Z, 0-9
            }
            return index;
        }
	}
	
}