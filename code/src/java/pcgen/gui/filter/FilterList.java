/*
 * FilterList.java
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
 * Created on Jun 18, 2008, 8:36:09 PM
 */
package pcgen.gui.filter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FilterList<E>
{

    private final List<FilterListListener<E>> listeners;
    private List<NamedFilter<? super E>> filters = null;

    public FilterList()
    {
        this.listeners = new LinkedList<FilterListListener<E>>();
        this.filters = Collections.emptyList();
    }

    public void setFilters(List<NamedFilter<? super E>> filters)
    {
        FilterListEvent<E> event = new FilterListEvent<E>(this, this.filters,
                                                          filters);
        this.filters = filters;
        fireFiltersChanged(event);
    }

    private void fireFiltersChanged(FilterListEvent<E> event)
    {
        for (FilterListListener<E> listener : listeners)
        {
            listener.filtersChanged(event);
        }
    }

    public void addFilterListListener(FilterListListener<E> listener)
    {
        listeners.add(listener);
    }

    public void removeFilterListListener(FilterListListener<?> listener)
    {
        listeners.remove(listener);
    }

    public List<NamedFilter<? super E>> getFilters()
    {
        return filters;
    }

}
