package toolbox.jsourceview;

/**
 * LineScanner tokenizes a line of source code
 */
public class LineScanner implements MachineConstants
{
    /**
     * Debug flag
     */
    private static boolean debug_;
    
    /** 
     * Line of source code 
     */
    private String line_;
    
    /** 
     * Current position 
     */
    private int position_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a LineScanner
     */
    public LineScanner()
    {
    }
    
    /**
     * Creates a LineScanner for the given line of source code. Assumes tabs
     * have already been removed from the line of source code.
     * 
     * @param  line   Line of source code
     */
    public LineScanner(String line)
    {
        setLine(line);
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Sets the current line
     * 
     * @param  line  Line of source code
     */
    public void setLine(String line)
    {
        line_ = line;
        position_ = 0;
    }

    /**
     * Peeks to next token in the line
     * 
     * @return  State of machine
     */
    public int peek()
    {
        String posStr = "pos=" + position_;
        
        if(debug_)
            System.out.println(posStr);
            
        if (line_.length() == 0)
        {
            if(debug_)
                System.out.println("EOL");
            return 1;
        }
        
        if (position_ == line_.length())
        {
            if(debug_)
                System.out.println("EOL");
            return 1;
        }
        
        if (position_ + 2 <= line_.length())
        {
            char ac[] = new char[2];
            ac[0] = line_.charAt(position_);
            ac[1] = line_.charAt(position_ + 1);
            
            String comment = new String(ac);
            
            if (comment.equals("//"))
            {
                if (debug_)
                    System.out.println("LINE_COMMENT");
                return 2;
            }
            
            if (comment.equals("/*"))
            {
                if (debug_)
                    System.out.println("COMMENT_BEGIN");
                return 3;
            }
            
            if (comment.equals("*/"))
            {
                if (debug_)
                    System.out.println("COMMENT_END");
                return 4;
            }
            
            if (debug_)
                System.out.println("ANY_CHAR");
                
            return 0;
        }
        
        if(debug_)
            System.out.println("ANY_CHAR");
            
        return 0;
    }
    
    /**
     * Returns the next token
     * 
     * @return token 
     */
    public int getNextToken()
    {
        int i = peek();
        
        switch(i)
        {
            case 0: /* '\0' */
                position_++;
                return i;
    
            case 2: /* '\002' */
            case 3: /* '\003' */
            case 4: /* '\004' */
                position_ += 2;
                return i;
    
            case 1: /* '\001' */
                return i;
        }
        
        throw new IllegalArgumentException("token " + i + " not valid!");
    }
}