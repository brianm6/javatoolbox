// =============================================================================
// Acrobat - prevent exit if running embedded
// =============================================================================

package com.adobe.acrobat.gui;

import org.apache.log4j.helpers.LogLog;

import toolbox.util.StringUtil;

public class TerminalErrorDialog extends ErrorDialog
{
    static
    {
        LogLog.debug(StringUtil.banner(
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
