package toolbox.jsourceview;

/**
 * Finite state machine to parse a line of source code
 * The original source code was lost so this is the touched up
 * mess from the decompilation.
 */
public class Machine implements MachineConstants 
{
    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------
    
    /**
     * Constructor 
     */
    public Machine() 
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Scans a line
     * 
     * @param  line        Line to scan
     * @param  lineStatus  Line's status
     */
    public static void scanLine(LineScanner line, LineStatus linestatus) 
    {
        int i = 0;
        
        label0:
        
        do
        {
            switch(i) 
            {
                //=============================================================
                                
                default:
                {
                    break;
                }

                //=============================================================
                
                case 0: // '\0'
                {
                    linestatus.setCountLine(false);
                    
                    if (linestatus.getInComment())
                        i = 8;
                    else
                        i = 1;
                    continue;
                }

                //=============================================================
                
                case 9: // '\t'
                {
                    break label0;
                }

                //=============================================================
                
                case 1: // '\001'
                {
                    switch (line.getNextToken()) 
                    {
                        case 1: // '\001'
                        case 2: // '\002'
                            i = 9;
                            break;
        
                        case 0: // '\0'
                        case 4: // '\004'
                            i = 2;
                            break;
        
                        case 3: // '\003'
                            i = 3;
                            break;
        
                        default:
                            throw new IllegalArgumentException(
                                "State 1 transition invalid! ");
                    }
                    continue;
                }

                //=============================================================
                
                case 2: // '\002'
                {
                
                    linestatus.setCountLine(true);
                    
                    switch(line.getNextToken()) 
                    {
                        case 0: // '\0'
                        case 4: // '\004'
                            i = 2;
                            break;
        
                        case 1: // '\001'
                        case 2: // '\002'
                            i = 9;
                            break;
        
                        case 3: // '\003'
                            i = 6;
                            break;
        
                        default:
                            throw new IllegalArgumentException(
                                "State 2 transition invalid!");
                    }
                    continue;
                }

                //=============================================================
                
                case 3: // '\003'
                {
                
                    linestatus.setInComment(true);
                    
                    switch(line.getNextToken()) 
                    {
                        case 0: // '\0'
                        case 2: // '\002'
                        case 3: // '\003'
                            i = 3;
                            break;
        
                        case 1: // '\001'
                            i = 9;
                            break;
        
                        case 4: // '\004'
                            i = 4;
                            break;
        
                        default:
                            throw new IllegalArgumentException(
                                "State 3 transition invalid!");
                    }
                    continue;
                }

                //=============================================================
                
                case 4: // '\004'
                {
                
                    linestatus.setInComment(false);
                    
                    switch(line.getNextToken()) 
                    {
                        case 1: // '\001'
                        case 2: // '\002'
                            i = 9;
                            break;
        
                        case 0: // '\0'
                        case 4: // '\004'
                            i = 5;
                            break;
        
                        case 3: // '\003'
                            i = 6;
                            break;
        
                        default:
                            throw new IllegalArgumentException(
                                "State 4 transition invalid!");
                    }
                    continue;
                }

                //=============================================================
                
                case 5: // '\005'
                {
                
                    linestatus.setCountLine(true);
                    i = 4;
                    continue;
                }

                //=============================================================
                                
                case 6: // '\006'
                {
                
                    linestatus.setInComment(true);
                    
                    switch(line.getNextToken()) 
                    {
                        case 0: // '\0'
                        case 2: // '\002'
                        case 3: // '\003'
                            i = 6;
                            break;
        
                        case 1: // '\001'
                            i = 9;
                            break;
        
                        case 4: // '\004'
                            i = 7;
                            break;
        
                        default:
                            throw new IllegalArgumentException(
                                "State 6 transition invalid!");
                    }
                    continue;
                }

                //=============================================================
                
                case 7: // '\007'
                {
                
                    linestatus.setInComment(false);
                    
                    switch(line.getNextToken()) 
                    {
                        case 0: // '\0'
                        case 4: // '\004'
                            i = 7;
                            break;
        
                        case 1: // '\001'
                        case 2: // '\002'
                            i = 9;
                            break;
        
                        case 3: // '\003'
                            i = 6;
                            break;
        
                        default:
                            throw new IllegalArgumentException(
                                "State 7 transition invalid!");
                    }
                    continue;
                }

                //=============================================================
                
                case 8: // '\b'
                {
                
                    switch(line.getNextToken()) 
                    {
                        case 0: // '\0'
                        case 2: // '\002'
                        case 3: // '\003'
                            i = 8;
                            break;
        
                        case 1: // '\001'
                            i = 9;
                            break;
        
                        case 4: // '\004'
                            i = 4;
                            break;
        
                        default:
                            throw new IllegalArgumentException(
                                "State 8 transition invalid!");
                    }
                    break;
                }
            }
        }
        while(true);
    }
}