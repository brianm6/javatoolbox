package toolbox.jedit;

import java.awt.Color;

import org.jedit.syntax.DefaultInputHandler;
import org.jedit.syntax.SyntaxDocument;
import org.jedit.syntax.SyntaxStyle;
import org.jedit.syntax.TextAreaDefaults;
import org.jedit.syntax.Token;

/**
 * Customized JEditTextArea defaults for editing Java files.
 */
public class JavaDefaults extends TextAreaDefaults
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor which overrides default values from superclass.
     */
    public JavaDefaults()
    {
        // Changed
        editable = true;
        caretVisible = true;
        caretBlinks = false;
        blockCaret = true;
        electricScroll=2;
        cols = 80;
        rows = 25;
        styles = getSyntaxStyles();
        eolMarkers=false;
        paintInvalid=false;
        popup = new JEditPopupMenu();

        // Same        
        inputHandler = new DefaultInputHandler();
        inputHandler.addDefaultKeyBindings();
        document = new SyntaxDocument();
        caretColor = Color.red;
        selectionColor = new Color(0xccccff);
        lineHighlightColor = new Color(0xe0e0e0);
        lineHighlight = true;
        bracketHighlightColor = Color.black;
        bracketHighlight = true;
        eolMarkerColor = new Color(0x009999);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Customizes the colors used for syntax hiliting the xml.
     * 
     * @return Syntax styles
     */
    protected SyntaxStyle[] getSyntaxStyles()
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
            new SyntaxStyle(Color.green.darker(), false, false);
                
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