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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
	private static final Category logger =
		Category.getInstance(JFileExplorer.class);

    private JComboBox rootsComboBox;
    private JScrollPane filesScrollPane, foldersScrollPane;
    private JSplitPane splitPane;
    private JPopupMenu popup, folderPopup;
    private JMenuItem deleteMenuItem, renameMenuItem, compileMenuItem, antMenuItem;
    private JMenu filterMenuA;
    private JTextArea textArea;
    private DefaultListModel dList = new DefaultListModel();
    private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

    private DefaultMutableTreeNode rootNode;
    private JList fileList = new JList();
    private JTree tree;
    private DefaultTreeModel treeModel;
    private String root, currentPath;
    private FileFilter fileFilter, folderFilter;
    private ImageIcon driveIcon;
    private GridBagLayout gridbag = new GridBagLayout();
    private GridBagConstraints constraints = new GridBagConstraints();

	/** Collection of listeners **/
	private List fileExplorerListeners = new ArrayList();

	/**
	 * Test class
	 */
	static class TestFrame extends JFrame implements JFileExplorerListener, ActionListener
	{
		private JTextField testField;
		private JButton    testButton;
		private JFileExplorer jfe;
		
		public TestFrame()
		{
			super("JFileExplorer");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	        jfe = new JFileExplorer();
	        jfe.addJFileExplorerListener(this);
	        getContentPane().setLayout(new BorderLayout());
	        getContentPane().add(jfe, BorderLayout.CENTER);
	        
	        JPanel testPanel = new JPanel(new FlowLayout());
	        testField = new JTextField(15);
	        testButton = new JButton("Set folder");
	        testButton.addActionListener(this);
	        testPanel.add(new JLabel("Folder"));
	        testPanel.add(testField);
	        testPanel.add(testButton);
	        getContentPane().add(testPanel, BorderLayout.SOUTH);
		}		

    	public void fileDoubleClicked(String file)
    	{
    		logger.info("file " + file + " double clicked");
    	}
    	
    	public void folderSelected(String folder)
    	{
    		logger.info("folder " + folder + " selected");
    	}
    	
    	public void actionPerformed(ActionEvent e)
    	{
    		jfe.selectFolder(this.testField.getText());
    	}
		
	}

    /**
     * Entrypoint. Works better embedded in another app :)
     */
    public static void main(String[] args)
    {
    	/* just launch the file explorer in a jframe..nuttin else */
	    JFrame testFrame = new TestFrame();
 	   	testFrame.pack();
   	 	testFrame.setVisible(true);
    }

	/**
	 * Default constructor
	 */
    public JFileExplorer()
    {
        rootsComboBox = new JComboBox(getRoots());
        rootsComboBox.setSelectedItem(new File(getDefaultRoot()));
        rootsComboBox.addItemListener(new ComboBoxAdapter());
        rootsComboBox.setRenderer(new IconCellRenderer());
        fileList.setModel(dList);
        fileList.addMouseListener(new JFEMouseHandler());
        setFileList(getDefaultRoot());
        fileList.setFixedCellHeight(15);
        fileList.setFont(new Font("Tahoma", Font.PLAIN, 12));
        filesScrollPane = new JScrollPane(fileList);

        // Set up our Tree
        setTreeRoot(getDefaultRoot());
        
       	renderer.setClosedIcon(new ImageIcon(TreeOpenGIF.getBytes()));
       	renderer.setLeafIcon(new ImageIcon(TreeCloseGIF.getBytes()));
       	renderer.setOpenIcon(new ImageIcon(TreeOpenGIF.getBytes()));

        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setRootVisible(true);
        tree.setScrollsOnExpand(true);
        tree.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tree.addTreeSelectionListener(new TreeFolderAdapter());
        tree.addMouseListener(new TreeMouseHandler());
        tree.setCellRenderer(renderer);
        tree.putClientProperty("JTree.lineStyle", "Angled");

        setTreeFolders(getDefaultRoot(), null);
        foldersScrollPane = new JScrollPane(tree);

//        splitPane = new JSplitPane(
//        	JSplitPane.VERTICAL_SPLIT,
//        	filesScrollPane,
//        	foldersScrollPane);

        splitPane = new JSplitPane(
        	JSplitPane.HORIZONTAL_SPLIT,
        	foldersScrollPane,
        	filesScrollPane);


        this.setLayout(gridbag);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        gridbag.setConstraints(rootsComboBox, constraints);
        this.add(rootsComboBox);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 100;
        constraints.weighty = 100;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        gridbag.setConstraints(splitPane, constraints);
        this.add(splitPane);

        splitPane.setDividerLocation(0.25D);
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
        return root;
    }

    /**
     * Sets the root for the JTree.
     *
     * @param root
     */
    private void setTreeRoot(String root)
    {
        rootNode = new DefaultMutableTreeNode(root);
        if (treeModel != null)
        {
            treeModel.setRoot(rootNode);
        }
        this.root = root;
    }

	/**
	 * Sets the tree folders
	 *
	 * @param  pathToAddFolders
	 * @param  currentNode
	 */
    private void setTreeFolders(final String pathToAddFolders,
    	DefaultMutableTreeNode currentNode)
    {
        File[] files = new File(pathToAddFolders).listFiles(getFolderFilter());
        Arrays.sort(files, new FileComparator());
        String[] fileList = new String[files.length];

        for (int i = 0; i < files.length; i++)
        {
            fileList[i] = files[i].getName();
        }

        if (fileList.length > 0)
            addTreeNodes(fileList, currentNode);
    }

    /**
     * Finds, sorts, and adds the files according to the path to the file list.
     *
     * @param file path
     */
    public void setFileList(String path)
    {
        setCurrentPath(path);
        dList.clear();
        File f = new File(path);
        File[] files = f.listFiles(getFileFilter());
        Arrays.sort(files, new FileComparator());
        for (int i = 0; i < files.length; i++)
        {
            dList.addElement(files[i].getName());
        }
    }

    /**
     * Removes all children of the tree root.
     */
    private void clear()
    {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    /**
     * Adds folders to the tree.
     *
     * @param folderList - an array of folders to add
     */
    private void addTreeNodes(String[] folderList, DefaultMutableTreeNode parentNode)
    {
        if (parentNode == null)
            parentNode = rootNode;

        DefaultMutableTreeNode childNode = null;
        for (int i = 0; i < folderList.length; i++)
        {
            childNode = new DefaultMutableTreeNode(folderList[i]);
            // Only insert if it doesn't already exist.
            boolean shouldAdd = true;
            Enumeration e = parentNode.children();
            while (e.hasMoreElements())
            {
                DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) e.nextElement();
                if (tNode.toString().equals(childNode.toString()))
                {
                    // Already exist, we're not going to add.
                    shouldAdd = false;
                    break;
                }
            }
            if (shouldAdd)
            {
                treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            }
        }

        tree.expandPath(new TreePath(parentNode.getPath()));
        tree.scrollPathToVisible(new TreePath(childNode.getPath()));
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
            {
                return roots[i].toString();
            }
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
        return currentPath;
    }

    /**
     * Sets the current directory path
     *
     * @param  currentPath   Path to set explorer to
     */
    public void setCurrentPath(String currentPath)
    {
        this.currentPath = currentPath;
    }

    /**
     * Returns the current file path.
     *
     * @return String
     */
    private String getFilePath()
    {
        StringBuffer s = new StringBuffer();
        s.append(getCurrentPath());
        s.append(File.separator);
        s.append(fileList.getSelectedValue().toString());
        return s.toString();
    }

    /**
     * Returns the folder filter
     *
     * @return FileFilter
     */
    private FileFilter getFolderFilter()
    {
        if (this.folderFilter == null)
            this.folderFilter = new JFileExplorerFolderFilter();

        return folderFilter;
    }

    /**
     * Returns the current file filter
     *
     * @return FileFilter
     */
    private FileFilter getFileFilter()
    {
        if (this.fileFilter == null)
            this.fileFilter = new JFileExplorerFileFilter();

        return fileFilter;
    }

    /**
     * Sets the current file filter
     *
     * @param  fileFilter  Filter for the listbox containing files
     */
    private void setFileFilter(FileFilter fileFilter)
    {
        if (fileFilter != null)
            this.fileFilter = fileFilter;
    }


    /**
     * Gets the Drive Icon for the Roots drop down menu display.
     *
     * @return  ImageIcon
     */
    private ImageIcon getDriveIcon() throws IOException
    {
        if (driveIcon == null)
            driveIcon = new ImageIcon(HardDriveGIF.getBytes());
            
        return driveIcon;
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
            	logger.fatal("getListCellRenderer", e);
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
    	 */
        public void mouseClicked(MouseEvent evt)
        {
            if (evt.getClickCount() == 2 && fileList.getSelectedIndex() != -1)
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
    	 */
        public void mouseClicked(MouseEvent evt)
        {
            if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0 && folderPopup != null)
            {
                folderPopup.show(tree, evt.getX(), evt.getY());
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
    	 */
        public void itemStateChanged(ItemEvent ie)
        {
            setFileList(rootsComboBox.getSelectedItem().toString());
            clear();
            setTreeRoot(rootsComboBox.getSelectedItem().toString());
            setTreeFolders(rootsComboBox.getSelectedItem().toString(), null);
        }
    }

    /**
     * Inner class to give support for Tree selection events.
     */
    private class TreeFolderAdapter implements TreeSelectionListener
    {
    	/**
    	 * Called when a selection has changed on the tree
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
    	 * @param  file  File to scrutinize
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
    	fileExplorerListeners.add(listener);
    }

    /**
     * Removes a JFileExplorerListener
     *
     * @param  listener  The listener to remove
     */
    public void removeJFileExplorerListener(JFileExplorerListener listener)
    {
    	fileExplorerListeners.remove(listener);
    }

    /**
     * Fires an event when a file is double clicked by the user
     */
    protected void fireFileDoubleClicked()
    {
    	for(Iterator i = fileExplorerListeners.iterator(); i.hasNext(); )
    	{
 			JFileExplorerListener listener = (JFileExplorerListener)i.next();
 			listener.fileDoubleClicked(getFilePath());
    	}
    }
    
    /**
     * Fires an event when a directory is selected
     */
    protected void fireFolderSelected(String folder)
    {
    	for(Iterator i = fileExplorerListeners.iterator(); i.hasNext(); )
    	{
 			JFileExplorerListener listener = (JFileExplorerListener)i.next();
 			listener.folderSelected(folder);
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
        
		StringTokenizer st = new StringTokenizer(folder, File.separator);
		List pathList = new ArrayList();
		
		while(st.hasMoreTokens())
		{
			String token = st.nextToken();
			if(token.length() == 2 && token.endsWith(":"))
				token += File.separator;
			logger.debug("[selectFolder] " + token);
			
			pathList.add(new DefaultMutableTreeNode(token));

			DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[pathList.size()];
			
			for(int i=0; i<pathList.size(); i++)
				nodes[i] = new DefaultMutableTreeNode(pathList.get(i));
			
			TreePath treePath = new TreePath(nodes);
				
			//TreePath treePath = new TreePath(
			//	new DefaultMutableTreeNode(
			//		pathList.toArray(new DefaultMutableTreeNode[0])));

//			pathList.add(token);

//			TreePath treePath = new TreePath((String[])pathList.toArray(new String[0]));
					
			tree.setSelectionPath(treePath);
			tree.expandPath(treePath);
		}
		
		TreePath current = tree.getSelectionPath();
		logger.debug("[selectFolder] Should be selected: " + ArrayUtil.toString(current.getPath())); 
		
//		TreePath path = new TreePath();
//		path.getPathComponent();
    }

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
	
			logger.debug("drive=" + drive);

            rootsComboBox.setSelectedItem(new File(drive.toUpperCase()));			
            //setFileList(drive);

            //clear();
            //setTreeRoot(drive);
            //setTreeFolders(folder, null); 			
 			//setCurrentPath(folder);
    	}
    	
    	StringTokenizer st = new StringTokenizer(folder, sep);
    	
    	DefaultMutableTreeNode current = rootNode;
 
 		TreePath tp = new TreePath(rootNode);
 		tree.expandPath(tp);
 		   	
    	while(st.hasMoreTokens())
    	{
    		String nextNode = st.nextToken();
 			tp = tp.pathByAddingChild(new DefaultMutableTreeNode(nextNode));
    		logger.debug(nextNode);
	    	tree.expandPath(tp);  
	    	tree.setSelectionPath(tp);  		
    	}
    	
    	logger.debug("Treepath in select()");
    	dumpTreePath(tp);
    	
    	//tree.setSelectionPath(tp);
    	
    }

    
    protected void dumpTreePath(TreePath tp)
    {
    	Object nodes[] = tp.getPath();
    	
    	for(int i=0; i<nodes.length; i++)
    		logger.debug("Node " + i + "\tClass " + nodes[i].getClass().getName() + "\tString " + nodes[i].toString());
    }
        
}