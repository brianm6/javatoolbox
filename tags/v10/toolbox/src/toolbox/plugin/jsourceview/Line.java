package toolbox.jsourceview;

import java.io.PrintStream;

public class Line implements MachineConstants
{

    public Line(String s1)
    {
        s = s1.trim();
        s = s.replace('\t', ' ');
    }

    public int peek()
    {
        String s1 = "pos=" + pos;
        if(DEBUG)
            System.out.println(s1);
        if(s.length() == 0)
        {
            if(DEBUG)
                System.out.println("EOL");
            return 1;
        }
        if(pos == s.length())
        {
            if(DEBUG)
                System.out.println("EOL");
            return 1;
        }
        if(pos + 2 <= s.length())
        {
            char ac[] = new char[2];
            ac[0] = s.charAt(pos);
            ac[1] = s.charAt(pos + 1);
            String s2 = new String(ac);
            if(s2.equals("//"))
            {
                if(DEBUG)
                    System.out.println("LINE_COMMENT");
                return 2;
            }
            if(s2.equals("/*"))
            {
                if(DEBUG)
                    System.out.println("COMMENT_BEGIN");
                return 3;
            }
            if(s2.equals("*/"))
            {
                if(DEBUG)
                    System.out.println("COMMENT_END");
                return 4;
            }
            if(DEBUG)
                System.out.println("ANY_CHAR");
            return 0;
        }
        if(DEBUG)
            System.out.println("ANY_CHAR");
        return 0;
    }

    public static void debug(String s1)
    {
        if(DEBUG)
            System.out.println(s1);
    }

    public int getNextToken()
    {
        int i = peek();
        switch(i)
        {
        case 0: /* '\0' */
            pos++;
            return i;
    
        case 2: /* '\002' */
        case 3: /* '\003' */
        case 4: /* '\004' */
            pos += 2;
            return i;
    
        case 1: /* '\001' */
            return i;
    
        }
        throw new IllegalArgumentException("token " + i + " not valid!");
    }

    static boolean DEBUG;
    String s;
    int pos;
}