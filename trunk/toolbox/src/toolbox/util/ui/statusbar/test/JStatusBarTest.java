package toolbox.util.ui.statusbar.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import toolbox.util.RandomUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.statusbar.JStatusBar;

public class JStatusBarTest extends JPanel
{
    /**
     * TODO: Convert to JUnit test
     */
    
    public static void main(String[] args) throws Exception
    {
        SwingUtil.setPreferredLAF();

        JStatusBar status = new JStatusBar();

        // Relative, twice normal
        status.addStatusComponent(new JLabel(" Relative x2 "), JStatusBar.RELATIVE, 2);
        
        // Relative normal
        status.addStatusComponent(new JLabel(" Relative x1 "), JStatusBar.RELATIVE, 1);
        
        // Fixed, based on preferred size
        status.addStatusComponent(new JLabel(" Preferred "));
        
        // Fixed, based on specified size
        status.addStatusComponent(new JLabel(" Fixed "), JStatusBar.FIXED, 40);
        
        // Relative normal
        status.addStatusComponent(new JLabel(" Relative x1 "), JStatusBar.RELATIVE);
        
        // Progress bar is based on preferred size
        JProgressBar progressBar = new JProgressBar(1, 100);
        progressBar.setValue(RandomUtil.nextInt(1, 100));
        status.addStatusComponent(progressBar);

        JFrame frame = new JFrame("JStatusBar Test");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(BorderLayout.SOUTH, status);
        frame.setSize(700, 150);
        SwingUtil.centerWindow(frame);
        frame.show();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

/*
Originally created by Claude Duguay
Copyright (c) 2000
*/
