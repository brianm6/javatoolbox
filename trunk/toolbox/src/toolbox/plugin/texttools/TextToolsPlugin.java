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
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.regexp.RESyntaxException;

import nu.xom.Attribute;
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
 *   <li>Sorts text alphabetically
 *   <li>Filters text dynamically using a regular expressions
 *   <li>Tokenizes strings
 *   <li>Creates Figlet banners
 *   <li>Encodes/decodes Base64
 *   <li>Escapes/unescapes XML and HTML
 * </ul>
 */ 
public class TextPlugin extends JPanel implements IPlugin, Stringz
{ 
    // TODO: Add checkbox/combo to set type of text (xml, java) and syntax 
    //       hilite as appropriate.
    
    public static final Logger logger_ =
        Logger.getLogger(TextPlugin.class);   
    
    // XML Preferences
    private static final String NODE_TEXTTOOLS_PLUGIN   = "TextToolsPlugin";
    private static final String   ATTR_DIVIDER_LOCATION = "dividerLocation";
    private static final String   NODE_INPUT_TEXTAREA   = "InputTextArea";
    private static final String   NODE_OUTPUT_TEXTAREA  = "OutputTextArea";
    
    /** 
     * Reference to the workspace status bar 
     */
    private IStatusBar statusBar_;

    /** 
     * Input text area 
     */    
    private JSmartTextArea inputArea_;
    
    /** 
     * Output text area 
     */    
    private JSmartTextArea outputArea_;
    
    /** 
     * Flippane attached to north wall of the component that houses the
     * various text tools.
     */ 
    private JFlipPane topFlipPane_;
   
    /**
     * Main splitter between the input and output areas
     */
    private JSplitPane splitter_;
   
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a TextPlugin
     */
    public TextPlugin()
    {
    }
    
    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /** 
     * Builds the GUI
     */
    protected void buildView()
    {
        outputArea_ = new JSmartTextArea();
        outputArea_.setFont(SwingUtil.getPreferredMonoFont());
        
        inputArea_ = new JSmartTextArea();
        inputArea_.setFont(SwingUtil.getPreferredMonoFont());
        
        // Buttons 
        JPanel buttonPanel = new JPanel(new FlowLayout());
            
        buttonPanel.add(new JButton(new SortAction()));
        buttonPanel.add(new JButton(new BannerAction()));
        buttonPanel.add(new JButton(outputArea_.new ClearAction()));
        
        // Root 
        setLayout(new BorderLayout());
        splitter_ = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitter_.setTopComponent(new JScrollPane(inputArea_));
        splitter_.setBottomComponent(new JScrollPane(outputArea_));
        
        add(splitter_, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Top flip pane
        topFlipPane_ = new JFlipPane(JFlipPane.TOP);
        topFlipPane_.addFlipper("Filter", new FilterFlipper());
        topFlipPane_.addFlipper("Tokenizer", new TokenizerFlipper());
        topFlipPane_.addFlipper("Codec", new CodecFlipper());
        add(topFlipPane_, BorderLayout.NORTH);
    }
    
    /**
     * Returns text to process. If no text is selected then the entire contents
     * of the input area is returned, otherwise only the selected text is 
     * returned.
     * 
     * @return  Input text to process
     */
    protected String getInputText()
    {
        String selected = inputArea_.getSelectedText();
        return (StringUtil.isNullOrBlank(selected) 
                    ? inputArea_.getText() 
                    : selected);
    }
    
    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.ui.plugin.IPlugin#startup(java.util.Map)
     */
    public void startup(Map params)
    {
        if (params != null)
            statusBar_= (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);
        
        buildView();
    }
    
    /**
     * @see java.awt.Component#getName()
     */
    public String getName()
    {
        return "Text Tools";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getComponent()
     */
    public JComponent getComponent()
    {
        return this;
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Various text processing utilities including sorting, " + 
               "tokenizing, and regular expression based filtering.";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#shutdown()
     */
    public void shutdown()
    {
        outputArea_.setText("");
        inputArea_.setText("");
        inputArea_ = null;
        outputArea_ = null;
        topFlipPane_ = null;
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) 
    {
        Element root = 
            XOMUtil.getFirstChildElement(prefs, NODE_TEXTTOOLS_PLUGIN, 
                new Element(NODE_TEXTTOOLS_PLUGIN));

        topFlipPane_.applyPrefs(root);
        
        inputArea_.applyPrefs(
            XOMUtil.getFirstChildElement(root, NODE_INPUT_TEXTAREA, 
                new Element(NODE_INPUT_TEXTAREA)));

        outputArea_.applyPrefs(
            XOMUtil.getFirstChildElement(root, NODE_OUTPUT_TEXTAREA, 
                new Element(NODE_OUTPUT_TEXTAREA)));
       
        final int dividerLocation = 
            XOMUtil.getIntegerAttribute(root, ATTR_DIVIDER_LOCATION, 150);

        // This may not have to be invoked later..investigate later...            
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                splitter_.setDividerLocation(dividerLocation);
            }
        });
    }

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_TEXTTOOLS_PLUGIN);
        
        root.addAttribute(new Attribute(
            ATTR_DIVIDER_LOCATION, splitter_.getDividerLocation()+""));
            
        topFlipPane_.savePrefs(root);
        
        Element input = new Element(NODE_INPUT_TEXTAREA);
        outputArea_.savePrefs(input);
        root.appendChild(input);
        
        Element output = new Element(NODE_OUTPUT_TEXTAREA);
        outputArea_.savePrefs(output);
        root.appendChild(output);
        
        XOMUtil.insertOrReplace(prefs, root);
    }
    
    //--------------------------------------------------------------------------
    //  Actions
    //--------------------------------------------------------------------------
    
    /**
     * Sorts the contents of the text area
     */
    class SortAction extends AbstractAction
    {
        SortAction()
        {
            super("Sort");
            putValue(MNEMONIC_KEY, new Integer('S'));
            putValue(SHORT_DESCRIPTION, "Sorts the text");
        }
    
        public void actionPerformed(ActionEvent e)
        {
            String text = getInputText();        
            
            if (StringUtil.isNullOrEmpty(text))
            {
                statusBar_.setStatus("Nothing to sort.");
            }
            else
            {
                outputArea_.setText("");
                Object[] lines = StringUtil.tokenize(text, NL);
                List linez = new ArrayList();
                
                for (int i=0; i<lines.length; i++)
                    linez.add(lines[i]);
                
                Collections.sort(linez);
                
                for (Iterator i = linez.iterator(); i.hasNext(); )
                    outputArea_.append(i.next() + NL);
            }
        }
    }

    /**
     * Creates a banner of the text
     */
    class BannerAction extends SmartAction
    {
        BannerAction()
        {
            super("Banner", true, false, null);
            putValue(MNEMONIC_KEY, new Integer('B'));
            putValue(SHORT_DESCRIPTION, "Creates an ascii text banner");
        }

        public void runAction(ActionEvent e) throws Exception
        {
            String[] lines = StringUtil.tokenize(getInputText(), NL);
            
            for (int i=0; i<lines.length; i++)
                outputArea_.append(NL + Banner.getBanner(lines[i]));
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
    class FilterFlipper extends JPanel
    {
        private JTextField filterField_;
        private String[] cache_;
        private TextChangedListener docListener_;
        
        FilterFlipper()
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
                cache_ = StringUtil.tokenize(getInputText(), NL);

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
                
                outputArea_.getDocument().
                    removeDocumentListener(docListener_);            
                    
                outputArea_.setText(sb.toString());
                outputArea_.moveCaretPosition(0);
                
                outputArea_.getDocument().
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
        
                String newValue = filterField_.getText().trim();
 
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
        
        /** 
         * Tokenizes the string in the input text area with the entered 
         * delimiter and dumps the result to the output text area.
         */
        class TokenizeAction extends AbstractAction
        {
            TokenizeAction()
            {
                super("Tokenize");
            }
            
            public void actionPerformed(ActionEvent e)
            {
                StringTokenizer st = 
                    new StringTokenizer(getInputText(), 
                        delimiterField_.getText());
            
                while(st.hasMoreElements())
                    outputArea_.append(st.nextToken() + NL);
                    
                statusBar_.setStatus(st.countTokens() + " tokens identified.");
            }
        }
        
        /** 
         * Compresses multiple lines in the input text area to a single line
         * in the output text area.
         */
        class SingleLineAction extends AbstractAction
        {
            SingleLineAction()
            {
                super("Convert to single line");
            }
                       
            public void actionPerformed(ActionEvent e)
            {
                StringTokenizer st = new StringTokenizer(getInputText(), NL);
                StringBuffer sb = new StringBuffer();
                    
                while (st.hasMoreElements())
                    sb.append(st.nextElement());
                
                outputArea_.setText(sb.toString());    
            }
        }
    }
    
    /**
     * Flipper containing common encoding/decoding schemes. 
     */
    class CodecFlipper extends JPanel
    {
        CodecFlipper()
        {
            buildView();
        }
        
        void buildView()
        {
            setLayout(new FlowLayout());
            add(new JButton(new Base64EncodeAction()));
            add(new JButton(new Base64DecodeAction()));
            add(new JButton(new HTMLEncodeAction()));
            add(new JButton(new HTMLDecodeAction()));
            add(new JButton(new XMLEncodeAction()));
            add(new JButton(new XMLDecodeAction()));
        }
        
        class Base64EncodeAction extends AbstractAction
        {
            Base64EncodeAction()
            {
                super("Base64 Encode");
            }
            
            public void actionPerformed(ActionEvent e)
            {
                byte[] b = Base64.encodeBase64(getInputText().getBytes());
                outputArea_.setText(new String(b));
            }
        }
        
        class Base64DecodeAction extends AbstractAction
        {
            Base64DecodeAction()
            {
                super("Base64 Decode");
            }
                       
            public void actionPerformed(ActionEvent e)
            {
                byte[] b = Base64.decodeBase64(getInputText().getBytes());
                outputArea_.setText(new String(b));
            }
        }
        
        class HTMLEncodeAction extends AbstractAction
        {
            HTMLEncodeAction()
            {
                super("HTML Encode");
            }
            
            public void actionPerformed(ActionEvent e)
            {
                outputArea_.setText(
                    StringEscapeUtils.escapeHtml(getInputText()));
            }
        }
        
        class HTMLDecodeAction extends AbstractAction
        {
            HTMLDecodeAction()
            {
                super("HTML Decode");
            }
            
            public void actionPerformed(ActionEvent e)
            {
                outputArea_.setText(
                    StringEscapeUtils.unescapeHtml(getInputText()));
            }
        }
        
        class XMLEncodeAction extends AbstractAction
        {
            XMLEncodeAction()
            {
                super("XML Encode");
            }
            
            public void actionPerformed(ActionEvent e)
            {
                outputArea_.setText(
                    StringEscapeUtils.escapeXml(getInputText()));
            }
        }
        
        class XMLDecodeAction extends AbstractAction
        {
            XMLDecodeAction()
            {
                super("XML Decode");
            }
            
            public void actionPerformed(ActionEvent e)
            {
                outputArea_.setText(
                    StringEscapeUtils.unescapeXml(getInputText()));
            }
        }
    }    
}