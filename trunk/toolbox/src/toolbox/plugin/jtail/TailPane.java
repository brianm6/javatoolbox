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
    private JCheckBox      autoScrollBox_;
    
    private BlockingQueue    queue_;
    private BatchQueueReader queueReader_;
    
    /** Output for tail **/
	private JSmartTextArea tailArea_;

    /** The tailer **/    
    private Tail tail_;


	/**
	 * Constructor for TailPanel.
     * 
     * @param  file  File to tail
	 */
	public TailPane(File file) throws FileNotFoundException
	{
		build();
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
        
        queueReader_ = new FastQueueConsumer(queue_);
        Thread consumer = new Thread(queueReader_);
        consumer.start();
        
        logger_.debug("tail and reader started");
    }


    /**
     * Pops thingies off the queue
     */
    class FastQueueConsumer extends BatchQueueReader
    {
        int lineNumber = 0;
        
        public FastQueueConsumer(BlockingQueue queue)
        {
            super(queue);
        }
        
        /**
         * @see toolbox.util.concurrent.BatchQueueReader#execute(Object[])
         */
        public void execute(Object[] objs)
        {
            logger_.debug("Lines popped: " + objs.length);
            
            StringBuffer sb = new StringBuffer();

            for(int i=0; i<objs.length; i++)
                sb.append(lineNumber++ + ": " + objs[i] + "\n");
            
            tailArea_.append(sb.toString()); 
        }
    }
    
    
    /**
     * Listener for tail
     */
    class TailListener extends TailAdapter
    {
        /**
         * Called when next line of input is available
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
         * @see toolbox.tail.TailAdapter#tailPaused()
         */
        public void tailPaused()
        {
            pauseButton_.setText("Unpause");
        }


        /**
         * @see toolbox.tail.TailAdapter#tailUnpaused()
         */
        public void tailUnpaused()
        {
            pauseButton_.setText("Pause");            
        }


        /**
         * @see toolbox.tail.TailAdapter#tailStarted()
         */
        public void tailStarted()
        {
            startButton_.setText("Stop");
            pauseButton_.setEnabled(true);
        }


        /**
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
	protected void build()
	{
		tailArea_ = new JSmartTextArea(false);
        tailArea_.setFont(SwingUtil.getPreferredMonoFont());
        
		clearButton_   = new JButton("Clear");
		pauseButton_   = new JButton("Pause");
		startButton_     = new JButton("Start");
        autoScrollBox_ = new JCheckBox("Autoscroll");
        
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(startButton_);
		buttonPanel.add(pauseButton_);
		buttonPanel.add(clearButton_);
        buttonPanel.add(autoScrollBox_);

		startButton_.addActionListener(this);
		pauseButton_.addActionListener(this);
		clearButton_.addActionListener(this);
        autoScrollBox_.addActionListener(this);
		
		setLayout(new BorderLayout());
		add(new JScrollPane(tailArea_), BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
    
	/**
	 * ActionListner interface
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
            else if (obj == autoScrollBox_)
                autoScrollBoxChanged();
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
     * Sets autoscroll on text area
     */
    protected void autoScrollBoxChanged()
    {
        tailArea_.setAutoScroll(autoScrollBox_.isSelected());
    }
}