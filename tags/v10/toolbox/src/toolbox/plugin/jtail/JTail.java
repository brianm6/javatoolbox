/*
 * (c) Copyright 2001 MyCorporation.
 * All Rights Reserved.
 */
package toolbox.jtail;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;

/**
 * @version 	1.0
 * @author
 */
public class JTail extends JFrame
{
	JDesktopPane desktop;
	JTailExplorer jte;
		
	/**
	 * Entry point 
	 */
	public static void main(String[] args)
	{
		JTail jtail = new JTail();
		jtail.setVisible(true);
	}

	/**
	 * Constructor for JTail.
	 * @throws HeadlessException
	 */
	public JTail() 
	{
		this("JTail");
	}

	/**
	 * Constructor for JTail.
	 * @param title
	 * @throws HeadlessException
	 */
	public JTail(String title) 
	{
		super(title);
		
		BasicConfigurator.configure();
		
		build();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
	}
	
	/**
	 * Builds the GUI
	 */
	protected void build()
	{
		desktop = new JDesktopPane();

		jte = new JTailExplorer();
		jte.pack();
		jte.setVisible(true);
		desktop.add(jte);

		//JInternalFrame jif = new JInternalFrame("JTail", true);
		JInternalFrame jif = new JTailRunner("Hello!");
		//jif.getContentPane().add(new JLabel("Hellow rold!"));
		jif.pack();
        jif.setVisible(true); //necessary as of kestrel
        desktop.add(jif);
        
       try 
		{
            jif.setSelected(true);
        } 
       catch (java.beans.PropertyVetoException e) 
        {
        }
		
		setContentPane(desktop);
		//setSize(400,300);
		pack();
		
	}

}
