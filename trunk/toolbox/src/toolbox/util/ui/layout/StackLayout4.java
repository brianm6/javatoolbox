package toolbox.util.ui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/*
 *  StackLayout.java (Market View Java Applet)
 *  Copyright (C) 1996 Softbear Inc. (info@softbear.com)
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
*/

/**
 * A layoutManager which stacks components one on top of the other,
 * regardless of their size.
 * @version Improved by Mark Grand
*/
public class StackLayout4 implements LayoutManager
{
	private int vgap;
	private static final int DEFAULT_VGAP = 3;

	/**
	 * Create a StackLayout with a default vertical gap.
	 */
	public StackLayout4()
	{
		this(DEFAULT_VGAP);
	} // StackLayout()

	/**
	 * Create a StackLayout with the specified vertical gap.
	 * @param vgap the number of pixels to leave between Components.
	 */
	public StackLayout4(int vgap)
	{
		this.vgap = vgap;
	} // StackLayout(int)

	/**
	 * Adds the specified component with the specified name to
	 * the layout.
	 * <p>
	 * The implementation of this method does not actually do anything
	 * because this LayoutManager uses the Container's information
	 * about its contents.
	 */
	public void addLayoutComponent(String name, Component comp)
	{
	}

	/**
	 * Removes the specified component from the layout.
	 * @param comp the component ot be removed
	 * <p>
	 * The implementation of this method does not actually do anything
	 * because this LayoutManager uses the Container's information
	 * about its contents.
	 */
	public void removeLayoutComponent(Component comp)
	{
	} // removeLayoutComponent(Component)

	/**
	 * Calculates the preferred size dimensions for the specified 
	 * panel given the components in the specified parent container.
	 * @param parent the component to be laid out
	 * @see #minimumLayoutSize
	 */
	public Dimension preferredLayoutSize(Container parent)
	{
		Insets insets = parent.insets();
		int ncomponents = parent.countComponents();
		int w = 0;
		int h = 0;

		for (int i = 0; i < ncomponents; i++)
		{
			Component comp = parent.getComponent(i);
			if (!comp.isVisible())
				continue;
			Dimension d = comp.preferredSize();
			if (w < d.width)
			{
				w = d.width;
			} // if
			h += d.height;
			if (i != 0)
			{
				h += this.vgap;
			} // if
		} // for
		return new Dimension(
			insets.left + insets.right + w,
			insets.top + insets.bottom + h);
	} // preferredLayoutSize(Container)

	/** 
	 * Calculates the minimum size dimensions for the specified 
	 * panel given the components in the specified parent container.
	 * @param parent the component to be laid out
	 * @see #preferredLayoutSize
	 */
	public Dimension minimumLayoutSize(Container parent)
	{
		Insets insets = parent.insets();
		int ncomponents = parent.countComponents();
		int w = 0;
		int h = 0;

		for (int i = 0; i < ncomponents; i++)
		{
			Component comp = parent.getComponent(i);
			if (!comp.isVisible())
				continue;
			Dimension d = comp.minimumSize();
			if (w < d.width)
			{
				w = d.width;
			} // if
			h += d.height;
			if (i != 0)
			{
				h += this.vgap;
			} // if
		} // for
		return new Dimension(
			insets.left + insets.right + w,
			insets.top + insets.bottom + h);
	}

	/** 
	 * Lays out the specified container.
	 * @param parent the Container to lay out 
	 */
	public void layoutContainer(Container parent)
	{
		int availableHeight; // Actual height available layout.
		Insets insets = parent.insets();
		int x = insets.left;
		int y = insets.top;
		Dimension parentSize = parent.size();
		int usableWidth = parentSize.width - insets.left - insets.right;

		int ncomponents = parent.countComponents();
		availableHeight = parentSize.height - insets.top - insets.bottom;
		boolean useMinimum;
		useMinimum = preferredLayoutSize(parent).height > availableHeight;
		for (int i = 0; i < ncomponents; ++i)
		{
			Component comp = parent.getComponent(i);
			if (!comp.isVisible())
				continue;
			Dimension d =
				useMinimum ? comp.minimumSize() : comp.preferredSize();
			comp.reshape(x, y, usableWidth, d.height);
			y += (d.height + this.vgap);
		}
	} // layoutContainer(Container)
} // class StackLayout
