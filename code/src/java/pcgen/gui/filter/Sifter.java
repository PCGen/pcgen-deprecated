/*
 * FilterAdapter.java
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
 * Created on Feb 6, 2008, 4:39:54 PM
 */
package pcgen.gui.filter;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * This class is a replacement for FilterAdapterPanel
 * Subclasses of this class are expected to initialize their
 * filters by calling the FilterFactory registerAll methods.
 * 
 * The easiest way to implement this class is as an inner class
 * of a display component.
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public abstract class Sifter implements Filter
{

    private static final Set<FilterMode> defaultAvailableModes =
	    Collections.unmodifiableSet(EnumSet.allOf(FilterMode.class));
    /**
     * The current filter mode, default is MATCH_ALL.
     */
    private FilterMode filterMode = FilterMode.MATCH_ALL;
    
    private final Set<PObjectFilter> availableFilters = new HashSet<PObjectFilter>();
    private final Set<PObjectFilter> selectedFilters = new HashSet<PObjectFilter>();
    /**
     * Because Collections unmodifiable set is only a wrapper for the set
     * all changes to the underlying set will be reflected in its
     * unmodifiable view. This saves the trouble of calling the
     * Collections.unmodifiableSet method every time one calls the 
     * filter getter methods.
     */
    private final Set<PObjectFilter> availableView = Collections.unmodifiableSet(availableFilters);
    private final Set<PObjectFilter> selectedView = Collections.unmodifiableSet(selectedFilters);
    private final QuickFilter qFilter = new QuickFilter();
    
    public final Set<PObjectFilter> getAvailableFilters()
    {
	return availableView;
    }

    final void setSelectedFilterMode(FilterMode mode)
    {
	this.filterMode = mode;
    }

    public final FilterMode getSelectedFilterMode()
    {
	return filterMode;
    }

    public final Set<PObjectFilter> getSelectedFilters()
    {
	return selectedView;
    }

    final void setSelectedFilters(Set<PObjectFilter> filters)
    {
	selectedFilters.clear();
	selectedFilters.addAll(filters);
	availableFilters.removeAll(filters);
	refreshFiltering();
    }
    
    public boolean hasQFilter()
    {
	return qFilter.getQuery() != null;
    }
    
    final void setQFilter(String filter)
    {
	qFilter.setQuery(filter);
	refreshFiltering();
    }

    /**
     * Gets the FilterModes that are usable for this Sifter
     * This returns all FilterModes by default, it must be overriden
     * to do otherwise.
     * @return A set of FilterModes, ideally an EnumSet
     */
    public Set<FilterMode> getAvailableFilterModes()
    {
	return defaultAvailableModes;
    }

    /**
     * convenience method<br>
     * adds a filter to the list of available filters for this Filterable
     *
     * <br>author: Thomas Behr
     *
     * @param filter   the filter to be registered
     */
    protected final void registerFilter(PObjectFilter filter)
    {
	if (filter != null)
	{
	    availableFilters.add(filter);
	}
    }

    /**
     * re-applies the selected filters;
     * has to be called after changes to the filter selection<br>
     * implementation of Filterable interface
     *
     * <br>author: Thomas Behr
     */
    public abstract void refreshFiltering();

    /**
     * initializes filters<br>
     * implementations are expected to call the FilterFactory register methods
     */
    public abstract void initializeFilters();

    public boolean accept(final PlayerCharacter aPC, final PObject pObject)
    {
	if (pObject == null || !qFilter.accept(aPC, pObject))
	{
	    return false;
	}

	final FilterMode mode = getSelectedFilterMode();
	for (PObjectFilter filter : getSelectedFilters())
	{
	    boolean accepts = filter.accept(aPC, pObject);
	    switch (mode)
	    {
		case MATCH_ALL:
		    if (!accepts)
		    {
			return false;
		    }
		    break;
		case MATCH_ALL_NEGATE:
		    if (!accepts)
		    {
			return true;
		    }
		    break;
		case MATCH_ANY:
		    if (accepts)
		    {
			return true;
		    }
		    break;
		case MATCH_ANY_NEGATE:
		    if (accepts)
		    {
			return false;
		    }
		    break;
	    }
	}
	switch (mode)
	{
	    case MATCH_ANY:
	    case MATCH_ALL_NEGATE:
		return selectedFilters.isEmpty();
	    default:
		return true;
	}
    }

}
