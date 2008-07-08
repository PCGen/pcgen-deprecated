/*
 * TreeViewUtil.java
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
 * Created on Jul 7, 2008, 6:17:31 PM
 */
package pcgen.gui.util.treeview;

import java.util.AbstractList;
import java.util.List;
import pcgen.gui.util.treetable.DefaultSortableTreeTableNode;
import pcgen.gui.util.treetable.SortableTreeTableNode;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class TreeViewUtil
{

    private TreeViewUtil()
    {
    }

    public static <T> SortableTreeTableNode createSortableTreeTableNode(T obj,
                                                                          DataView<T> view)
    {
        return new DefaultSortableTreeTableNode(new DataList(obj, view));
    }

    private static class DataList extends AbstractList<Object>
    {

        private Object obj;
        private List<?> data;

        public <T> DataList(T obj, DataView<T> view)
        {
            this.obj = obj;
            this.data = view.getData(obj);
        }

        @Override
        public Object get(int index)
        {
            switch (index)
            {
                case 0:
                    return obj;
                default:
                    return data.get(index - 1);
            }
        }

        @Override
        public int size()
        {
            return data.size() + 1;
        }

    }
}
