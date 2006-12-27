package toolbox.plugin.jsourceview;

/**
 * LineScanner tokenizes a line of source code.
 */
public class LineScanner implements MachineConstants
{
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /**
     * Debug flag prints to system.out when true.
     */
    private static boolean debug_;
    
    /** 
     * Line of source code to scan. 
     */
    private String line_;
    
    /** 
     * Current position of the scanner.
     */
    private int position_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a LineScanner.
     */
    public LineScanner()
    {
    }
    
    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Sets the current line.
     * 
     * @param line Line of source code.
     */
    public void setLine(String line)
    {
        line_ = line;
        position_ = 0;
    }

    
    /**
     * Peeks at the next token in line and returns the state of the machine.
     * 
     * @return int
     */
    public int peek()
    {
        if (debug_)
        {
            String posStr = "pos=" + position_;
            System.out.println(posStr);
        }
            
        int len = line_.length();
        
        if (len == 0)
        {
            if (debug_)
                System.out.println("EOL");
            return 1;
        }
        
        if (position_ == len)
        {
            if (debug_)
                System.out.println("EOL");
            return 1;
        }
        
        if (position_ + 2 <= len)
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
        
        if (debug_)
            System.out.println("ANY_CHAR");
            
        return 0;
    }
    
    
    /**
     * Returns the next token in line.
     * 
     * @return int 
     */
    public int getNextToken()
    {
        int i = peek();
        
        switch (i)
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
                
            default: 
                throw new IllegalArgumentException("token " + i + " not valid");
        }
    }
}