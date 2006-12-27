package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;
import java.io.File;

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
public class SQLReferenceAction extends BaseAction
{
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
        super(plugin, "SQL Reference", false, null, null);
    }

    //--------------------------------------------------------------------------
    // SmartAction Abstract Methods
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e)
    {
        String sqlRef = 
            File.separator + FileUtil.trailWithSeparator(
                ClassUtil.packageToPath(
                    ClassUtils.getPackageName(QueryPlugin.class))) 
                    + "sqlref.txt";

        sqlRef = sqlRef.replace(File.separatorChar, '/');

        getPlugin().getSQLEditor().setText(
            getPlugin().getSQLEditor().getText() + "\n" +
                ResourceUtil.getResourceAsString(sqlRef));
    }
}