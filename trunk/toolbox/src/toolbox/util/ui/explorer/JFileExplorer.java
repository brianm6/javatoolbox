package toolbox.util.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.Platform;
import toolbox.util.PropertiesUtil;
import toolbox.util.ResourceUtil;
import toolbox.util.StringUtil;
import toolbox.util.io.filter.DirectoryFilter;
import toolbox.util.io.filter.FileFilter;

/**
 * Tree based file browser widget based on an open-source project and heavily 
 * updated.
 * 
 * <pre>
 * TODO: Add filter for file list
 * TODO: Add refresh button
 * TODO: Add file size and timestamp info somewhere
 * </pre>
 */
public class JFileExplorer extends JPanel
{
    private static final Logger logger_ = 
        Logger.getLogger(JFileExplorer.class);

    // Saved component properties
    private static final String PROP_PATH    = ".explorer.path";
    private static final String PROP_FILE    = ".explorer.file";
    private static final String PROP_DIVIDER = ".explorer.dividerlocation";

    // Models
    private DefaultListModel        listModel_;
    private DefaultMutableTreeNode  rootNode_;
    private DefaultTreeModel        treeModel_;

    // Views        
    private JSplitPane  splitPane_;
    private JList       fileList_;
    private JTree       tree_;
    private JComboBox   rootsComboBox_;
    private JPopupMenu  folderPopup_;

    private String root_;
    private String currentPath_;
    private Icon   driveIcon_;

    /** Collection of listeners */ 
    private List fileExplorerListeners_ = new ArrayList();

	/** Flag to prevent events from triggering new events to be generated */
    private boolean processingTreeEvent_;
    
    /** Listener for the change in selection to the directory */
    private DirTreeSelectionListener treeSelectionListener_;
        
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JFileExplorer with a default horizontal splitter
     */
    public JFileExplorer()
    {
        this(false);
    }

    /**
     * Creates a JFileExplorer
     * 
     * @param  verticalSplitter Set to true if you want the folder and file
     *                          panes to be split by a vertical splitter, 
     *                          otherwise a horizontal splitter will be used.
     */
    public JFileExplorer(boolean verticalSplitter)
    {
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
     * Sets the current directory path
     *
     * @param  currentPath   Path to set explorer to
     */
    public void setCurrentPath(String currentPath)
    {
        currentPath_ = currentPath;
    }

    /**
     * Returns the current file path.
     *
     * @return String
     */
    public String getFilePath()
    {
        StringBuffer s = new StringBuffer();
        s.append(getCurrentPath());
        s.append(File.separator);
        s.append(fileList_.getSelectedValue().toString());
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
     * @param  path  Folder to select. Must be absolute in absolute form 
     *               from the root.
     */
    public void selectFolder(String path)
    {
        if (!processingTreeEvent_)
        {
            processingTreeEvent_ = true;
                
            //logger_.debug("path = " + path);
            //logger_.debug(Stringz.BR);
            //logger_.debug("**", new Exception("From selectFolder()"));
            //logger_.debug(Stringz.BR);
            
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
                    // Root drive a possibility
                    File root  = new File(path.substring(0,3));
                    
                    if (ArrayUtil.contains(File.listRoots(), root))
                    {
                        // Update the root since the path separator was stripped
                        // by the tokenizer
                        pathTokens[0] = root.toString();
                        
                        // Switch to different drive if necessary
                        rootsComboBox_.setSelectedItem(new File(pathTokens[0]));
                        
                        logger_.debug(
                            "Switching to root " + 
                                rootsComboBox_.getSelectedItem() + 
                                    " in path " + path);
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
            
            DefaultTreeModel model = (DefaultTreeModel)tree_.getModel();
            FileNode root = (FileNode) model.getRoot();
    
            // Discover path by iterating over pathTokens and building a TreePath 
            // dynamically
            
            if (root.equals(new FileNode(pathTokens[0])))
            {
                FileNode current = root;
                
                //logger_.debug(method + 
                //  "Current " + current + " children: " + current.getChildCount());
                
                // Starts at 1 to skip over root
                for(int i=1; i<pathTokens.length; i++) 
                {
                    if (current.getChildCount() == 0)
                    {
                        // Expand node on demand
                        String partialPath = "";
                        for (int j=0; j< i; j++) 
                        {
                            if (pathTokens[j].endsWith(File.separator))
                                pathTokens[j] = pathTokens[j].substring(0, 
                                    pathTokens[j].length() -1);
                                
                            partialPath = partialPath + 
                                pathTokens[j] + File.separator;
                        }
                            
                        //logger_.debug(method + "Partial path = "  + partialPath);
                            
                        setTreeFolders(partialPath, current);
                    }
                    
                    FileNode child = new FileNode(pathTokens[i]);
                    child.setParent(current);
                    int idx = current.getIndex(child);
                    
                    //logger_.debug(method + 
                    //    "node " + current + " found at index " + idx);
                        
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
    // Preferences Support
    //--------------------------------------------------------------------------

    /**
     * Saves preferences to a properties object
     * 
     * @param props   Properties to save preferences to
     * @param prefix  Prefix to tack onto the beginning of each property name
     *                to allow for uniqueness if more than one JFileExplorer is
     *                saves to the same properties object.
     */
    public void savePrefs(Properties props, String prefix)
    {
        String path = getCurrentPath();
        if (!StringUtil.isNullOrEmpty(path))
            props.setProperty(prefix + ".explorer.path", path);
            
        String file = (String) fileList_.getSelectedValue();
        if (!StringUtil.isNullOrEmpty(file))
            props.setProperty(prefix + ".explorer.file", file);
            
        int dividerLoc = splitPane_.getDividerLocation();
        PropertiesUtil.setInteger(
            props, prefix + ".explorer.dividerlocation", dividerLoc);
        
    }
    
    /**
     * Restores preferences from a properties object and applies them.
     * 
     * @param props   Properties to read preferences from  
     * @param prefix  Prefix to tack onto the beginning of each property name
     */    
    public void applyPrefs(Properties props, String prefix)
    {
        // Restore expanded directory
        String path = props.getProperty(prefix + PROP_PATH);
        if (!StringUtil.isNullOrEmpty(path))
            selectFolder(path);
        
        // Restore selected file    
        String file = props.getProperty(prefix + PROP_FILE);
        if (!StringUtil.isNullOrEmpty(file))
            fileList_.setSelectedValue(file, true);
        
        // Restore divider location    
        int dividerLoc = PropertiesUtil.getInteger(
            props, prefix + PROP_DIVIDER, 150);
            
        splitPane_.setDividerLocation(dividerLoc);    
    }

    //--------------------------------------------------------------------------
    //  Overridden from java.awt.Component
    //--------------------------------------------------------------------------
    
    /**
     * @return Preferred dimension
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(200, 400);
    }

    //--------------------------------------------------------------------------
    //  Event Notification Support
    //--------------------------------------------------------------------------

    /**
     * Adds a JFileExplorerListener
     *
     * @param  listener  Listener to add
     */
    public void addJFileExplorerListener(JFileExplorerListener listener)
    {
        fileExplorerListeners_.add(listener);
    }

    /**
     * Removes a JFileExplorerListener
     *
     * @param  listener  Listener to remove
     */
    public void removeJFileExplorerListener(JFileExplorerListener listener)
    {
        fileExplorerListeners_.remove(listener);
    }

    /**
     * Fires an event when a file is double clicked by the user
     */
    protected void fireFileDoubleClicked()
    {
        for(Iterator i = fileExplorerListeners_.iterator(); i.hasNext(); )
             ((JFileExplorerListener)i.next()).fileDoubleClicked(getFilePath());
    }
    
    /**
     * Fires an event when a directory is selected
     * 
     * @param  folder  Folder that was selected
     */
    protected void fireFolderSelected(String folder)
    {
        for(Iterator i = fileExplorerListeners_.iterator(); i.hasNext(); )
             ((JFileExplorerListener)i.next()).folderSelected(folder);
    }
    
    /**
     * Fire an event when a directory is double clicked
     * 
     * @param  folder  Folder that was double clicked
     */
    protected void fireFolderDoubleClicked(String folder)
    {
        for(Iterator i = fileExplorerListeners_.iterator(); i.hasNext(); )
             ((JFileExplorerListener)i.next()).folderDoubleClicked(folder); 
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------

    /**
     * Builds the GUI 
     * 
     * @param  verticalSplitter  Splitter orientation
     */
    protected void buildView(boolean verticalSplitter)
    {
        // File system roots combobox
        rootsComboBox_ = new JComboBox(File.listRoots());
        rootsComboBox_.setSelectedItem(new File(getDefaultRoot()));
        rootsComboBox_.addItemListener(new DriveComboListener());
        rootsComboBox_.setRenderer(new DriveIconCellRenderer());
        
        // File list
        fileList_ = new JList();
        fileList_.setModel(listModel_ = new DefaultListModel());
        fileList_.addMouseListener(new FileListMouseListener());
        setFileList(getDefaultRoot());
        fileList_.setFixedCellHeight(15);
        JScrollPane filesScrollPane = new JScrollPane(fileList_);

        // Set up our Tree
        setTreeRoot(getDefaultRoot());

        // Load tree icons        
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
         
        renderer.setClosedIcon(
            ResourceUtil.getResourceAsIcon(
                "/toolbox/util/ui/images/TreeOpen.gif"));

        renderer.setOpenIcon(
            ResourceUtil.getResourceAsIcon(
                "/toolbox/util/ui/images/TreeOpen.gif"));

        renderer.setLeafIcon(
            ResourceUtil.getResourceAsIcon(
                "/toolbox/util/ui/images/TreeClosed.gif"));

        // Directory tree
        treeModel_ = new DefaultTreeModel(rootNode_);
        tree_ = new JTree(treeModel_);
        tree_.setEditable(false);
        
        tree_.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
            
        tree_.setRootVisible(true);
        tree_.setScrollsOnExpand(true);
        
        treeSelectionListener_ = new DirTreeSelectionListener();
        tree_.addTreeSelectionListener(treeSelectionListener_);
        
        tree_.addMouseListener(new DirTreeMouseListener());
        tree_.setCellRenderer(renderer);
        tree_.putClientProperty("JTree.lineStyle", "Angled");

        setTreeFolders(getDefaultRoot(), null);
        JScrollPane foldersScrollPane = new JScrollPane(tree_);

        // Configurable splitter orientation
        if (verticalSplitter)
            splitPane_ = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                foldersScrollPane, filesScrollPane);
        else
            splitPane_ = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                foldersScrollPane, filesScrollPane);

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
              
        splitPane_.setDividerLocation(150);
    }

    /**
     * Sets the root for the JTree.
     *
     * @param root  Root of the tree
     */
    protected void setTreeRoot(String root)
    {
        rootNode_ = new FileNode(root);
        
        if (treeModel_ != null)
            treeModel_.setRoot(rootNode_);

        root_ = root;
    }

    /**
     * Sets the tree folders
     *
     * @param  pathToAddFolders  Path to add folders to
     * @param  currentNode       Current node
     */
    protected void setTreeFolders(String pathToAddFolders,
        DefaultMutableTreeNode currentNode)
    {
        File[] files = 
            new File(pathToAddFolders).listFiles(new DirectoryFilter());
            
        Arrays.sort(files, new FileComparator());
        String[] fileList = new String[files.length];

        for (int i = 0; i < files.length; i++)
            fileList[i] = files[i].getName();

        if (fileList.length > 0)
            addTreeNodes(fileList, currentNode);
    }

    /**
     * Finds, sorts, and adds the files according to the path to the file list.
     *
     * @param  path  Path with files
     */
    protected void setFileList(String path)
    {
        setCurrentPath(path);
        listModel_.clear();
        File f = new File(path);
        File[] files = f.listFiles(new FileFilter());
        Arrays.sort(files, new FileComparator());
        
        for (int i = 0; i < files.length; i++)
            listModel_.addElement(files[i].getName());
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
     * @param  folderList  An array of folders to add
     * @param  parentNode  Parent node of nodes to add
     */
    protected void addTreeNodes(String[] folderList, 
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
     * @return  Default root drive/directory
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

    /**
     * Gets the Drive Icon for the Roots drop down menu display.
     *
     * @return  ImageIcon of the drive
     * @throws  IOException on IO error
     */
    protected Icon getDriveIcon() throws IOException
    {
        if (driveIcon_ == null)
            driveIcon_ = 
                ResourceUtil.getResourceAsIcon(
                    "/toolbox/util/ui/images/HardDrive.gif");
            
        return driveIcon_;
    }

    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * FileNode used to represent directories in the directory tree
     */
    public class FileNode extends DefaultMutableTreeNode
    {
        /**
         * Constructor for FileNode.
         */
        public FileNode()
        {
        }
    
        /**
         * Constructor for FileNode.
         * 
         * @param  userObject  Object to associate with the file node
         */
        public FileNode(Object userObject)
        {
            super(userObject);
        }
    
        /**
         * Constructor for FileNode.
         * 
         * @param  userObject      User object
         * @param  allowsChildren  Should node allow children
         */
        public FileNode(Object userObject, boolean allowsChildren)
        {
            super(userObject, allowsChildren);
        }
        
        /**
         * Compares based on directory/file name. Is sensetive to the host
         * platform w.r.t. case sensetivity
         * 
         * @param  obj  Object to compare
         * @return True if nodes are equal, false otherwise
         */
        public boolean equals(Object obj)
        {
            if (!(obj instanceof DefaultMutableTreeNode))
                return false;
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)obj;
            
            String file1 = (String)getUserObject();
            String file2 = (String)node.getUserObject();
            
            if (Platform.isUnix())
                return file1.equals(file2);
            else
                return file1.equalsIgnoreCase(file2);
        }
    }
    
    /**
     * Inner class that compares file names for sorting.
     */
    private class FileComparator implements Comparator
    {
        /**
         * Compares two files using the file name
         *
         * @param  a   First file
         * @param  b   Second file
         * @return -1 if a<b, 0 if equal, and 1 if a>b
         */
        public int compare(Object a, Object b)
        {
            File fileA = (File) a;
            File fileB = (File) b;
            return fileA.getName().compareToIgnoreCase(fileB.getName());
        }
    }

    /**
     * Inner class for rendering our own display for the Roots drop down menu.
     */
    private class DriveIconCellRenderer extends JLabel
        implements ListCellRenderer
    {
        /**
         * Default constructor
         */
        public DriveIconCellRenderer()
        {
            this.setOpaque(true);
        }

        /**
         * Gets the renderer for the list cell
         *
         * @param   list          JList
         * @param   value         Value
         * @param   index         Index
         * @param   isSelected    boolean
         * @param   cellHasFocus  boolean
         * @return  Component
         */
        public Component getListCellRendererComponent(JList list,
            Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            setText(value.toString());
            
            try
            {
                setIcon(getDriveIcon());
            }
            catch (IOException e)
            {
                logger_.fatal("getListCellRenderer", e);
            }
            
            setBackground(isSelected ? Color.blue : Color.white);
            setForeground(isSelected ? Color.white : Color.black);
            return this;
        }
    }

    /**
     * Inner class for handling click events on the file list.
     */
    private class FileListMouseListener extends MouseAdapter
    {
        /**
         * Handles mouse clicked events
         * 
         * @param  evt  Mouse event to handle
         */
        public void mouseClicked(MouseEvent evt)
        {
            if (evt.getClickCount() == 2 && fileList_.getSelectedIndex() != -1)
            {
                // double click on a file fires event to listeners 
                fireFileDoubleClicked();
            }
            else if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
            {
                ; // nothing tied to right mouse button click
            }
        }
    }

    /**
     * Inner class for handling click event on the JTree.
     */
    private class DirTreeMouseListener extends MouseAdapter
    {
        /**
         * Handles mouse clicks in the tree
         * 
         * @param  evt  Mouse event to handle
         */
        public void mouseClicked(MouseEvent evt)
        {
            if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0 && 
                folderPopup_ != null)
            {
                folderPopup_.show(tree_, evt.getX(), evt.getY());
            }
            else if (evt.getClickCount() == 2)
            {
                fireFolderDoubleClicked(getCurrentPath());
            }
        }
    }

    /**
     * Handles the changing of the selection of the file root (drive letter)
     */
    private class DriveComboListener implements ItemListener
    {
        public void itemStateChanged(ItemEvent ie)
        {
            if (ie.getStateChange() == ItemEvent.SELECTED)
            {
                String fileRoot = rootsComboBox_.getSelectedItem().toString();
                
                //logger_.debug("new drive = " + fileRoot);
                //logger_.debug(Stringz.BR);            
                //logger_.debug(Dumper.dump(ie,5)); 
                //logger_.debug(Stringz.BR);
                //logger_.debug("$$$$$", new Exception("From itemStateChanged()"));
                //logger_.debug(Stringz.BR);
                 
                setFileList(fileRoot);
                clear();
                setTreeRoot(fileRoot);
                setTreeFolders(fileRoot, null);
            }
        }
    }

    /**
     * Inner class to give support for Tree selection events.
     */
    private class DirTreeSelectionListener implements TreeSelectionListener
    {
        /**
         * Called when a selection has changed on the tree
         * 
         * @param  e  TreeSelectionEvent to handle
         */
        public void valueChanged(TreeSelectionEvent e)
        {
            StringBuffer s = new StringBuffer();
            TreePath path = e.getPath();
            Object[] o = path.getPath();
    
            DefaultMutableTreeNode currentNode =
                (DefaultMutableTreeNode) (path.getLastPathComponent());
    
            // Should optimize
            s.append(o[0]);
            
            for (int i = 1; i < o.length; i++)
            {
                if (!o[i - 1].toString().endsWith(File.separator))
                    s.append(File.separator);
                    
                s.append(o[i]);
            }
    
            String folder = s.toString();
            setTreeFolders(folder, currentNode);
            setFileList(folder);
            
            fireFolderSelected(folder);
            
            selectFolder(folder);
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