package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

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
    private JTextField     filterField_;
    
    private BlockingQueue    queue_;
    private TailQueueReader  queueReader_;
    
    /** Output for tail **/
	private JSmartTextArea tailArea_;

    /** The tailer **/    
    private Tail tail_;

    /** Configuration **/
    private TailConfig config_;
    
    
    /** 
     * Creates a TAilPane with the given configuration
     * 
     * @param  config  TailConfig
     */
    public TailPane(TailConfig config) throws FileNotFoundException
    {
        buildView();        
        setConfiguration(config);                
        init();
    }
    
    
    /**
     * Init tail
     * 
     * @param  file  File to tail
     */
    protected void init() throws FileNotFoundException
    {
        // Setup queue
        queue_ = new BlockingQueue();
        queueReader_ = new TailQueueReader(queue_);
        Thread consumer = new Thread(queueReader_);
        consumer.start();
        
        // Setup tail
        tail_ = new Tail();
        tail_.addTailListener(new TailListener());
        tail_.setTailFile(config_.getFilename());
        startButton_.getAction().actionPerformed(new ActionEvent(this, 0, "Start"));
        //tail_.start();
        
        logger_.debug("tail and reader started");
    }
    
	/**
	 * Builds the GUI
	 */	
	protected void buildView()
	{
		tailArea_ = new JSmartTextArea("");
        tailArea_.setFont(SwingUtil.getPreferredMonoFont());
        tailArea_.setDoubleBuffered(false);
        
		clearButton_    = new JButton(new ClearAction());
		pauseButton_    = new JButton(new PauseUnpauseAction());
		startButton_    = new JButton(new StartStopAction());
        closeButton_    = new JButton(new CloseAction());
        autoScrollBox_  = new JCheckBox("Autoscroll");
        lineNumbersBox_ = new JCheckBox("Line Numbers");
        filterField_    = new JTextField(12);
        
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(startButton_);
		buttonPanel.add(pauseButton_);
		buttonPanel.add(clearButton_);
        buttonPanel.add(closeButton_);
        buttonPanel.add(autoScrollBox_);
        buttonPanel.add(lineNumbersBox_);
        buttonPanel.add(new JLabel("Filter"));
        buttonPanel.add(filterField_);
        

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
            if (obj == autoScrollBox_)
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


    /**
     * Sets the configuration
     * 
     * @param  config  Tail configuration
     */
    public void setConfiguration(TailConfig config)
    {
        config_ = config;

        autoScrollBox_.setSelected(config_.isAutoScroll());
        tailArea_.setAutoScroll(config_.isAutoScroll());
        lineNumbersBox_.setSelected(config_.isShowLineNumbers());
        tailArea_.setFont(config_.getFont());
    }


    /**
     * Gets the configuration
     * 
     * @return TailConfig
     */
    public TailConfig getConfiguration()
    {
        // Make sure configuration up to date
        config_.setAutoScroll(autoScrollBox_.isSelected());
        config_.setShowLineNumbers(lineNumbersBox_.isSelected());
        config_.setFont(tailArea_.getFont());
        return config_;
    }    
 
 
    /**
     * @return  Font for the tail output area
     */   
    public Font getTailFont()
    {
        return tailArea_.getFont();
    }
 
    
    /**
     * Sets the tail output area's font
     * 
     * @param  font  Font to set for tail output
     */
    public void setTailFont(Font font)
    {
        tailArea_.setFont(font);
        config_.setFont(font);
    }
    
    
    /**
     * @return Filter text
     */
    public String getFilter()
    {
        return filterField_.getText().trim();
    }
    
    
    /**
     * Sets the filter text
     * 
     * @param  filter  Filter text as a regular expression
     */
    public void setFilter(String filter)
    {
        filterField_.setText(filter);
    }


    /**
     * Listener for tail
     */
    class TailListener extends TailAdapter
    {
        /**
         * Called when next line of input is available. When a new line is
         * available, just push it on the shared queue.
         * 
         * @param  line  Next line read
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
    }

    
    /**
     * Pops groups of messages off the queue (as many as can be read without 
     * waiting) and consolidates before sending then to the textarea
     */
    private class TailQueueReader extends BatchQueueReader
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
            if (objs.length > 1)
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
     * Starts/stops the tail
     */
    private class StartStopAction extends AbstractAction
    {
        private static final String MODE_START = "Start";
        private static final String MODE_STOP  = "Stop";
        
        private String mode = MODE_START;
            
        /**
         * Default constructor
         */
        public StartStopAction()
        {
            super(MODE_START);
            putValue(MNEMONIC_KEY, new Integer('S'));
            putValue(SHORT_DESCRIPTION, "Starts/Stops the tail");
        }
    
        /**
         * Stops the tail if it is alraedy alive and then starts the tail
         * 
         * @param  e    ActionEvent
         */
        public void actionPerformed(ActionEvent e)
        { 
            if (mode.equals(MODE_START))
            {
                try
                {
                    tail_.start();
                    mode = MODE_STOP;
                    putValue(Action.NAME, mode);
                    pauseButton_.setEnabled(true);
                    queueReader_.resetLines();
                    logger_.debug("Started tail: " + tail_.getFile());                                                     
                }
                catch(FileNotFoundException fnfe)
                {
                    logger_.error(e);
                }
            }
            else
            {
                tail_.stop();
                mode = MODE_START;
                putValue(Action.NAME, mode);                
                pauseButton_.setEnabled(false);
                logger_.debug("Stopped tail: " + tail_.getFile());                
            }
        }
    }

    /**
     * Pauses/unpauses the tail
     */
    private class PauseUnpauseAction extends AbstractAction
    {
        private static final String MODE_PAUSE   = "Pause";
        private static final String MODE_UNPAUSE = "Unpause";
            
        /**
         * Default constructor
         */
        public PauseUnpauseAction()
        {
            super(MODE_PAUSE);
            putValue(MNEMONIC_KEY, new Integer('P'));
            putValue(SHORT_DESCRIPTION, "Pause/Unpauses the tail");
        }
    
        /**
         * Pauses/unpauses the tail based on the current state
         * 
         * @param  e    ActionEvent
         */
        public void actionPerformed(ActionEvent e)
        { 
            if (tail_.isPaused())
            {
                tail_.unpause();
                putValue(Action.NAME, MODE_PAUSE);                
            }
            else
            {
                tail_.pause();
                putValue(Action.NAME, MODE_UNPAUSE);                                
            }
        }
    }
    

    /**
     * Closes the tail pane
     */
    private class CloseAction extends AbstractAction
    {
        /**
         * Default constructor
         */
        public CloseAction()
        {
            super("Close");
            putValue(MNEMONIC_KEY, new Integer('C'));
            putValue(SHORT_DESCRIPTION, "Closes the tail pane");
        }
    
        /**
         * Closes the tail pane
         * 
         * @param  e    ActionEvent
         */
        public void actionPerformed(ActionEvent e)
        { 
            if (tail_.isPaused())
                tail_.unpause();
                
            if (tail_.isAlive())
                tail_.stop();
                
            queueReader_.shutdown();
        }
    }

    /**
     * Clears the output area
     */
    private class ClearAction extends AbstractAction
    {
        /**
         * Default constructor
         */
        public ClearAction()
        {
            super("Clear");
            putValue(MNEMONIC_KEY, new Integer('r'));
            putValue(SHORT_DESCRIPTION, "Clears the output area");
        }
    
        /**
         * Clears the output area
         * 
         * @param  e    ActionEvent
         */
        public void actionPerformed(ActionEvent e)
        { 
            tailArea_.setText("");
        }
    }
}