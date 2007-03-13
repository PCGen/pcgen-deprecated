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

import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.mode.Size;
import pcgen.core.Equipment;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.util.Logging;

/**
 * Deals with SIZE token
 */
public class SizeToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "SIZE";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setSize(value, true);
		return true;
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Size size = Size.valueOf(value);
			context.graph.linkObjectIntoGraph(getTokenName(), eq, size);
			return true;
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint("Invalid Size in " + getTokenName() + ": "
				+ value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Set<PCGraphEdge> links =
				context.graph.getChildLinksFromToken(getTokenName(), eq,
					Size.class);
		if (links == null || links.isEmpty())
		{
			return null;
		}
		if (links.size() > 1)
		{
			context.addWriteMessage("Only 1 Size is allowed per Equipment");
			return null;
		}
		Size s = (Size) links.iterator().next().getSinkNodes().get(0);
		return new String[]{s.toString()};
	}
}
