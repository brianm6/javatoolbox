package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import org.apache.commons.lang.ClassUtils;

import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.ClassUtil;
import toolbox.util.FileUtil;
import toolbox.util.ResourceUtil;

/**
 * Dumps a short SQL reference text to the SQL editor.
 * 
 * @see toolbox.plugin.jdbc.QueryPlugin
 */
public class SQLReferenceAction extends AbstractAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Parent plugin.
     */
    private final QueryPlugin plugin_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SQLReferenceAction.
     * 
     * @param plugin Parent plugin.
     */
    public SQLReferenceAction(QueryPlugin plugin)
    {
        plugin_ = plugin;
    }

    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        String sqlRef = File.separator +
            FileUtil.trailWithSeparator(ClassUtil.packageToPath(
                ClassUtils.getPackageName(QueryPlugin.class))) + "sqlref.txt";

        sqlRef = sqlRef.replace(File.separatorChar, '/');

        plugin_.getSQLEditor().setText(
            plugin_.getSQLEditor().getText() + "\n" +
                ResourceUtil.getResourceAsString(sqlRef));
    }
}