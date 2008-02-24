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
 * Current Ver: $Revision: 197 $
 * Last Editor: $Author: nuance $
 * Last Edited: $Date: 2006-03-14 17:59:43 -0500 (Tue, 14 Mar 2006) $
 */
package plugin.lsttokens.equipment;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.Quality;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with ACCHECK token
 */
public class QualityToken implements EquipmentLstToken, CDOMPrimaryToken<CDOMEquipment>
{

	public String getTokenName()
	{
		return "QUALITY";
	}

	public boolean parse(Equipment eq, String value)
	{
		StringTokenizer tok = new StringTokenizer(value, "|");
		String key = "";
		String val = "";
		if (tok.hasMoreTokens())
		{
			key = tok.nextToken();
		}
		if (tok.hasMoreTokens())
		{
			val = tok.nextToken();
		}
		eq.setQuality(key, val);
		return true;
	}

	public boolean parse(LoadContext context, CDOMEquipment eq, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.errorPrint(getTokenName() + " expecting '|', format is: "
				+ "QualityType|Quality value was: " + value);
			return false;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.errorPrint(getTokenName() + " expecting only one '|', "
				+ "format is: QualityType|Quality value was: " + value);
			return false;
		}
		String key = value.substring(0, pipeLoc);
		if (key.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " expecting non-empty type, "
				+ "format is: QualityType|Quality value was: " + value);
			return false;
		}
		String val = value.substring(pipeLoc + 1);
		if (val.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " expecting non-empty value, "
				+ "format is: QualityType|Quality value was: " + value);
			return false;
		}
		context.getObjectContext().addToList(eq, ListKey.QUALITY,
			new Quality(key, val));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMEquipment eq)
	{
		Changes<Quality> changes =
				context.getObjectContext().getListChanges(eq, ListKey.QUALITY);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (Quality q : changes.getAdded())
		{
			set.add(new StringBuilder().append(q.getQuality()).append(
				Constants.PIPE).append(q.getValue()).toString());
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<CDOMEquipment> getTokenClass()
	{
		return CDOMEquipment.class;
	}
}
