package toolbox.jedit.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.jedit.DynamicTokenMarker;
import toolbox.jedit.JEditTextArea;

/**
 * Unit test for JEditTextArea
 */
public class JEditTextAreaTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JEditTextAreaTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(JEditTextAreaTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testJEditTextArea()
    {
        JEditTest test = new JEditTest();
        test.setVisible(true);            
    }
    
    //--------------------------------------------------------------------------
    // Helper Classes
    //--------------------------------------------------------------------------
    
    class JEditTest extends JFrame implements ActionListener
    {
        private JEditTextArea jeta_;
        private JComboBox fgCombo_;
        private JComboBox bgCombo_;
        private Map clut_;
                        
        public JEditTest()
        {
            super("testJEditTextArea");
            buildView();             
        }
        
        protected void buildView()
        {
           Container c = getContentPane();
           
           jeta_ = new JEditTextArea();
           jeta_.setTokenMarker(new DynamicTokenMarker());
           c.add(BorderLayout.CENTER, new JScrollPane(jeta_));
           c.add(BorderLayout.SOUTH, buildControlView());
           setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
           pack();
        }
        
        protected JPanel buildControlView() 
        {
            clut_ = new HashMap();
            clut_.put("red", Color.red);
            clut_.put("green", Color.green);
            clut_.put("blue", Color.blue);
            
            JPanel p = new JPanel(new FlowLayout());
            
            p.add(new JLabel("Foreground"));
            p.add(fgCombo_ = new JComboBox(new String [] {"red", "green", "blue"} ));
            p.add(new JLabel("Background"));
            p.add(bgCombo_ = new JComboBox(new String [] {"red", "green", "blue"} ));
            
            fgCombo_.addActionListener(this);
            bgCombo_.addActionListener(this);
            return p;
        }
        
        public void actionPerformed(ActionEvent e)
        {
            Object obj = e.getSource();
            
            if (obj == fgCombo_)
            {
                jeta_.getPainter().setForeground((Color) clut_.get(fgCombo_.getSelectedItem()));
            }
            else if (obj == bgCombo_)
            {
                jeta_.getPainter().setBackground((Color) clut_.get(bgCombo_.getSelectedItem()));
            }
            else
                logger_.debug("Nothing to do!");
        }
    }
}
