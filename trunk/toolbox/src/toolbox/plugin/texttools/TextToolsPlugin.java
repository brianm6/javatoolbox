package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;
import org.apache.regexp.RESyntaxException;

import toolbox.jtail.filter.RegexLineFilter;
import toolbox.util.Banner;
import toolbox.util.ExceptionUtil;
import toolbox.util.StringUtil;
import toolbox.util.Stringz;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.JTextComponentPopupMenu;
import toolbox.util.ui.flippane.JFlipPane;

/**
 * Plugin for simple text manipulation that include:
 * 
 * <ul>
 * <li>Sorting text alphabetically</li>
 * <li>Filtering text dynamically using regular expressions</li>
 * <li>Tokenizing strings<li>
 * </ul>
 */ 
public class TextPlugin extends JPanel implements IPlugin, Stringz
{ 
    public static final Logger logger_ =
        Logger.getLogger(TextPlugin.class);   
    
    private IStatusBar statusBar_;    
    private JTextArea  textArea_;
    private JFlipPane  topFlipPane_;
   
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default Constructor
     */
    public TextPlugin()
    {
    }
    
    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------
    
    /** 
     * Builds the GUI
     */
    protected void buildView()
    {
        textArea_ = new JSmartTextArea();
        textArea_.setFont(SwingUtil.getPreferredMonoFont());
        new JTextComponentPopupMenu(textArea_);
        
        // Buttons 
        JPanel buttonPanel = new JPanel(new FlowLayout());
            
        buttonPanel.add(new JButton(new SortAction()));
        buttonPanel.add(new JButton(new BannerAction()));
        buttonPanel.add(new JButton(new ClearAction()));
        
        // Root 
        setLayout(new BorderLayout());
        add(new JScrollPane(textArea_), BorderLayout.CENTER);                
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Top flip pane
        topFlipPane_ = new JFlipPane(JFlipPane.TOP);
        topFlipPane_.addFlipper("Filter", new RegexFlipper());
        topFlipPane_.addFlipper("Tokenizer", new TokenizerFlipper());
        add(BorderLayout.NORTH, topFlipPane_);
    }
    
    //--------------------------------------------------------------------------
    //  IPlugin Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.ui.plugin.IPlugin#init()
     */
    public void init()
    {
        buildView();
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getName()
     */
    public String getName()
    {
        return "Text";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getComponent()
     */
    public Component getComponent()
    {
        return this;
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getMenuBar()
     */
    public JMenuBar getMenuBar()
    {
        return null;
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#applyPrefs(Properties)
     */
    public void applyPrefs(Properties prefs)
    {
        topFlipPane_.applyPrefs(prefs, "textplugin");
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#savePrefs(Properties)
     */
    public void savePrefs(Properties prefs)
    {
        topFlipPane_.savePrefs(prefs, "textplugin");
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#setStatusBar(IStatusBar)
     */
    public void setStatusBar(IStatusBar statusBar)
    {
        statusBar_ = statusBar;
    }
    
    /**
     * @see toolbox.util.ui.plugin.IPlugin#shutdown()
     */
    public void shutdown()
    {
    }
    
    //--------------------------------------------------------------------------
    //  Actions
    //--------------------------------------------------------------------------
    
    /**
     * Sorts the contents of the text area
     */
    private class SortAction extends AbstractAction
    {
        public SortAction()
        {
            super("Sort");
            putValue(MNEMONIC_KEY, new Integer('S'));
            putValue(SHORT_DESCRIPTION, "Sorts the text");
        }
    
        public void actionPerformed(ActionEvent e)
        {
            String text = textArea_.getText();        
            
            if (StringUtil.isNullOrEmpty(text))
            {
                statusBar_.setStatus("Nothing to sort.");
            }
            else
            {
                textArea_.setText("");
                Object[] lines = StringUtil.tokenize(text, NL);
                List linez = new ArrayList();
                
                for (int i=0; i<lines.length; i++)
                    linez.add(lines[i]);
                
                Collections.sort(linez);
                
                for (Iterator i = linez.iterator(); i.hasNext(); )
                    textArea_.append(i.next() + NL);
            }
        }
    }

    /**
     * Creates a banner of the text
     */
    private class BannerAction extends AbstractAction
    {
        public BannerAction()
        {
            super("Banner");
            putValue(MNEMONIC_KEY, new Integer('B'));
            putValue(SHORT_DESCRIPTION, "Creates an ascii text banner");
        }
    
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                textArea_.append(NL + Banner.convert(textArea_.getText()));
            }
            catch (Throwable t)
            {
                ExceptionUtil.handleUI(t, logger_);
            }
        }
    }
    
    /**
     * Clears the output
     */
    private class ClearAction extends AbstractAction
    {
        public ClearAction()
        {
            super("Clear");
            putValue(MNEMONIC_KEY, new Integer('C'));
            putValue(SHORT_DESCRIPTION, "Clears the output");
        }
    
        public void actionPerformed(ActionEvent e)
        {
            textArea_.setText("");            
        }
    }
    
    //--------------------------------------------------------------------------
    // Flippers
    //--------------------------------------------------------------------------
    
    /**
     * Flipper that allows filtering of text dynamically i.e. As the regular
     * expression is typed in, the matching set is updated accordingly with
     * each keystroke.
     */
    private class RegexFlipper extends JPanel
    {
        private JTextField filterField_;
        private String[] cache_;
        private TextChangedListener docListener_;
        
        public RegexFlipper()
        {
            buildView();
        }
        
        protected void buildView()
        {
            setLayout(new FlowLayout());
            
            add(new JLabel("Filter"));
            add(filterField_ = new JTextField(20));
            add(new JLabel("(regular expression)"));
            filterField_.addKeyListener(new FilterKeyListener());
            
            docListener_ = new TextChangedListener();
        }
        
        protected void filter(String regex)
        {
            statusBar_.setStatus("RE: '" + regex + "'");
            
            if (cache_ == null)
                cache_ = StringUtil.tokenize(textArea_.getText(), NL);

            String[] lines = cache_;                
            
            StringBuffer sb = new StringBuffer();
            RegexLineFilter filter = null;
            
            try
            {
                filter = new RegexLineFilter(regex);
                filter.setEnabled(true);
                
                for (int i=0; i<lines.length; i++)
                {
                    String passed = filter.filter(lines[i]);
                
                    if (passed != null)
                    {
                        sb.append(passed);
                        sb.append(NL);
                    }
                }
                
                // Want to ignore document change events while the filter is
                // updating the text area. Just detach and reattach after
                // mutations are done.
                
                textArea_.getDocument().
                    removeDocumentListener(docListener_);            
                    
                textArea_.setText(sb.toString());
                textArea_.moveCaretPosition(0);
                
                textArea_.getDocument().
                    addDocumentListener(docListener_);
                
            }
            catch (RESyntaxException e)
            {
                // The regular expression is going to be invalid as the user
                // types it in up just shoot the message to the status bar
                statusBar_.setStatus(e.getMessage());
            }
        }
        
        /**
         * Enabled dynamic filtering  of regex as it is typed
         */    
        class FilterKeyListener extends KeyAdapter
        {
            private String oldValue_ = "";
        
            public void keyReleased(KeyEvent e)
            {
                super.keyReleased(e);
        
                String newValue = 
                    filterField_.getText().trim();
 
                // Only refresh if the filter has changed           
                if (!newValue.equals(oldValue_))
                {                
                    oldValue_ = newValue;
                    filter(newValue);            
                }
            }
        }
        
        /**
         * Catchs modifications to the original document so that we know when
         * to throw away our cached copy of the text currently being regex'ed  
         */
        class TextChangedListener implements DocumentListener
        {
            public void changedUpdate(DocumentEvent e)
            { 
                crud("changed ");
            }

            public void insertUpdate(DocumentEvent e)
            {
                crud("insert ");
            }
            
            public void removeUpdate(DocumentEvent e)
            {
                crud("remove ");
            }
            
            protected void crud(String s)
            {
                cache_ = null;
            }
        }
    }
    
    /**
     * Flipper that allows the user to tokenize strings by providing the 
     * token delimiter. Multiline strings can also be merged into one line. 
     */
    private class TokenizerFlipper extends JPanel
    {
        private JTextField  delimiterField_;
        
        public TokenizerFlipper()
        {
            buildView();
        }
        
        protected void buildView()
        {
            setLayout(new FlowLayout());
            
            add(new JLabel("Token Delimiter"));
            add(delimiterField_ = new JTextField(20));
            add(new JButton(new TokenizeAction()));
            add(new JButton(new SingleLineAction()));
        }
        
        class TokenizeAction extends AbstractAction
        {
            public TokenizeAction()
            {
                super("Tokenize");
            }
            
            public void actionPerformed(ActionEvent e)
            {
                StringTokenizer st = 
                    new StringTokenizer(textArea_.getText(), 
                        delimiterField_.getText());
            
                while(st.hasMoreElements())
                    textArea_.append(st.nextToken() + NL);
                    
                statusBar_.setStatus(st.countTokens() + " tokens identified.");
            }
        }
        
        class SingleLineAction extends AbstractAction
        {
            public SingleLineAction()
            {
                super("Convert to single line");
            }
                       
            public void actionPerformed(ActionEvent e)
            {
                StringTokenizer st = 
                    new StringTokenizer(textArea_.getText(), NL);
                
                StringBuffer sb = new StringBuffer();
                    
                while (st.hasMoreElements())
                    sb.append(st.nextElement());
                
                textArea_.setText(sb.toString());    
            }
        }
    }
}