package toolbox.plugin.texttools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.Banner;
import toolbox.util.FontUtil;
import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.textarea.ClearAction;
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
    
    private static final Logger logger_ = 
        Logger.getLogger(TextToolsPlugin.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
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
    // Protected
    //--------------------------------------------------------------------------
    
    /** 
     * Constructs the user interface.
     */
    protected void buildView()
    {
        outputArea_ = new JSmartTextArea();
        outputArea_.setFont(FontUtil.getPreferredMonoFont());
        
        inputArea_ = new JSmartTextArea();
        inputArea_.setFont(FontUtil.getPreferredMonoFont());
        
        // Buttons 
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JSmartButton(new ClearAction(outputArea_)));
        buttonPanel.add(new JSmartButton(new SortAction()));
        buttonPanel.add(new JSmartButton(new BannerAction()));
        buttonPanel.add(new JSmartButton(new QuoteAction()));
        buttonPanel.add(new JSmartButton(new WrapAction()));
        buttonPanel.add(wrapField_ = new JSmartTextField(3));
        wrapField_.setText("80");
        
        // Root 
        setLayout(new BorderLayout());
        splitter_ = new JSmartSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        splitter_.setTopComponent(
            new JHeaderPanel(
                "Input", 
                JHeaderPanel.createToolBar(inputArea_), 
                new JScrollPane(inputArea_)));
        
        JToolBar tb = JHeaderPanel.createToolBar(outputArea_);
        tb.add(JHeaderPanel.createButton(new CopyOutputToInputAction()), 0);
        
        splitter_.setBottomComponent(
            new JHeaderPanel(
                "Output",
                tb,
                new JScrollPane(outputArea_)));
        
        add(splitter_, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Top flip pane
        topFlipPane_ = new JFlipPane(JFlipPane.TOP);
        topFlipPane_.addFlipper("Filter", new FilterPane(this));
        topFlipPane_.addFlipper("Tokenizer", new TokenizerPane(this));
        topFlipPane_.addFlipper("Codec", new CodecPane(this));
        topFlipPane_.addFlipper("Format", new FormatPane(this));
        add(topFlipPane_, BorderLayout.NORTH);
    }
    
    
    /**
     * Returns text to process. If no text is selected then the entire contents
     * of the input area is returned, otherwise only the selected text is 
     * returned.
     * 
     * @return Input text to process.
     */
    protected String getInputText()
    {
        String selected = inputArea_.getSelectedText();
        return (StringUtil.isNullOrBlank(selected) 
                    ? inputArea_.getText() 
                    : selected);
    }
    
    
    /**
     * Returns the statusBar.
     * 
     * @return IStatusBar
     */
    public IStatusBar getStatusBar()
    {
        return statusBar_;
    }

    
    /**
     * Returns the outputArea.
     * 
     * @return JSmartTextArea
     */
    public JSmartTextArea getOutputArea()
    {
        return outputArea_;
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
            statusBar_ = (IStatusBar) 
                params.get(PluginWorkspace.KEY_STATUSBAR);
        
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
        /**
         * Creates a SortAction.
         */
        SortAction()
        {
            super("Sort");
            putValue(MNEMONIC_KEY, new Integer('S'));
            putValue(SHORT_DESCRIPTION, "Sorts the text");
        }
    
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
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
                
                for (int i = 0; i < lines.length; i++)
                    linez.add(lines[i]);
                
                Collections.sort(linez);
                
                for (Iterator i = linez.iterator(); i.hasNext();)
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
        /**
         * Creates a BannerAction.
         */
        BannerAction()
        {
            super("Banner", true, false, null);
            putValue(MNEMONIC_KEY, new Integer('B'));
            putValue(SHORT_DESCRIPTION, "Creates an ascii text banner");
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            String[] lines = StringUtil.tokenize(getInputText(), StringUtil.NL);
            
            for (int i = 0; i < lines.length; i++)
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
        /**
         * Creates a QuoteAction.
         */
        QuoteAction()
        {
            super("Quote");
            putValue(MNEMONIC_KEY, new Integer('Q'));
            putValue(SHORT_DESCRIPTION, "Encloses text in quotes");
        }

        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            String text = getInputText();        
            String[] lines = StringUtil.tokenize(text, StringUtil.NL, true);
            StringBuffer sb = new StringBuffer();
            
            logger_.debug(StringUtil.addBars(ArrayUtil.toString(lines, true)));
            
            for (int i = 0; i < lines.length; i++)
            {
                // Escape embedded quotes
                String line = StringUtil.replace(lines[i], "\"", "\\\"");
                
                if (i > 0)
                    sb.append("+ ");
                
                sb.append("\"");
                if (line.charAt(0) != '\n') 
                    sb.append(line);
                sb.append("\\n\"");
                sb.append(StringUtil.NL);
                
                if (!line.equals(StringUtil.NL))
                    i++;
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
        /**
         * Creates a WrapAction.
         */
        WrapAction()
        {
            super("Wrap");
            putValue(MNEMONIC_KEY, new Integer('W'));
            putValue(SHORT_DESCRIPTION, "Wraps text before quoting");
        }

        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            String text = getInputText();
            
            String wrapped = StringUtil.wrap(
                    text, Integer.parseInt(wrapField_.getText()), "", "");
            
            String[] lines = StringUtil.tokenize(wrapped, StringUtil.NL);
            StringBuffer sb = new StringBuffer();
            
            for (int i = 0; i < lines.length; i++)
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
    // CopyOutputToInputAction
    //--------------------------------------------------------------------------
    
    /**
     * Copies the contents of the output text area to the input text area.
     */
    class CopyOutputToInputAction extends AbstractAction
    {
        /**
         * Creates a CopyOutputToInputAction.
         */
        CopyOutputToInputAction()
        {
            super("", ImageCache.getIcon(ImageCache.IMAGE_SWAP_PANES));
            putValue(MNEMONIC_KEY, new Integer('S'));
            putValue(SHORT_DESCRIPTION, "Copies output text to input area");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            inputArea_.setText(outputArea_.getText());
        }
    }
}