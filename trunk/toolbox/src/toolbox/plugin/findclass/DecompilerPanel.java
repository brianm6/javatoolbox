package toolbox.findclass;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.jedit.syntax.JavaTokenMarker;
import org.jedit.syntax.TextAreaDefaults;

import toolbox.jedit.JEditPopupMenu;
import toolbox.jedit.JEditTextArea;
import toolbox.jedit.JavaDefaults;
import toolbox.util.ClassUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.StreamUtil;
import toolbox.util.decompiler.Decompiler;
import toolbox.util.decompiler.DecompilerException;
import toolbox.util.decompiler.DecompilerFactory;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartComboBox;
import toolbox.util.ui.tabbedpane.JSmartTabbedPane;
import toolbox.workspace.WorkspaceAction;

/**
 * Decompiler Panel
 */
public class DecompilerPanel extends JPanel
{
	private JSmartComboBox decompilerCombo_;
	private JSmartTabbedPane tabbedPane_;
	private JTable resultTable_;

    private static final Logger logger_ = 
		Logger.getLogger(DecompilerPanel.class);
		
			
	public DecompilerPanel(JTable resultTable)
	{
		resultTable_ = resultTable;
		buildView();
	}
	

	private void addTab (String source)
	{
		TextAreaDefaults defaults = new JavaDefaults();
        
		JEditTextArea sourceArea = new JEditTextArea(
			new JavaTokenMarker(), defaults);

		// Hack for circular reference in popup menu            
		((JEditPopupMenu) defaults.popup).setTextArea(sourceArea);
		((JEditPopupMenu) defaults.popup).buildView();
		
		sourceArea.setText(source);
		sourceArea.setCaretPosition(0);
		
		tabbedPane_.add(sourceArea);
	}

    /**
     * Builds the GUI
     */
    private void buildView()
    {
		Decompiler[] decompilers = null;
        
		try
		{
			decompilers = DecompilerFactory.createAll(); 
		}
		catch (DecompilerException de)
		{
			ExceptionUtil.handleUI(de, logger_);
		}

		decompilerCombo_ = new JSmartComboBox(decompilers);
		JButton decompileButton = new JSmartButton(new DecompileAction());
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(decompilerCombo_);
		buttonPanel.add(decompileButton);

        
        tabbedPane_ = new JSmartTabbedPane();
        
		add(tabbedPane_, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		//decompilerPanel.setPreferredSize(new Dimension(100, 400));
    }
    
	//--------------------------------------------------------------------------
	// DecompileAction
	//--------------------------------------------------------------------------
        
	/**
	 * Action to decompile the currently selected class file.
	 */
	class DecompileAction extends WorkspaceAction
	{
		DecompileAction()
		{
			super("Decompile", true, null, null /*statusBar_*/);
			putValue(MNEMONIC_KEY, new Integer('D'));    
			putValue(SHORT_DESCRIPTION, "Decompiles the selected class");
		}
        
        
		/**
		 * @see toolbox.util.ui.SmartAction#runAction(
		 *      java.awt.event.ActionEvent)
		 */
		public void runAction(ActionEvent e) throws Exception
		{
			
			int idx = resultTable_.getSelectedRow();
            
			if (idx >= 0)
			{
				// Jar or directory path
				String location = (String) 
					resultTable_.getModel().getValueAt(
						idx, ResultsTableModel.COL_SOURCE); 
                    
				// FQN of class
				String clazz  = (String) 
					resultTable_.getModel().getValueAt(
						idx, ResultsTableModel.COL_CLASS);
            
				location = location.trim();
				clazz  = clazz.trim();
                
				Decompiler d = (Decompiler) decompilerCombo_.getSelectedItem();

				String source = null;
                
				try
				{
					source = d.decompile(clazz, location);
				}
				catch (IllegalArgumentException iae)
				{
					if (ClassUtil.isArchive(location))
					{   
						// Build up a jar url pointing to the class 
						// file inside the jar
						StringBuffer sb = new StringBuffer();
						sb.append("jar:file:");
						sb.append(new File(location).getCanonicalPath());
						sb.append("!/");
						sb.append(clazz.replace('.','/'));
						sb.append(".class");
                    
						//jar:file:/c:/almanac/my.jar!/com/mycompany/MyClass.class"
                    
						logger_.debug("JAR URL=" + sb);
                    
						// TODO: Also extract all anonymous/innerclasses
                        
						URL url = new URL(sb.toString());
                        
						logger_.debug("URL as file=" + url.getFile());
						logger_.debug("Class file len=" + 
							url.openConnection().getContentLength());
                    
						// Extract the class file as an array of bytes
						byte[] bytecode = StreamUtil.toBytes(
							url.openConnection().getInputStream());
                    
						// Write out class file to the temp dir on disk 
						// (least common denominator so that all decompilers 
						// can get to it.
						File tempClassFile = 
							new File(FileUtil.getTempDir(), 
								ClassUtil.stripPackage(clazz) + ".class");
                        
						FileUtil.setFileContents(
							tempClassFile.getCanonicalPath(), bytecode, false);
                    
						// Do some decompiling...
						source = d.decompile(tempClassFile);
                        
						// Cleanup
						FileUtil.delete(tempClassFile.getCanonicalPath());
					}
					else
						source = "Not supported";
				}

				addTab(source);
			}           
		}
	}

}
