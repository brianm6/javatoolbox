// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst space 
// Source File Name:   LicAgreement.java

package com.adobe.acrobat.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.adobe.acrobat.util.Util;

// Referenced classes of package com.adobe.acrobat.gui:
//            BasicDialog, ReaderPrefs

public class LicAgreement extends BasicDialog
{
    static
    {
        System.out.println("\n\n\t\tHacked com.adobee.acrobat.gui.LicAgreement v1\n\n");
    }
    
    public LicAgreement(Frame frame)
    {
        super(frame, Util.getDialogString("LicAgreement:Title"), true);
        String s = Util.getDialogString("LicAgreement:Agreement");
        int i = 0;
        int j = s.length();
        int k = 0;
        int l = 0;
        int i1;
        for (; i < j; i = i1 + 1)
        {
            k++;
            i1 = s.indexOf('\n', i);
            if (i1 < 0)
                i1 = j;
            l = Math.max(l, i1 - i);
        }

        TextArea textarea = new TextArea(s, k + 1, l);
        textarea.setEditable(false);
        textarea.setBackground(Color.white);
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent windowevent)
            {
                dispose();
                cancel();
            }

        });
        add(textarea, "Center");
        add(super.okCancelPanel, "South");
        pack();
    }

    protected void arrangeButtons()
    {
        super.buttons.setLayout(new GridLayout(1, 2, 5, 5));
        super.ok.setLabel(Util.getDialogString("LicAgreement:Accept"));
        super.cancel.setLabel(Util.getDialogString("LicAgreement:Reject"));
        super.buttons.add(super.ok);
        super.buttons.add(super.cancel);
    }

    public void cancel()
    {
        ReaderPrefs.setUserAcceptedLicAgreement(false);
        setVisible(false);
    }

    public void ok()
    {
        ReaderPrefs.setUserAcceptedLicAgreement(true);
        setVisible(false);
    }
    
    /**
     * @see com.adobe.pe.awt.BaseDialog#setVisible(boolean)
     */
    public void setVisible(boolean flag)
    {
        if (!flag)
            super.setVisible(flag);
    }
}
