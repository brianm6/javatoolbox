package toolbox.jsourceview;

import java.io.PrintStream;

/**
 * LineScanner scanner
 */
public class LineScanner implements MachineConstants
{
    static boolean DEBUG;
    
    /** line of source code **/
    private String line_;
    
    /** current position **/
    private int position_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------    
    
    /**
     * Creates a LineScanner
     * 
     * @param  line   Line of source code
     */
    public LineScanner(String line)
    {
        line_ = line.trim().replace('\t', ' ');
    }

    //--------------------------------------------------------------------------
    //  Implementation
    //--------------------------------------------------------------------------

    /**
     * Peeks to next token in the line
     * 
     * @return  State of machine
     */
    public int peek()
    {
        String posStr = "pos=" + position_;
        
        if(DEBUG)
            System.out.println(posStr);
            
        if (line_.length() == 0)
        {
            if(DEBUG)
                System.out.println("EOL");
            return 1;
        }
        
        if (position_ == line_.length())
        {
            if(DEBUG)
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
                if (DEBUG)
                    System.out.println("LINE_COMMENT");
                return 2;
            }
            
            if (comment.equals("/*"))
            {
                if (DEBUG)
                    System.out.println("COMMENT_BEGIN");
                return 3;
            }
            
            if (comment.equals("*/"))
            {
                if (DEBUG)
                    System.out.println("COMMENT_END");
                return 4;
            }
            
            if (DEBUG)
                System.out.println("ANY_CHAR");
                
            return 0;
        }
        
        if(DEBUG)
            System.out.println("ANY_CHAR");
            
        return 0;
    }

    
    /**
     * Returns the next token
     * 
     * @return  The next token 
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