package toolbox.util;

import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;

import org.apache.log4j.Logger;

import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.util.Dumper;
import org.netbeans.jemmy.util.NameComponentChooser;

/**
 * Utility methods to use in conjunction with Jemmy.
 */
public class JemmyUtil
{
    private static final Logger logger_ = Logger.getLogger(JemmyUtil.class);

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction of this static singleton.
     */
    private JemmyUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Convenience method to find a button operatator withing a given container
     * operator.
     * 
     * @param operator 
     * @param name
     * @return
     */
    public static JButtonOperator findButton(
        ContainerOperator operator, 
        String name)
    {
        return new JButtonOperator(operator, new NameComponentChooser(name));
    }
    
    
    /**
     * @param operator
     * @return
     */
    public static JButton getSource(JButtonOperator operator)
    {
        return (JButton) operator.getSource();
    }
    
    
    /**
     * @param operator
     * @return
     */
    public static JList getSource(JListOperator operator)
    {
        return (JList) operator.getSource();
    }
    
    
    /**
     * @param operator
     * @return
     */
    public static JDialog getSource(JDialogOperator operator)
    {
        return (JDialog) operator.getSource();
    }

    
    /**
     * Convenience method to dump all to the logger as debug output. 
     */
    public static void dump()
    {
        StringWriter writer = new StringWriter();
        Dumper.dumpAll(new PrintWriter(writer));
        logger_.debug("\n" + writer);
    }
    
    
    /**
     * Convenience method to dump a component to the logger as debug output.
     * 
     * @param c Component to dump.
     */
    public static void dump(Component c)
    {
        StringWriter writer = new StringWriter();
        Dumper.dumpComponent(c, new PrintWriter(writer));
        logger_.debug("\n" + writer);
    }
}