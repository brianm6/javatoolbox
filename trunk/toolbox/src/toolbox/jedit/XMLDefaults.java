package toolbox.jedit;

import java.awt.Color;

import org.jedit.syntax.DefaultInputHandler;
import org.jedit.syntax.SyntaxDocument;
import org.jedit.syntax.SyntaxStyle;
import org.jedit.syntax.TextAreaDefaults;
import org.jedit.syntax.Token;

/**
 * Customized JEditTextArea defaults for editing XML files.
 */
public class XMLDefaults extends TextAreaDefaults
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an XMLDefaults.
     */
    public XMLDefaults()
    {
        editable = true;
        caretVisible = true;
        caretBlinks = false;
        blockCaret = true;
        electricScroll = 3;
        
        //public int cols;
        //defaults_.rows = 5;
        styles = getSyntaxStyles();
        //public Color caretColor;
        //public Color selectionColor;
        //public Color lineHighlightColor;
        //public boolean lineHighlight;
        //public Color bracketHighlightColor;
        //public boolean bracketHighlight;
        //public Color eolMarkerColor;
        eolMarkers = false;
        paintInvalid = false;
        popup = new JEditPopupMenu();
        document = new SyntaxDocument();
        inputHandler = new DefaultInputHandler();
        inputHandler.addDefaultKeyBindings();
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Customizes the colors used for syntax hiliting the xml.
     * 
     * @return Syntax styles
     */
    protected static SyntaxStyle[] getSyntaxStyles()
    {
        SyntaxStyle[] styles = new SyntaxStyle[Token.ID_COUNT];

        styles[Token.COMMENT1] =
            new SyntaxStyle(Color.red.darker(), false, false);
            
        styles[Token.COMMENT2] =
            new SyntaxStyle(new Color(0x990033), false, false);
            
        styles[Token.KEYWORD1] =
            new SyntaxStyle(Color.blue.darker(), false, false);
            
        styles[Token.KEYWORD2] =
            new SyntaxStyle(Color.blue.darker(), false, false);
            
        styles[Token.KEYWORD3] =
            new SyntaxStyle(new Color(0x009600), false, false);
            
        styles[Token.LITERAL1] =
            new SyntaxStyle(Color.green.darker() /*new Color(0x650099)*/
                , false, false);
                
        styles[Token.LITERAL2] =
            new SyntaxStyle(new Color(0x650099), false, false);
            
        styles[Token.LABEL] =
            new SyntaxStyle(new Color(0x990033), false, false);
            
        styles[Token.OPERATOR] =
            new SyntaxStyle(Color.blue.darker(), false, false);
            
        styles[Token.INVALID] = 
            new SyntaxStyle(Color.red, false, false);

        return styles;
    }
}