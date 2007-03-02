/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.equipment;

import java.util.Set;

import pcgen.cdom.content.Weight;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Equipment;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with WT token
 */
public class WtToken implements EquipmentLstToken
{

	/**
	 * Get token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "WT";
	}

	/**
	 * Parse WT token, set the equipment weight
	 * 
	 * @param eq
	 * @param value
	 * @return true
	 */
	public boolean parse(Equipment eq, String value)
	{
		eq.setWeight(value);
		return true;
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Weight w = new Weight(Double.parseDouble(value));
			context.graph.linkObjectIntoGraph(getTokenName(), eq, w);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public String unparse(LoadContext context, Equipment eq)
	{
		Set<PCGraphEdge> edgeList =
				context.graph.getChildLinksFromToken(getTokenName(), eq,
					Weight.class);
		if (edgeList.isEmpty())
		{
			return null;
		}
		if (edgeList.size() > 1)
		{
			context
				.addWriteMessage("A Piece of Equipment is only allowed one Weight");
			return null;
		}
		PCGraphEdge edge = edgeList.iterator().next();
		Weight w = (Weight) edge.getSinkNodes().get(0);
		return new StringBuilder().append(getTokenName()).append(':').append(
			w.getWeight()).toString();
	}
}
