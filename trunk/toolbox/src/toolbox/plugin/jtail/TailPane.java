package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Category;
import toolbox.tail.Tail;
import toolbox.tail.TailAdapter;
import toolbox.util.SwingUtil;
import toolbox.util.concurrent.BatchQueueReader;
import toolbox.util.concurrent.BlockingQueue;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JSmartTextArea;

/**
 * Tail pane
 */
public class TailPane extends JPanel implements ActionListener
{
    /** Logger **/
	private static final Category logger_ = 
		Category.getInstance(TailPane.class);
	
	private JButton        clearButton_;
	private JButton        pauseButton_;
	private JButton        startButton_;
    private JButton        closeButton_;
    private JCheckBox      autoScrollBox_;
    private JCheckBox      lineNumbersBox_;
    
    private BlockingQueue    queue_;
    private TailQueueReader  queueReader_;
    
    /** Output for tail **/
	private JSmartTextArea tailArea_;

    /** The tailer **/    
    private Tail tail_;


	/**
	 * Creates a TailPane with the given file. Autoscroll and line numbers
     * are turned on by default.
     * 
     * @param  file  File to tail
	 */
	public TailPane(File file) throws FileNotFoundException
	{
        this(file, true, true);
	}


    /**
     * Creates a TailPane with the given settings
     * 
     * @param  file             File to tail
     * @param  autoScroll       If true, autoscroll will be enabled
     * @param  showLineNumbers  If true, lines numbers will be shown
     */
    public TailPane(File file, boolean autoScroll, boolean showLineNumbers)
        throws FileNotFoundException
    {
        buildView(autoScroll, showLineNumbers);
        init(file);
    }
    
    
    /**
     * Init tail
     * 
     * @param  file  File to tail
     */
    protected void init(File file) throws FileNotFoundException
    {
        queue_ = new BlockingQueue();
        
        tail_ = new Tail();
        tail_.addTailListener(new TailListener());
        tail_.setTailFile(file);
        tail_.start();
        
        queueReader_ = new TailQueueReader(queue_);
        Thread consumer = new Thread(queueReader_);
        consumer.start();
        
        logger_.debug("tail and reader started");
    }


    /**
     * Pops groups of messages off the queue (as many as can be read without 
     * waiting) and consolidates before sending then to the textarea
     */
    class TailQueueReader extends BatchQueueReader
    {
        int lineNumber_ = 0;

        /**
         * Creates a queue consumer for the given queue
         * 
         * @param  queue  Queue to consume messages from
         */        
        public TailQueueReader(BlockingQueue queue)
        {
            super(queue);
        }

        
        /**
         * Adds contents of queue to the output
         *
         * @see toolbox.util.concurrent.BatchQueueReader#execute(Object[])
         */
        public void execute(Object[] objs)
        {
            logger_.debug("Lines popped: " + objs.length);
            
            StringBuffer sb = new StringBuffer();

            for(int i=0; i<objs.length; i++)
            {
                lineNumber_++;
                if (lineNumbersBox_.isSelected())
                    sb.append("[" + lineNumber_ + "] ");
                    
                sb.append(objs[i] + "\n");
                
  
            }
            
            tailArea_.append(sb.toString()); 
        }
        
        
        /**
         * Resets line number back to zero. This is done with the tail is
         * stopped and then restarted.
         */
        public void resetLines()
        {
            lineNumber_ = 0;
        }
    }
    
    
    /**
     * Listener for tail
     */
    class TailListener extends TailAdapter
    {
        /**
         * Called when next line of input is available. When a new line is
         * available, just push it on the shared queue.
         */
        public void nextLine(String line)
        {
            try
            {
                queue_.push(line);
            }
            catch (InterruptedException ie)
            {
                JSmartOptionPane.showExceptionMessageDialog(null, ie);
            }
        }

        
        /**
         * When the tail is paused, change the text of the button because
         * it has dual functionality
         * 
         * @see toolbox.tail.TailAdapter#tailPaused()
         */
        public void tailPaused()
        {
            pauseButton_.setText("Unpause");
        }


        /**
         * When the tail is unpaused, change the text of the button because
         * it has dual functionality
         *
         * @see toolbox.tail.TailAdapter#tailUnpaused()
         */
        public void tailUnpaused()
        {
            pauseButton_.setText("Pause");            
        }


        /**
         * When the tail is started, change button text to "stop" and enabled
         * the pause button
         * 
         * @see toolbox.tail.TailAdapter#tailStarted()
         */
        public void tailStarted()
        {
            startButton_.setText("Stop");
            pauseButton_.setEnabled(true);
            queueReader_.resetLines();
        }


        /**
         * When the tail is stopped, change the text to "start" and disable
         * the pause button because there is nothing to pause
         * 
         * @see toolbox.tail.TailAdapter#tailStopped()
         */
        public void tailStopped()
        {
            startButton_.setText("Start");
            pauseButton_.setEnabled(false);
        }
    }

    
	/**
	 * Builds the GUI
	 */	
	protected void buildView(boolean autoScroll, boolean showLineNumbers)
	{
		tailArea_ = new JSmartTextArea(autoScroll);
        tailArea_.setFont(SwingUtil.getPreferredMonoFont());
        tailArea_.setDoubleBuffered(false);
        
		clearButton_    = new JButton("Clear");
		pauseButton_    = new JButton("Pause");
		startButton_    = new JButton("Start");
        closeButton_    = new JButton("Close");
        autoScrollBox_  = new JCheckBox("Autoscroll", autoScroll);
        lineNumbersBox_ = new JCheckBox("Line Numbers", showLineNumbers);
        
        
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(startButton_);
		buttonPanel.add(pauseButton_);
		buttonPanel.add(clearButton_);
        buttonPanel.add(closeButton_);
        buttonPanel.add(autoScrollBox_);
        buttonPanel.add(lineNumbersBox_);

		startButton_.addActionListener(this);
		pauseButton_.addActionListener(this);
		clearButton_.addActionListener(this);
        closeButton_.addActionListener(this);
        autoScrollBox_.addActionListener(this);
        lineNumbersBox_.addActionListener(this);
		
		setLayout(new BorderLayout());
		add(new JScrollPane(tailArea_), BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
    
	/**
	 * Handles all widget button clicks and checkbox state changes
     * 
     * @param  e  ActionEvent
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();
		
        try
        {
    		if (obj == startButton_)
    			startButtonClicked();
    		else if (obj == pauseButton_)
    			pauseButtonClicked();
    		else if (obj == clearButton_)
    			clearButtonClicked();
            else if (obj == closeButton_)
                closeButtonClicked();
            else if (obj == autoScrollBox_)
                autoScrollBoxChanged();
            else if (obj == lineNumbersBox_)
                lineNumbersBoxChanged();
    		else        
    			logger_.warn("No action handler for " + e);
        }
        catch (Exception ee)
        {
            JSmartOptionPane.showExceptionMessageDialog(null, ee);
        }
	}
	
    
	/**
	 * Starts the tail
	 */
	protected void startButtonClicked() throws FileNotFoundException
	{
		logger_.info("start");
        
        if (tail_.isAlive())
            tail_.stop();
        else
            tail_.start();
	}
	
    
	/**
	 * Pauses the tail
	 */
	protected void pauseButtonClicked()
	{
		logger_.info("pause");
        
        if (tail_.isPaused())
            tail_.unpause();
        else
            tail_.pause();
	}
	
    
	/**
	 * Clears the tail output 
	 */
	protected void clearButtonClicked()
	{
		logger_.info("clear");
        tailArea_.setText("");
	}	


    /**
     * Close button clicked
     */
    protected void closeButtonClicked()
    {
        logger_.info("close");
        
        if (tail_.isPaused())
            tail_.unpause();
            
        if (tail_.isAlive())
            tail_.stop();
            
        firePaneClosing();            
    }   


    /** 
     * Spread closing event
     */
    protected void firePaneClosing()
    {
                
    }    

    
    /**
     * Called when the user has toggled the autoscroll checkbox
     */
    protected void autoScrollBoxChanged()
    {
        tailArea_.setAutoScroll(autoScrollBox_.isSelected());
    }

    
    /**
     * Called when the user has toggled the show line numbers checkbox
     */
    protected void lineNumbersBoxChanged()
    {
        // no op since the checkbox is queried directly for its state
    }
 
 
    /**
     * @return Close button
     */
    public JButton getCloseButton()
    {
        return closeButton_;
    }
    
}