package toolbox.util;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;

import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.util.NameComponentChooser;

/**
 * JemmyUtil is responsible for ___.
 */
public class JemmyUtil
{

    /**
     * Creates a JemmyUtil.
     */
    public JemmyUtil()
    {
    }

    
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
    
    public static JButton getSource(JButtonOperator operator)
    {
        return (JButton) operator.getSource();
    }
    
    public static JList getSource(JListOperator operator)
    {
        return (JList) operator.getSource();
    }
    
    public static JDialog getSource(JDialogOperator operator)
    {
        return (JDialog) operator.getSource();
    }
}
