package toolbox;

/**
 * Just a place where project wide to do items are lumped...
 * <pre>
 * TODO: SSH on CVS
 * TODO: Move Tomcat server to booger2
 * TODO: Add vizant for visualization of build dependencies
 * TODO: Add support to minimize to system tray - systray4j
 * TODO: Integrate automatic build number generation into the build process.
 * TODO: Get PropertyPrompt working properly
 * TODO: Fix tailing of Log4j in JTail
 * TODO: Add detach notification to tail.
 * TODO: Upgrade to CLI2
 * TODO: add .cvspass to conf
 * TODO: Add icons to statusbar.
 * TODO: Add way to copy the current selection from the file explorer dir pane.
 * TODO: Really pick apart JSmartTextArea and figure out a good way optimizing
 *       the addition of large sections of text. Unhook model, insert, rehook 
 *       model is a possiblity.
 * TODO: Add a filled bar mode to JSourceView
 * TODO: Create JSmartDialog - rememver size and position, bind esc to cancel
 * TODO: Writer a proper multisplit pane.
 * TODO: Figure out how to use forms
 * TODO: Add tile and cascade to Desktop plugin host
 * TODO: Integrate new icons from 
 *       http://dev.eclipse.org/viewcvs/index.cgi/~checkout~/jdt-ui-home/r3_0/proposals/icons/
 * TODO: Make plugins detachable
 * TODO: Write log4j pattern layout that combines class name and method
 * TODO: Investigate JModalWindow
 * TODO: Update banner with more external fonts.
 * TODO: Fix JTail recent menu
 * TODO: Update all command line tools using new help format in findclass
 * TODO: Tree - add inverse regex, case sensetive regex, and html output
 * TODO: Change String parameter to a File object in FileExplorerListener
 * TODO: Added number of files/selected files to the JFileExplorer InfoBar
 * =============================================================================
 * TODO: DocViewer: Fix colors in pollo doc viewer.
 * =============================================================================
 * 								JDBCPlugin
 * =============================================================================
 * TODO: icons for execute all, execute selected, execute current
 * TODO: Ctrl-Up/Down should scroll through query history
 * TODO: Build error pane because dialog boxes are annoying
 * TODO: Update ExecuteCurrentAction to execute multiple sql statements within
 *       a selection.
 * TODO: Switch between grid and text view
 * TODO: Add sql formatter preferences to prefsview
 * TODO: Lift more stuff from hsqldb manager
 * TODO: Move benchmark to its own panel with preferences.
 * TODO: Base 64 encode passwords (statcvs, jdbcplugin, etc)
 * =============================================================================
 * 								FindClassPlugin
 * =============================================================================
 * TODO: Reduce startup time.
 * TODO: Make table cells editable for text that doesn't fit in cell
 * TODO: Add option to have decompiler dump to one textarea or multiple tabs
 * TODO: Update JSMartList so that scroll on demand is an option
 * TODO: Have decompiler text area rembember font setting if dump to tabs
 * TODO: Add a way to close all tabs in the decompiler 
 * TODO: Add toggle for tab heading to be FQCN or just the class name
 * TODO: Add decompile on select
 * TODO: Add additional search criteria: A extends B, A implements C
 * TODO: Add timer to see perf.
 * TODO: For a selected Jar, make it the search target and autolist all the 
 *       classes within it.
 * TODO: Add size and index number columns to the search target page.
 * =============================================================================
 * TODO: TCPTunnel: Add option just to monitor the count and not output data.
 * TODO: TCPTunnel: Figure out why it freezes up
 * =============================================================================
 * 
 * =============================== COMPLETED ===================================
 *
 * June 2004
 *  - Updated Jode to 1.1.2-pre1
 *  - Added StringUtil.indent()
 *  - Imported ThrottledInput/OutputSream
 *  - Added ThroughputMonitor component
 *  - Added TransferredMonitor component
 *  - Added MonitoredInput/OutputStream
 *  - Updated to 
 *    commons-dbcp-1.2.1 
 *    commons-pool-1.2 
 *    commons-logging-1.0.4
 *    xom-1.0a2
 *  - Look and feel decorations flag now works but requires a restart
 *  - Minor updates to NetMeter - button states, init state, etc.
 *  - Added JavaViewer to DocumentViewerPlugin
 *  - Added SortedListModel
 * 
 * May 2004
 *  - Added JDBC benchmark to JDBC plugin
 *  - Added NapkinLookAndFeel to the Look and Feel menu
 *  - Upgraded to commons-net-1.2.1
 *  - Encoded passwords in JDBC plugin
 *  - StatcvsPlugin cvs project combobox is sorted
 *  - Added connection pooling to JDBCUtil using commons-dbcp
 *  - Upgraded to commons-io-1.0
 *  - Added 3D look and feel
 *  - Added a tree based DTD viewer to the DocumentViewerPlugin
 *  - Updated usage template in FindClass
 *  - Updated FileComparator to use static instances
 *  - Relaced ClassUtil.stripClass and stripPackage with commons-lang equivalent
 *  - Removed toolbox.util.Assert and replaced with Validate from commons-lang
 *  - Added maxdepth (-l) and show fullpath (-p) flags to Tree
 * 
 * April 2004 
 * 	- Scan source for refactorings to use DisposeAction 
 * 	- Updated to the latest Sun TableSorter
 * 	- Integrated statcvs-xml from berlios into the StatCVS plugin
 * 	- Add HTTP proxy support (changeable via the preferences dialog) 
 * 	- Added group Javadoc separators to the maven project.xml
 * 	- Update to latest Multivalent
 * 	- Updated JFreeChart to 0.9.18 
 * 	- Alphasorted the Plugin menu
 *  - Replaced select methods in StringUtil and StreamUtil with apache commons
 *  - Fixed splash image to use a blown up version of the regular toolbox icon
 *  - Made the jar search activity an async operation in the FindClassPlugin  
 *  - JEditTextArea now has ability to save the contents between instances
 *  - JDBCPlugin sql editor now makes use of JEditTextArea saving contents  
 *  - SQL stmts in editor popup menu are now formatted multiline
 *  - Connect/Disconnect buttons in JDBCPlugin merged into a single button 
 *  - SearchTargetPanel extracted from FindClassPane as top level class
 *  - Added tooltips to the plugin menu items
 * 
 * February 2004
 * 	- Add Plugin menu
 * 	- Separate unit tests from main source branch + reconfig maven.
 * 	- Created JSmartInternalFrame
 * 	- DesktopPluginHost now rembers window position and sizes
 * 	- Add wildcard matching to the file switch in tree
 * ============================================================================= 
 * </pre>
 */
public class TODO
{
}