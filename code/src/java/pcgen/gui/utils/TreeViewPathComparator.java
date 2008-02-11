/*
 * TreeViewPathComparator.java
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
 * Created on Feb 10, 2008, 3:06:15 AM
 */
package pcgen.gui.utils;

import java.util.Comparator;
import pcgen.core.PObject;
import pcgen.util.StringComparator;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class TreeViewPathComparator<E extends PObject> implements Comparator<TreeViewPath<E>>
{
    private static final Comparator<Object> defaultcomparator = new StringComparator<Object>();
    private final Comparator<? super E> pobjectcomparator;
    private final Comparator<Object> objectcomparator;

    public TreeViewPathComparator()
    {
	this(defaultcomparator);
    }
    
    public TreeViewPathComparator(Comparator<? super E> pobjectcomparator)
    {
	this(pobjectcomparator, defaultcomparator);
    }

    public TreeViewPathComparator(Comparator<? super E> pobjectcomparator, Comparator<Object> objectcomparator)
    {
	this.pobjectcomparator = pobjectcomparator;
	this.objectcomparator = objectcomparator;
    }

    public int compare(TreeViewPath o1, TreeViewPath o2)
    {
	int length1 = o1.getPathCount();
	int length2 = o2.getPathCount();
	for (int index = 0; index < length1 && index < length2; index++)
	{
	    Object obj1 = o1.getPathComponent(index);
	    Object obj2 = o2.getPathComponent(index);
	    int comp;
	    if (obj1 instanceof String || obj2 instanceof String)
	    {
		comp = objectcomparator.compare(obj1, obj2);
	    }
	    else
	    {
		comp = pobjectcomparator.compare((E)obj1, (E)obj2);
	    }
	    if (comp != 0)
	    {
		return comp;
	    }
	}
	if (length1 < length2)
	{
	    return -1;
	}
	else if (length1 > length2)
	{
	    return 1;
	}
	else
	{
	    return 0;
	}
    }

}
