package toolbox.plugin.texttools;

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
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import nu.xom.Element;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.regexp.RESyntaxException;

import toolbox.jtail.filter.RegexLineFilter;
import toolbox.util.Banner;
import toolbox.util.FontUtil;
import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.workspace.IPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;

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
public class TextToolsPlugin extends JPanel implements IPlugin
{ 
    // TODO: Add checkbox/combo to set type of text (xml, java) and syntax 
    //       hilite as appropriate.
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    public static final Logger logger_ =
        Logger.getLogger(TextToolsPlugin.class);   
    
    // XML Preferences
    private static final String NODE_TEXTTOOLS_PLUGIN   = "TextToolsPlugin";
    private static final String   NODE_INPUT_TEXTAREA   = "InputTextArea";
    private static final String   NODE_OUTPUT_TEXTAREA  = "OutputTextArea";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Reference to the workspace status bar. 
     */
    private IStatusBar statusBar_;

    /** 
     * Input text area.
     */    
    private JSmartTextArea inputArea_;
    
    /** 
     * Output text area. 
     */    
    private JSmartTextArea outputArea_;
    
    /** 
     * Flippane attached to north wall of the component that houses the
     * various text tools.
     */ 
    private JFlipPane topFlipPane_;
   
    /**
     * Main splitter between the input and output areas.
     */
    private JSmartSplitPane splitter_;

    /**
     * Text field that holds the column at which to wrap for WrapAction.
     */
    private JSmartTextField wrapField_;
   
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a TextToolsPlugin.
     */
    public TextToolsPlugin()
    {
    }
    
    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /** 
     * Builds the GUI.
     */
    protected void buildView()
    {
        outputArea_ = new JSmartTextArea();
        outputArea_.setFont(FontUtil.getPreferredMonoFont());
        
        inputArea_ = new JSmartTextArea();
        inputArea_.setFont(FontUtil.getPreferredMonoFont());
        
        // Buttons 
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JSmartButton(outputArea_.new ClearAction()));
        buttonPanel.add(new JSmartButton(new SortAction()));
        buttonPanel.add(new JSmartButton(new BannerAction()));
        buttonPanel.add(new JSmartButton(new QuoteAction()));
        buttonPanel.add(new JSmartButton(new WrapAction()));
        buttonPanel.add(wrapField_ = new JSmartTextField(3));
        wrapField_.setText("80");
        
        // Root 
        setLayout(new BorderLayout());
        splitter_ = new JSmartSplitPane(JSplitPane.VERTICAL_SPLIT);
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
     * @return Input text to process
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
     * @see toolbox.workspace.IPlugin#startup(java.util.Map)
     */
    public void startup(Map params)
    {
        if (params != null)
            statusBar_= (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);
        
        buildView();
    }
    
    
    /**
     * @see toolbox.workspace.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "Text Tools";
    }

    
    /**
     * @see toolbox.workspace.IPlugin#getComponent()
     */
    public JComponent getComponent()
    {
        return this;
    }

    
    /**
     * @see toolbox.workspace.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Various text processing utilities including sorting, " + 
               "tokenizing, and regular expression based filtering.";
    }

    
    /**
     * @see toolbox.workspace.IPlugin#shutdown()
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
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
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

        splitter_.applyPrefs(root);
               
        // This may not have to be invoked later..investigate later...            
//        SwingUtilities.invokeLater(new Runnable()
//        {
//            public void run()
//            {
//                splitter_.setDividerLocation(dividerLocation);
//            }
//        });
    }

    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_TEXTTOOLS_PLUGIN);
        
        splitter_.savePrefs(root);
        topFlipPane_.savePrefs(root);
        
        Element input = new Element(NODE_INPUT_TEXTAREA);
        inputArea_.savePrefs(input);
        root.appendChild(input);
        
        Element output = new Element(NODE_OUTPUT_TEXTAREA);
        outputArea_.savePrefs(output);
        root.appendChild(output);
        
        XOMUtil.insertOrReplace(prefs, root);
    }
    
    //--------------------------------------------------------------------------
    // SortAction
    //--------------------------------------------------------------------------
    
    /**
     * Sorts the contents of the text area.
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
                Object[] lines = StringUtil.tokenize(text, StringUtil.NL);
                List linez = new ArrayList();
                
                for (int i=0; i<lines.length; i++)
                    linez.add(lines[i]);
                
                Collections.sort(linez);
                
                for (Iterator i = linez.iterator(); i.hasNext(); )
                    outputArea_.append(i.next() + StringUtil.NL);
            }
        }
    }

    //--------------------------------------------------------------------------
    // BannerAction
    //--------------------------------------------------------------------------
    
    /**
     * Creates a banner of the text.
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
            String[] lines = StringUtil.tokenize(getInputText(), StringUtil.NL);
            
            for (int i=0; i<lines.length; i++)
                outputArea_.append(StringUtil.NL + Banner.getBanner(lines[i]));
        }
    }

    //--------------------------------------------------------------------------
    // QuoteAction
    //--------------------------------------------------------------------------
    
    /**
     * Wraps a multiline string in double quotes.
     */
    class QuoteAction extends AbstractAction
    {
        QuoteAction()
        {
            super("Quote");
            putValue(MNEMONIC_KEY, new Integer('Q'));
            putValue(SHORT_DESCRIPTION, "Encloses text in quotes");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            String text = getInputText();        
            String[] lines = StringUtil.tokenize(text, StringUtil.NL);
            StringBuffer sb = new StringBuffer();
            
            for (int i=0; i<lines.length; i++)
            {
                // Escape embedded quotes
                String line = StringUtil.replace(lines[i], "\"", "\\\"");
                
                if (i > 0)
                    sb.append("+ ");
                
                sb.append("\"");
                sb.append(line);
                sb.append("\"");
                sb.append(StringUtil.NL);
            }
            
            outputArea_.append(sb.toString());
        }
    }
    
    //--------------------------------------------------------------------------
    // WrapAction
    //--------------------------------------------------------------------------
    
    /**
     * Wraps a multiline string in double quotes.
     */
    class WrapAction extends AbstractAction
    {
        WrapAction()
        {
            super("Wrap");
            putValue(MNEMONIC_KEY, new Integer('W'));
            putValue(SHORT_DESCRIPTION, "Wraps text before quoting");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            String text = getInputText();
            
            String wrapped = StringUtil.wrap(
                    text, Integer.parseInt(wrapField_.getText()), "", "");
            
            String[] lines = StringUtil.tokenize(wrapped, StringUtil.NL);
            StringBuffer sb = new StringBuffer();
            
            for (int i=0; i<lines.length; i++)
            {
                // Escape embedded quotes
                String line = StringUtil.replace(lines[i], "\"", "\\\"");
                
                if (i > 0)
                    sb.append("+ ");
                
                sb.append("\"");
                sb.append(line);
                sb.append("\"");
                sb.append(StringUtil.NL);
            }
            
            outputArea_.append(sb.toString());
        }
    }
    
    //--------------------------------------------------------------------------
    // FilterFlipper
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
            
            add(new JSmartLabel("Filter"));
            add(filterField_ = new JSmartTextField(20));
            add(new JSmartLabel("(regular expression)"));
            filterField_.addKeyListener(new FilterKeyListener());
            
            docListener_ = new TextChangedListener();
        }
        
        protected void filter(String regex)
        {
            statusBar_.setStatus("RE: '" + regex + "'");
            
            if (cache_ == null)
                cache_ = StringUtil.tokenize(getInputText(), StringUtil.NL);

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
                        sb.append(StringUtil.NL);
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
        
        //----------------------------------------------------------------------
        // FilterKeyListener
        //----------------------------------------------------------------------
        
        /**
         * Enabled dynamic filtering  of regex as it is typed.
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
        
        //----------------------------------------------------------------------
        // TextChangedListener
        //----------------------------------------------------------------------
        
        /**
         * Catchs modifications to the original document so that we know when
         * to throw away our cached copy of the text currently being regex'ed.  
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
    
    //--------------------------------------------------------------------------
    // TokenizerFlipper
    //--------------------------------------------------------------------------
    
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
            
            add(new JSmartLabel("Token Delimiter"));
            add(delimiterField_ = new JSmartTextField(20));
            add(new JSmartButton(new TokenizeAction()));
            add(new JSmartButton(new SingleLineAction()));
        }
    
        //----------------------------------------------------------------------
        // TokenizeAction
        //----------------------------------------------------------------------
        
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
                    outputArea_.append(st.nextToken() + StringUtil.NL);
                    
                statusBar_.setStatus(st.countTokens() + " tokens identified.");
            }
        }
        
        //----------------------------------------------------------------------
        // SingleLineAction
        //----------------------------------------------------------------------
        
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
                StringTokenizer st = new StringTokenizer(getInputText(), StringUtil.NL);
                StringBuffer sb = new StringBuffer();
                    
                while (st.hasMoreElements())
                    sb.append(st.nextElement());
                
                outputArea_.setText(sb.toString());    
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // CodecFlipper
    //--------------------------------------------------------------------------
    
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
            add(new JSmartButton(new Base64EncodeAction()));
            add(new JSmartButton(new Base64DecodeAction()));
            add(new JSmartButton(new HTMLEncodeAction()));
            add(new JSmartButton(new HTMLDecodeAction()));
            add(new JSmartButton(new XMLEncodeAction()));
            add(new JSmartButton(new XMLDecodeAction()));
        }
    
        //----------------------------------------------------------------------
        // Base64EncodeAction
        //----------------------------------------------------------------------

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
        
        //----------------------------------------------------------------------
        // Base64DecodeAction
        //----------------------------------------------------------------------

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
        
        //----------------------------------------------------------------------
        // HTMLEncodeAction
        //----------------------------------------------------------------------

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
        
        //----------------------------------------------------------------------
        // HTMLDecodeAction
        //----------------------------------------------------------------------

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
        
        //----------------------------------------------------------------------
        // XMLEncodeAction
        //----------------------------------------------------------------------

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
        
        //----------------------------------------------------------------------
        // XMLDecodeAction
        //----------------------------------------------------------------------
        
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