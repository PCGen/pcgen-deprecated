/*
 * FilterView.java
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
 * Created on Feb 11, 2008, 12:47:14 AM
 */

package pcgen.gui.filter;

import java.util.List;
import pcgen.gui.utils.treeview.TreeView;
import pcgen.gui.utils.treeview.TreeViewPath;
import pcgen.util.PropertyFactory;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public enum FilterView implements TreeView<PObjectFilter>
{
    NAME("in_nameLabel"),
    CATAGORY_NAME("");
    
    private String name;
    private FilterView(String key)
    {
	this.name = PropertyFactory.getString(key);
    }
    public String getViewName()
    {
	return name;
    }

    public List<TreeViewPath<PObjectFilter>> getPaths(PObjectFilter pobj)
    {
	throw new UnsupportedOperationException("Not supported yet.");
    }
}
