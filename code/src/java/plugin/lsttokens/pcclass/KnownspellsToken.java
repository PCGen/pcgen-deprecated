/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.SpellFilter;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.PCClassUniversalLstToken;
import pcgen.util.Logging;

/**
 * Class deals with KNOWNSPELLS Token
 */
public class KnownspellsToken implements PCClassLstToken,
		PCClassUniversalLstToken
{

	public String getTokenName()
	{
		return "KNOWNSPELLS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		StringTokenizer pipeTok;

		if (value.startsWith(".CLEAR"))
		{
			pcclass.clearKnownSpellsList();

			if (".CLEAR".equals(value))
			{
				return true;
			}

			pipeTok = new StringTokenizer(value.substring(6), Constants.PIPE);
		}
		else
		{
			pipeTok = new StringTokenizer(value, Constants.PIPE);
		}

		while (pipeTok.hasMoreTokens())
		{
			String totalFilter = pipeTok.nextToken();
			StringTokenizer commaTok = new StringTokenizer(totalFilter, ",");
			SpellFilter sf = new SpellFilter();

			// must satisfy all elements in a comma delimited list
			while (commaTok.hasMoreTokens())
			{
				String filterString = commaTok.nextToken();

				/*
				 * CONSIDER Want to add deprecation during 5.11 alpha cycle,
				 * thus, can be removed in 5.14 or 6.0 - thpr 11/4/06
				 */
				if (filterString.startsWith("LEVEL."))
				{
					// Logging.errorPrint("LEVEL. format deprecated in
					// KNOWNSPELLS. Please use LEVEL=");
					filterString = "LEVEL=" + filterString.substring(6);
				}
				if (filterString.startsWith("TYPE."))
				{
					// Logging.errorPrint("TYPE. format deprecated in
					// KNOWNSPELLS. Please use TYPE=");
					filterString = "TYPE=" + filterString.substring(5);
				}

				if (filterString.startsWith("LEVEL="))
				{
					// if the argument starts with LEVEL=, compare the level to
					// the desired spellLevel
					sf.setSpellLevel(Integer
						.parseInt(filterString.substring(6)));
				}
				else if (filterString.startsWith("TYPE="))
				{
					// if it starts with TYPE=, compare it to the spells type
					// list
					sf.setSpellType(filterString.substring(5));
				}
				else
				{
					// otherwise it must be the spell's name
					sf.setSpellName(filterString);
				}
			}
			if (sf.isEmpty())
			{
				Logging.errorPrint("Illegal (empty) KNOWNSPELLS Filter: "
					+ totalFilter);
			}
			pcclass.addKnownSpell(sf);
		}
		return true;
	}

	public boolean parse(LoadContext context, PObject po, String value)
		throws PersistenceLayerException
	{
		StringTokenizer pipeTok;

		if (value.startsWith(".CLEAR"))
		{
			context.graph.unlinkChildNodesOfClass(getTokenName(), po,
				SpellFilter.class);

			if (".CLEAR".equals(value))
			{
				return true;
			}

			pipeTok = new StringTokenizer(value.substring(6), Constants.PIPE);
		}
		else
		{
			pipeTok = new StringTokenizer(value, Constants.PIPE);
		}

		while (pipeTok.hasMoreTokens())
		{
			String totalFilter = pipeTok.nextToken();
			StringTokenizer commaTok =
					new StringTokenizer(totalFilter, Constants.COMMA);
			SpellFilter sf = new SpellFilter();

			// must satisfy all elements in a comma delimited list
			while (commaTok.hasMoreTokens())
			{
				String filterString = commaTok.nextToken();

				if (filterString.startsWith("LEVEL="))
				{
					// if the argument starts with LEVEL=, compare the level to
					// the desired spellLevel
					sf.setSpellLevel(Integer
						.parseInt(filterString.substring(6)));
				}
				else if (filterString.startsWith("TYPE="))
				{
					// if it starts with TYPE=, compare it to the spells type
					// list
					sf.setSpellType(filterString.substring(5));
				}
				else
				{
					// otherwise it must be the spell's name
					sf.setSpellName(filterString);
				}
			}
			if (sf.isEmpty())
			{
				Logging.errorPrint("Illegal (empty) KNOWNSPELLS Filter: "
					+ totalFilter);
			}
			context.graph.linkObjectIntoGraph(getTokenName(), po, sf);
		}
		return true;
	}

	public String[] unparse(LoadContext context, PObject po)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
