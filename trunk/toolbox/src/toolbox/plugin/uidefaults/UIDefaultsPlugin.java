package toolbox.plugin.uidefaults;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.tabbedpane.JSmartTabbedPane;
import toolbox.workspace.IPlugin;

/**
 * Shows UIDefaults for each widget in Swing's library for all currently
 * installed Look and Feels.
 *
 * @author Unascribed
 */
public class UIDefaultsPlugin extends JPanel implements IPlugin, ActionListener
{
    private static final Logger logger_ =
        Logger.getLogger(UIDefaultsPlugin.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * One tab per Swing component.
     */
    private JTabbedPane tabbedPane_;

    /**
     * Table cell renderer for look and feel resources like fonts and colors.
     */
    private UIDefaultsTableCellRenderer tableCellRenderer_;

    /**
     * Look and feel information map.
     */
    private Map infoMap_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a UIDefaultsPlugin.
     */
    public UIDefaultsPlugin()
    {
    }

    //--------------------------------------------------------------------------
    // Initializable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map params)
    {
        buildView();
    }
    
    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "Look & Feel Defaults";
    }


    /**
     * @see toolbox.workspace.IPlugin#getComponent()
     */
    public JComponent getComponent()
    {
        return this;
    }


    /**
     * @see toolbox.workspace.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Displays UI defaults for the installed Look and Feels.";
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs)
    {
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
    }

    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy()
    {
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
        String name = ((JButton) e.getSource()).getText();

        String clazz =
            ((UIManager.LookAndFeelInfo) infoMap_.get(name)).getClassName();

        try
        {
            UIManager.setLookAndFeel(clazz);
        }
        catch (Exception e2)
        {
            ExceptionUtil.handleUI(e2, logger_);
        }

        remove(tabbedPane_);
        tabbedPane_ = getTabbedPane();
        add(tabbedPane_);
        SwingUtilities.updateComponentTreeUI(SwingUtil.getFrameAncestor(this));
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        setLayout(new BorderLayout());
        tabbedPane_ = getTabbedPane();
        add(tabbedPane_);

        UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
        infoMap_ = new HashMap(info.length);

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, info.length));
        add(buttons, BorderLayout.SOUTH);

        for (int i = 0; i < info.length; i++)
        {
            JButton button = new JSmartButton(info[i].getName());
            button.addActionListener(this);
            buttons.add(button);
            infoMap_.put(info[i].getName(), info[i]);
        }
    }


    /**
     * Returns a component populated tabbed pane.
     *
     * @return JTabbedPane
     */
    protected JTabbedPane getTabbedPane()
    {
        Map components = new TreeMap();
        UIDefaults defaults = UIManager.getDefaults();

        //  Build of Map of attributes for each component
        for (Enumeration enum = defaults.keys(); enum.hasMoreElements();)
        {
            Object key = enum.nextElement();
            Object value = defaults.get(key);

            Map componentMap = getComponentMap(components, key.toString());

            if (componentMap != null)
                componentMap.put(key, value);
        }

        JTabbedPane pane = new JSmartTabbedPane(SwingConstants.BOTTOM);
        pane.setPreferredSize(new Dimension(800, 400));
        addComponentTabs(pane, components);

        return pane;
    }


    /**
     * Creates a map of the components.
     *
     * @param components Input map.
     * @param key Map key.
     * @return Map
     */
    protected Map getComponentMap(Map components, String key)
    {
        if (key.startsWith("class") | key.startsWith("javax"))
            return null;

        //  Component name is found before the first "."
        String componentName;
        int pos = key.indexOf(".");

        if (pos == -1)
            if (key.endsWith("UI"))
                componentName = key.substring(0, key.length() - 2);
            else
                componentName = "System Colors";
        else
            componentName = key.substring(0, pos);

        //  Get the Map for this particular component
        Object componentMap = components.get(componentName);

        if (componentMap == null)
        {
            componentMap = new TreeMap();
            components.put(componentName, componentMap);
        }

        return (Map) componentMap;
    }


    /**
     * Adds components as tabs to the given tabbed pane.
     *
     * @param pane Tabbed pane.
     * @param components Components to add.
     */
    protected void addComponentTabs(JTabbedPane pane, Map components)
    {
        tableCellRenderer_ = new UIDefaultsTableCellRenderer();

        String[] colName = {"Key", "Value", "Sample"};
        Set c = components.keySet();

        for (Iterator ci = c.iterator(); ci.hasNext();)
        {
            String component = (String) ci.next();
            Map attributes = (Map) components.get(component);
            Object[][] rowData = new Object[attributes.size()][3];
            int i = 0;
            Set a = attributes.keySet();

            for (Iterator ai = a.iterator(); ai.hasNext(); i++)
            {
                String attribute = (String) ai.next();
                rowData[i][0] = attribute;
                Object o = attributes.get(attribute);

                if (o != null)
                {
                    rowData[i][1] = o.toString();
                    rowData[i][2] = "";

                    if (o instanceof Font)
                        rowData[i][2] = o;

                    if (o instanceof Color)
                        rowData[i][2] = o;

                    if (o instanceof Icon)
                        rowData[i][2] = o;
                }
                else
                {
                    rowData[i][1] = "";
                    rowData[i][2] = "";
                }
            }

            UIDefaultsTableModel myModel = 
                new UIDefaultsTableModel(tableCellRenderer_, rowData, colName);
            
            JTable table = new JTable(myModel);

            table.setDefaultRenderer(
                tableCellRenderer_.getClass(), tableCellRenderer_);

            table.getColumnModel().getColumn(0).setPreferredWidth(250);
            table.getColumnModel().getColumn(1).setPreferredWidth(500);
            table.getColumnModel().getColumn(2).setPreferredWidth(50);

            pane.addTab(component, new JScrollPane(table));
        }
    }
}