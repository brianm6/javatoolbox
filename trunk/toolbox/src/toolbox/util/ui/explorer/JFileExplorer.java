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
package toolbox.util.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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

import org.apache.log4j.Category;
import toolbox.util.ArrayUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.images.HardDriveGIF;
import toolbox.util.ui.images.TreeCloseGIF;
import toolbox.util.ui.images.TreeOpenGIF;

/**
 * Tree based file browser widget ripped from
 * an open-source project and heavily modded.
 */
public class JFileExplorer extends JPanel
{
    /** Logger **/
    private static final Category logger_ =
        Category.getInstance(JFileExplorer.class);

    private JComboBox   rootsComboBox_;
    private JPopupMenu  folderPopup_;

    private DefaultListModel        listModel_;
    private DefaultMutableTreeNode  rootNode_;
    
    private JList            fileList_;
    private JTree            tree_;
    private DefaultTreeModel treeModel_;
    private String           root_, 
                             currentPath_;
    private FileFilter       fileFilter_, 
                             folderFilter_;
    private ImageIcon        driveIcon_;


    /** Collection of listeners **/
    private List fileExplorerListeners_ = new ArrayList();

    /**
     * Default constructor
     */
    public JFileExplorer()
    {
        this(true);
    }


    /**
     * Creates a JFileExplorer
     * 
     * @param  verticalSplitter  Set to true if you want the folder and file
     *                           panes to be split by a vertical splitter, 
     *                           otherwise a horizontal splitter will be used.
     */
    public JFileExplorer(boolean verticalSplitter)
    {
        buildView(verticalSplitter);
    }


    /**
     * Builds the GUI 
     * 
     * @param  verticalSplitter  Splitter orientation
     */
    protected void buildView(boolean verticalSplitter)
    {
        // File system roots 
        rootsComboBox_ = new JComboBox(getRoots());
        rootsComboBox_.setSelectedItem(new File(getDefaultRoot()));
        rootsComboBox_.addItemListener(new ComboBoxAdapter());
        rootsComboBox_.setRenderer(new IconCellRenderer());
        
        // File list
        fileList_ = new JList();
        fileList_.setModel(listModel_ = new DefaultListModel());
        fileList_.addMouseListener(new JFEMouseHandler());
        setFileList(getDefaultRoot());
        fileList_.setFixedCellHeight(15);
        fileList_.setFont(SwingUtil.getPreferredSerifFont());
        JScrollPane filesScrollPane = new JScrollPane(fileList_);

        // Set up our Tree
        setTreeRoot(getDefaultRoot());

        // Load tree icons        
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer(); 
        renderer.setClosedIcon(new ImageIcon(TreeOpenGIF.getBytes()));
        renderer.setLeafIcon(new ImageIcon(TreeCloseGIF.getBytes()));
        renderer.setOpenIcon(new ImageIcon(TreeOpenGIF.getBytes()));

        // Directory tree
        treeModel_ = new DefaultTreeModel(rootNode_);
        tree_ = new JTree(treeModel_);
        tree_.setEditable(false);
        tree_.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree_.setRootVisible(true);
        tree_.setScrollsOnExpand(true);
        tree_.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tree_.addTreeSelectionListener(new TreeFolderAdapter());
        tree_.addMouseListener(new TreeMouseHandler());
        tree_.setCellRenderer(renderer);
        tree_.putClientProperty("JTree.lineStyle", "Angled");

        setTreeFolders(getDefaultRoot(), null);
        JScrollPane foldersScrollPane = new JScrollPane(tree_);

        JSplitPane  splitPane;

        // Configurable splitter orientation
        if (verticalSplitter)
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                foldersScrollPane, filesScrollPane);
        else
            splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
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
        gridbag.setConstraints(splitPane, constraints);
        add(splitPane);
              
        // TODO: fix this so its proportional!!!
        splitPane.setDividerLocation(150);
    }


    /**
     * Returns a file array of all available roots.
     *
     * @return File[]
     */
    private File[] getRoots()
    {
        return File.listRoots();
    }


    /**
     * Returns the root of our JTree as a String.
     *
     * @return  String
     */
    private String getTreeRoot()
    {
        return root_;
    }


    /**
     * Sets the root for the JTree.
     *
     * @param root  Root of the tree
     */
    private void setTreeRoot(String root)
    {
        rootNode_ = new SmartTreeNode(root);
        
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
    private void setTreeFolders(final String pathToAddFolders,
        DefaultMutableTreeNode currentNode)
    {
        File[] files = new File(pathToAddFolders).listFiles(getFolderFilter());
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
    public void setFileList(String path)
    {
        setCurrentPath(path);
        listModel_.clear();
        File f = new File(path);
        File[] files = f.listFiles(getFileFilter());
        Arrays.sort(files, new FileComparator());
        
        for (int i = 0; i < files.length; i++)
            listModel_.addElement(files[i].getName());
    }


    /**
     * Removes all children of the tree root.
     */
    private void clear()
    {
        rootNode_.removeAllChildren();
        treeModel_.reload();
    }


    /**
     * Adds folders to the tree.
     *
     * @param  folderList  An array of folders to add
     * @param  parentNode  Parent node of nodes to add
     */
    private void addTreeNodes(String[] folderList, 
        DefaultMutableTreeNode parentNode)
    {
        if (parentNode == null)
            parentNode = rootNode_;

        DefaultMutableTreeNode childNode = null;
        for (int i = 0; i < folderList.length; i++)
        {
            childNode = new SmartTreeNode(folderList[i]);
            
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
     * @return  String
     */
    private String getDefaultRoot()
    {
        File[] roots = getRoots();
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
     * Returns the current directory path.
     *
     * @return String
     */
    private String getCurrentPath()
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
     * Returns the folder filter
     *
     * @return FileFilter
     */
    private FileFilter getFolderFilter()
    {
        if (folderFilter_ == null)
            folderFilter_ = new JFileExplorerFolderFilter();

        return folderFilter_;
    }


    /**
     * Returns the current file filter
     *
     * @return FileFilter
     */
    private FileFilter getFileFilter()
    {
        if (fileFilter_ == null)
            fileFilter_ = new JFileExplorerFileFilter();

        return fileFilter_;
    }


    /**
     * Sets the current file filter
     *
     * @param  fileFilter  Filter for the listbox containing files
     */
    private void setFileFilter(FileFilter fileFilter)
    {
        if (fileFilter != null)
            fileFilter_ = fileFilter;
    }


    /**
     * Gets the Drive Icon for the Roots drop down menu display.
     *
     * @return  ImageIcon of the drive
     * @throws  IOException on IO error
     */
    private ImageIcon getDriveIcon() throws IOException
    {
        if (driveIcon_ == null)
            driveIcon_ = new ImageIcon(HardDriveGIF.getBytes());
            
        return driveIcon_;
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
    private class IconCellRenderer extends JLabel implements ListCellRenderer
    {
        /**
         * Default constructor
         */
        public IconCellRenderer()
        {
            this.setOpaque(true);
        }

        /**
         * Gets the renderer for the list cell
         *
         * @param  list         JList
         * @param  value        Value
         * @param  index        Index
         * @param  isSelected   boolean
         * @param  cellHasFocus boolean
         * @return Component
         */
        public Component getListCellRendererComponent(JList list,
            Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            setText(value.toString());
            
            try
            {
                setIcon(getDriveIcon());
            }
            catch(IOException e)
            {
                logger_.fatal("getListCellRenderer", e);
            }
            
            this.setBackground(isSelected ? Color.blue : Color.white);
            this.setForeground(isSelected ? Color.white : Color.black);
            return this;
        }
    }


    /**
     * Inner class for handling click events on the file list.
     */
    private class JFEMouseHandler extends MouseAdapter
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
                /* double click on a file fires event to listeners */
                fireFileDoubleClicked();
            }
            else if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
            {
                /* nothing tied to right mouse button click */
            }
        }
    }


    /**
     * Inner class for handling click event on the JTree.
     */
    private class TreeMouseHandler extends MouseAdapter
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
     * Inner class to give support for Roots ComboBox changes.
     */
    private class ComboBoxAdapter implements ItemListener
    {
        /**
         * Called when an item state has changed
         * 
         * @param  ie  ItemEvent to handle
         */
        public void itemStateChanged(ItemEvent ie)
        {
            setFileList(rootsComboBox_.getSelectedItem().toString());
            clear();
            setTreeRoot(rootsComboBox_.getSelectedItem().toString());
            setTreeFolders(rootsComboBox_.getSelectedItem().toString(), null);
        }
    }


    /**
     * Inner class to give support for Tree selection events.
     */
    private class TreeFolderAdapter implements TreeSelectionListener
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
        }
    }


    /**
     * Inner class for filtering only folders.
     */
    private class JFileExplorerFolderFilter implements FileFilter
    {
        /**
         * Accepts only directories as folders
         *
         * @param   file  File to scrutinize
         * @return  True if file is accepted, false otherwise
         */
        public boolean accept(File file)
        {
               return file.isDirectory();
        }
    }


    /**
     * Inner class for filtering files
     */
    private class JFileExplorerFileFilter implements FileFilter
    {
        /**
         * Accepts only files
         *
         * @param  file  File to scrutinize
         * @return  True if file is accepted, false otherwise
         */
        public boolean accept(File file)
        {
            return !file.isDirectory();
        }
    }


    /**
     * Adds a JFileExplorerListener
     *
     * @param  listener   The listener to add
     */
    public void addJFileExplorerListener(JFileExplorerListener listener)
    {
        fileExplorerListeners_.add(listener);
    }


    /**
     * Removes a JFileExplorerListener
     *
     * @param  listener  The listener to remove
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
        {
             JFileExplorerListener listener = (JFileExplorerListener)i.next();
             listener.fileDoubleClicked(getFilePath());
        }
    }

    
    /**
     * Fires an event when a directory is selected
     * 
     * @param  folder  Folder that was selected
     */
    protected void fireFolderSelected(String folder)
    {
        for(Iterator i = fileExplorerListeners_.iterator(); i.hasNext(); )
        {
             JFileExplorerListener listener = (JFileExplorerListener)i.next();
             listener.folderSelected(folder);
        }
    }

    
    /**
     * Fire an event when a directory is double clicked
     * 
     * @param  folder  Folder that was double clicked
     */
    protected void fireFolderDoubleClicked(String folder)
    {
        for(Iterator i = fileExplorerListeners_.iterator(); i.hasNext(); )
        {
             JFileExplorerListener listener = (JFileExplorerListener)i.next();
             listener.folderDoubleClicked(folder); 
        }
    }

    
    /**
     * Selects the given folder. Folder is a fully qualified directory structure
     * starting from the file root. 
     * 
     * Windows folder : c:\home\stuff   
     * Unix folder    : /usr/export/home
     * 
     * @param  folder  Folder to select. Must be absolute from a root.
     */
    public void selectFolder(String folder)
    {
        String[] pathTokens = StringUtil.tokenize(folder, File.separator);
     
        if(pathTokens[0].endsWith(":"))
            pathTokens[0] = pathTokens[0] + File.separator;
            
        logger_.debug("Path Tokens: " + ArrayUtil.toString(pathTokens));
        
        DefaultTreeModel model = (DefaultTreeModel)tree_.getModel();
        
        SmartTreeNode root = (SmartTreeNode) model.getRoot();
        
        
        if (root.getUserObject().equals(pathTokens[0]))
        {
            SmartTreeNode current = root;
            logger_.debug("Current " + current + " children = " + current.getChildCount());
            
            
            for(int i=1; i<pathTokens.length; i++)
            {
                if (current.getChildCount() == 0)
                {
                    // expand node on demand
                    
                    String partialPath = "";
                    for (int j=0; j< i; j++)
                    {
                        if (pathTokens[j].endsWith(File.separator))
                            pathTokens[j] = pathTokens[j].substring(0, pathTokens[j].length() -1);
                            
                        partialPath = partialPath + pathTokens[j] + File.separator;
                    }
                        
                    logger_.debug("Partialpath = "  + partialPath);    
                        
                    setTreeFolders(partialPath, current);
                }
                
                
                SmartTreeNode child = new SmartTreeNode(pathTokens[i]);
                child.setParent(current);
                int idx = current.getIndex(child);
                logger_.debug("node " + current + " found at index " + idx);
                current = (SmartTreeNode)current.getChildAt(idx);
            }
            
            TreePath tp = new TreePath(current.getPath());
            tree_.setSelectionPath(tp);
            tree_.scrollPathToVisible(tp);
                
        }
        else
            System.out.println("Root didnt match in model!!!!!");
        
        
        //logger.debug("Treepath in listener");            
        //dumpTreePath(path);
        
        //DefaultMutableTreeNode currentNode =
        //    (DefaultMutableTreeNode) (path.getLastPathComponent());

        // Should optimize
//        s.append(o[0]);
//        for (int i = 1; i < o.length; i++)
//        {
//            if (!o[i - 1].toString().endsWith(File.separator))
//                s.append(File.separator);
//            s.append(o[i]);
//        }


        //setTreeFolders(folder, rootNode);
        //setFileList(folder);
        
        //fireFolderSelected(s.toString());
        
//        StringTokenizer st = new StringTokenizer(folder, File.separator);
//        List pathList = new ArrayList();
//        
//        while(st.hasMoreTokens())
//        {
//            String token = st.nextToken();
//            if(token.length() == 2 && token.endsWith(":"))
//                token += File.separator;
//            logger_.debug("[selectFolder] " + token);
//            
//            pathList.add(new SmartTreeNode(token));
//
//            DefaultMutableTreeNode[] nodes = 
//                new SmartTreeNode[pathList.size()];
//            
//            for(int i=0; i<pathList.size(); i++)
//                nodes[i] = new SmartTreeNode(pathList.get(i));
//            
//            TreePath treePath = new TreePath(nodes);
//
//            {                
//                StringBuffer s = new StringBuffer();
//                Object[] currNodes = treePath.getPath();
//    
//                DefaultMutableTreeNode currentNode =
//                    (DefaultMutableTreeNode) (treePath.getLastPathComponent());
//    
//                // Should optimize
//                s.append(currNodes[0]);
//                for (int i = 1; i < currNodes.length; i++)
//                {
//                    if (!currNodes[i - 1].toString().endsWith(File.separator))
//                        s.append(File.separator);
//                    s.append(currNodes[i]);
//                }
//    
//                String localFolder = s.toString();
//                setTreeFolders(localFolder, currentNode);
//                setFileList(localFolder);
//                
//                //fireFolderSelected(folder);
//            }
//
//            
//            tree_.expandPath(treePath);
//            tree_.makeVisible(treePath);        
//            tree_.setSelectionPath(treePath);
//            
//        }
//        
//        TreePath current = tree_.getSelectionPath();
//        logger_.debug("[selectFolder] Should be selected: " + 
//            ArrayUtil.toString(current.getPath())); 
        
//        TreePath path = new TreePath();
//        path.getPathComponent();
    }

    
    /**
     * @param  folder  ???
     */
    public void selectFolder2(String folder)
    {
        String sep = null;
        
        if (folder.startsWith("/"))    
        {
            /* unix */
            sep = "/";    
        }
        else
        {
            /* assume windows */
            sep = "\\";
             
             /* chop drive letter off */
             String drive = folder.substring(0,3);
             folder = folder.substring(3);           
    
            logger_.debug("drive=" + drive);

            rootsComboBox_.setSelectedItem(
                new File(drive.toUpperCase()));            
                
            //setFileList(drive);

            //clear();
            //setTreeRoot(drive);
            //setTreeFolders(folder, null);             
             //setCurrentPath(folder);
        }
        
        StringTokenizer st = new StringTokenizer(folder, sep);
        
        DefaultMutableTreeNode current = rootNode_;
 
         TreePath tp = new TreePath(rootNode_);
         tree_.expandPath(tp);
                
        while(st.hasMoreTokens())
        {
            String nextNode = st.nextToken();
             tp = tp.pathByAddingChild(new SmartTreeNode(nextNode));
            logger_.debug(nextNode);
            tree_.expandPath(tp);  
            tree_.setSelectionPath(tp);          
        }
        
        logger_.debug("Treepath in select()");
        dumpTreePath(tp);
        
        //tree.setSelectionPath(tp);
        
    }


    /**
     * DUmps treepath to logger
     * 
     * @param  tp  Treepath to dump
     */
    protected void dumpTreePath(TreePath tp)
    {
        Object nodes[] = tp.getPath();
        
        for(int i=0; i<nodes.length; i++)
            logger_.debug("Node " + i + "\tClass " + 
                nodes[i].getClass().getName() + "\tString " + 
                    nodes[i].toString());
    }
    
    public Dimension getPreferredSize()
    {
        return new Dimension(200, 400);
    }
    
    
    /**
     * SmartTreeNode
     */
    public class SmartTreeNode extends DefaultMutableTreeNode
    {
    
        /**
         * Constructor for SmartTreeNode.
         */
        public SmartTreeNode()
        {
            super();
        }
    
        /**
         * Constructor for SmartTreeNode.
         * @param userObject
         */
        public SmartTreeNode(Object userObject)
        {
            super(userObject);
        }
    
        /**
         * Constructor for SmartTreeNode.
         * @param userObject
         * @param allowsChildren
         */
        public SmartTreeNode(Object userObject, boolean allowsChildren)
        {
            super(userObject, allowsChildren);
        }
        
        /**
         * Allows node comparision on the equality of the user objects instead
         * of the reference equality specified in Object.equals()
         * 
         * @param  obj  Object to compare
         */
        public boolean equals(Object obj)
        {
            if (!(obj instanceof DefaultMutableTreeNode))
                return false;
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)obj;
            return getUserObject().equals(node.getUserObject());
        }
    }
}