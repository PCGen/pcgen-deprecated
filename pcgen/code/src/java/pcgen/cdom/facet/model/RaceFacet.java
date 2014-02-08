/*
 * Copyright (c) Thomas Parker, 2009.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet.model;

import pcgen.cdom.content.Selection;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.Race;

/**
 * RaceFacet is a Facet that tracks the Race of a Player Character.
 */
public class RaceFacet extends AbstractItemFacet<Race> implements
		DataFacetChangeListener<Selection<Race, ?>>
{
	@Override
	public void dataAdded(DataFacetChangeEvent<Selection<Race, ?>> dfce)
	{
		set(dfce.getCharID(), dfce.getCDOMObject().getObject());
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<Selection<Race, ?>> dfce)
	{
		remove(dfce.getCharID());
	}
}
