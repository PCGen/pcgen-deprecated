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
package plugin.qualifier.pobject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.helper.PrimitiveChoiceFilter;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.ChooseLstGlobalQualifierToken;
import pcgen.util.Logging;

public class PCToken<T extends CDOMObject> implements
		ChooseLstGlobalQualifierToken<T>
{

	private Class<T> refClass;

	private PrimitiveChoiceFilter<T> pcs = null;

	public String getTokenName()
	{
		return "PC";
	}

	public boolean initialize(LoadContext context, Class<T> cl, String condition, String value)
	{
		if (condition != null)
		{
			Logging.addParseMessage(Level.SEVERE, "Cannot make "
					+ getTokenName()
					+ " into a conditional Qualifier, remove =");
			return false;
		}
		if (cl == null)
		{
			throw new IllegalArgumentException();
		}
		refClass = cl;
		if (value != null)
		{
			pcs = context.getPrimitiveChoiceFilter(cl, value);
			return pcs != null;
		}
		return true;
	}

	public Class<T> getChoiceClass()
	{
		return refClass;
	}

	public Set<T> getSet(CharacterDataStore pc)
	{
		List<T> objects = pc.getActiveGraph().getGrantedNodeList(refClass);
		Set<T> returnSet = new HashSet<T>();
		if (objects != null && pcs != null)
		{
			for (T po : objects)
			{
				if (pcs.allow(pc, po))
				{
					returnSet.add(po);
				}
			}
		}
		return returnSet;
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
}
