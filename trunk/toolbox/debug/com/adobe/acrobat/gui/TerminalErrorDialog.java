// =============================================================================
// Acrobat - prevent exit if running embedded
// =============================================================================

package com.adobe.acrobat.gui;

import toolbox.util.StringUtil;

public class TerminalErrorDialog extends ErrorDialog
{
    static
    {
        System.out.println(StringUtil.addBars(
            "Loaded debug com.adobe.acrobat.gui.TerminalErrorDialog"));
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
