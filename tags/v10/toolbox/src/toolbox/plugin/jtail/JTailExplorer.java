package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import org.apache.log4j.Category;
import toolbox.util.ui.JFileExplorer;

/**
 * Custom explorer panel for JTail window
 */
public class JTailExplorer extends JInternalFrame implements ActionListener
{
	private static final Category logger =
		Category.getInstance(JTailExplorer.class);
		
	private JFileExplorer jfe;
	private JButton tailButton;
		
	/**
	 * Default constructor
	 */
	public JTailExplorer()
	{
		super("File Explorer", true, true, true, true);
		build();	
	}
	
	/**
	 * Builds the GUI
	 */
	protected void build()
	{
		getContentPane().setLayout(new BorderLayout());
		jfe = new JFileExplorer();
		getContentPane().add(jfe, BorderLayout.CENTER);
	
		JPanel buttonPanel = new JPanel(new FlowLayout());
		tailButton = new JButton("Tail");
		tailButton.addActionListener(this);
		buttonPanel.add(tailButton);
		
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * ActionListener interface
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();
		
		if(obj == tailButton)
			tailButtonClicked();
		else
			logger.warn("No handler for " + e);
	}
	
	/**
	 * Tail button 
	 */
	protected void tailButtonClicked()
	{
		logger.info("tail");
	}
}
