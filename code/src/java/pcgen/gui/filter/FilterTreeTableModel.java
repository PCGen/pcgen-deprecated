/*
 * FilterTreeTableModel.java
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
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
 * Created on Feb 8, 2008, 3:33:34 PM
 */

package pcgen.gui.filter;

import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.PObjectNode;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class FilterTreeTableModel extends AbstractTreeTableModel{
    private String name;
    public FilterTreeTableModel(String name)
    {
	super(new PObjectNode());
	this.name = name;
    }
    public int getColumnCount()
    {
	return 1;
    }

    public String getColumnName(int column)
    {
	return name;
    }

    public Object getValueAt(Object node, int column)
    {
	return node.toString();
    }
    
    @Override
    public Class<?> getColumnClass(int column)
    {
	return String.class;
    }
}
