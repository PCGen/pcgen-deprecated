/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.qualifier.stat;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import pcgen.cdom.helper.PrimitiveChoiceFilter;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.ChooseLstQualifierToken;
import pcgen.util.Logging;

public class RemoveToken implements ChooseLstQualifierToken<PCStat>
{
	private PrimitiveChoiceFilter<PCStat> pcs = null;

	public String getTokenName()
	{
		return "REMOVE";
	}

	public Class<PCStat> getChoiceClass()
	{
		return PCStat.class;
	}

	public Set<PCStat> getSet(PlayerCharacter pc)
	{
		Set<PCStat> stats =
				pc.getContext().ref.getConstructedCDOMObjects(PCStat.class);
		if (stats != null && pcs != null)
		{
			for (Iterator<PCStat> it = stats.iterator(); it.hasNext();)
			{
				if (pcs.allow(pc, it.next()))
				{
					it.remove();
				}
			}
		}
		return stats;
	}

	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getTokenName());
		if (pcs != null)
		{
			sb.append('[').append(pcs.getLSTformat()).append(']');
		}
		return sb.toString();
	}

	public boolean initialize(LoadContext context, Class<PCStat> cl, String condition, String value)
	{
		if (condition != null)
		{
			Logging.addParseMessage(Level.SEVERE, "Cannot make "
					+ getTokenName()
					+ " into a conditional Qualifier, remove =");
			return false;
		}
		if (value != null)
		{
			pcs = ChooseLoader.getPrimitiveChoiceFilter(context, cl, value);
			return pcs != null;
		}
		return true;
	}
}
