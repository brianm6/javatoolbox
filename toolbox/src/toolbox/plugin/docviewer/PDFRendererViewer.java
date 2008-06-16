/*
 * Modified version of PDFViewer. 
 * 
 * $Id: PDFViewer.java,v 1.5 2007/12/20 18:33:33 rbair Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package toolbox.plugin.docviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.SwingUtil;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceState;
import toolbox.util.ui.JSmartOptionPane;

import com.sun.pdfview.FullScreenWindow;
import com.sun.pdfview.OutlineNode;
import com.sun.pdfview.PDFDestination;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPrintPage;
import com.sun.pdfview.PDFViewer;
import com.sun.pdfview.PageChangeListener;
import com.sun.pdfview.PagePanel;
import com.sun.pdfview.ThumbPanel;
import com.sun.pdfview.action.GoToAction;
import com.sun.pdfview.action.PDFAction;

public class PDFRendererViewer extends JPanel implements DocumentViewer, KeyListener, TreeSelectionListener, PageChangeListener {

    private static final Logger log = Logger.getLogger(AcrobatViewer.class);

    /** The current PDFFile */
    PDFFile curFile;

    /** the name of the current document */
    String docName;
    
    /** The split between thumbs and page */
    JSplitPane split;
    
    /** The thumbnail scroll pane */
    JScrollPane thumbscroll;
    
    /** The thumbnail display */
    ThumbPanel thumbs;
    
    /** The page display */
    PagePanel page;
    
    /** The full screen page display, or null if not in full screen mode */
    PagePanel fspp;

    //    Thread anim;
    /** The current page number (starts at 0), or -1 if no page */
    int curpage = -1;
    
    /** the full screen button */
    JToggleButton fullScreenButton;
    
    /** the current page number text field */
    JTextField pageField;

    /** the full screen window, or null if not in full screen mode */
    FullScreenWindow fullScreen;
    
    /** the root of the outline, or null if there is no outline */
    OutlineNode outline = null;
    
    /** The page format for printing */
    PageFormat pformat = PrinterJob.getPrinterJob().defaultPage();
    
    /** true if the thumb panel should exist at all */
    boolean doThumb = true;
    
    /** a thread that pre-loads the next page for faster response */
    PagePreparer pagePrep;
    
    /** the window containing the pdf outline, or null if one doesn't exist */
    JDialog olf;
    
    private File prevDirChoice;

    private PageBuilder pb = new PageBuilder();

    // =========================================================================
    // Constructors
    // =========================================================================

    public PDFRendererViewer() {
        this(false);
    }
    
    /**
     * Create a new EmbeddedPDFRenderer based on a user, with or without a thumbnail
     * panel.
     * @param useThumbs true if the thumb panel should exist, false if not.
     */
    public PDFRendererViewer(boolean useThumbs) {
        doThumb = useThumbs;
        init();
    }

    // =========================================================================
    // DocumentViewer Interface
    // =========================================================================

    public boolean canView(File file) {
        return ArrayUtil.contains(
                getViewableFileTypes(), 
                FileUtil.getExtension(file).toLowerCase());
    }

    public JComponent getComponent() {
        return this;
    }

    public String[] getViewableFileTypes() {
        return new String[] { "pdf" };
    }

    public void view(File file) throws DocumentViewerException {
        try {
            openFile(file);
        }
        catch (IOException e) {
            throw new DocumentViewerException(e.getMessage(), e);
        }
    }

    public void view(InputStream is) throws DocumentViewerException {
        // TODO: Fixme
    }

    // =========================================================================
    // Initializable Interface
    // =========================================================================
    
    public ServiceState getState() {
        // TODO: Fixme
        return null;
    }

    public void initialize(Map config) throws IllegalStateException, ServiceException {
    }

    // =========================================================================
    // Destroyable Interface
    // =========================================================================

    public void destroy() throws IllegalStateException, ServiceException {
        doClose();
    }

    public boolean isDestroyed() {
        // TODO: Fixme
        return false;
    }

    // =========================================================================
    // Nameable Interface
    // =========================================================================
    
    public String getName() {
        return "PDF Renderer";
    }
    
    // =========================================================================
    // PageChangeListener Interface 
    // =========================================================================

    /**
     * Changes the displayed page, desyncing if we're not on the
     * same page as a presenter.
     * @param pagenum the page to display
     */
    public void gotoPage(int pagenum) {
        if (pagenum < 0) {
            pagenum = 0;
        }
        else if (pagenum >= curFile.getNumPages()) {
            pagenum = curFile.getNumPages() - 1;
        }
        forceGotoPage(pagenum);
    }

    // =========================================================================
    // Public 
    // =========================================================================

    /**
     * Changes the displayed page.
     * @param pagenum the page to display
     */
    public void forceGotoPage(int pagenum) {
        if (pagenum <= 0) {
            pagenum = 0;
        }
        else if (pagenum >= curFile.getNumPages()) {
            pagenum = curFile.getNumPages() - 1;
        }
        
        log.debug("Going to page " + pagenum);
        curpage = pagenum;

        // update the page text field
        pageField.setText(String.valueOf(curpage + 1));

        // fetch the page and show it in the appropriate place
        PDFPage pg = curFile.getPage(pagenum + 1);
        if (fspp != null) {
            fspp.showPage(pg);
            fspp.requestFocus();
        }
        else {
            page.showPage(pg);
            page.requestFocus();
        }

        // update the thumb panel
        if (doThumb) {
            thumbs.pageShown(pagenum);
        }

        // stop any previous page prepper, and start a new one
        if (pagePrep != null) {
            pagePrep.quit();
        }

        pagePrep = new PagePreparer(pagenum);
        pagePrep.start();
        setEnabling();
    }

    /**
     * Enable or disable all of the actions based on the current state.
     */
    public void setEnabling() {
        boolean fileavailable = curFile != null;
        boolean pageshown = ((fspp != null) ? fspp.getPage() != null : page.getPage() != null);
        boolean printable = fileavailable && curFile.isPrintable();

        pageField.setEnabled(fileavailable);
        printAction.setEnabled(printable);
        closeAction.setEnabled(fileavailable);
        fullScreenAction.setEnabled(pageshown);
        prevAction.setEnabled(pageshown);
        nextAction.setEnabled(pageshown);
        firstAction.setEnabled(fileavailable);
        lastAction.setEnabled(fileavailable);
        zoomToolAction.setEnabled(pageshown);
        fitInWindowAction.setEnabled(pageshown);
        zoomInAction.setEnabled(pageshown);
        zoomOutAction.setEnabled(pageshown);
    }

    /**
     * Open a specific pdf file.  Creates a DocumentInfo from the file,
     * and opens that.
     * @param file the file to open
     */
    public void openFile(File file) throws IOException {
        // first open the file for random access
        RandomAccessFile raf = new RandomAccessFile(file, "r");

        // extract a file channel
        FileChannel channel = raf.getChannel();

        // now memory-map a byte-buffer
        ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

        // create a PDFFile from the data
        PDFFile newfile = null;
        try {
            newfile = new PDFFile(buf);
        }
        catch (IOException ioe) {
            openError(file.getPath() + " doesn't appear to be a PDF file.");
            return;
        }

        // Now that we're reasonably sure this document is real, close the
        // old one.
        doClose();

        // set up our document
        this.curFile = newfile;
        docName = file.getName();
        //setTitle(TITLE + ": " + docName);

        // set up the thumbnails
        if (doThumb) {
            thumbs = new ThumbPanel(curFile);
            thumbs.addPageChangeListener(this);
            thumbscroll.getViewport().setView(thumbs);
            thumbscroll.getViewport().setBackground(Color.gray);
        }

        setEnabling();

        // display page 1.
        forceGotoPage(0);
        forceGotoPage(1);
        forceGotoPage(0);

        // if the PDF has an outline, display it.
        try {
            outline = curFile.getOutline();
        }
        catch (IOException ioe) {
            log.warn(ioe.getMessage(), ioe);
        }
        
        if (outline != null) {
            if (outline.getChildCount() > 0) {
                olf = new JDialog(SwingUtil.getFrameAncestor(this), "Outline");
                olf.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                olf.setLocation(this.getLocation());
                JTree jt = new JTree(outline);
                jt.setRootVisible(false);
                jt.addTreeSelectionListener(this);
                JScrollPane jsp = new JScrollPane(jt);
                olf.getContentPane().add(jsp);
                olf.pack();
                olf.setVisible(true);
            }
            else {
                if (olf != null) {
                    olf.setVisible(false);
                    olf = null;
                }
            }
        }
    }

    /**
     * Display a dialog indicating an error.
     */
    public void openError(String message) {
        JSmartOptionPane.showMessageDialog(split, message, "Error opening file", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Open a local file, given a string filename
     * @param name the name of the file to open
     */
    public void doOpen(String name) {
        try {
            openFile(new File(name));
        }
        catch (IOException ioe) {
        }
    }

    /**
     * Posts the Page Setup dialog
     */
    public void doPageSetup() {
        PrinterJob pjob = PrinterJob.getPrinterJob();
        pformat = pjob.pageDialog(pformat);
    }

    /**
     * Print the current document.
     */
    public void doPrint() {
        PrinterJob pjob = PrinterJob.getPrinterJob();
        pjob.setJobName(docName);
        Book book = new Book();
        PDFPrintPage pages = new PDFPrintPage(curFile);
        book.append(pages, pformat, curFile.getNumPages());

        pjob.setPageable(book);
        if (pjob.printDialog()) {
            new PrintThread(pages, pjob).start();
        }
    }

    /**
     * Close the current document.
     */
    public void doClose() {
        if (thumbs != null) {
            thumbs.stop();
        }
        if (olf != null) {
            olf.setVisible(false);
            olf = null;
        }
        if (doThumb) {
            thumbs = new ThumbPanel(null);
            thumbscroll.getViewport().setView(thumbs);
        }

        setFullScreenMode(false, false);
        page.showPage(null);
        curFile = null;
        setEnabling();
    }

    /**
     * Turns on zooming
     */
    public void doZoomTool() {
        if (fspp == null) {
            page.useZoomTool(true);
        }
    }

    /**
     * Turns off zooming; makes the page fit in the window
     */
    public void doFitInWindow() {
        if (fspp == null) {
            page.useZoomTool(false);
            page.setClip(null);
        }
    }

    /**
     * Shows or hides the thumbnails by moving the split pane divider
     */
    public void doThumbs(boolean show) {
        if (show) {
            split.setDividerLocation((int) thumbs.getPreferredSize().width +
                    (int) thumbscroll.getVerticalScrollBar().
                    getWidth() + 4);
        } 
        else {
            split.setDividerLocation(0);
        }
    }

    /**
     * Enter full screen mode
     * @param force true if the user should be prompted for a screen to
     * use in a multiple-monitor setup.  If false, the user will only be
     * prompted once.
     */
    public void doFullScreen(boolean force) {
        setFullScreenMode(fullScreen == null, force);
    }

    public void doZoom(double factor) {
    }

    public void doNext() {
        gotoPage(curpage + 1);
    }

    public void doPrev() {
        gotoPage(curpage - 1);
    }

    public void doFirst() {
        gotoPage(0);
    }

    public void doLast() {
        gotoPage(curFile.getNumPages() - 1);
    }

    /**
     * Goes to the page that was typed in the page number text field
     */
    public void doPageTyped() {
        int pagenum = -1;
        try {
            pagenum = Integer.parseInt(pageField.getText()) - 1;
        }
        catch (NumberFormatException nfe) {
        }

        if (pagenum >= curFile.getNumPages()) {
            pagenum = curFile.getNumPages() - 1;
        }
        if (pagenum >= 0) {
            if (pagenum != curpage) {
                gotoPage(pagenum);
            }
        }
        else {
            pageField.setText(String.valueOf(curpage));
        }
    }

    /**
     * Starts or ends full screen mode.
     * @param full true to enter full screen mode, false to leave
     * @param force true if the user should be prompted for a screen
     * to use the second time full screen mode is entered.
     */
    public void setFullScreenMode(boolean full, boolean force) {
        //  curpage= -1;
        if (full && fullScreen == null) {
            fullScreenAction.setEnabled(false);
            new Thread(new PerformFullScreenMode(force)).start();
            fullScreenButton.setSelected(true);
        }
        else if (!full && fullScreen != null) {
            fullScreen.close();
            fspp = null;
            fullScreen = null;
            gotoPage(curpage);
            fullScreenButton.setSelected(false);
        }
    }

    /**
     * utility method to get an icon from the resources of this class
     * @param name the name of the icon
     * @return the icon, or null if the icon wasn't found.
     */
    public Icon getIcon(String name) {
        Icon icon = null;
        URL url = null;
        
        try {
            url = PDFViewer.class.getResource(name);

            icon = new ImageIcon(url);
            if (icon == null) {
                log.warn("Couldn't find " + url);
            }
        } 
        catch (Exception e) {
            log.error("Couldn't find " + getClass().getName() + "/" + name, e);
        }
        return icon;
    }

    // =========================================================================
    // KeyListener Interface
    // =========================================================================

    /**
     * Handle a key press for navigation
     */
    public void keyPressed(KeyEvent evt) {
        int code = evt.getKeyCode();
        
        if (code == KeyEvent.VK_LEFT) {
            doPrev();
        }
        else if (code == KeyEvent.VK_RIGHT) {
            doNext();
        }
        else if (code == KeyEvent.VK_UP) {
            doPrev();
        }
        else if (code == KeyEvent.VK_DOWN) {
            doNext();
        }
        else if (code == KeyEvent.VK_HOME) {
            doFirst();
        }
        else if (code == KeyEvent.VK_END) {
            doLast();
        }
        else if (code == KeyEvent.VK_PAGE_UP) {
            doPrev();
        }
        else if (code == KeyEvent.VK_PAGE_DOWN) {
            doNext();
        }
        else if (code == KeyEvent.VK_SPACE) {
            doNext();
        }
        else if (code == KeyEvent.VK_ESCAPE) {
            setFullScreenMode(false, false);
        }
    }

    public void keyReleased(KeyEvent evt) {
    }

    /**
     * gets key presses and tries to build a page if they're numeric
     */
    public void keyTyped(KeyEvent evt) {
        char key = evt.getKeyChar();
        if (key >= '0' && key <= '9') {
            int val = key - '0';
            pb.keyTyped(val);
        }
    }

    // =========================================================================
    // TreeSelectionListener Interface
    // =========================================================================
    
    /**
     * Someone changed the selection of the outline tree. Go to the new page.
     */
    public void valueChanged(TreeSelectionEvent e) {
        if (e.isAddedPath()) {
            OutlineNode node = (OutlineNode) e.getPath().getLastPathComponent();
            if (node == null) {
                return;
            }

            try {
                PDFAction action = node.getAction();
                if (action == null) {
                    return;
                }

                if (action instanceof GoToAction) {
                    PDFDestination dest = ((GoToAction) action).getDestination();
                    if (dest == null) {
                        return;
                    }

                    PDFObject page = dest.getPage();
                    if (page == null) {
                        return;
                    }

                    int pageNum = curFile.getPageNumber(page);
                    if (pageNum >= 0) {
                        gotoPage(pageNum);
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
    // =========================================================================
    // Protected 
    // =========================================================================
   
    /**
     * Initialize this EmbeddedPDFRenderer by creating the GUI.
     */
    protected void init() {
        setLayout(new BorderLayout());
        page = new PagePanel();
        page.addKeyListener(this);

        if (doThumb) {
            split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            split.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
                    thumbAction);
            split.setOneTouchExpandable(true);
            thumbs = new ThumbPanel(null);
            thumbscroll = new JScrollPane(thumbs,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            split.setLeftComponent(thumbscroll);
            split.setRightComponent(page);
            add(split, BorderLayout.CENTER);
        } 
        else {
            add(page, BorderLayout.CENTER);
        }

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton jb;

        jb = new JButton(firstAction);
        jb.setText("");
        toolbar.add(jb);
        jb = new JButton(prevAction);
        jb.setText("");
        toolbar.add(jb);
        pageField = new JTextField("-", 3);
        //  pageField.setEnabled(false);
        pageField.setMaximumSize(new Dimension(45, 32));
        pageField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                doPageTyped();
            }
        });
        toolbar.add(pageField);
        jb = new JButton(nextAction);
        jb.setText("");
        toolbar.add(jb);
        jb = new JButton(lastAction);
        jb.setText("");
        toolbar.add(jb);

        toolbar.add(Box.createHorizontalGlue());

        fullScreenButton = new JToggleButton(fullScreenAction);
        fullScreenButton.setText("");
        toolbar.add(fullScreenButton);
        fullScreenButton.setEnabled(true);

        toolbar.add(Box.createHorizontalGlue());

        JToggleButton jtb;
        ButtonGroup bg = new ButtonGroup();

        jtb = new JToggleButton(zoomToolAction);
        jtb.setText("");
        bg.add(jtb);
        toolbar.add(jtb);
        jtb = new JToggleButton(fitInWindowAction);
        jtb.setText("");
        bg.add(jtb);
        jtb.setSelected(true);
        toolbar.add(jtb);

        toolbar.add(Box.createHorizontalGlue());

        jb = new JButton(printAction);
        jb.setText("");
        toolbar.add(jb);

        add(toolbar, BorderLayout.NORTH);

        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        //file.add(openAction);
        file.add(closeAction);
        file.addSeparator();
        file.add(pageSetupAction);
        file.add(printAction);
        file.addSeparator();
        mb.add(file);
        JMenu view = new JMenu("View");
        JMenu zoom = new JMenu("Zoom");
        zoom.add(zoomInAction);
        zoom.add(zoomOutAction);
        zoom.add(fitInWindowAction);
        zoom.setEnabled(false);
        view.add(zoom);
        view.add(fullScreenAction);

        if (doThumb) {
            view.addSeparator();
            view.add(thumbAction);
        }

        mb.add(view);
        //setJMenuBar(mb);
        setEnabling();
    }
    
    /**
     * A file filter for PDF files.
     */
    FileFilter pdfFilter = new FileFilter() {

        public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".pdf");
        }

        public String getDescription() {
            return "Choose a PDF file";
        }
    };


    Action pageSetupAction = new AbstractAction("Page setup...") {

        public void actionPerformed(ActionEvent evt) {
            doPageSetup();
        }
    };
    
    Action printAction = new AbstractAction("Print...", getIcon("gfx/print.gif")) {

        public void actionPerformed(ActionEvent evt) {
            doPrint();
        }
    };
    
    Action closeAction = new AbstractAction("Close") {

        public void actionPerformed(ActionEvent evt) {
            doClose();
        }
    };

    // =========================================================================
    
    /**
     * Runs the FullScreenMode change in another thread
     */
    class PerformFullScreenMode implements Runnable {

        boolean force;

        public PerformFullScreenMode(boolean forcechoice) {
            force = forcechoice;
        }

        public void run() {
            fspp = new PagePanel();
            fspp.setBackground(Color.black);
            page.showPage(null);
            fullScreen = new FullScreenWindow(fspp, force);
            fspp.addKeyListener(PDFRendererViewer.this);
            gotoPage(curpage);
            fullScreenAction.setEnabled(true);
        }
    }

    // =========================================================================

    class ZoomAction extends AbstractAction {

        double zoomfactor = 1.0;

        public ZoomAction(String name, double factor) {
            super(name);
            zoomfactor = factor;
        }

        public ZoomAction(String name, Icon icon, double factor) {
            super(name, icon);
            zoomfactor = factor;
        }

        public void actionPerformed(ActionEvent evt) {
            doZoom(zoomfactor);
        }
    }
    
    ZoomAction zoomInAction = new ZoomAction("Zoom in",
            getIcon("gfx/zoomin.gif"),
            2.0);
    
    ZoomAction zoomOutAction = new ZoomAction("Zoom out",
            getIcon("gfx/zoomout.gif"),
            0.5);
    
    Action zoomToolAction = new AbstractAction("", getIcon("gfx/zoom.gif")) {

        public void actionPerformed(ActionEvent evt) {
            doZoomTool();
        }
    };
    
    Action fitInWindowAction = new AbstractAction("Fit in window",
            getIcon("gfx/fit.gif")) {

        public void actionPerformed(ActionEvent evt) {
            doFitInWindow();
        }
    };

    // =========================================================================
    
    class ThumbAction extends AbstractAction
            implements PropertyChangeListener {

        boolean isOpen = true;

        public ThumbAction() {
            super("Hide thumbnails");
        }

        public void propertyChange(PropertyChangeEvent evt) {
            int v = ((Integer) evt.getNewValue()).intValue();
            if (v <= 1) {
                isOpen = false;
                putValue(ACTION_COMMAND_KEY, "Show thumbnails");
                putValue(NAME, "Show thumbnails");
            } else {
                isOpen = true;
                putValue(ACTION_COMMAND_KEY, "Hide thumbnails");
                putValue(NAME, "Hide thumbnails");
            }
        }

        public void actionPerformed(ActionEvent evt) {
            doThumbs(!isOpen);
        }
    }
    
    ThumbAction thumbAction = new ThumbAction();
    
    Action fullScreenAction = new AbstractAction("Full screen",
            getIcon("gfx/fullscrn.gif")) {

        public void actionPerformed(ActionEvent evt) {
            doFullScreen((evt.getModifiers() & ActionEvent.SHIFT_MASK) != 0);
        }
    };
  
    Action nextAction = new AbstractAction("Next", getIcon("gfx/next.gif")) {

        public void actionPerformed(ActionEvent evt) {
            doNext();
        }
    };
    
    Action firstAction = new AbstractAction("First", getIcon("gfx/first.gif")) {

        public void actionPerformed(ActionEvent evt) {
            doFirst();
        }
    };
    
    Action lastAction = new AbstractAction("Last", getIcon("gfx/last.gif")) {

        public void actionPerformed(ActionEvent evt) {
            doLast();
        }
    };
    
    Action prevAction = new AbstractAction("Prev", getIcon("gfx/prev.gif")) {

        public void actionPerformed(ActionEvent evt) {
            doPrev();
        }
    };

    // =========================================================================
    
    /**
     * A class to pre-cache the next page for better UI response
     */
    class PagePreparer extends Thread {

        int waitforPage;
        int prepPage;

        /**
         * Creates a new PagePreparer to prepare the page after the current
         * one.
         * @param waitforPage the current page number, 0 based 
         */
        public PagePreparer(int waitforPage) {
            setDaemon(true);

            this.waitforPage = waitforPage;
            this.prepPage = waitforPage + 1;
        }

        public void quit() {
            waitforPage = -1;
        }

        public void run() {
            Dimension size = null;
            Rectangle2D clip = null;

            // wait for the current page
            //            System.out.println("Preparer waiting for page " + (waitforPage + 1));
            if (fspp != null) {
                fspp.waitForCurrentPage();
                size = fspp.getCurSize();
                clip = fspp.getCurClip();
            } else if (page != null) {
                page.waitForCurrentPage();
                size = page.getCurSize();
                clip = page.getCurClip();
            }

            if (waitforPage == curpage) {
                // don't go any further if the user changed pages.
                //                System.out.println("Preparer generating page " + (prepPage + 2));
                PDFPage pdfPage = curFile.getPage(prepPage + 1, true);
                if (pdfPage != null && waitforPage == curpage) {
                    // don't go any further if the user changed pages
                    //                    System.out.println("Generating image for page " + (prepPage + 2));

                    pdfPage.getImage(size.width, size.height, clip, null, true, true);
                //          System.out.println("Generated image for page "+ (prepPage+2));
                }
            }
        }
    }

    // =========================================================================
    
    /**
     * A thread for printing in.
     */
    class PrintThread extends Thread {

        PDFPrintPage ptPages;
        PrinterJob ptPjob;

        public PrintThread(PDFPrintPage pages, PrinterJob pjob) {
            ptPages = pages;
            ptPjob = pjob;
        }

        public void run() {
            try {
                ptPages.show(ptPjob);
                ptPjob.print();
            } 
            catch (PrinterException pe) {
                JOptionPane.showMessageDialog(PDFRendererViewer.this,
                                              "Printing Error: " + pe.getMessage(),
                                              "Print Aborted", 
                                              JOptionPane.ERROR_MESSAGE);
            }
            ptPages.hide();
        }
    }

    // =========================================================================
    
    /**
     * Combines numeric key presses to build a multi-digit page number.
     */
    class PageBuilder implements Runnable {

        int value = 0;
        long timeout;
        Thread anim;
        static final long TIMEOUT = 500;

        /** add the digit to the page number and start the timeout thread */
        public synchronized void keyTyped(int keyval) {
            value = value * 10 + keyval;
            timeout = System.currentTimeMillis() + TIMEOUT;
            if (anim == null) {
                anim = new Thread(this);
                anim.start();
            }
        }

        /**
         * waits for the timeout, and if time expires, go to the specified
         * page number
         */
        public void run() {
            long now, then;
            synchronized (this) {
                now = System.currentTimeMillis();
                then = timeout;
            }
            while (now < then) {
                try {
                    Thread.sleep(timeout - now);
                }
                catch (InterruptedException ie) {
                }
                synchronized (this) {
                    now = System.currentTimeMillis();
                    then = timeout;
                }
            }
            synchronized (this) {
                gotoPage(value - 1);
                anim = null;
                value = 0;
            }
        }
    }
}
