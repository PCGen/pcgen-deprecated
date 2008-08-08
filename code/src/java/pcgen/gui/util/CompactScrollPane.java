/*
 * CompactScrollPane.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Aug 7, 2008, 5:09:31 PM
 */
package pcgen.gui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CompactScrollPane extends JScrollPane
{

    public CompactScrollPane()
    {
        super(JScrollPane.VERTICAL_SCROLLBAR_NEVER,
              JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    @Override
    public Dimension getMinimumSize()
    {
        JViewport view = getViewport();
        if (view != null)
        {
            Dimension size = view.getMinimumSize();
            view = getRowHeader();
            if (view != null)
            {
                size.width += view.getMinimumSize().width;
            }
            view = getColumnHeader();
            if (view != null)
            {
                size.height += view.getMinimumSize().height;
            }
            Insets insets = getInsets();
            size.width += insets.left + insets.right;
            size.height += insets.top + insets.bottom;
            return size;
        }
        return super.getMinimumSize();
    }

    @Override
    public Dimension getPreferredSize()
    {
        Component view = getViewport().getView();
        if (view != null)
        {
            Dimension size = view.getPreferredSize();
            view = getRowHeader();
            if (view != null)
            {
                size.width += view.getPreferredSize().width;
            }
            view = getColumnHeader();
            if (view != null)
            {
                size.height += view.getPreferredSize().height;
            }
            Insets insets = getInsets();
            size.width += insets.left + insets.right;
            size.height += insets.top + insets.bottom;
            return size;
        }
        return super.getPreferredSize();
    }

}
