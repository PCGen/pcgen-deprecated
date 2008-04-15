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

import java.io.StringWriter;
import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.core.PCClass;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.BonusTokenLoader;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with MONSKILL Token
 */
public class MonskillToken extends AbstractToken implements PCClassLstToken,
		CDOMPrimaryToken<CDOMPCClass>
{

	@Override
	public String getTokenName()
	{
		return "MONSKILL";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass
			.addBonusList("0|MONSKILLPTS|NUMBER|" + value + "|PRELEVELMAX:1");
		return true;
	}

	public boolean parse(LoadContext context, CDOMPCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		String bonusString = st.nextToken();
		if (PreParserFactory.isPreReqString(bonusString))
		{
			Logging.errorPrint(getTokenName()
				+ " cannot be only a Prerequisite");
			return false;
		}
		BonusObj bonus =
				BonusTokenLoader.getBonus(context, pcc, "MONSKILLPTS", "NUMBER",
					bonusString);
		bonus.addPrerequisite(getPrerequisite("PRELEVELMAX:1"));
		while (st.hasMoreTokens())
		{
			bonus.addPrerequisite(getPrerequisite(st.nextToken()));
		}
		context.getObjectContext().give(getTokenName(), pcc, bonus);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMPCClass pcc)
	{
		Changes<BonusObj> changes =
				context.getObjectContext().getGivenChanges(getTokenName(),
					pcc, BonusObj.class);
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		Set<String> set = new TreeSet<String>();
		Collection<BonusObj> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			for (BonusObj ab : added)
			{
				// TODO Validate MONNONSKILLHD?
				// TODO Validate NUMBER?
				// TODO Validate PRExxx?
				StringBuilder sb = new StringBuilder();
				sb.append(ab.getLSTformat());
				if (ab.hasPrerequisites())
				{
					for (Prerequisite prereq : ab.getPrerequisiteList())
					{
						sb.append(Constants.PIPE);
						StringWriter swriter = new StringWriter();
						try
						{
							prereqWriter.write(swriter, prereq);
						}
						catch (PersistenceLayerException e)
						{
							context
								.addWriteMessage("Error writing Prerequisite: "
									+ e);
							return null;
						}
						sb.append(swriter.toString());
					}
				}
				set.add(sb.toString());
			}
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}
