package toolbox.jedit;

import javax.swing.text.Segment;

import org.apache.log4j.Logger;
import org.jedit.syntax.KeywordMap;
import org.jedit.syntax.Token;
import org.jedit.syntax.TokenMarker;

/**
 * Dynamic Token Marker
 */
public class DynamicTokenMarker extends TokenMarker
{
    private static final Logger logger_ =
        Logger.getLogger(DynamicTokenMarker.class);

    private static KeywordMap TO;
    
    private KeywordMap keywords_;
    private int lastOffset_;
    private int lastKeyword_;
        
    public DynamicTokenMarker()
    {
        this(getKeywords());
    }
    
    public DynamicTokenMarker(KeywordMap keywords)
    {
        keywords_ = keywords;
    }

    public byte markTokensImpl(byte token, Segment line, int lineIndex)
    {
        char[] array = line.array;
        int offset = line.offset;
        lastOffset_ = offset;
        lastKeyword_ = offset;
        int length = line.count + offset;
        boolean backslash = false;
    
 loop : for (int i = offset; i < length; i++)
        {
            int i1 = (i + 1);
    
            char c = array[i];
            
            logger_.debug("handling char: " + c);
            
//            if (c == '\\')
//            {
//                backslash = !backslash;
//                continue;
//            }
    
            switch (token)
            {
                case Token.NULL :
                
                    switch (c)
                    {
                        /*
                        case '#' :
                            if (backslash)
                                backslash = false;
                            else if (cpp)
                            {
                                if (doKeyword(line, i, c))
                                    break;
                                addToken(i - lastOffset, token);
                                addToken(length - i, Token.KEYWORD2);
                                lastOffset = lastKeyword = length;
                                break loop;
                            }
                            break;
                        case '"' :
                            doKeyword(line, i, c);
                            if (backslash)
                                backslash = false;
                            else
                            {
                                addToken(i - lastOffset, token);
                                token = Token.LITERAL1;
                                lastOffset = lastKeyword = i;
                            }
                            break;
                        case '\'' :
                            doKeyword(line, i, c);
                            if (backslash)
                                backslash = false;
                            else
                            {
                                addToken(i - lastOffset, token);
                                token = Token.LITERAL2;
                                lastOffset = lastKeyword = i;
                            }
                            break;
                        case ':' :
                            if (lastKeyword == offset)
                            {
                                if (doKeyword(line, i, c))
                                    break;
                                backslash = false;
                                addToken(i1 - lastOffset, Token.LABEL);
                                lastOffset = lastKeyword = i1;
                            }
                            else if (doKeyword(line, i, c))
                                break;
                            break;
                        case '/' :
                            backslash = false;
                            doKeyword(line, i, c);
                            if (length - i > 1)
                            {
                                switch (array[i1])
                                {
                                    case '*' :
                                        addToken(i - lastOffset, token);
                                        lastOffset = lastKeyword = i;
                                        if (length - i > 2 && array[i + 2] == '*')
                                            token = Token.COMMENT2;
                                        else
                                            token = Token.COMMENT1;
                                        break;
                                    case '/' :
                                        addToken(i - lastOffset, token);
                                        addToken(length - i, Token.COMMENT1);
                                        lastOffset = lastKeyword = length;
                                        break loop;
                                }
                            }
                            break;
                            */
                        default :
                            //backslash = false;
                            if (!Character.isLetterOrDigit(c) && c != '_')
                            {
                                logger_.info("!Char: " + c);
                                doKeyword(line, i, c);
                            }
                            break;
                    }
                    break;
                
                /*    
                case Token.COMMENT1 :
                case Token.COMMENT2 :
                    backslash = false;
                    if (c == '*' && length - i > 1)
                    {
                        if (array[i1] == '/')
                        {
                            i++;
                            addToken((i + 1) - lastOffset_, token);
                            token = Token.NULL;
                            lastOffset_ = lastKeyword_ = i + 1;
                        }
                    }
                    break;
                case Token.LITERAL1 :
                    if (backslash)
                        backslash = false;
                    else if (c == '"')
                    {
                        addToken(i1 - lastOffset_, token);
                        token = Token.NULL;
                        lastOffset_ = lastKeyword_ = i1;
                    }
                    break;
                case Token.LITERAL2 :
                    if (backslash)
                        backslash = false;
                    else if (c == '\'')
                    {
                        addToken(i1 - lastOffset_, Token.LITERAL1);
                        token = Token.NULL;
                        lastOffset_ = lastKeyword_ = i1;
                    }
                    break;
                */
                default :
                    logger_.debug("Default Token:" + token);
                    //throw new InternalError("Invalid state: " + token);
            }
        }
    
        if (token == Token.NULL)
        {
            boolean dok = doKeyword(line, length, '\0');
            logger_.debug("Token.NULL -> doKeyword('\0') = " + dok);
            
        }
    
        switch (token)
        {
            case Token.LITERAL1 :
            case Token.LITERAL2 :
                logger_.debug("addToken:Literal1/2");
                addToken(length - lastOffset_, Token.INVALID);
                token = Token.NULL;
                break;
            case Token.KEYWORD2 :
                logger_.debug("addToken:keyword2");
                addToken(length - lastOffset_, token);
                if (!backslash)
                    token = Token.NULL;
            default :
                //token = Token.KEYWORD2;
                logger_.debug("addToken:default " + (length - lastOffset_) + " " + token);
                addToken(length - lastOffset_, token);
                break;
        }
    
        return token;
    }
    
    public static KeywordMap getKeywords()
    {
        if (TO == null)
        {
            TO = new KeywordMap(false);
            TO.add("monkey", Token.LITERAL1);
            TO.add("sumi"  , Token.LITERAL2);
            TO.add("jelly" , Token.KEYWORD1);
            TO.add("roll"  , Token.KEYWORD2);
        }
        return TO;
    }
    
    private boolean doKeyword(Segment line, int i, char c)
    {
        logger_.debug(line + " " + i + " '" + c + "'");
        
        int i1 = i + 1;
    
        int len = i - lastKeyword_;
        byte id = keywords_.lookup(line, lastKeyword_, len);
        
        if (id != Token.NULL)
        {
            if (lastKeyword_ != lastOffset_)
                addToken(lastKeyword_ - lastOffset_, Token.NULL);
                
            addToken(len, id);
            lastOffset_ = i;
        }
        
        lastKeyword_ = i1;
        
        if (id != Token.NULL)
        {
            logger_.debug("doKeyword = true!");
            return true;
        }
        else 
            return false;
    }
}