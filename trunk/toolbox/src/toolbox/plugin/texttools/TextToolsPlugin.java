package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.regexp.RESyntaxException;

import nu.xom.Element;

import toolbox.jtail.filter.RegexLineFilter;
import toolbox.util.Banner;
import toolbox.util.StringUtil;
import toolbox.util.Stringz;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.flippane.JFlipPane;

/**
 * Plugin for simple text manipulation.
 * <p>
 * Features:
 * <ul>
 *   <li>Sorting text alphabetically
 *   <li>Filtering text dynamically using regular expressions
 *   <li>Tokenizing strings
 *   <li>Base64 encoding/decoding
 *   <li>Banner
 * </ul>
 */ 
public class TextPlugin extends JPanel implements IPlugin, Stringz
{ 
    // TODO: Add checkbox/combo to set type of text (xml, java) and syntax 
    //       hilite as appropriate.
    
    public static final Logger logger_ =
        Logger.getLogger(TextPlugin.class);   
    
    private static final String NODE_TEXTTOOLS_PLUGIN = "TextToolsPlugin";
    
    /** 
     * Reference to the workspace status bar 
     */
    private IStatusBar statusBar_;
    
    /** 
     * Output text area 
     */    
    private JSmartTextArea textArea_;
    
    /** 
     * Flippane attached to north wall of the component that houses the
     * various text tools.
     */ 
    private JFlipPane topFlipPane_;
   
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
        
        // Buttons 
        JPanel buttonPanel = new JPanel(new FlowLayout());
            
        buttonPanel.add(new JButton(new SortAction()));
        buttonPanel.add(new JButton(new BannerAction()));
        buttonPanel.add(new JButton(textArea_.new ClearAction()));
        
        // Root 
        setLayout(new BorderLayout());
        add(new JScrollPane(textArea_), BorderLayout.CENTER);                
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Top flip pane
        topFlipPane_ = new JFlipPane(JFlipPane.TOP);
        topFlipPane_.addFlipper("Filter", new RegexFlipper());
        topFlipPane_.addFlipper("Tokenizer", new TokenizerFlipper());
        topFlipPane_.addFlipper("Codec", new CodecFlipper());
        add(BorderLayout.NORTH, topFlipPane_);
    }
    
    //--------------------------------------------------------------------------
    //  IPlugin Interface
    //--------------------------------------------------------------------------

    public void startup(Map params)
    {
        if (params != null)
            statusBar_= (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);
        
        buildView();
    }

    public String getName()
    {
        return "Text Tools";
    }

    public JComponent getComponent()
    {
        return this;
    }

    public String getDescription()
    {
        return "Various text processing utilities including sorting, " + 
               "tokenizing, and regular expression based filtering.";
    }

    public void applyPrefs(Element prefs) 
    {
        Element root = prefs.getFirstChildElement(NODE_TEXTTOOLS_PLUGIN);
        
        if (root != null)
        {
            topFlipPane_.applyPrefs(root);
            textArea_.applyPrefs(root);
        }
    }

    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_TEXTTOOLS_PLUGIN);
        topFlipPane_.savePrefs(root);
        textArea_.savePrefs(root);
        XOMUtil.insertOrReplace(prefs, root);
    }

    public void shutdown()
    {
        textArea_.setText("");
    }
    
    //--------------------------------------------------------------------------
    //  Actions
    //--------------------------------------------------------------------------
    
    /**
     * Sorts the contents of the text area
     */
    class SortAction extends AbstractAction
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
    class BannerAction extends SmartAction
    {
        public BannerAction()
        {
            super("Banner", true, false, null);
            putValue(MNEMONIC_KEY, new Integer('B'));
            putValue(SHORT_DESCRIPTION, "Creates an ascii text banner");
        }

        public void runAction(ActionEvent e) throws Exception
        {
            String[] lines = StringUtil.tokenize(textArea_.getText(), "\n");
            
            for (int i=0; i<lines.length; i++)
                textArea_.append(NL + Banner.getBanner(lines[i]));
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
    class RegexFlipper extends JPanel
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
                s.toString();
                cache_ = null;
            }
        }
    }
    
    /**
     * Flipper that allows the user to tokenize strings by providing the 
     * token delimiter. Multiline strings can also be merged into one line. 
     */
    class TokenizerFlipper extends JPanel
    {
        private JTextField  delimiterField_;
        
        TokenizerFlipper()
        {
            buildView();
        }
        
        void buildView()
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
    
    /**
     * Flipper containing common encoding/decoding schemes. 
     */
    class CodecFlipper extends JPanel
    {
        private JTextField  textField_;
        
        CodecFlipper()
        {
            buildView();
        }
        
        void buildView()
        {
            setLayout(new FlowLayout());
            
            add(new JLabel("Text"));
            add(textField_ = new JTextField(20));
            add(new JButton(new Base64EncodeAction()));
            add(new JButton(new Base64DecodeAction()));
        }
        
        class Base64EncodeAction extends AbstractAction
        {
            public Base64EncodeAction()
            {
                super("Base64 Encode");
            }
            
            public void actionPerformed(ActionEvent e)
            {
                byte[] b = Base64.encodeBase64(textField_.getText().getBytes());
                textArea_.setText(new String(b));
            }
        }
        
        class Base64DecodeAction extends AbstractAction
        {
            public Base64DecodeAction()
            {
                super("Base64 Decode");
            }
                       
            public void actionPerformed(ActionEvent e)
            {
                byte[] b = Base64.decodeBase64(textField_.getText().getBytes());
                textArea_.setText(new String(b));
            }
        }
    }    
}