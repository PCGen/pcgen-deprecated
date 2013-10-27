/*
 * RaceSubTypeFilter.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on November 27, 2007
 */
package pcgen.gui.filter;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.util.PropertyFactory;

/**
 * <code>RaceSubTypeFilter</code> is a filter for races by race sub type.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
final class RaceSubTypeFilter extends AbstractPObjectFilter
{
	private String raceSubTypeName;

	/**
	 * Create a new RaceTypeFilter for a race type name
	 * @param raceSubTypeName The name to be matched.
	 */
	RaceSubTypeFilter(String raceSubTypeName)
	{
		super(PropertyFactory.getString("in_filterRaceSubType"), raceSubTypeName); //$NON-NLS-1$
		this.raceSubTypeName = raceSubTypeName;
	}

	
	/* (non-Javadoc)
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Race)
		{
			return ((Race) pObject).getRacialSubTypes().contains(raceSubTypeName);
		}

		return true;
	}
}
