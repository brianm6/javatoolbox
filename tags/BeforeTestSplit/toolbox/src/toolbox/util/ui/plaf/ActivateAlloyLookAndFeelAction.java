package toolbox.util.ui.plaf;

import java.awt.Font;

import javax.swing.UIManager;

import com.incors.plaf.alloy.AlloyLookAndFeel;
import com.incors.plaf.alloy.AlloyTheme;
import com.incors.plaf.alloy.DefaultAlloyTheme;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;

/**
 * Action that activates the Alloy Look And Feel.
 */
public class ActivateAlloyLookAndFeelAction extends ActivateLookAndFeelAction
{
    private static final Logger logger_ =
        Logger.getLogger(ActivateAlloyLookAndFeelAction.class);

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a ActivateAlloyLookAndFeelAction.
     */
    public ActivateAlloyLookAndFeelAction()
    {
    }
    
    //--------------------------------------------------------------------------
    // Overrides ActivateLookAndFeelAction
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plaf.ActivateLookAndFeelAction#setLookAndFeelInfo(
     *      toolbox.util.ui.plaf.LAFInfo)
     */
    public void setLookAndFeelInfo(LAFInfo lookAndFeelInfo)
    {
        super.setLookAndFeelInfo(lookAndFeelInfo);
        setName(lookAndFeelInfo.getProperty("theme.name"));
    }
    
    
    /**
     * @see toolbox.util.ui.plaf.ActivateLookAndFeelAction#activate()
     */
    public void activate() throws Exception
    {
        LAFInfo info = getLookAndFeelInfo();
        //String name = info.getProperty("theme.name"); 
        String clazz = info.getProperty("theme.class");
        String license = info.getProperty("theme.license");
      
        AlloyLookAndFeel.setProperty("alloy.licenseCode", license);
        AlloyTheme defaultTheme = new DefaultAlloyTheme();
        AlloyTheme theme = defaultTheme;
        
        if (!StringUtil.isNullOrEmpty(clazz))
        {
            try
            {
                theme = (AlloyTheme) Class.forName(clazz).newInstance();
            }
            catch (Exception e)
            {
                logger_.warn("Error setting Alloy theme: " + info, e);
                theme = defaultTheme;
            }
        }
        
        AlloyLookAndFeel alloy = new AlloyLookAndFeel(theme);
        UIManager.setLookAndFeel(alloy);

        // Fix alloy fonts
        
        Font f = new Font("Tahoma", Font.PLAIN, 11);
        //Font f = new Font("Bitstream Vera Sans", Font.PLAIN, 11);
        //Font f = new Font("Trebuchet MS", Font.PLAIN, 11);
        //Font f = new Font("Lucida Sans", Font.PLAIN, 11);
        //Font f = new Font("Lucida Sans Typewriter", Font.PLAIN, 10);
        
        //Font f = FontUtil.getPreferredSerifFont();
        //Font f = FontUtil.getPreferredMonoFont();
        
        UIManager.put("Button.font", f);
        UIManager.put("DesktopIcon.font", f);
        UIManager.put("ComboBox.font", f);
        UIManager.put("CheckBox.font", f);
        UIManager.put("CheckBoxMenuItem.font", f);
        UIManager.put("ColorChooser.font", f);
        UIManager.put("EditorPane.font", f);
        UIManager.put("FormattedTextField.font", f);
        UIManager.put("InternalFrame.titleFont", f);
        UIManager.put("Label.font", f);
        UIManager.put("List.font", f);
        UIManager.put("Menu.font", f);
        UIManager.put("MenuBar.font", f);
        UIManager.put("MenuItem.font", f);
        UIManager.put("MenuItem.acceleratorFont", f);
        UIManager.put("OptionPane.font", f);
        UIManager.put("Panel.font", f);
        UIManager.put("PasswordField.font", f);
        UIManager.put("PopupMenu.font", f);
        UIManager.put("ProgressBar.font", f);
        UIManager.put("RadioButton.font", f);
        UIManager.put("RadioButtonMenuItem.font", f);
        UIManager.put("RadioButtonMenuItem.acceleratorFont", f);
        UIManager.put("RootPane.titleFont", f);
        UIManager.put("ScrollPane.font", f);
        UIManager.put("Spinner.font", f);
        UIManager.put("TabbedPane.font", f);
        UIManager.put("Table.font", f);
        UIManager.put("TableHeader.font", f);
        UIManager.put("TextArea.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("TextPane.font", f);
        UIManager.put("TitledBorder.font", f);
        UIManager.put("ToggleButton.font", f);
        UIManager.put("Toolbar.font", f);
        UIManager.put("ToolTip.font", f);
        UIManager.put("Tree.font", f);
        UIManager.put("Viewport.font", f);
        
        //UIManager.put("ToolTip.border", new LineBorder(Color.black));
        
        //UIManager.put(
        //    "ToolTip.background", UIManager.getColor("Panel.background"));
        
        UIManager.getLookAndFeel().getDefaults().put(
            LAFInfo.PROP_HIDDEN_KEY, getLookAndFeelInfo());
        
        LookAndFeelUtil.propagateChangeInLAF();
    }
}