package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.IconUIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;

/**
 * Shows UIDefaults for each widget in Swing's library. 
 * 
 * @author Unascribed
 */
public class UIDefaultsPlugin extends JPanel implements IPlugin, ActionListener
{
    private static final Logger logger_ =
        Logger.getLogger(UIDefaultsPlugin.class);
        
    private JTabbedPane     tabbedPane_;
    private JButton         metalButton_;
    private JButton         windowsButton_;
    private JButton         motifButton_;
    private SampleRenderer  sampleRenderer_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Default constructor
     */    
    public UIDefaultsPlugin()
    {
    }

    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------
        
    public String getName()
    {
        return "UIDefaults Viewer";
    }
    
    public Component getComponent()
    {
        return this;
    }
    
    public String getDescription()
    {
        return "Displays UI defaults for the installed Look and Feels.";
    }
    
    public void startup(Map params)
    {
        //if (params != null)
        //  statusBar_= (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);
        
        setLayout(new BorderLayout());
        tabbedPane_ = getTabbedPane();
        add(tabbedPane_);

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 3));
        add(buttons, BorderLayout.SOUTH);

        metalButton_ = new JButton("Metal");
        metalButton_.addActionListener(this);
        buttons.add(metalButton_);

        windowsButton_ = new JButton("Windows");
        windowsButton_.addActionListener(this);
        buttons.add(windowsButton_);

        motifButton_ = new JButton("Motif");
        motifButton_.addActionListener(this);
        buttons.add(motifButton_);
    }
    
    public void savePrefs(Properties prefs)
    {
    }
    
    public void applyPrefs(Properties prefs)
    {
    }
    
    public void shutdown()
    {
    }

    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------
    
    public void actionPerformed(ActionEvent e)
    {
        String laf = "";
        Object o = e.getSource();

        if (o == metalButton_)
            laf = "javax.swing.plaf.metal.MetalLookAndFeel";
        else if (o == windowsButton_)
            laf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        else if (o == motifButton_)
            laf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

        try
        {
            UIManager.setLookAndFeel(laf);
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
    // Private
    //--------------------------------------------------------------------------
        
    private JTabbedPane getTabbedPane()
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

        JTabbedPane pane = new JTabbedPane(SwingConstants.BOTTOM);
        pane.setPreferredSize(new Dimension(800, 400));
        addComponentTabs(pane, components);

        return pane;
    }

    private Map getComponentMap(Map components, String key)
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

    private void addComponentTabs(JTabbedPane pane, Map components)
    {
        sampleRenderer_ = new SampleRenderer();

        String[] colName = { "Key", "Value", "Sample" };
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
                        rowData[i][2] = (Font) o;

                    if (o instanceof Color)
                        rowData[i][2] = (Color) o;

                    if (o instanceof IconUIResource)
                        rowData[i][2] = (Icon) o;
                }
                else
                {
                    rowData[i][1] = "";
                    rowData[i][2] = "";
                }

            }

            MyTableModel myModel = new MyTableModel(rowData, colName);
            JTable table = new JTable(myModel);
            
            table.setDefaultRenderer(
                sampleRenderer_.getClass(),sampleRenderer_);
                
            table.getColumnModel().getColumn(0).setPreferredWidth(250);
            table.getColumnModel().getColumn(1).setPreferredWidth(500);
            table.getColumnModel().getColumn(2).setPreferredWidth(50);

            pane.addTab(component, new JScrollPane(table));
        }
    }

    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    class MyTableModel extends AbstractTableModel
    {
        private String[] columnNames;
        private Object[][] rowData;

        public MyTableModel(Object[][] rowData, String[] columnNames)
        {
            this.rowData = rowData;
            this.columnNames = columnNames;
        }

        public int getColumnCount()
        {
            return columnNames.length;
        }

        public int getRowCount()
        {
            return rowData.length;
        }

        public String getColumnName(int col)
        {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col)
        {
            return rowData[row][col];
        }

        public Class getColumnClass(int c)
        {
            Object o;

            if (c == 2)
                o = sampleRenderer_;
            else
                o = getValueAt(0, c);

            return o.getClass();
        }

        public void setValueAt(Object value, int row, int col)
        {
            rowData[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }

    class SampleRenderer extends JLabel implements TableCellRenderer
    {
        public SampleRenderer()
        {
            super();
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true); //MUST do this for background to show up.
        }

        public Component getTableCellRendererComponent(
            JTable table,
            Object sample,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column)
        {
            setBackground(null);
            setIcon(null);
            setText("");

            if (sample instanceof Color)
            {
                setBackground((Color) sample);
            }

            if (sample instanceof Font)
            {
                setText("Sample");
                setFont((Font) sample);
            }

            if (sample instanceof Icon)
            {
                setIcon((Icon) sample);
            }

            return this;
        }
    }
}
