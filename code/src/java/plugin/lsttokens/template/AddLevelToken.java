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
package plugin.lsttokens.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * New Token to support Adding Levels to say a Lycanthorpe template
 */
public class AddLevelToken implements PCTemplateLstToken
{

	public boolean parse(PCTemplate template, String value)
	{
		template.addLevelMod("ADD|" + value);
		return true;
	}

	public String getTokenName()
	{
		return "ADDLEVEL";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.errorPrint("No | found in " + getTokenName());
			Logging.errorPrint("  " + getTokenName()
				+ " requires at format: Class|LevelCount");
			return false;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.errorPrint("Two | found in " + getTokenName());
			Logging.errorPrint("  " + getTokenName()
				+ " requires at format: Class|LevelCount");
			return false;
		}
		String classString = value.substring(0, pipeLoc);
		if (classString.length() == 0)
		{
			Logging.errorPrint("Empty Class found in " + getTokenName());
			Logging.errorPrint("  " + getTokenName()
				+ " requires at format: Class|LevelCount");
			return false;
		}
		CDOMSimpleSingleRef<PCClass> cl =
				context.ref.getCDOMReference(PCClass.class, classString);
		String numLevels = value.substring(pipeLoc + 1);
		int lvls;
		try
		{
			lvls = Integer.parseInt(numLevels);
			if (lvls <= 0)
			{
				Logging.errorPrint("Number of Levels granted in "
					+ getTokenName() + " must be greater than zero");
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Class Level found in " + getTokenName() + " ("
				+ numLevels + ") was not an Integer.");
			Logging.errorPrint("  " + getTokenName()
				+ " requires at format: Class|LevelCount");
			return false;
		}
		LevelCommandFactory cf = new LevelCommandFactory(cl, lvls);
		context.graph.grant(getTokenName(), template, cf);
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		GraphChanges<LevelCommandFactory> changes =
				context.graph.getChangesFromToken(getTokenName(), pct,
					LevelCommandFactory.class);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (Iterator<LSTWriteable> it = added.iterator(); it.hasNext();)
		{
			StringBuilder sb = new StringBuilder();
			LevelCommandFactory lcf = (LevelCommandFactory) it.next();
			int lvls = lcf.getLevelCount();
			if (lvls <= 0)
			{
				context.addWriteMessage("Number of Levels granted in "
					+ getTokenName() + " must be greater than zero");
				return null;
			}
			sb.append(lcf.getLSTformat()).append(Constants.PIPE).append(lvls);
			list.add(sb.toString());
		}

		return list.toArray(new String[list.size()]);
	}
}
