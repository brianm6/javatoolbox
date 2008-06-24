package toolbox.plugin.jtail.filter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.StringReader;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;

import nu.xom.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import org.codehaus.janino.SimpleCompiler;

import org.jedit.syntax.JavaTokenMarker;

import toolbox.jedit.JEditTextArea;
import toolbox.jedit.JavaDefaults;
import toolbox.plugin.jtail.JTail;
import toolbox.util.FontUtil;
import toolbox.util.XOMUtil;
import toolbox.util.formatter.JavaFormatter;
import toolbox.util.io.StringInputStream;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.SmartAction;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PreferencedException;

/**
 * UI Component that allows the user to enter a dynamic filter as java code.
 */
public class DynamicFilterView extends JHeaderPanel implements IPreferenced
{
    private static final Logger logger_ = 
        Logger.getLogger(DynamicFilterView.class);

    //--------------------------------------------------------------------------
    // Icons
    //--------------------------------------------------------------------------
    
    /**
     * Icon for header and flipper.
     */
    public static final Icon ICON = ImageCache.getIcon(ImageCache.IMAGE_FUNNEL);
    
    /**
     * Label associated with this view.
     */
    public static final String LABEL = "Dynamic Filters";

    /**
     * Preferences Node.
     */
    public static final String NODE_DYNAMIC_FILTER_VIEW = "DynamicFilterView";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * JTail plugin.
     */
    private JTail plugin_;
    
    /**
     * Editor for a filter's source code.
     */
    private JEditTextArea sourceArea_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DynamicFilterView
     * 
     * @param plugin JTail
     */
    public DynamicFilterView(JTail plugin)
    {
        super(ICON, LABEL);
        buildView();
        setPlugin(plugin);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the plugin.
     * 
     * @param plugin JTail plugin.
     */
    public void setPlugin(JTail plugin)
    {
        plugin_ = plugin;
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        sourceArea_ = new JEditTextArea(new JavaTokenMarker(), new JavaDefaults());
        sourceArea_.setSaveContents(true);
        sourceArea_.setTabSize(4);
        sourceArea_.setFont(FontUtil.getPreferredMonoFont());
        
        JPanel view = new JPanel(new BorderLayout());
        view.add(sourceArea_, BorderLayout.CENTER);
        view.add(new JSmartButton(new CompileAction()), BorderLayout.SOUTH);
        setContent(view);
        
        JButton formatButton = createButton(
            ImageCache.getIcon(ImageCache.IMAGE_BRACES),
            "Formats the source code",
            new FormatAction());
        
        JToolBar toolbar = createToolBar();
        toolbar.add(formatButton);
        setToolBar(toolbar);
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root = XOMUtil.getFirstChildElement(prefs, 
            NODE_DYNAMIC_FILTER_VIEW, new Element(NODE_DYNAMIC_FILTER_VIEW));
        
        sourceArea_.applyPrefs(root);
        
        // Fill with default code template if nothing there
        if (StringUtils.isBlank(sourceArea_.getText()))
        {
        	String template = 
        		"import org.apache.commons.lang.*;\n"
        		+ "\n"
        		+ "import toolbox.plugin.jtail.filter.*;\n"
        		+ "\n"
        		+ "\n"
        		+ "public class MyLineFilter extends AbstractLineFilter {\n"
        		+ "    public boolean filter(StringBuffer line) {\n"
        		+ "        if (isEnabled() && (line != null)) {\n"
        		+ "            line.insert(0, \"(\");\n"
        		+ "            line.append(\")\");\n"
        		+ "        }\n"
        		+ "\n"
        		+ "        return true;\n"
        		+ "    }\n"
        		+ "\n"
        		+ "    boolean filter(StringBuffer line, java.util.List segments) {\n"
        		+ "        return filter(line);\n"
        		+ "    }\n"
        		+ "}\n";
        	
        	sourceArea_.setText(template);
        }
    }

    
    /*
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_DYNAMIC_FILTER_VIEW);
        sourceArea_.savePrefs(root);
        XOMUtil.insertOrReplace(prefs, root);
    }

    //--------------------------------------------------------------------------
    // FormatAction
    //--------------------------------------------------------------------------

    /**
     * Formats the source code.
     */
    class FormatAction extends SmartAction
    {
        public FormatAction()
        {
            super("Format", true, false, null);
        }
        
        /*
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
           sourceArea_.setText(new JavaFormatter().format(sourceArea_.getText()));
        }
    }
    
    //--------------------------------------------------------------------------
    // CompileAction
    //--------------------------------------------------------------------------
    
    /**
     * Compiles the source code to an ILineFilter.
     */
    class CompileAction extends SmartAction
    {
        public CompileAction()
        {
            super("Compile", true, false, null);
        }
        
        /*
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            String sourceCode = sourceArea_.getText();
            
            //
            // Extract the classname using QDox
            //
            
            JavaDocBuilder builder = new JavaDocBuilder();
            builder.addSource(new StringReader(sourceCode));
            JavaClass[] classes = builder.getClasses();
  
            Validate.isTrue(
                classes.length == 1, 
                "One class must be defined in the source");
            
            String className = classes[0].getFullyQualifiedName();
            
            //
            // Compile to bytecode using Janino
            //
            
            SimpleCompiler compiler = new SimpleCompiler(
                className, new StringInputStream(sourceCode));
            
            Class filterClass = compiler.getClassLoader().loadClass(className);
            
            logger_.debug("Name = " + filterClass.getName());
            ILineFilter filter = (ILineFilter) filterClass.newInstance();
            filter.setEnabled(true);
            
            StringBuffer crap = new StringBuffer("hello");
            filter.filter(crap);
            logger_.debug(crap);
        }
    }
}


/*
====================================
    EXAMPLE FILTER
====================================

import org.apache.commons.lang.*;
import toolbox.plugin.jtail.filter.*;

public class MyLineFilter 
	extends AbstractLineFilter {

    public boolean filter(StringBuffer line) {
		if (isEnabled() && line != null) {		
	        line.insert(0,"(");
	        line.append(")");
	    }
		return true;
    }

    boolean filter(StringBuffer line, java.util.List segments) {
        return filter(line);
    }
}
*/