package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.apache.log4j.Category;

/**
 * 
 */
public class TailPanel extends JPanel implements ActionListener
{
	private static final Category logger = 
		Category.getInstance(TailPanel.class);
	
	private JButton clearButton;
	private JButton pauseButton;
	private JButton runButton;
	private JTextArea tailArea;

	/**
	 * Constructor for TailPanel.
	 */
	public TailPanel()
	{
		build();
	}

	/**
	 * Builds the GUI
	 */	
	protected void build()
	{
		tailArea = new JTextArea();
		clearButton = new JButton("Clear");
		pauseButton = new JButton("Pause");
		runButton = new JButton("Run");
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(runButton);
		buttonPanel.add(pauseButton);
		buttonPanel.add(clearButton);
		
		runButton.addActionListener(this);
		pauseButton.addActionListener(this);
		clearButton.addActionListener(this);
		
		setLayout(new BorderLayout());
		add(tailArea, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * ActionListner interface
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();
		
		if(obj == runButton)
			runButtonClicked();
		else if(obj == pauseButton)
			pauseButtonClicked();
		else if(obj == clearButton)
			clearButtonClicked();
		else
			logger.warn("No action handler for " + e);
	}
	
	/**
	 * Run
	 */
	protected void runButtonClicked()
	{
		logger.info("run");
	}
	
	/**
	 * Pause
	 */
	protected void pauseButtonClicked()
	{
		logger.info("pause");
	}
	
	/**
	 * Clear
	 */
	protected void clearButtonClicked()
	{
		logger.info("clear");
	}	
}
