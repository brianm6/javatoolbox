package com.javio.webwindow;

import toolbox.util.StringUtil;

class BZ implements Runnable
{
    static
    {
        System.out.println(StringUtil.addBars(
            "Loaded debug com.javio.webwindow.BZ"));
    }
    
    private final HTMLPane exit;

    
    BZ(HTMLPane htmlpane)
    {
        exit = htmlpane;
    }

    
    public final void run()
    {
        // =====================================================================
        // OVERRIDE: Disable timer
        
        /*
        System.out.println("Evaluation timer has started...");
        try
        {
            char c = '\u03E8';
            int i = 60 * c;
            Thread.sleep(30 * i);
        }
        catch (Exception exception) { }
        finally
        {
            String s = "This is an evaluation copy of the WebWindow! Time has expired.";
            Object obj;
            for (obj = exit.getParent(); obj != null && !(obj instanceof Frame); obj = ((Component) (obj)).getParent());
            if (obj == null)
                obj = new Frame();
            J j = new J((Frame)obj, "Evaluation Edition", s);
            j.show();
            System.exit(0);
        }
        */
        
        // =====================================================================
    }
}