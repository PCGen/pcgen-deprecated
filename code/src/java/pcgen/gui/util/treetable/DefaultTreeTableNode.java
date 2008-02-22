/*
 * DefaultTreeTableNode.java
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
 * Created on Feb 20, 2008, 6:55:50 PM
 */
package pcgen.gui.util.treetable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class DefaultTreeTableNode extends DefaultMutableTreeNode implements TreeTableNode
{
    private Object[] data;

    public DefaultTreeTableNode(Object name, Object[] data)
    {
        super(name);
        this.data = data;
    }

    public Object getValueAt(int column)
    {
        return data[column];
    }

    public void setValueAt(Object value, int column)
    {
        data[column] = value;
    }
}
