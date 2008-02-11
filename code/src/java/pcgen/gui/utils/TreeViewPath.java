/*
 * TreeViewPath.java
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
 * Created on Feb 10, 2008, 5:29:12 PM
 */
package pcgen.gui.utils;

import javax.swing.tree.TreePath;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class TreeViewPath<E> extends TreePath
{

    /**
     * This saves the trouble of saying:<br>
     * new TreePath(new Object[]{string1, string2, pobj})<br>
     * instead you can now say:<br>
     * new TreeViewPath(pobj, string1, string2)
     * @param obj the last element in the list
     * @param path the string path leading to the last element
     */
    public TreeViewPath(E pobj, String path)
    {
	super(new TreePath(path), pobj);
    }

    public TreeViewPath(E pobj, String... path)
    {
	super(new TreePath(path), pobj);
    }

    public TreeViewPath(E pobj)
    {
	super(pobj);
    }

    public TreeViewPath(E... pobjs)
    {
	super(pobjs, pobjs.length);
    }

    public TreeViewPath(Object[] path, E pobj)
    {
	super(new TreePath(path), pobj);
    }

    @Override
    public E getLastPathComponent()
    {
	return (E) super.getLastPathComponent();
    }

}
