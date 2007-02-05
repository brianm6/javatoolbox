package toolbox.plugin.jtail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.text.StyleConstants;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.output.NullWriter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.WriterAppender;
import org.apache.regexp.RESyntaxException;

import toolbox.plugin.jtail.config.ITailViewConfig;
import toolbox.plugin.jtail.filter.CutLineFilter;
import toolbox.plugin.jtail.filter.ILineFilter;
import toolbox.plugin.jtail.filter.LineNumberDecorator;
import toolbox.plugin.jtail.filter.RegexLineFilter;
import toolbox.plugin.jtail.filter.StyledSegment;
import toolbox.tail.Tail;
import toolbox.tail.TailAdapter;
import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.FontUtil;
import toolbox.util.concurrent.BatchingQueueReader;
import toolbox.util.concurrent.IBatchingQueueListener;
import toolbox.util.service.ServiceException;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.JSmartTextPane;
import toolbox.util.ui.JSmartToggleButton;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.action.NullAction;
import toolbox.util.ui.textarea.action.ClearAction;
import toolbox.workspace.IStatusBar;

/**
 * A UI component that serves as the view for the tailing one or more files to
 * a single output textarea.
 * <p>
 * Design: TailPane that aggregates the tail output from 3 separate files
 * <pre>
 *
 *                       (2)
 *          (1)          Tail         (3)
 *      TailListener   Listener  TailListener
 *                  \     |     /
 *                   \    |    /            pushes lines (originating from Tail)
 *                    \   |   /
 *                     v  v  v
 *                      Queue               (concentrator for our purposes)
 *                        ^
 *                        |
 *                        |                 pops lines one at a time
 *                        |
 *                BatchingQueueReader       (aggregates lines)
 *                        |
 *                        |
 *                        |                 provides block of lines to (batched)
 *                        |
 *                        v
 *                TailQueueListener
 *                        |
 *                        |
 *                        |                 append block of lines
 *                        |
 *                        v
 *                    TextArea
 *
 *</pre>
 */
public class TailPane extends JHeaderPanel
{
     // TODO: Color code keywords
     // TODO: Color code time lapse delays
     // TODO: Add option to tail the whole file from the beginning
     // TODO: Create filter that will accept a janino script
     // TODO: Renamed to TailView
     // TODO: Tie the state of the buttons directly to thte state of the tail

    private static final Logger logger_ = Logger.getLogger(TailPane.class);

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    /**
     * Special tail type for System.out.
     */
    public static final String LOG_SYSTEM_OUT = "[System.out]";

    /**
     * Specital tail type for Log4J.
     */
    public static final String LOG_LOG4J = "[Log4J]";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Reference to workspace status bar.
     */
    private IStatusBar statusBar_;

    /**
     * Tail output is appended into this text area.
     */
    private JSmartTextPane tailArea_;

    /**
     * Toggle button that handles pause/unpause of tail.
     */
    private JSmartToggleButton pauseButton_;

    /**
     * Start the tail.
     */
    private JButton startButton_;

    /**
     * Stops the tail.
     */
    private JButton stopButton_;

    /**
     * Closes the tail (also triggers adding the tail to the recent menu).
     */
    private JButton closeButton_;

    /**
     * Regular expression filter field that includes matching lines.
     */
    private JTextField regexField_;

    /**
     * Cut expression filter field that chops columns from a line.
     */
    private JTextField cutField_;

    /**
     * Lines are places in this queue for the UI component to pick up from.
     */
    private BlockingQueue queue_;

    /**
     * Optimization to read lines from the queue in batch instead of one'zies.
     */
    private BatchingQueueReader queueReader_;

    /**
     * Listener for queue events.
     */
    private TailQueueListener queueListener_;

    /**
     * List of filters that are applied to each line.
     */
    private ILineFilter[] filters_;

    /**
     * Filter that includes lines matching a regular expression.
     */
    private RegexLineFilter regexFilter_;

    /**
     * Filter that cuts columns from a line.
     */
    private CutLineFilter cutFilter_;

    /**
     * Filter that adds a line number to the beginning of each line.
     */
    private LineNumberDecorator lineNumberDecorator_;

    /**
     * Contexts for the individual tails that are aggregated by this TailPane.
     */
    private TailContext[] contexts_;

    /**
     * TailPane configuration.
     */
    private ITailViewConfig config_;

    /**
     * List of listeners interested in newData() and tailAggregated().
     */
    private TailViewListener[] listeners_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a TailPane with the given configuration.
     *
     * @param config Details of the tail configuration.
     * @param statusBar Status bar.
     * @throws FileNotFoundException if file not found.
     * @throws IOException if an I/O error occurs.
     */
    public TailPane(
        ITailViewConfig config, 
        IStatusBar statusBar) throws IOException, FileNotFoundException
    {
        super(config.getFilenames()[0]);
        statusBar_ = statusBar;
        listeners_ = new TailViewListener[0];
        contexts_ = new TailContext[0];
        buildView(config);
        buildFilters();
        setConfiguration(config);

        // Start tail through action so button states are OK
        if (config_.isAutoStart())
        {
            startButton_.getAction().actionPerformed(
                new ActionEvent(this, 0, "Start"));
        }
        else
        {
            pauseButton_.setEnabled(false);
            stopButton_.setEnabled(false);
        }
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Initializes the tail.
     *
     * @throws IOException if problems occur tailed System.out
     */
    protected void init() throws IOException
    {
        queue_          = new LinkedBlockingQueue();
        queueListener_  = new TailQueueListener();
        queueReader_    = new BatchingQueueReader(queue_,
                              ArrayUtil.toString(config_.getFilenames()) +
                              "-BatchingQueueReader");

        queueReader_.addBatchingQueueListener(queueListener_);
        queueReader_.start();

        String[] filenames = config_.getFilenames();
        //contexts_ = new TailContext[0];

        for (int i = 0; i < filenames.length; i++)
        {
            TailContext tc = new TailContext(filenames[i]);
            contexts_ = (TailContext[]) ArrayUtil.add(contexts_, tc);
            tc.init();
        }
    }


    /**
     * Builds the GUI.
     *
     * @param config Tailpane configuration.
     */
    protected void buildView(ITailViewConfig config)
    {
        JPanel p = new JPanel(new BorderLayout());
        JToolBar tb = JHeaderPanel.createToolBar();

        tailArea_ = new JSmartTextPane();
        tailArea_.setFont(FontUtil.getPreferredMonoFont());

        JButton clearButton = JHeaderPanel.createButton(
            ImageCache.getIcon(ImageCache.IMAGE_CLEAR),
            "Clears the output",
            new ClearAction(tailArea_));

        pauseButton_ =
            JHeaderPanel.createToggleButton(
                ImageCache.getIcon(ImageCache.IMAGE_PAUSE),
                "Pause/Resume",
                new PauseUnpauseAction());

        startButton_ =
            JHeaderPanel.createButton(
                ImageCache.getIcon(ImageCache.IMAGE_PLAY),
                "Starts the tail",
                new StartAction());

        stopButton_ =
            JHeaderPanel.createButton(
                ImageCache.getIcon(ImageCache.IMAGE_STOP),
                "Stops the tail",
                new StopAction());

        closeButton_ =
            JHeaderPanel.createButton(
                ImageCache.getIcon(ImageCache.IMAGE_DELETE),
                "Close tail",
                new CloseAction());

        JSmartToggleButton autoScrollButton =
            JHeaderPanel.createToggleButton(
                ImageCache.getIcon(ImageCache.IMAGE_LOCK),
                "AutoTail",
                new NullAction(), //new AutoTailAction(tailArea_),
                tailArea_,
                JSmartTextArea.PROP_AUTOTAIL);

        JSmartToggleButton wrapLinesButton =
            JHeaderPanel.createToggleButton(
                ImageCache.getIcon(ImageCache.IMAGE_LINEWRAP),
                "Wrap Lines",
                new NullAction(), //new LineWrapAction(tailArea_),
                tailArea_,
                JSmartTextArea.PROP_LINEWRAP);

        JSmartToggleButton showLineNumbersButton =
            JHeaderPanel.createToggleButton(
                ImageCache.getIcon(ImageCache.IMAGE_BRACES),
                "Line Numbers",
                new ShowLineNumbersAction(),
                tailArea_,
                "lineNumbers");

        tb.add(startButton_);
        tb.add(pauseButton_);
        tb.add(stopButton_);
        tb.addSeparator();
        tb.add(clearButton);
        tb.add(wrapLinesButton);
        tb.add(autoScrollButton);
        tb.add(showLineNumbersButton);
        tb.add(closeButton_);

        regexField_ = new JSmartTextField(5);
        cutField_ = new JSmartTextField(5);

        regexField_.addActionListener(new RegexActionListener());
        cutField_.addActionListener(new CutActionListener());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JSmartLabel("Include filter"));
        buttonPanel.add(regexField_);
        buttonPanel.add(new JSmartLabel("Cut"));
        buttonPanel.add(cutField_);

        p.add(new JScrollPane(tailArea_), BorderLayout.CENTER);
        p.add(buttonPanel, BorderLayout.SOUTH);

        setContent(p);
        setToolBar(tb);
    }


    /**
     * Sets up appropriate filters based on configuration.
     */
    protected void buildFilters()
    {
        filters_ = new ILineFilter[3];

        try
        {
            //
            // Filter based on regular expression
            //
            regexFilter_ = new RegexLineFilter();
            regexFilter_.setEnabled(false);
            filters_[0] = regexFilter_;
        }
        catch (RESyntaxException re)
        {
            ExceptionUtil.handleUI(re, logger_);
        }

        //
        // Cut filter
        //
        cutFilter_ = new CutLineFilter();
        cutFilter_.setEnabled(false);
        filters_[1] = cutFilter_;

        //
        // Show line numbers
        //
        lineNumberDecorator_ = new LineNumberDecorator();
        lineNumberDecorator_.setEnabled(false);
        filters_[2] = lineNumberDecorator_;
    }


    /**
     * Returns regular expression filter.
     *
     * @return Filter text.
     */
    protected String getRegularExpression()
    {
        return regexField_.getText().trim();
    }


    /**
     * Sets the filter text.
     *
     * @param filter Filter text as a regular expression.
     */
    protected void setRegularExpression(String filter)
    {
        try
        {
            regexFilter_.setEnabled(true);
            regexField_.setText(filter);
            regexFilter_.setRegularExpression(filter);
        }
        catch (RESyntaxException res)
        {
            logger_.info("Invalid regular expression: " + filter);
        }
    }


    /**
     * Returns the cut expression.
     *
     * @return Cut expression.
     */
    protected String getCutExpression()
    {
        return cutField_.getText().trim();
    }


    /**
     * Sets the cut text.
     *
     * @param cut Cut text. Example: 1-10 cuts columns one through ten.
     */
    protected void setCutExpression(String cut)
    {
        try
        {
            cutFilter_.setCut(cut);
            cutFilter_.setEnabled(true);
            cutField_.setText(cut);
        }
        catch (IllegalArgumentException e)
        {
            logger_.info("Invalid cut expression: " + cut);
        }
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Sets this tail view's configuration.
     *
     * @param config Tail view configuration.
     */
    public void setConfiguration(ITailViewConfig config)
    {
        config_ = config;
        //tailArea_.setAutoTail(config_.isAutoTail());
        lineNumberDecorator_.setEnabled(config_.isShowLineNumbers());
        tailArea_.setFont(config_.getFont());
        //tailArea_.setAntiAliased(config.isAntiAliased());
        setRegularExpression(config_.getRegularExpression());
        setCutExpression(config_.getCutExpression());
    }


    /**
     * Gets this tail view's configuration.
     *
     * @return ITailViewConfig
     * @throws IOException on I/O error.
     */
    public ITailViewConfig getConfiguration() throws IOException
    {
        // Make sure configuration up to date
        //config_.setAutoTail(tailArea_.isAutoTail());
        config_.setShowLineNumbers(lineNumberDecorator_.isEnabled());
        config_.setFont(tailArea_.getFont());
        //config_.setAntiAliased(tailArea_.isAntiAliased());
        config_.setRegularExpression(getRegularExpression());
        config_.setCutExpression(getCutExpression());

        String files[] = new String[0];

        for (int i = 0; i < contexts_.length; i++)
        {
            File f = contexts_[i].getTail().getFile();

            // TODO: Fix me. Temp fix for handlding non-file based tails
            files = (String[]) ArrayUtil.add(files,
                (f == null) ? "[Log4J]" : f.getCanonicalPath());
        }

        config_.setFilenames(files);
        config_.setAutoStart(startButton_.isEnabled());

        return config_;
    }


    /**
     * Returns the close button.
     *
     * @return Close button.
     */
    public JButton getCloseButton()
    {
        return closeButton_;
    }


    /**
     * Aggregates a file into an existing tail.
     *
     * @param file File to aggregate.
     * @throws ServiceException on service error.
     * @throws IOException on I/O error.
     */
    public void aggregate(String file) throws ServiceException, IOException
    {
        TailContext tc = new TailContext(file);
        tc.init();
        contexts_ = (TailContext[]) ArrayUtil.add(contexts_, tc);
        tc.getTail().start();
        fireTailAggregated(this);
    }

    //--------------------------------------------------------------------------
    //  TailViewListener Interface & Supporting Event Methods
    //--------------------------------------------------------------------------

    /**
     * Fires notifications of new tail data available.
     *
     * @param tailPane Tailpane.
     */
    protected void fireNewDataAvailable(TailPane tailPane)
    {
        for (int i = 0; i < listeners_.length; i++)
            listeners_[i].newDataAvailable(tailPane);
    }


    /**
     * Fires notification of a tail being aggregated into an existing tail.
     *
     * @param tailPane Tailpane.
     */
    public void fireTailAggregated(TailPane tailPane)
    {
        for (int i = 0; i < listeners_.length; i++)
            listeners_[i].tailAggregated(tailPane);
    }


    /**
     * Adds a listener.
     *
     * @param listener TailViewListener to add.
     */
    public void addTailViewListener(TailViewListener listener)
    {
        listeners_ = (TailViewListener[]) ArrayUtil.add(listeners_, listener);
    }


    /**
     * Removes a listener.
     *
     * @param listener TailViewListener to remove.
     */
    public void removeTailViewListener(TailViewListener listener)
    {
        listeners_ = (TailViewListener[])
            ArrayUtil.remove(listeners_, listener);
    }

    //--------------------------------------------------------------------------
    // TailContext
    //--------------------------------------------------------------------------

    /**
     * TailContext represents a single logical tail in a TailPane that may
     * contain multiple aggregate tails.
     * <p>
     * TailPane(Tail1 + Tail2 + Tail) = Aggregate Tail
     * <p>
     * Each Tail is represented as a single TailContext
     */
    public class TailContext
    {
        //----------------------------------------------------------------------
        // Fields
        //----------------------------------------------------------------------
        
        /**
         * File to tail.
         */
        private String filename_;

        /**
         * Does all the work.
         */
        private Tail tail_;

        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------

        /**
         * Creates a TailContext.
         *
         * @param filename File to tail.
         */
        public TailContext(String filename)
        {
            filename_ = filename;
        }

        //----------------------------------------------------------------------
        // Public
        //----------------------------------------------------------------------

        /**
         * Initializes the context.
         *
         * @throws IOException on I/O error.
         */
        public void init() throws IOException
        {
            // Figure out what type of tail we're dealing with and let 'er rip

            tail_ = new Tail();
            tail_.addTailListener(new TailListener());

            if (filename_.equals(TailPane.LOG_SYSTEM_OUT))
            {
                PipedOutputStream pos = new PipedOutputStream();
                PipedInputStream  pis = new PipedInputStream(pos);
                PrintStream ps = new PrintStream(pos, true);
                System.setOut(ps);

                tail_.follow(
                    new InputStreamReader(pis), 
                    new NullWriter(), 
                    filename_);

                logger_.debug("Tailing System.out...");
            }
            else if (filename_.equals(TailPane.LOG_LOG4J))
            {
                PipedOutputStream pos = new PipedOutputStream();
                PipedInputStream  pis = new PipedInputStream(pos);

                WriterAppender appender =
                    new WriterAppender(new TTCCLayout(), pos);


                //appender.setImmediateFlush(true);
                //appender.setThreshold(Priority.DEBUG);
                appender.setName("toolbox-stream-appender");
                LogManager.getLogger("toolbox").addAppender(appender);

                tail_.follow(
                    new InputStreamReader(pis), new NullWriter(), filename_);

                logger_.debug("Tailing Log4J...");

                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            logger_.info(
                                "Testing tailing of log4j from another" +
                                "thread...");
                        }
                        catch (Exception e)
                        {
                            logger_.error(e);
                        }
                    }
                });
            }
            else
            {
                tail_.follow(new File(filename_), new NullWriter());
            }
        }


        /**
         * Returns the tail.
         * 
         * @return Tail
         */
        public Tail getTail()
        {
            return tail_;
        }
    }

    //--------------------------------------------------------------------------
    // TailListener
    //--------------------------------------------------------------------------

    /**
     * Listener for tail.
     */
    class TailListener extends TailAdapter
    {
        /**
         * Called when next line of input is available. When a new line is
         * available, just push it on the shared queue.
         * 
         * @see toolbox.tail.TailListener#nextLine(toolbox.tail.Tail, 
         *      java.lang.String)
         */
        public void nextLine(Tail tail, String line)
        {
            try
            {
                queue_.put(line);
            }
            catch (InterruptedException e)
            {
                logger_.error(e);
            }
        }


        /**
         * @see toolbox.tail.TailListener#tailReattached(toolbox.tail.Tail)
         */
        public void tailReattached(Tail tail)
        {
            statusBar_.setInfo(
                "Tail reattached to " + tail.getFile().getName());
        }
    }

    //--------------------------------------------------------------------------
    // TailQueueListener
    //--------------------------------------------------------------------------

    /**
     * Pops groups of messages off the queue (as many as can be read without
     * waiting) and consolidates before sending then to the textarea.
     */
    class TailQueueListener implements IBatchingQueueListener
    {
        int cnt = 0;
        
        //----------------------------------------------------------------------
        //  IBatchingQueueListener Interface
        //----------------------------------------------------------------------

        /**
         * @see toolbox.util.concurrent.IBatchingQueueListener#nextBatch(
         *      java.lang.Object[])
         */
//        public void nextBatch(Object[] objs)
//        {
//            StringBuffer sb = new StringBuffer();
//
////            if (objs.length > 1)
////                logger_.debug("Batch size = " + objs.length);
////            else
////            {
////                if (cnt++ % 100 == 0)
////                    logger_.debug("[Batch size] = " + objs.length);
////            }
//            
//            // Iterate over each line delivered
//            for (int i = 0; i < objs.length; i++)
//            {
//                StringBuffer line = new StringBuffer((String) objs[i]);
//
//                // Apply filters
//                boolean discard = false;
//                
//                for (int j = 0; j < filters_.length; j++)
//                {
//                    discard = !filters_[j].filter(line);
//                    if (discard)
//                        break;
//                }
//
//                //if (line != null)
//                if (!discard)
//                    sb.append(line + "\n");
//            }
//
//            tailArea_.append(sb.toString());
//            fireNewDataAvailable(TailPane.this);
//        }
        
        
        /**
         * @see toolbox.util.concurrent.IBatchingQueueListener#nextBatch(
         *      java.lang.Object[])
         */
        public void nextBatch(Object[] objs)
        {
//            if (objs.length > 1)
//                logger_.debug("Batch size = " + objs.length);
//            else
//            {
//                if (cnt++ % 100 == 0)
//                    logger_.debug("[Batch size] = " + objs.length);
//            }
            
            List segments = new ArrayList();
            
            // Iterate over each line delivered
            for (int i = 0; i < objs.length; i++)
            {
                segments.clear();
                StringBuffer line = new StringBuffer((String) objs[i]);

                // Apply filters
                boolean discard = false;
                
                for (int j = 0; j < filters_.length; j++)
                {
                    discard = !filters_[j].filter(line, segments);
                    if (discard)
                        break;
                }

                
                if (!discard)
                {
                    if (segments.isEmpty())
                        tailArea_.append(line + "\n");
                    else
                    {
                        //logger_.debug("at least one segment exists!");
                        int pos = 0;

                        for (Iterator it = segments.iterator(); it.hasNext();)
                        {
                            StyledSegment segment = (StyledSegment) it.next();

                            //logger_.debug("segment size: " + segment.getBegin() + " " + segment.getEnd());
                            
                            // Current to segment begin
                            if (pos < segment.getBegin())
                            {
                                tailArea_.setActiveBackground(Color.white);
                                tailArea_.setActiveForeground(Color.blue);
                                
                                String s = line.substring(pos, segment.getBegin());
                                //logger_.debug("Non-segment: " + s);
                                tailArea_.append(s);
                            }
                            
                            // Segment begin to segment end
                            tailArea_.setActiveBackground(StyleConstants.getBackground(segment.getStyle()));
                            tailArea_.setActiveForeground(StyleConstants.getForeground(segment.getStyle()));
                            
                            String s2 = line.substring(segment.getBegin(), segment.getEnd() + 1);
                            //logger_.debug("segment: " + s2);
                            tailArea_.append(s2);

                            pos = segment.getEnd();
                            
                            if (!it.hasNext() && pos < line.length())
                            {
                                tailArea_.setActiveBackground(Color.white);
                                tailArea_.setActiveForeground(Color.blue);
                                
                                String s = line.substring(pos);
                                //logger_.debug("Non-segment: " + s);
                                tailArea_.append(s);
                            }
                        }
                        
                        tailArea_.append("\n");
                    }
                }
                
                //    sb.append(line + "\n");
            }

            //tailArea_.append(sb.toString());
            fireNewDataAvailable(TailPane.this);
        }
        
    }

    //--------------------------------------------------------------------------
    // RegexActionListener
    //--------------------------------------------------------------------------

    /**
     * Listens for changes in the regular expression (user must press enter) and
     * applies the new regular expression accordingly.
     */
    class RegexActionListener implements ActionListener
    {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            String s = getRegularExpression();

            if (StringUtils.isBlank(s))
                regexFilter_.setEnabled(false);
            else
            {
                setRegularExpression(getRegularExpression());
                statusBar_.setInfo("Filtering on regular expression: " + s);
            }
        }
    }

    //--------------------------------------------------------------------------
    // CutActionListener
    //--------------------------------------------------------------------------

    /**
     * Listens for changes in the cut expression (user must press enter) and
     * applies the new cut expression accordingly.
     */
    class CutActionListener implements ActionListener
    {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            String s = getCutExpression();

            if (StringUtils.isBlank(s))
                cutFilter_.setEnabled(false);
            else
                setCutExpression(getCutExpression());
        }
    }

    //--------------------------------------------------------------------------
    // StartAction
    //--------------------------------------------------------------------------

    /**
     * Starts the tail.
     */
    class StartAction extends SmartAction
    {
        /**
         * Creates a StartAction.
         */
        StartAction()
        {
            super(null, true, false, null);
            putValue(MNEMONIC_KEY, new Integer('S'));
            putValue(SHORT_DESCRIPTION, "Starts the tail");
        }


        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            init();

            for (int i = 0;
                i < contexts_.length;
                contexts_[i++].getTail().start());

            pauseButton_.setEnabled(true);
            stopButton_.setEnabled(true);
            startButton_.setEnabled(false);

            statusBar_.setInfo("Started tail for " +
                ArrayUtil.toString(config_.getFilenames()));
        }
    }

    //--------------------------------------------------------------------------
    // StopAction
    //--------------------------------------------------------------------------

    /**
     * Stops the tail.
     */
    class StopAction extends SmartAction
    {
        /**
         * Creates a StopAction.
         */
        StopAction()
        {
            super(null, true, false, null);
            putValue(MNEMONIC_KEY, new Integer('S'));
            putValue(SHORT_DESCRIPTION, "Stops the tail");
        }


        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            queueReader_.stop();

            for (int i = 0;
                i < contexts_.length;
                contexts_[i++].getTail().stop());

            pauseButton_.setEnabled(false);
            startButton_.setEnabled(true);
            stopButton_.setEnabled(false);

            statusBar_.setInfo("Stopped tail for " +
                ArrayUtil.toString(config_.getFilenames()));
        }
    }

    //--------------------------------------------------------------------------
    // PauseUnpauseAction
    //--------------------------------------------------------------------------

    /**
     * Pauses/unpauses the tail.
     */
    class PauseUnpauseAction extends SmartAction
    {
        private static final String MODE_PAUSE   = "Pause";
        private static final String MODE_UNPAUSE = "Unpause";

        /**
         * Creates a PauseUnpauseAction.
         */
        PauseUnpauseAction()
        {
            super(MODE_PAUSE, true, false, null);
            putValue(MNEMONIC_KEY, new Integer('P'));
            putValue(SHORT_DESCRIPTION, "Pause/Unpauses the tail");
        }


        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            for (int i = 0; i < contexts_.length; i++)
            {
                Tail tail = contexts_[i].getTail();

                if (tail.isSuspended())
                {
                    tail.resume();
                    statusBar_.setInfo(
                        "Unpaused " + tail.getFile().getCanonicalPath());
                }
                else
                {
                    tail.suspend();
                    statusBar_.setInfo(
                        "Paused " + tail.getFile().getCanonicalPath());
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    // CloseAction
    //--------------------------------------------------------------------------

    /**
     * Closes the tail pane.
     */
    class CloseAction extends AbstractAction
    {
        /**
         * Creates a CloseAction.
         */
        CloseAction()
        {
            super("Close");
            putValue(MNEMONIC_KEY, new Integer('e'));
            putValue(SHORT_DESCRIPTION, "Closes the tail pane");
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            for (int i = 0; i < contexts_.length; i++)
            {
                Tail tail = contexts_[i].getTail();

                if (tail.isSuspended())
                    tail.resume();

                if (tail.isRunning())
                    tail.stop();
            }

            statusBar_.setInfo(
                "Closed " + ArrayUtil.toString(config_.getFilenames()));
        }
    }

    //--------------------------------------------------------------------------
    // ShowLineNumbersAction
    //--------------------------------------------------------------------------

    /**
     * Toggles line numbers in the output area.
     */
    class ShowLineNumbersAction extends AbstractAction
    {
        /**
         * Creates a ShowLineNumbersAction.
         */
        ShowLineNumbersAction()
        {
            super("Line numbers");
            putValue(MNEMONIC_KEY, new Integer('L'));
            putValue(SHORT_DESCRIPTION,
                "Toggles display of line numbers in the output");
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            lineNumberDecorator_.setEnabled(!lineNumberDecorator_.isEnabled());
        }
    }
}