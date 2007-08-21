/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2005-2006 (C) Devon Jones
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
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.core.DamageReduction;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */

public class DrLst extends AbstractToken implements GlobalLstToken
{

	private static final Class<DamageReduction> DR_CLASS =
			DamageReduction.class;

	@Override
	public String getTokenName()
	{
		return "DR";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		ArrayList<Prerequisite> preReqs = new ArrayList<Prerequisite>();
		if (anInt > -9)
		{
			try
			{
				PreParserFactory factory = PreParserFactory.getInstance();
				String preLevelString = "PRELEVEL:" + anInt;
				if (obj instanceof PCClass)
				{
					// Classes handle this differently
					preLevelString =
							"PRECLASS:1," + obj.getKeyName() + "=" + anInt;
				}
				Prerequisite r = factory.parse(preLevelString);
				preReqs.add(r);
			}
			catch (PersistenceLayerException notUsed)
			{
				return false;
			}
		}

		if (".CLEAR".equals(value))
		{
			obj.clearDR();
			return true;
		}

		StringTokenizer tok = new StringTokenizer(value, "|");
		String[] values = tok.nextToken().split("/");
		if (values.length != 2)
		{
			return false;
		}
		DamageReduction dr = new DamageReduction(values[0], values[1]);

		if (tok.hasMoreTokens())
		{
			try
			{
				PreParserFactory factory = PreParserFactory.getInstance();
				Prerequisite r = factory.parse(tok.nextToken());
				preReqs.add(r);
			}
			catch (PersistenceLayerException notUsed)
			{
				return false;
			}
		}
		for (Prerequisite prereq : preReqs)
		{
			dr.addPreReq(prereq);
		}

		obj.addDR(dr);
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (".CLEAR".equals(value))
		{
			context.getGraphContext().removeAll(getTokenName(), obj);
			return true;
		}

		StringTokenizer tok = new StringTokenizer(value, "|");
		DamageReduction dr;
		try
		{
			String[] values = tok.nextToken().split("/");
			if (values.length != 2)
			{
				Logging.errorPrint(getTokenName()
					+ " failed to build DamageReduction with value " + value);
				Logging
					.errorPrint("  ...expected a String with one / as a separator");
				return false;
			}
			if (values[0].length() == 0)
			{
				Logging.errorPrint("Amount of Reduction in " + getTokenName()
					+ " cannot be empty");
				return false;
			}
			if (values[1].length() == 0)
			{
				Logging.errorPrint("Damage Type in " + getTokenName()
					+ " cannot be empty");
				return false;
			}
			dr = new DamageReduction(values[0], values[1]);
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint(getTokenName()
				+ " failed to build DamageReduction with value " + value
				+ " ... " + iae.getLocalizedMessage());
			return false;
		}

		if (tok.hasMoreTokens())
		{
			String currentToken = tok.nextToken();
			Prerequisite prereq = getPrerequisite(currentToken);
			if (prereq == null)
			{
				return false;
			}
			dr.addPrerequisite(prereq);
		}
		context.getGraphContext().grant(getTokenName(), obj, dr);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		GraphChanges<DamageReduction> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					obj, DR_CLASS);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		List<String> list = new ArrayList<String>(added.size() + 1);
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		else if (added.isEmpty())
		{
			// Zero indicates no Token (and no global clear, so nothing to do)
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (LSTWriteable lw : added)
		{
			set.add(lw.getLSTformat());
		}
		list.addAll(set);
		return list.toArray(new String[list.size()]);
	}
}
