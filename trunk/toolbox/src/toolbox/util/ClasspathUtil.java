package toolbox.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ClasspathUtil
{

    /**
     * Constructor for ClasspathUtil.
     */
    public ClasspathUtil()
    {
        super();
    }

    public static void main(String[] args)
    {
        ClasspathUnwrapper gui = new ClasspathUnwrapper();
        gui.setVisible(true);   
    }
    
    public static String[] unwrap(String classpath)
    {
        StringTokenizer st = new StringTokenizer(classpath, ";");
        
        List cps = new ArrayList();
        
        while (st.hasMoreTokens())
        {
            String next = (String)st.nextToken();
            cps.add(next);
        }
        
        return (String[])cps.toArray(new String[cps.size()]);
    }    
    
    public static class ClasspathUnwrapper extends JFrame 
        implements ActionListener
    {
        JTextArea textArea;
        JButton   unwrapButton;
        
        public ClasspathUnwrapper()
        {
            super("Classpath Unwrapper");
            buildView();
            pack();
            SwingUtil.centerWindow(this);
        }
        
        protected void buildView()
        {
            textArea = new JTextArea();
            textArea.setFont(SwingUtil.getPreferredMonoFont());
         
            getContentPane().add(
                new JScrollPane(textArea), BorderLayout.CENTER);
                
            unwrapButton = new JButton("Unwrap");
            unwrapButton.addActionListener(this);
            getContentPane().add(unwrapButton, BorderLayout.SOUTH);
            
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            String s = textArea.getText();
            String[] cps = unwrap(s);
            
            for(int i=0; i<cps.length; i++)
            {
                textArea.append("\n" + cps[i]);
            }            
        }
    }    
}
