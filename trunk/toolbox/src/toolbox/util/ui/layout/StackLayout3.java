package toolbox.util.ui.layout;

import java.awt.Insets;

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
*/

public class StackLayout3 implements java.awt.LayoutManager
{

	int vgap;

    public StackLayout3()
    {
        this(5);
    }
    
	public StackLayout3(int vgap)
	{
		this.vgap = vgap;
	}

	public void addLayoutComponent(
		java.lang.String name,
		java.awt.Component comp)
	{
	}

	public java.awt.Dimension preferredLayoutSize(java.awt.Container parent)
	{
		Insets insets = parent.getInsets();
		int ncomponents = parent.getComponentCount();
		int w = 0;
		int h = 0;

		for (int i = 0; i < ncomponents; i++)
		{
			java.awt.Component comp = parent.getComponent(i);
			java.awt.Dimension d = comp.getPreferredSize();
			if (w < d.width)
			{
				w = d.width;
			}
			h += d.height;
			if (i != 0)
			{
				h += this.vgap;
			}
		}
		return new java.awt.Dimension(
			insets.left + insets.right + w,
			insets.top + insets.bottom + h);
	}

	public void layoutContainer(java.awt.Container parent)
	{
		java.awt.Insets insets = parent.getInsets();
		int x = insets.left;
		int y = insets.top;
		int w = this.preferredLayoutSize(parent).width;

		int ncomponents = parent.getComponentCount();
		for (int i = 0; i < ncomponents; ++i)
		{
			java.awt.Component comp = parent.getComponent(i);
			java.awt.Dimension d = comp.getPreferredSize();
			comp.setBounds(x, y, w, d.height);
			y += (d.height + this.vgap);
		}
	}

	public java.awt.Dimension minimumLayoutSize(java.awt.Container parent)
	{
		return preferredLayoutSize(parent);
	}

	public void removeLayoutComponent(java.awt.Component comp)
	{
	}
}