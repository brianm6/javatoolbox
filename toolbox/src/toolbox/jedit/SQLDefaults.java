package toolbox.jedit;

import java.awt.Color;

import org.jedit.syntax.DefaultInputHandler;
import org.jedit.syntax.SyntaxDocument;
import org.jedit.syntax.SyntaxStyle;
import org.jedit.syntax.TextAreaDefaults;
import org.jedit.syntax.Token;

import toolbox.util.ui.Colors;

/**
 * Customized JEditTextArea defaults for editing SQL files.
 */
public class SQLDefaults extends TextAreaDefaults
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor which overrides default values from superclass.
     */
    public SQLDefaults()
    {
        // Changed
        editable = true;
        caretVisible = true;
        caretBlinks = false;
        blockCaret = true;
        electricScroll = 3;
        cols = 80;
        rows = 10;
        styles = getSyntaxStyles();
        eolMarkers = false;
        paintInvalid = false;
        popup = new JEditPopupMenu();

        // Same        
        inputHandler = new DefaultInputHandler();
        inputHandler.addDefaultKeyBindings();
        document = new SyntaxDocument();
        caretColor = Color.blue;
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
     * @return Syntax styles.
     */
    protected SyntaxStyle[] getSyntaxStyles()
    {
        SyntaxStyle[] myStyles = new SyntaxStyle[Token.ID_COUNT];

        myStyles[Token.COMMENT1] = 
            new SyntaxStyle(Colors.dark_red, false, false);
            
        myStyles[Token.COMMENT2] = 
            new SyntaxStyle(Color.gray, false, true);
            
        myStyles[Token.KEYWORD1] = 
            new SyntaxStyle(Colors.dark_blue, false, false);
            
        myStyles[Token.KEYWORD2] = 
            new SyntaxStyle(Color.blue, true, true);
            
        myStyles[Token.KEYWORD3] = 
            new SyntaxStyle(Color.magenta, false, false);
            
        myStyles[Token.LITERAL1] = 
            new SyntaxStyle(Colors.blue4, false, false);
                
        myStyles[Token.LITERAL2] = 
            new SyntaxStyle(Color.orange, false, false);
            
        myStyles[Token.LABEL] = 
            new SyntaxStyle(Color.pink, false, false);
            
        myStyles[Token.OPERATOR] = 
            new SyntaxStyle(Colors.red4, false, false);
            
        myStyles[Token.INVALID] = 
            new SyntaxStyle(Colors.orange4, true, true);

        return myStyles;
    }
}