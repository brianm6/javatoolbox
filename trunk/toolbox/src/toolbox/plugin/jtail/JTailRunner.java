package toolbox.jtail;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JTextArea;

/**
 */
public class JTailRunner extends JInternalFrame
{
	JButton clearButton;
	JButton pauseButton;
	JButton runButton;
	JTextArea tailArea;

	/**
	 * Constructor for JTailRunner.
	 * @param title
	 */
	public JTailRunner(String title)
	{
		super(title, true, true, true, true);
		build();
	}
	
	protected void build()
	{
		TailPanel tailPanel = new TailPanel();
		getContentPane().add(tailPanel, BorderLayout.CENTER);
	}
}
