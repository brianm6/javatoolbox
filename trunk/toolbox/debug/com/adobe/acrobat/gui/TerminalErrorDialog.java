// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst space 
// Source File Name:   TerminalErrorDialog.java

package com.adobe.acrobat.gui;


// Referenced classes of package com.adobe.acrobat.gui:
//            ErrorDialog

public class TerminalErrorDialog extends ErrorDialog
{
    static
    {
        System.out.println("\n\n\t\tDebug TerminalErrorDialog v1\n\n");
    }
    
    private boolean isApplication;

    public TerminalErrorDialog(String s, boolean flag)
    {
        super(s);
        isApplication = false;
    }

    public TerminalErrorDialog(Throwable throwable, boolean flag)
    {
        super(throwable);
        isApplication = false;
    }

    public void ok()
    {
        super.ok();
        if (isApplication)
            System.exit(2002);
    }
}
