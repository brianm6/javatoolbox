package com.adobe.acrobat.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.adobe.acrobat.util.Util;

import toolbox.util.StringUtil;

public class LicAgreement extends BasicDialog
{
    static
    {
        System.out.println(StringUtil.addBars(
            "Loaded debug com.adobee.acrobat.gui.LicAgreement v1"));
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