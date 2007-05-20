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
package plugin.lsttokens.equipmentmodifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import pcgen.base.util.TypeSafeMap;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.modifier.ChangeArmorType;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.util.Logging;

/**
 * Deals with ARMORTYPE token
 */
public class ArmortypeToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "ARMORTYPE";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.setArmorType(value);
		return true;
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.errorPrint(getTokenName()
				+ " has no PIPE character: Must be of the form old|new");
			return false;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.errorPrint(getTokenName()
				+ " has too many PIPE characters: "
				+ "Must be of the form old|new");
			return false;
		}
		/*
		 * TODO Are the ArmorTypes really a subset of Encumbrence?
		 */
		try
		{
			Type oldType = Type.getConstant(value.substring(0, pipeLoc));
			Type newType = Type.getConstant(value.substring(pipeLoc + 1));
			/*
			 * TODO Need some check if the Armor Types in value are not valid...
			 * does the above throw exceptions, etc.
			 */
			ChangeArmorType cat = new ChangeArmorType(oldType, newType);
			context.graph.grant(getTokenName(), mod, cat);
		}
		catch (IllegalArgumentException e)
		{
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		GraphChanges<ChangeArmorType> changes =
				context.graph.getChangesFromToken(getTokenName(), mod,
					ChangeArmorType.class);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		TypeSafeMap<Type, Type> m = new TypeSafeMap<Type, Type>(Type.class);
		for (LSTWriteable ab : added)
		{
			ChangeArmorType cat = (ChangeArmorType) ab;
			Type source = cat.getSourceType();
			Type result = cat.getResultType();
			m.put(source, result);
		}
		List<String> list = new ArrayList<String>();
		for (Entry<Type, Type> me : m.entrySet())
		{
			StringBuilder sb = new StringBuilder();
			sb.append(me.getKey());
			sb.append(Constants.PIPE);
			sb.append(me.getValue());
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}
}
