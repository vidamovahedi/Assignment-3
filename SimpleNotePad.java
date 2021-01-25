import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;

public class SimpleNotePad {
    
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenu editMenu = new JMenu("Edit");
    JTextPane textPane = new JTextPane();
    JMenuItem newFileMenuItem = new JMenuItem("New File");
    JMenuItem saveFileMenuItem = new JMenuItem("Save File");
    JMenuItem openFileMenuItem = new JMenuItem("Open File");
    JMenuItem printFileMenuItem = new JMenuItem("Print File");
    JMenuItem copyMenuItem = new JMenuItem("Copy");
    JMenuItem pasteMenuItem = new JMenuItem("Paste");
    JMenuItem undoMenuItem = new JMenuItem("Undo");
    JMenuItem replaceMenuItem = new JMenuItem("Replace");
    JMenu recentMenu = new JMenu("Recent");
    ArrayList<JMenuItem> recentFilesMenuItemsList = new ArrayList();
    ArrayList<File> recentFilesList = new ArrayList();
    JMenuItem recentfile1 = new JMenuItem();
    JMenuItem recentfile2 = new JMenuItem();
    JMenuItem recentfile3 = new JMenuItem();
    JMenuItem recentfile4 = new JMenuItem();
    JMenuItem recentfile5 = new JMenuItem();
    private Document editorPaneDocument;
    protected UndoManager undoManager = new UndoManager();
    int recentFilesLength = 0;
    JFrame frame = new JFrame();
    JRootPane rootPane = new JRootPane();
    
    public SimpleNotePad() {
        
        textPane.getDocument().addUndoableEditListener(undoManager);
        frame.setTitle("A Simple Notepad Tool");
        fileMenu.add(newFileMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveFileMenuItem);
        fileMenu.add(openFileMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(printFileMenuItem);
        fileMenu.add(recentMenu);
        editMenu.add(undoMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.add(replaceMenuItem);
        NotepadListener listenerObj = new NotepadListener();
        newFileMenuItem.addActionListener(listenerObj);
        newFileMenuItem.setActionCommand("new");
        saveFileMenuItem.addActionListener(listenerObj);
        saveFileMenuItem.setActionCommand("save");
        openFileMenuItem.addActionListener(listenerObj);
        openFileMenuItem.setActionCommand("open");
        printFileMenuItem.addActionListener(listenerObj);
        replaceMenuItem.addActionListener(listenerObj);
        replaceMenuItem.setActionCommand("replace");
        printFileMenuItem.setActionCommand("print");
        recentMenu.addActionListener(listenerObj);
        recentMenu.setActionCommand("recentMenu");
        copyMenuItem.addActionListener(listenerObj);
        
        copyMenuItem.setActionCommand("copy");
        pasteMenuItem.addActionListener(listenerObj);
        pasteMenuItem.setActionCommand("paste");
        undoMenuItem.addActionListener(listenerObj);
        undoMenuItem.setActionCommand("undo");
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        frame.setJMenuBar(menuBar);
        frame.add(new JScrollPane(textPane));
        frame.setPreferredSize(new Dimension(600, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setVisible(true);
        frame.pack();
    }
    
    public void openFile() {
        File fileToRead = null;
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileToRead = fc.getSelectedFile();
            //System.out.println("fileToRead : " + fileToRead);
        }
        recentFilesList.add(0, fileToRead);
        
        addRecentFilesName();
        
        try {
            Scanner inputStream = new Scanner(new FileInputStream(fileToRead));
            String inputText = null;
            while (inputStream.hasNext()) {
                inputText += inputStream.nextLine() + "\n";
            }
            textPane.setText(inputText);
        } catch (Exception ex) {
        }
    }
    
    public void openFile(File fileToRead) {
        try {
            Scanner inputStream = new Scanner(new FileInputStream(fileToRead));
            String inputText = null;
            while (inputStream.hasNext()) {
                inputText += inputStream.nextLine() + "\n";
            }
            textPane.setText(inputText);
        } catch (Exception ex) {
        }
    }
    
    public void printFile() {
        try {
            PrinterJob pjob = PrinterJob.getPrinterJob();
            pjob.setJobName("Sample Command Pattern");
            pjob.setCopies(1);
            pjob.setPrintable(new Printable() {
                public int print(Graphics pg, PageFormat pf, int pageNum) {         /// Refactoring... Name changed
                    if (pageNum > 0) {
                        return Printable.NO_SUCH_PAGE;
                    }
                    pg.drawString(textPane.getText(), 500, 500);
                    frame.paint(pg);
                    return Printable.PAGE_EXISTS;
                }
            });
            
            if (pjob.printDialog() == false) {
                return;
            }
            pjob.print();
        } catch (PrinterException pe) {
            JOptionPane.showMessageDialog(null,
                    "Printer error" + pe, "Printing error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void saveFile() {
        File fileToWrite = null;
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileToWrite = fc.getSelectedFile();
        }
        try {
            PrintWriter out = new PrintWriter(new FileWriter(fileToWrite));
            out.println(textPane.getText());
            JOptionPane.showMessageDialog(null, "File is saved successfully...");
            out.close();
        } catch (IOException ex) {
        }
    }
    
    public void newNotepad() {
        textPane.setText("");
    }
    
    public void pasteToNotepad() {
        StyledDocument doc = textPane.getStyledDocument();
        Position position = doc.getEndPosition();
        System.out.println("offset" + position.getOffset());
        textPane.paste();
    }
    
    public void copyFromNotepad() {
        textPane.copy();
    }
    
    public void replaceToNotePad() {
        String replaceWith;
        replaceWith = JOptionPane.showInputDialog(rootPane, "Replace or Insert with", "Input", 1);
        textPane.replaceSelection(replaceWith);
    }
    
    public void undoNotepadText() {
        try {
            editorPaneDocument = textPane.getDocument();
            editorPaneDocument.addUndoableEditListener(undoManager);
            textPane.getDocument().addUndoableEditListener(undoManager);
            undoManager.undo();
        } catch (Exception e) {
            
        }
    }
    
    public void addRecentFilesName() {
        //System.out.println("addrecentFilesName() reached");
        recentMenu.removeAll();
        JMenuItem recentFile = new JMenuItem();
        recentFile.setText(recentFilesList.get(0).getName());
        recentFile.setActionCommand("RecentFileClicked");
        recentFilesMenuItemsList.add(0, recentFile);
        int i = 0;
        while (i < 5 && i < recentFilesMenuItemsList.size()) {
            //System.out.println("REached");
            recentMenu.add(recentFilesMenuItemsList.get(i++));
        }
        
    }
    
    
    private class NotepadListener implements ActionListener{
        @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getActionCommand().equals("new")) {
            newNotepad();
            
        } else if (e.getActionCommand().equals("save")) {
            saveFile();
            
        } else if (e.getActionCommand().equals("open")) {
            openFile();
            
        } else if (e.getActionCommand().equals("print")) {
            printFile();
            
        } else if (e.getActionCommand().equals("copy")) {
            copyFromNotepad();
            
        } else if (e.getActionCommand().equals("paste")) {
            pasteToNotepad();
            
        } else if (e.getActionCommand().equals("undo")) {
            //TODO: implement undo operation
            undoNotepadText();
            
        } else if (e.getActionCommand().equals("replace")) {
            replaceToNotePad();
            
        } else if (e.getActionCommand().equals("RecentFileClicked")) {
            //addRecentFilesName();
            //System.out.println("REached");
        }
    }
    }
    
    public static void main(String[] args) {
        SimpleNotePad app = new SimpleNotePad();
        app.frame.setVisible(true);
    }
}
