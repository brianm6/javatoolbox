package toolbox.util;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Classpath Util
 */
public class ClasspathUtil
{

    /**
     * Constructor for ClasspathUtil.
     */
    public ClasspathUtil()
    {
        super();
    }

    /**
     * Entry point
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        ClasspathUnwrapper gui = new ClasspathUnwrapper();
        gui.setVisible(true);   
    }
    
    /**
     * Unwraps classpath
     * 
     * @param  classpath  Classpath
     * @return String array
     */
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
    
    /**
     * Frame
     */
    static class ClasspathUnwrapper extends JFrame 
        implements ActionListener
    {
        private JTextArea textArea_;
        private JButton   unwrapButton_;
        
        
        public ClasspathUnwrapper()
        {
            super("Classpath Unwrapper");
            buildView();
            pack();
            SwingUtil.centerWindow(this);
        }
        
        protected void buildView()
        {
            textArea_ = new JTextArea();
            textArea_.setFont(SwingUtil.getPreferredMonoFont());
         
            getContentPane().add(
                new JScrollPane(textArea_), BorderLayout.CENTER);
                
            unwrapButton_ = new JButton("Unwrap");
            unwrapButton_.addActionListener(this);
            getContentPane().add(unwrapButton_, BorderLayout.SOUTH);
            
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            String s = textArea_.getText();
            String[] cps = unwrap(s);
            
            for(int i=0; i<cps.length; i++)
            {
                textArea_.append("\n" + cps[i]);
            }            
        }
    }    
}
