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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.Figlet;
import toolbox.util.FontUtil;
import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceTransition;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.textarea.action.ClearAction;
import toolbox.workspace.AbstractPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;
import toolbox.workspace.PreferencedException;
import toolbox.workspace.prefs.IConfigurator;

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
public class TextToolsPlugin extends AbstractPlugin
{
    // TODO: Add checkbox/combo to set type of text (xml, java) and syntax
    //       hilite as appropriate.

    private static final Logger logger_ =
        Logger.getLogger(TextToolsPlugin.class);

    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------

    private static final String NODE_TEXTTOOLS_PLUGIN   = "TextToolsPlugin";
    private static final String   NODE_INPUT_TEXTAREA   = "InputTextArea";
    private static final String   NODE_OUTPUT_TEXTAREA  = "OutputTextArea";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * View for this plugin.
     */
    private JComponent view_;
    
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

    /**
     * Text formatter view.
     */
    private FormatterView formatterView_;
    
    /**
     * Configuration that integrates with the workspace's preferences dialog
     * box. 
     */
    private TextToolsConfigurator preferences_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    public TextToolsPlugin()
    {
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    protected void buildView()
    {
        view_ = new JPanel(new BorderLayout());
        
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
        buttonPanel.add(new JSmartButton(new UnquoteAction()));
        buttonPanel.add(new JSmartButton(new StripAction()));
        buttonPanel.add(new JSmartButton(new AddLineNumbersAction()));
        buttonPanel.add(new JSmartButton(new WrapAction()));
        buttonPanel.add(wrapField_ = new JSmartTextField(3));
        wrapField_.setText("80");

        // Root
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

        view_.add(splitter_, BorderLayout.CENTER);
        view_.add(buttonPanel, BorderLayout.SOUTH);

        // Top flip pane
        topFlipPane_ = new JFlipPane(JFlipPane.TOP);
        
        topFlipPane_.addFlipper(
            ImageCache.getIcon(ImageCache.IMAGE_FUNNEL), 
            "Filter", 
            new FilterView(this));
        
        topFlipPane_.addFlipper(
            ImageCache.getIcon(ImageCache.IMAGE_LINEWRAP),
            "Tokenizer", 
            new TokenizerView(this));
        
        topFlipPane_.addFlipper(
            ImageCache.getIcon(ImageCache.IMAGE_PLAY),
            "Codec", 
            new CodecView(this));
        
        topFlipPane_.addFlipper(
            ImageCache.getIcon(ImageCache.IMAGE_BRACES),
            "Format", 
            formatterView_ = new FormatterView(this));
        
        view_.add(topFlipPane_, BorderLayout.NORTH);
    }


    /**
	 * @return Text to process. If no text is selected then the entire contents
	 *         of the input area is returned, otherwise only the selected text
	 *         is returned.
	 */
    protected String getInputText()
    {
        String selected = inputArea_.getSelectedText();
        return (StringUtils.isBlank(selected) ? inputArea_.getText() : selected);
    }

    
    /**
     * @return If no selection in output textarea, then entire text, otherwise
     *         only the selected text.
     */
    protected String getOutputText()
    {
        String selected = outputArea_.getSelectedText();
        return (StringUtils.isBlank(selected) ? outputArea_.getText() : selected);
    }
    

    public IStatusBar getStatusBar()
    {
        return statusBar_;
    }


    public JSmartTextArea getOutputArea()
    {
        return outputArea_;
    }

    
    public FormatterView getFormatterView()
    {
        return formatterView_;
    }

    //--------------------------------------------------------------------------
    // Initializable Interface
    //--------------------------------------------------------------------------

    public void initialize(Map params) throws ServiceException
    {
        checkTransition(ServiceTransition.INITIALIZE);
        if (params != null)
            statusBar_ = (IStatusBar)
                params.get(PluginWorkspace.KEY_STATUSBAR);

        buildView();
        transition(ServiceTransition.INITIALIZE);
    }

    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------

    public String getPluginName()
    {
        return "Text Tools";
    }


    public JComponent getView()
    {
        return view_;
    }


    public String getDescription()
    {
        return "Various text processing utilities including sorting, " +
               "tokenizing, and regular expression based filtering.";
    }

    
    public IConfigurator getConfigurator()
    {
        if (preferences_ == null)
            preferences_ = new TextToolsConfigurator(this);
        
        return preferences_;
    }
    
    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------
    
    public void destroy() throws ServiceException
    {
        checkTransition(ServiceTransition.DESTROY);
        outputArea_.setText("");
        inputArea_.setText("");
        inputArea_ = null;
        outputArea_ = null;
        topFlipPane_ = null;
        transition(ServiceTransition.DESTROY);
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root =
            XOMUtil.getFirstChildElement(
                prefs, 
                NODE_TEXTTOOLS_PLUGIN,
                new Element(NODE_TEXTTOOLS_PLUGIN));

        topFlipPane_.applyPrefs(root);

        inputArea_.applyPrefs(
            XOMUtil.getFirstChildElement(
                root, 
                NODE_INPUT_TEXTAREA,
                new Element(NODE_INPUT_TEXTAREA)));

        outputArea_.applyPrefs(
            XOMUtil.getFirstChildElement(
                root, 
                NODE_OUTPUT_TEXTAREA,
                new Element(NODE_OUTPUT_TEXTAREA)));

        splitter_.applyPrefs(root);
        
        // if the splitter is not visible, then reset it
        //if (!splitter_.isShowing())
        //   splitter_.setDividerLocation((double) 0.5);
        
        formatterView_.applyPrefs(root);
    }


    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_TEXTTOOLS_PLUGIN);

        splitter_.savePrefs(root);
        topFlipPane_.savePrefs(root);
        formatterView_.savePrefs(root);
        
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

            if (StringUtils.isBlank(text))
            {
                statusBar_.setInfo("Nothing to sort.");
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
        BannerAction()
        {
            super("Figlet", true, false, null);
            putValue(MNEMONIC_KEY, new Integer('B'));
            putValue(SHORT_DESCRIPTION, "Creates an ascii text banner");
        }


        public void runAction(ActionEvent e) throws Exception
        {
            String[] lines = StringUtil.tokenize(getInputText(), StringUtil.NL);

            for (int i = 0; i < lines.length; i++)
                outputArea_.append(StringUtil.NL + Figlet.getBanner(lines[i]));
        }
    }

    //--------------------------------------------------------------------------
    // QuoteAction
    //--------------------------------------------------------------------------

    /**
     * Wraps a multiline string in double quotes to be pasted into java code.
     */
    class QuoteAction extends AbstractAction
    {
        QuoteAction()
        {
            super("Quote");
            putValue(MNEMONIC_KEY, new Integer('Q'));
            putValue(SHORT_DESCRIPTION, "Encloses text in quotes for pasting into java code");
        }


        public void actionPerformed(ActionEvent e)
        {
            String text = getInputText();
            String[] lines = StringUtil.tokenize(text, StringUtil.NL, true);
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < lines.length; i++)
            {
                // Escape embedded quotes
                String line = StringUtils.replace(lines[i], "\"", "\\\"");

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
    // UnquoteAction
    //--------------------------------------------------------------------------

    /**
     * Transforms a multiline string embedded in java code to plain text.
     */
    public class UnquoteAction extends AbstractAction
    {
        UnquoteAction()
        {
            super("Unquote");
            putValue(MNEMONIC_KEY, new Integer('U'));
            putValue(SHORT_DESCRIPTION, "Converts embedded java text string to plain string");
        }


        public void actionPerformed(ActionEvent e)
        {
            outputArea_.append(unquote(getInputText()));
        }


		public String unquote(String text) {
			String[] lines = StringUtil.tokenize(text, "\n", false);
            StringBuffer sb = new StringBuffer();

            
            for (int i = 0; i < lines.length; i++)
            {
            	String line = lines[i];
            	//logger_.debug(i + ":" + line);
            	
                // Escape embedded quotes
                //String line = StringUtils.replace(lines[i], "\"", "\\\"");
            	line = line.trim();
            	line = StringUtils.strip(line, ";");
            	line = line.trim();
            	line = StringUtils.strip(line, "+");
            	line = line.trim();
            	line = StringUtils.strip(line, "\"");
            	sb.append(line);
                if (lines.length >= 1 && i != (lines.length - 1))
                    sb.append("\n");
            }
			return sb.toString();
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

            for (int i = 0; i < lines.length; i++)
            {
                // Escape embedded quotes
                String line = StringUtils.replace(lines[i], "\"", "\\\"");

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
     * Copies the contents of the output text area to the input text area if
     * there is no selection, otherwise copies only the selected text.
     */
    class CopyOutputToInputAction extends AbstractAction
    {
        CopyOutputToInputAction()
        {
            super("", ImageCache.getIcon(ImageCache.IMAGE_SWAP_PANES));
            putValue(MNEMONIC_KEY, new Integer('S'));
            putValue(SHORT_DESCRIPTION, "Copies output text to input area");
        }

        public void actionPerformed(ActionEvent e)
        {
            inputArea_.setText(getOutputText());
            outputArea_.setText("");
        }
    }
    
    
    //--------------------------------------------------------------------------
    // AddLineNumbersAction
    //--------------------------------------------------------------------------

    class AddLineNumbersAction extends AbstractAction
    {
        AddLineNumbersAction()
        {
            super("Add Line Numbers");
            putValue(MNEMONIC_KEY, new Integer('A'));
            putValue(SHORT_DESCRIPTION, "Prefixes each line with a line number");
        }


        public void actionPerformed(ActionEvent e)
        {
            String[] lines = StringUtil.tokenize(getInputText(), StringUtil.NL, true);
            StringBuffer sb = new StringBuffer(getInputText().length() + (4 * lines.length));

            for (int i = 0; i < lines.length; i++)
            	sb.append((i + 1) + " " + lines[i]);

            outputArea_.append(sb.toString());
        }
    }
    
    //--------------------------------------------------------------------------
    // StripAction
    //--------------------------------------------------------------------------

    class StripAction extends AbstractAction
    {
        StripAction()
        {
            super("Strip");
            putValue(MNEMONIC_KEY, new Integer('T'));
            putValue(SHORT_DESCRIPTION, "Strips leading and trailing whitespace from each line of text");
        }


        public void actionPerformed(ActionEvent e)
        {
            String[] lines = StringUtil.tokenize(getInputText(), System.getProperty("line.separator"), false);
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < lines.length; i++)
            	sb.append(StringUtils.strip(lines[i]) + "\n");

            outputArea_.append(sb.toString());
        }
    }    
}