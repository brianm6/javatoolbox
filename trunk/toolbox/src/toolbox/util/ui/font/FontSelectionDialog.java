package toolbox.util.ui.font;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.apache.log4j.Category;

/**
 * Simple font selection dialog
 */
public class FontSelectionDialog extends JDialog
{
    /** Logger **/
    private  static final Category logger_ = 
        Category.getInstance(FontSelectionDialog.class);
        
    private  FontSelectionPane fontPanel_;
    private  JButton okButton_;
    private  JButton cancelButton_;
    private  JButton applyButton_;
    private  List    listeners_;
    
    
    /**
     * Constructor for FontSelectionDialog.
     */
    public FontSelectionDialog()
    {
        this(null);
    }


    /**
     * Constructor for FontSelectionDialog.
     * 
     * @param owner
     */
    public FontSelectionDialog(Frame owner)
    {
        this(owner, true);
    }


    /**
     * Constructor for FontSelectionDialog.
     * 
     * @param owner
     * @param modal
     */
    public FontSelectionDialog(Frame owner, boolean modal)
    {
        this(owner, "", modal);
    }


    /**
     * Constructor for FontSelectionDialog.
     * 
     * @param   owner           Parent frame
     * @param   modal           Set to true for a model dialog
     * @param   defaultFont    Font to select by default
     */
    public FontSelectionDialog(Frame owner, boolean modal,
        Font defaultFont)
    {
        this(owner, "", modal);
        fontPanel_.setSelectedFont(defaultFont);
    }


    /**
     * Constructor for FontSelectionDialog.
     * 
     * @param owner
     * @param title
     */
    public FontSelectionDialog(Frame owner, String title)
    {
        this(owner, title, true);
    }


    /**
     * Constructor for FontSelectionDialog.
     * 
     * @param owner
     * @param title
     * @param modal
     */
    public FontSelectionDialog(Frame owner, String title, boolean modal)
    {
        super(owner, title, modal);
        buildView();
    }

    
    /**
     * Builds the GUI
     */
    protected void buildView()
    {
        getContentPane().setLayout(new BorderLayout());
        fontPanel_ = new FontSelectionPane();
        getContentPane().add(BorderLayout.CENTER, fontPanel_);

        okButton_     = new JButton(new OKAction());
        cancelButton_ = new JButton(new CancelAction());
        applyButton_  = new JButton(new ApplyAction());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton_);
        buttonPanel.add(applyButton_);        
        buttonPanel.add(cancelButton_);
        getContentPane().add(BorderLayout.SOUTH, buttonPanel);

        listeners_ = new ArrayList();
       
        pack();
    }


    /**
     * Adds a listener
     */
    public void addFontDialogListener(IFontDialogListener listener)
    {
        listeners_.add(listener);
    }


    /**
     * Removes a listener
     */
    public void removeFontDialogListener(IFontDialogListener listener)
    {
        listeners_.remove(listener);
    }

    
    /**
     * Action when the OK button is pressed
     */
    private class OKAction extends AbstractAction
    {
        public OKAction()
        {
            super("OK");
            
            putValue(MNEMONIC_KEY, new Integer('o'));
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

        }
    
        /**
         * Notifies listener and disposes
         */
        public void actionPerformed(ActionEvent e)
        {
            for (Iterator i = listeners_.iterator(); i.hasNext(); )
                ((IFontDialogListener)i.next()).okButtonPressed(fontPanel_);
            
            dispose();
        }
    }


    /**
     * Action when the apply button is pressed
     */
    private class ApplyAction extends AbstractAction
    {
        public ApplyAction()
        {
            super("Apply");
            putValue(MNEMONIC_KEY, new Integer('A'));
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_A, 0));
        }
    
        /**
         * Notifies listener
         */
        public void actionPerformed(ActionEvent e)
        {
            for (Iterator i = listeners_.iterator(); i.hasNext(); )
                ((IFontDialogListener)i.next()).applyButtonPressed(fontPanel_);
        }
    }


    /**
     * Action when the Cancel button is pressed
     */
    private class CancelAction extends AbstractAction
    {
        public CancelAction()
        {
            super("Cancel");
            putValue(MNEMONIC_KEY, new Integer('C'));
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        }
    
        /**
         * Notifies listeners and disposess
         */
        public void actionPerformed(ActionEvent e)
        {
            IFontDialogListener listener = null;
            for (Iterator i = listeners_.iterator(); i.hasNext(); )
                ((IFontDialogListener)i.next()).cancelButtonPressed(fontPanel_);

            dispose();
        }
    }
}