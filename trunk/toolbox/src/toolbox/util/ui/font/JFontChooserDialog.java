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

import org.apache.log4j.Logger;

/**
 * Simple font selection dialog
 */
public class JFontChooserDialog extends JDialog
{
    /** Logger */
    private  static final Logger logger_ = 
        Logger.getLogger(JFontChooserDialog.class);
        
    private  JFontChooser   fontChooser_;
    private  JButton        okButton_;
    private  JButton        cancelButton_;
    private  JButton        applyButton_;
    private  List           listeners_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public JFontChooserDialog()
    {
        this(null);
    }


    /**
     * Creates a JFontChooserDialog
     * 
     * @param  owner  Parent frame
     */
    public JFontChooserDialog(Frame owner)
    {
        this(owner, true);
    }


    /**
     * Creates a JFontChooserDialog
     * 
     * @param  owner  Parent frame
     * @param  modal  Dialog is modal
     */
    public JFontChooserDialog(Frame owner, boolean modal)
    {
        this(owner, "", modal);
    }


    /**
     * Creates a JFontChooserDialog
     * 
     * @param  owner        Parent frame
     * @param  modal        Set to true for a model dialog
     * @param  defaultFont  Font to select by default
     */
    public JFontChooserDialog(Frame owner, boolean modal,
        Font defaultFont)
    {
        this(owner, modal, defaultFont, false);
    }


    /**
     * Creates a JFontChooserDialog
     * 
     * @param  owner        Parent frame
     * @param  modal        Set to true for a model dialog
     * @param  defaultFont  Font to select by default
     * @param  antiAlias    Turns antialias on
     */
    public JFontChooserDialog(Frame owner, boolean modal,
        Font defaultFont, boolean antiAlias)
    {
        this(owner, "Select Font", modal);
        fontChooser_.setSelectedFont(defaultFont);
    }


    /**
     * Creates a JFontChooserDialog
     * 
     * @param  owner  Parent frame
     * @param  title  Frame title
     */
    public JFontChooserDialog(Frame owner, String title)
    {
        this(owner, title, true);
    }


    /**
     * Creates a JFontChooserDialog
     * 
     * @param  owner  Parent frame
     * @param  title  Frame title
     * @param  modal  Modal dialog
     */
    public JFontChooserDialog(Frame owner, String title, boolean modal)
    {
        super(owner, title, modal);
        buildView();
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------
    
    /**
     * Builds the GUI
     */
    protected void buildView()
    {
        getContentPane().setLayout(new BorderLayout());
        fontChooser_ = new JFontChooser();
        getContentPane().add(BorderLayout.CENTER, fontChooser_);

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

    //--------------------------------------------------------------------------
    // Event Listener Support 
    //--------------------------------------------------------------------------
    
    /**
     * Adds a listener
     * 
     * @param  listener  FontChooserDialog listener to add
     */
    public void addFontDialogListener(IFontChooserDialogListener listener)
    {
        listeners_.add(listener);
    }


    /**
     * Removes a listener
     * 
     * @param  listener  FontChooserDialog listener to remove
     */
    public void removeFontDialogListener(IFontChooserDialogListener listener)
    {
        listeners_.remove(listener);
    }

    //--------------------------------------------------------------------------
    //  Actions
    //--------------------------------------------------------------------------
    
    /**
     * Notifies listeners that OK was selected and disposes of the dialog box.
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
                ((IFontChooserDialogListener)i.next()).
                    okButtonPressed(fontChooser_);
            
            dispose();
        }
    }


    /**
     * Notifies listeners that the apply was selected
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
                ((IFontChooserDialogListener)i.next()).
                    applyButtonPressed(fontChooser_);
        }
    }


    /**
     * Notifies listeners that cancel was selected and disposes of the
     * dialog box.
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
            IFontChooserDialogListener listener = null;
            for (Iterator i = listeners_.iterator(); i.hasNext(); )
                ((IFontChooserDialogListener)i.next()).
                    cancelButtonPressed(fontChooser_);

            dispose();
        }
    }
}