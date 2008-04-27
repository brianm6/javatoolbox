package toolbox.util.ui.explorer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.Platform;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.file.FileComparator;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartComboBox;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.explorer.action.DeleteDirAction;
import toolbox.util.ui.explorer.action.DeleteFileAction;
import toolbox.util.ui.explorer.action.RefreshAction;
import toolbox.util.ui.explorer.action.RenameDirAction;
import toolbox.util.ui.explorer.action.RenameFileAction;
import toolbox.util.ui.explorer.action.UpOneLevelAction;
import toolbox.util.ui.explorer.action.ViewFileAction;
import toolbox.util.ui.explorer.listener.DirTreeMouseListener;
import toolbox.util.ui.explorer.listener.DirTreeSelectionListener;
import toolbox.util.ui.explorer.listener.DriveComboListener;
import toolbox.util.ui.explorer.listener.FileListMouseListener;
import toolbox.util.ui.explorer.listener.FileListSelectionListener;
import toolbox.util.ui.list.JListPopupMenu;
import toolbox.util.ui.list.JSmartList;
import toolbox.util.ui.list.SmartListCellRenderer;
import toolbox.util.ui.tree.JSmartTree;
import toolbox.util.ui.tree.SmartTreeCellRenderer;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PreferencedException;

/**
 * Simple file system explorer. 
 * <p>
 * Features:
 * <ul>
 *   <li>Vertical or horizontal splitter orientation
 *   <li>Event notification for
 *     <ul>
 *       <li>Directory selection
 *       <li>Directory double-click
 *       <li>File selection
 *       <li>File double-click
 *     </ul>
 *   <li>Infobar with file attributes
 *   <li>Popup menu
 * </ul>
 * <p>
 * Hotkeys:
 * <ul>
 *   <li>Del - Delete
 *   <li>F2  - Rename
 *   <li>F5  - Refresh
 *   <li>Backspace - Up one level 
 * </ul>
 * <p>
 * Persistent Preferences:
 * <ul>
 *   <li>Splitter location
 *   <li>Last selected directory and file.
 * </ul>
 * 
 * @see toolbox.util.ui.explorer.FileExplorerListener
 * @see toolbox.util.ui.explorer.FileNode
 * @see toolbox.util.ui.explorer.InfoBar 
 */
public class JFileExplorer extends JPanel implements IPreferenced
{
    private static final Logger logger_ = Logger.getLogger(JFileExplorer.class);

    //--------------------------------------------------------------------------
    // Icons
    //--------------------------------------------------------------------------
    
    /**
     * JFileExplorer icon.
     */
    public static final Icon ICON = 
        ImageCache.getIcon(ImageCache.IMAGE_TREE_CLOSED);

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /** 
     * Filter to identify files (versus directories).
     */
    private static final IOFileFilter FILTER_FILEONLY = 
        new NotFileFilter(DirectoryFileFilter.INSTANCE);

    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------
    
    /**
     * Root node under which JFileExplorer preferences are saved.
     */
    private static final String NODE_JFILEEXPLORER = "JFileExplorer";
    
    /**
     * Attribute for last selected directory.
     */
    private static final String ATTR_PATH = "path";
    
    /**
     * Attribute for the last selected file.
     */
    private static final String ATTR_FILE = "file";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    
    // Model--------------------------------------------------------------------
    
    /**
     * Model for the root node of the directory tree.
     */
    private DefaultMutableTreeNode rootNode_;
    
    /**
     * Model for the directory tree.
     */
    private DefaultTreeModel treeModel_;

    
    // UI ----------------------------------------------------------------------
    
    /**
     * Splitter that separates the directory tree and file list. The location 
     * of the splitter bar is saved between applications instances.
     */
    private JSmartSplitPane splitPane_;
    
    /**
     * JList that displays the list of files for the currently selected 
     * directory. The index of the currently selected file is saved between
     * application instances.
     */
    private JList fileList_;
    
    /**
     * Directory tree that displays the filesystem directory structure. 
     * Selecting a directory will automatically populate the its files in the
     * file list. The currently selected directory path is saved between 
     * application instances.
     */
    private JTree tree_;
    
    /**
     * Drop down combo with drive letters. This combo usually only has one
     * element on unix filesystems. Otherwise, drive letters are available for
     * selection on FAT and other similar filesystems. The currently selected
     * root element is saved between application instances.
     */
    private JComboBox rootsComboBox_;
    
    /**
     * Popup menu for the directory tree.
     */
    private JPopupMenu folderPopup_;
    

    // Event -------------------------------------------------------------------
    
    /** 
     * List of objects interested in file explorer generated events. 
     */ 
    private FileExplorerListener[] fileExplorerListeners_;

    /** 
     * Flag to prevent events from triggering new events from being processed 
     * while an operation is pending on the tree.
     */
    private boolean processingTreeEvent_;
    
    /** 
     * Listener for the change in selection to the directory tree. 
     */
    private DirTreeSelectionListener treeSelectionListener_;

    
    // Misc --------------------------------------------------------------------
    
    /**
     * Current selection path.
     */
    private String currentPath_;
        
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JFileExplorer with horizontal splitter between the file and
     * directory views.
     */
    public JFileExplorer()
    {
        this(false);
    }


    /**
     * Creates a JFileExplorer.
     * 
     * @param verticalSplitter Set to true if you want the folder and file
     *        panes to be split by a vertical splitter, otherwise a horizontal 
     *        splitter will be used.
     */
    public JFileExplorer(boolean verticalSplitter)
    {
        fileExplorerListeners_ = new FileExplorerListener[0];
        buildView(verticalSplitter);
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Returns the current directory path.
     *
     * @return String
     */
    public String getCurrentPath()
    {
        return currentPath_;
    }


    /**
     * Sets the current directory path.
     *
     * @param currentPath Path to set explorer to.
     */
    public void setCurrentPath(String currentPath)
    {
        currentPath_ = currentPath;
    }


    /**
     * Returns the current file path.
     * <p> 
     * Example: /tmp/work/Foo.java
     *
     * @return String
     */
    public String getFilePath()
    {
        StringBuffer s = new StringBuffer();
        s.append(getCurrentPath());
        s.append(File.separator);
        Object file = fileList_.getSelectedValue();
        
        // Handle case where no file is selected. Return only the path
        if (file != null)
            s.append(file.toString());
            
        return s.toString();
    }

    
    /**
     * Selects the given folder. Folder is a fully qualified directory structure
     * starting from the file root. 
     * <pre>
     *  
     * Windows folder : c:\home\stuff   
     * Unix folder    : /usr/export/home
     * 
     * </pre>
     * 
     * @param path Folder to select. Must be absolute in absolute form from the 
     *             root.
     */
    public void selectFolder(String path)
    {
        if (!processingTreeEvent_)
        {
            processingTreeEvent_ = true;
                
            String[] pathTokens = StringUtil.tokenize(path, File.separator);
            
            if (Platform.isUnix())
            {
                if (path.startsWith(File.separator))
                {
                    // Set root to "/"
                    pathTokens = (String[]) 
                        ArrayUtil.insert(pathTokens, File.separator);
                }
                else
                {
                   throw new IllegalArgumentException("Path must begin with /");
                }
            }
            else // if (Platform.isWindows())
            {
                // Treat all other platforms like windows
                
                if (path.startsWith(File.separator))
                {
                    // Update the root since the path separator was stripped by
                    // the tokenizer
                    pathTokens[0] = getDefaultRoot();
                }
                else
                {
                    File root  = new File(path.substring(0, 3));
                    
                    if (ArrayUtil.contains(File.listRoots(), root))
                    {
                        // Update the root since the path separator was stripped
                        // by the tokenizer
                        pathTokens[0] = root.toString();
                        
                        String currentRoot = 
                            rootsComboBox_.getSelectedItem().toString();
                        
                        // Only change combo box if drive has changed
                        if (!currentRoot.equals(pathTokens[0]))
                        {
                            rootsComboBox_.setSelectedItem(
                                new File(pathTokens[0]));
                            
                            logger_.debug("Switching to root " + 
                                rootsComboBox_.getSelectedItem() + 
                                    " in path " + path);
                        }
                    }
                    else
                    {
                        // Root not found in list
                        throw new IllegalArgumentException(
                            "Root could not be determined in path " + path);
                    }
                }
            }
    
            logger_.debug("Path Tokens = " + ArrayUtil.toString(pathTokens));
            
            DefaultTreeModel model = (DefaultTreeModel) tree_.getModel();
            FileNode root = (FileNode) model.getRoot();
    
            // Discover path by iterating over pathTokens and building a 
            // TreePath dynamically
            
            if (root.equals(new FileNode(pathTokens[0])))
            {
                FileNode current = root;
                
                // Starts at 1 to skip over root which is zero
                for (int i = 1; i < pathTokens.length; i++) 
                {
                    if (current.getChildCount() == 0)
                    {
                        // Expand node on demand
                        String partialPath = "";
                        
                        for (int j = 0; j < i; j++) 
                        {
                            if (pathTokens[j].endsWith(File.separator))
                                pathTokens[j] = pathTokens[j].substring(0, 
                                    pathTokens[j].length() - 1);
                                
                            partialPath = partialPath + 
                                pathTokens[j] + File.separator;
                        }
                            
                        setTreeFolders(partialPath, current);
                    }
                    
                    FileNode child = new FileNode(pathTokens[i]);
                    child.setParent(current);
                    int idx = current.getIndex(child);
                    current = (FileNode) current.getChildAt(idx);
                }
                
                TreePath tp = new TreePath(current.getPath());
                tree_.setSelectionPath(tp);
                tree_.scrollPathToVisible(tp);
            }
            else
            {
                throw new IllegalArgumentException(
                    "Root didnt match in model!" +
                        root + "doesnt match " + new FileNode(pathTokens[0]));
            }
            
            processingTreeEvent_ = false;
        }
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * Saves preferences as XML.
     * 
     * @param prefs Element to save preferences to.
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_JFILEEXPLORER);
        
        String path = getCurrentPath();
        if (!StringUtils.isBlank(path))
            root.addAttribute(new Attribute(ATTR_PATH, path));
            
        String file = (String) fileList_.getSelectedValue();
        if (!StringUtils.isBlank(file))
            root.addAttribute(new Attribute(ATTR_FILE, file));
            
        splitPane_.savePrefs(root);
            
        XOMUtil.insertOrReplace(prefs, root);
    }

    
    /**
     * Restores preferences from XML and applies them.
     * 
     * @param prefs XML DOM to read the preferences from.  
     */   
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_JFILEEXPLORER, new Element(NODE_JFILEEXPLORER));
        
        // Restore expanded directory or just select the root if this is the
        // first time.
        selectFolder(
            XOMUtil.getStringAttribute(root, ATTR_PATH, getDefaultRoot()));
        
        // Restore selected file    
        String file = XOMUtil.getStringAttribute(root, ATTR_FILE, null);
        if (!StringUtils.isBlank(file))
            fileList_.setSelectedValue(file, true);

        splitPane_.applyPrefs(root);        
    }

    //--------------------------------------------------------------------------
    // Overrides java.awt.Component
    //--------------------------------------------------------------------------

    /**
     * @see java.awt.Component#getPreferredSize()
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(200, 400);
    }

    //--------------------------------------------------------------------------
    // Event Notification Support
    //--------------------------------------------------------------------------

    /**
     * Adds a FileExplorerListener.
     *
     * @param listener Listener to add.
     */
    public void addFileExplorerListener(FileExplorerListener listener)
    {
        fileExplorerListeners_ = 
            (FileExplorerListener[]) 
                ArrayUtil.add(fileExplorerListeners_, listener);
    }


    /**
     * Removes a FileExplorerListener.
     *
     * @param listener Listener to remove.
     */
    public void removeFileExplorerListener(FileExplorerListener listener)
    {
        fileExplorerListeners_ = 
            (FileExplorerListener[]) 
                ArrayUtil.remove(fileExplorerListeners_, listener);
    }


    /**
     * Fires an event when a file is double clicked by the user.
     */
    protected void fireFileDoubleClicked()
    {
        for (int i = 0; i < fileExplorerListeners_.length; 
            fileExplorerListeners_[i++].fileDoubleClicked(getFilePath()));
    }


    /**
     * Fires an event when a file is selected by the user.
     */
    protected void fireFileSelected()
    {
        for (int i = 0; i < fileExplorerListeners_.length; 
            fileExplorerListeners_[i++].fileSelected(getFilePath()));
    }

    
    /**
     * Fires an event when a directory is selected.
     * 
     * @param folder Folder that was selected.
     */
    protected void fireFolderSelected(String folder)
    {
        for (int i = 0; i < fileExplorerListeners_.length; 
            fileExplorerListeners_[i++].folderSelected(folder));
    }

    
    /**
     * Fire an event when a directory is double clicked.
     * 
     * @param folder Folder that was double clicked.
     */
    protected void fireFolderDoubleClicked(String folder)
    {
        for (int i = 0; i < fileExplorerListeners_.length; 
            fileExplorerListeners_[i++].folderDoubleClicked(folder));
    }
    
    //--------------------------------------------------------------------------
    // Builders
    //--------------------------------------------------------------------------
    
    /**
     * Constructs the user interface. 
     * 
     * @param verticalSplitter Splitter orientation.
     */
    protected void buildView(boolean verticalSplitter)
    {
        // File system roots combobox
        rootsComboBox_ = new JSmartComboBox(File.listRoots());
        rootsComboBox_.setSelectedItem(new File(getDefaultRoot()));
        rootsComboBox_.addItemListener(new DriveComboListener(this));
        rootsComboBox_.setRenderer(new DriveIconCellRenderer());
        
        buildFileList();

        // Set up our Tree
        setTreeRoot(getDefaultRoot());

        buildDirectoryTree();
        
        setTreeFolders(getDefaultRoot(), null);

        // Configurable splitter orientation
        splitPane_ = new JSmartSplitPane(
            verticalSplitter 
                ? JSplitPane.HORIZONTAL_SPLIT 
                : JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(tree_), 
            new JScrollPane(fileList_));
            
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();        
        setLayout(gridbag);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        gridbag.setConstraints(rootsComboBox_, constraints);
        add(rootsComboBox_);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 100;
        constraints.weighty = 100;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        gridbag.setConstraints(splitPane_, constraints);
        add(splitPane_);
              
        InfoBar infoBar = new InfoBar(this);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 100;
        constraints.weighty = 000;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.SOUTH;
        gridbag.setConstraints(infoBar, constraints);
        add(infoBar);
              
        addFileExplorerListener(infoBar.new InfoBarUpdater());              
        splitPane_.setDividerLocation(150);
    }


    /**
     * Builds the tree that represents the fie system directories.
     */
    protected void buildDirectoryTree()
    {
        // Load tree icons        
        DefaultTreeCellRenderer renderer = new SmartTreeCellRenderer();
        renderer.setClosedIcon(ImageCache.getIcon(ImageCache.IMAGE_TREE_OPEN));
        renderer.setOpenIcon(ImageCache.getIcon(ImageCache.IMAGE_TREE_OPEN));
        renderer.setLeafIcon(ImageCache.getIcon(ImageCache.IMAGE_TREE_CLOSED));

        // Directory tree
        treeModel_ = new DefaultTreeModel(rootNode_);
        tree_ = new JSmartTree(treeModel_);
        tree_.setEditable(false);
        
        tree_.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
            
        tree_.setRootVisible(true);
        tree_.setScrollsOnExpand(true);
        
        treeSelectionListener_ = new DirTreeSelectionListener(this);
        tree_.addTreeSelectionListener(treeSelectionListener_);
        tree_.addMouseListener(new DirTreeMouseListener(this));
        
        tree_.setCellRenderer(renderer);
        tree_.putClientProperty("JTree.lineStyle", "Angled");

        // Add keybindings
        SwingUtil.bindKey(
            tree_, 
            new DeleteDirAction(this), 
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
            JComponent.WHEN_FOCUSED);
        
        SwingUtil.bindKey(
            tree_,
            new RefreshAction(this),
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
            JComponent.WHEN_FOCUSED);
        
        SwingUtil.bindKey(
            tree_,
            new RenameDirAction(this),
            KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
            JComponent.WHEN_FOCUSED);
        
        SwingUtil.bindKey(
            tree_,
            new UpOneLevelAction(this),
            KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
            JComponent.WHEN_FOCUSED);
    }


    /**
     * Builds the list that shows the files in for the currently selected node
     * in the directory tree.
     */
    protected void buildFileList()
    {
        // File list
        fileList_ = new JSmartList();
        fileList_.setModel(new DefaultListModel());
        fileList_.addMouseListener(new FileListMouseListener(this));
        fileList_.addListSelectionListener(new FileListSelectionListener(this));
        setFileList(getDefaultRoot());
        fileList_.setFixedCellHeight(15);
        JListPopupMenu popup = new JListPopupMenu(fileList_);

        // Popup menu accessible operations...
        AbstractAction deleteFileAction = new DeleteFileAction(this);
        AbstractAction renameFileAction = new RenameFileAction(this);
        popup.add(new JSmartMenuItem(deleteFileAction));
        popup.add(new JSmartMenuItem(renameFileAction));
        popup.add(new JSmartMenuItem(new ViewFileAction(this)));
        
        // Key binding accessible operations...
        SwingUtil.bindKey(
            fileList_,
            deleteFileAction,
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
            JComponent.WHEN_FOCUSED);
        
        SwingUtil.bindKey(
            fileList_,
            new RefreshAction(this),
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
            JComponent.WHEN_FOCUSED);
        
        SwingUtil.bindKey(
            fileList_,
            renameFileAction,
            KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
            JComponent.WHEN_FOCUSED);

        SwingUtil.bindKey(
            fileList_,
            new UpOneLevelAction(this),
            KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
            JComponent.WHEN_FOCUSED);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Package level access for the roots combobox. Used by the InfoBar.
     * 
     * @return JComboBox
     */
    protected JComboBox getRootsComboBox()
    {
        return rootsComboBox_;
    }

    
    /**
     * Package level access for the file list. Used by the InfoBar.
     * 
     * @return JList
     */
    public JList getFileList()
    {
        return fileList_;
    }
    
    
    /**
     * Returns the popup menu for the directory list.
     * 
     * @return JPopupMenu
     */
    protected JPopupMenu getFolderPopup()
    {
        return folderPopup_;
    }

    
    /**
     * Sets the popup menu for the directory list.
     * 
     * @param folderPopup Popupmenu
     */
    protected void setFolderPopup(JPopupMenu folderPopup)
    {
        folderPopup_ = folderPopup;
    }

    
    /**
     * Sets the root for the JTree.
     *
     * @param root Root of the tree.
     */
    protected void setTreeRoot(String root)
    {
        rootNode_ = new FileNode(root);
        
        if (treeModel_ != null)
            treeModel_.setRoot(rootNode_);
    }


    /**
     * Sets the tree folders.
     *
     * @param pathToAddFolders Path to add folders to.
     * @param currentNode Current node.
     */
    protected void setTreeFolders(
        String pathToAddFolders,
        DefaultMutableTreeNode currentNode)
    {
        File[] files = 
            new File(pathToAddFolders).listFiles(
                (FileFilter) FileFilterUtils.directoryFileFilter());
            
        Arrays.sort(files, FileComparator.COMPARE_NAME);
        String[] fileList = new String[files.length];

        for (int i = 0; i < files.length; i++)
            fileList[i] = files[i].getName();

        if (fileList.length > 0)
            addTreeNodes(fileList, currentNode);
    }


    /**
     * Finds, sorts, and adds the files in the given directory.
     *
     * @param path Path with files.
     */
    protected void setFileList(String path)
    {
        setCurrentPath(path);
        DefaultListModel model = (DefaultListModel) fileList_.getModel();
        model.clear();
        File f = new File(path);
        File[] files = f.listFiles((FileFilter) FILTER_FILEONLY);
        Arrays.sort(files, FileComparator.COMPARE_NAME);
        
        for (int i = 0; i < files.length; i++)
            model.addElement(files[i].getName());
    }


    /**
     * Removes all children of the tree root.
     */
    protected void clear()
    {
        // Disable events while the tree is being cleared
        tree_.removeTreeSelectionListener(treeSelectionListener_);
        
        rootNode_.removeAllChildren();
        treeModel_.reload();
        
        tree_.addTreeSelectionListener(treeSelectionListener_);
    }


    /**
     * Adds folders to the tree.
     *
     * @param folderList An array of folders to add.
     * @param parentNode Parent node of nodes to add.
     */
    protected void addTreeNodes(
        String[] folderList, 
        DefaultMutableTreeNode parentNode)
    {
        if (parentNode == null)
            parentNode = rootNode_;

        DefaultMutableTreeNode childNode = null;
        
        for (int i = 0; i < folderList.length; i++)
        {
            childNode = new FileNode(folderList[i]);
            
            // Only insert if it doesn't already exist.
            boolean shouldAdd = true;
            Enumeration e = parentNode.children();
            
            while (e.hasMoreElements())
            {
                DefaultMutableTreeNode tNode = 
                    (DefaultMutableTreeNode) e.nextElement();
                    
                if (tNode.toString().equals(childNode.toString()))
                {
                    // Already exist, we're not going to add.
                    shouldAdd = false;
                    break;
                }
            }
            
            if (shouldAdd)
            {
                treeModel_.insertNodeInto(
                    childNode, parentNode, parentNode.getChildCount());
            }
        }

        tree_.expandPath(new TreePath(parentNode.getPath()));
        tree_.scrollPathToVisible(new TreePath(childNode.getPath()));
    }


    /**
     * Returns the default root.
     *
     * @return Default root drive/directory.
     */
    protected String getDefaultRoot()
    {
        File[] roots = File.listRoots();
        String userHome = System.getProperty("user.home");
        
        for (int i = 0; i < roots.length; i++)
        {
            if (userHome.startsWith(roots[i].toString()))
                return roots[i].toString();
        }
        
        // Should never happen
        return "";
    }

    //--------------------------------------------------------------------------
    // DriveIconCellRenderer
    //--------------------------------------------------------------------------

    /**
     * Inner class for rendering our own display for the Roots drop down menu.
     */
    class DriveIconCellRenderer extends SmartListCellRenderer implements 
        ListCellRenderer
    {
        /**
         * Creates a DriveIconCellRenderer.
         */
        DriveIconCellRenderer()
        {
            //this.setOpaque(true);
        }

        //----------------------------------------------------------------------
        // ListCellRenderer Interface
        //----------------------------------------------------------------------
        
        /**
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(
         *      javax.swing.JList, java.lang.Object, int, boolean, boolean)
         */
        public Component getListCellRendererComponent(
            JList list,
            Object value, 
            int index, 
            boolean isSelected, 
            boolean cellHasFocus)
        {
            super.getListCellRendererComponent(
                list, 
                value, 
                index, 
                isSelected, 
                cellHasFocus);
            
            setText(value.toString());
            setIcon(ImageCache.getIcon(ImageCache.IMAGE_HARD_DRIVE));
            
            return this;
        }
    }
}

/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Modded to be generic file browser.
 */