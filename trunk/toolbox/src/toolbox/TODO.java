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
 * 
 * ========================= JDBC Plugin =======================================
 * TODO: icons for execute all, execute selected, execute current
 * TODO: Ctrl-Up/Down should scroll through query history
 * TODO: Enable connect/disconnect buttons based on state
 * TODO: Build error pane because dialog boxes are annoying
 * TODO: Change prefs saver to save editor contents to a separate file instead
 *       of trying to embed it in toolbox.xml
 * TODO: Update ExecuteCurrentAction to execute multiple sql statements within
 *       a selection.
 * =============================================================================
 * TODO: DocViewer: Fix colors in pollo doc viewer.
 * =============================================================================
 * TODO: FindClass: Reduce startup time.
 * TODO: FindClass: Make table cells editable for text that doesn't fit in cell
 * TODO: FindClass: Add option to have decompiler dump to one textarea or 
 *                  multiple tabs
 * TODO: FindClass: Update JSMartList so that scroll on demand is an option
 * TODO: FindClass: Have decompiler text area rembember font setting if dump 
 *                  to tabs
 * TODO: FindClass: Add a way to close all tabs in the decompiler 
 * TODO: FindClass: Add toggle for tab heading to be FQCN or just the 
 *                  class name
 * TODO: FindClass: Add decompile on select
 * TODO: FindClass: Add additional search criteria: A extends B, A implements C
 * TODO: FindClass: Add timer to see perf.
 * TODO: FindClass: For a selected Jar, make it the search target and autolist
 *                  all the classes within it.
 * =============================================================================
 * TODO: TCPTunnel: Add option just to monitor the count and not output data.
 * TODO: TCPTunnel: Figure out why it freezes up
 * =============================================================================
 * 
 * =============================== COMPLETED ===================================
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