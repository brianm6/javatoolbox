package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import toolbox.jtail.config.*;
import toolbox.tail.Tail;
import toolbox.tail.TailAdapter;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.concurrent.BatchQueueReader;
import toolbox.util.concurrent.BlockingQueue;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JSmartTextArea;

/**
 * Tail pane
 */
public class TailPane extends JPanel
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
    private ITailPaneConfig config_;
    
    /** Dirty filter flag **/
    private boolean filterDirty_;
    
        
    /** 
     * Creates a TAilPane with the given configuration
     * 
     * @param   config  TailConfig
     * @throws  FileNotFoundException
     */
    public TailPane(ITailPaneConfig config) throws FileNotFoundException
    {
        buildView();        
        setConfiguration(config);                
        init();
    }
    
    
    //
    //  MEATY STUFF
    //
    
    
    /**
     * Initializes the tail
     * 
     * @param  file  File to tail
     * @throws FileNotFoundException
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

        // Start tail through action so button states are OK        
        startButton_.getAction().actionPerformed(
            new ActionEvent(this, 0, "Start"));
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
        autoScrollBox_  = new JCheckBox(new AutoScrollAction());
        lineNumbersBox_ = new JCheckBox(new ShowLineNumbersAction());
        filterField_    = new JTextField(10);
        
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(startButton_);
		buttonPanel.add(pauseButton_);
		buttonPanel.add(clearButton_);
        buttonPanel.add(closeButton_);
        buttonPanel.add(autoScrollBox_);
        buttonPanel.add(lineNumbersBox_);
        buttonPanel.add(new JLabel("Filter"));
        buttonPanel.add(filterField_);
		
		setLayout(new BorderLayout());
		add(new JScrollPane(tailArea_), BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
 

    /**
     * Sets the configuration
     * 
     * @param  config  Tail configuration
     */
    public void setConfiguration(ITailPaneConfig config)
    {
        config_ = config;

        autoScrollBox_.setSelected(config_.isAutoScroll());
        tailArea_.setAutoScroll(config_.isAutoScroll());
        lineNumbersBox_.setSelected(config_.isShowLineNumbers());
        tailArea_.setFont(config_.getFont());
        setFilter(config_.getFilter());
    }


    /**
     * Gets the configuration
     * 
     * @return TailConfig
     */
    public ITailPaneConfig getConfiguration()
    {
        // Make sure configuration up to date
        config_.setAutoScroll(autoScrollBox_.isSelected());
        config_.setShowLineNumbers(lineNumbersBox_.isSelected());
        config_.setFont(tailArea_.getFont());
        config_.setFilter(getFilter());
        return config_;
    }    


    //
    //  ACCESSORS/MUTATORS
    //

    
    /**
     * @return Close button
     */
    public JButton getCloseButton()
    {
        return closeButton_;
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


    //
    //  INNER CLASSES
    //

    
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
        RE regExp_;
        String oldFilter = "";
        
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
         * @see toolbox.util.concurrent.BatchQueueReader#execute()
         */
        public void execute(Object[] objs)
        {
            if (objs.length > 1)
                logger_.debug("Lines popped: " + objs.length);
            
            StringBuffer sb = new StringBuffer();
            String filter = filterField_.getText().trim();   
            boolean byPassFilter = StringUtil.isNullOrEmpty(filter);
            
            if (!byPassFilter)
            {
                if(!filter.equals(oldFilter))
                {
                    try
                    {
                        regExp_ = new RE(filter);
                    }
                    catch (RESyntaxException re)
                    {
                        JSmartOptionPane.showExceptionMessageDialog(null, re);
                    }
                
                    oldFilter = filter;   
                }
            }


            // Iterate over each line delivered            
            for(int i=0; i<objs.length; i++)
            {
                String line = (String)objs[i];

                // Filter was detected, apply to line and return (skip)
                // if match found
                if ((!byPassFilter) && regExp_.match(line))
                {
                    // skip
                }    
                else
                {
                    lineNumber_ += 1;
                    
                    // Decorate with line number if checked
                    if (lineNumbersBox_.isSelected())
                        sb.append("[" + lineNumber_ + "] ");
                        
                    sb.append(objs[i] + "\n");
                }
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

   
    //
    //  GUI ACTIONS
    //
    
    
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
            super("Closez");
            putValue(MNEMONIC_KEY, new Integer('z'));
            putValue(SHORT_DESCRIPTION, "Closes the tail pane");
//            putValue(ACCELERATOR_KEY,
//                 KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0));            
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

    
    /**
     * Toggles autoscroll of the output text area
     */
    private class AutoScrollAction extends AbstractAction
    {
        /**
         * Default constructor
         */
        public AutoScrollAction()
        {
            super("Autoscroll");
            putValue(MNEMONIC_KEY, new Integer('a'));
            putValue(SHORT_DESCRIPTION, "Toggles autoscroll");
        }
    
        /**
         * Toggle autoscroll
         * 
         * @param  e    ActionEvent
         */
        public void actionPerformed(ActionEvent e)
        { 
            tailArea_.setAutoScroll(autoScrollBox_.isSelected()); 
        }
    }


    /**
     * Toggles line numbers in the output area
     */
    private class ShowLineNumbersAction extends AbstractAction
    {
        /**
         * Default constructor
         */
        public ShowLineNumbersAction()
        {
            super("Line numbers");
            putValue(MNEMONIC_KEY, new Integer('L'));
            putValue(SHORT_DESCRIPTION, 
                "Toggles display of line numbers in the output");
        }
    
        /**
         * Toggle line numbers
         * 
         * @param  e    ActionEvent
         */
        public void actionPerformed(ActionEvent e)
        { 
            // no op since the checkbox is queried directly 
            // for its state            
        }
    }
    
    //
    //  INTERFACES
    //
    
    public interface ITailPaneListener
    {
        public void newDataAvailable(TailPane tailPane);
    }
}