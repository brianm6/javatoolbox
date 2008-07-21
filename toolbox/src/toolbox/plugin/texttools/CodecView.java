package toolbox.plugin.texttools;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import toolbox.util.StringUtil;
import toolbox.util.ui.JSmartButton;

/**
 * Flipper containing common encoding/decoding schemes.
 */
public class CodecView extends JPanel
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Reference to parent plugin.
     */
    private final TextToolsPlugin plugin_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    CodecView(TextToolsPlugin plugin)
    {
        buildView();
        plugin_ = plugin;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    protected void buildView()
    {
        setLayout(new FlowLayout());
        add(new JSmartButton(new Base64EncodeAction()));
        add(new JSmartButton(new Base64DecodeAction()));
        add(new JSmartButton(new HTMLEncodeAction()));
        add(new JSmartButton(new HTMLDecodeAction()));
        add(new JSmartButton(new XMLEncodeAction()));
        add(new JSmartButton(new XMLDecodeAction()));
        add(new JSmartButton(new SkipLinesAction()));
    }

    //----------------------------------------------------------------------
    // Base64EncodeAction
    //----------------------------------------------------------------------

    /**
     * Base64 encodes the text in the input text area.
     */
    class Base64EncodeAction extends AbstractAction
    {
        Base64EncodeAction()
        {
            super("Base64 Encode");
        }


        public void actionPerformed(ActionEvent e)
        {
            byte[] b = Base64.encodeBase64(plugin_.getInputText().getBytes());
            plugin_.getOutputArea().setText(new String(b));
        }
    }

    //----------------------------------------------------------------------
    // Base64DecodeAction
    //----------------------------------------------------------------------

    /**
     * Base64 decodes the current selection.
     */
    class Base64DecodeAction extends AbstractAction
    {
        Base64DecodeAction()
        {
            super("Base64 Decode");
        }


        public void actionPerformed(ActionEvent e)
        {
            byte[] b = Base64.decodeBase64(plugin_.getInputText().getBytes());
            plugin_.getOutputArea().setText(new String(b));
        }
    }

    //----------------------------------------------------------------------
    // HTMLEncodeAction
    //----------------------------------------------------------------------

    /**
     * HTML encodes the current selection.
     */
    class HTMLEncodeAction extends AbstractAction
    {
        HTMLEncodeAction()
        {
            super("HTML Encode");
        }


        public void actionPerformed(ActionEvent e)
        {
            plugin_.getOutputArea().setText(
                StringEscapeUtils.escapeHtml(plugin_.getInputText()));
        }
    }

    //----------------------------------------------------------------------
    // HTMLDecodeAction
    //----------------------------------------------------------------------

    /**
     * HTML decodes the current selection.
     */
    class HTMLDecodeAction extends AbstractAction
    {
        HTMLDecodeAction()
        {
            super("HTML Decode");
        }


        public void actionPerformed(ActionEvent e)
        {
            plugin_.getOutputArea().setText(
                StringEscapeUtils.unescapeHtml(plugin_.getInputText()));
        }
    }

    //----------------------------------------------------------------------
    // XMLEncodeAction
    //----------------------------------------------------------------------

    /**
     * XML encodes the current selection.
     */
    class XMLEncodeAction extends AbstractAction
    {
        XMLEncodeAction()
        {
            super("XML Encode");
        }


        public void actionPerformed(ActionEvent e)
        {
            plugin_.getOutputArea().setText(
                StringEscapeUtils.escapeXml(plugin_.getInputText()));
        }
    }

    //----------------------------------------------------------------------
    // XMLDecodeAction
    //----------------------------------------------------------------------

    /**
     * XML decode the current selection.
     */
    class XMLDecodeAction extends AbstractAction
    {
        XMLDecodeAction()
        {
            super("XML Decode");
        }


        public void actionPerformed(ActionEvent e)
        {
            plugin_.getOutputArea().setText(
                StringEscapeUtils.unescapeXml(plugin_.getInputText()));
        }
    }
    
    
    //----------------------------------------------------------------------
    // SkipLinesAction
    //----------------------------------------------------------------------

    /**
     * Skips every other line.
     */
    class SkipLinesAction extends AbstractAction
    {
        SkipLinesAction()
        {
            super("Skip Lines");
        }


        public void actionPerformed(ActionEvent e)
        {
            String text = plugin_.getInputText();
            StringBuffer sb = new StringBuffer();
            String[] lines = StringUtil.tokenize(text, "\n", true);
            
            if (lines.length > 1)
            {
                int begin = 0;
                
                if (!StringUtils.isBlank(lines[0]))
                    begin++;
             
                for (int i = begin; i < lines.length;)
                {
                    sb.append(lines[i] + "\n");
                    
                    if (StringUtils.isBlank(lines[i]))
                        i += 1;
                    else
                        i += 2;
                }
            }
            
            plugin_.getOutputArea().setText(sb.toString());
        }
    }
}