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
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.core.PCClass;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.BonusTokenLoader;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with MONNONSKILLHD Token
 */
public class MonnonskillhdToken extends AbstractToken implements
		PCClassLstToken, CDOMPrimaryToken<CDOMPCClass>
{

	@Override
	public String getTokenName()
	{
		return "MONNONSKILLHD";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.addBonusList("0|MONNONSKILLHD|NUMBER|" + value);
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
				BonusTokenLoader.getBonus(context, pcc, "MONNONSKILLHD", "NUMBER",
					bonusString);
		AssociatedPrereqObject assoc =
				context.getGraphContext().grant(getTokenName(), pcc, bonus);
		assoc.addPrerequisite(getPrerequisite("PRELEVELMAX:1"));
		while (st.hasMoreTokens())
		{
			assoc.addPrerequisite(getPrerequisite(st.nextToken()));
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMPCClass pcc)
	{
		AssociatedChanges<BonusObj> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					pcc, BonusObj.class);
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		Set<String> set = new TreeSet<String>();
		MapToList<LSTWriteable, AssociatedPrereqObject> mtl =
				changes.getAddedAssociations();
		if (mtl != null && !mtl.isEmpty())
		{
			for (LSTWriteable ab : mtl.getKeySet())
			{
				// TODO Validate MONNONSKILLHD?
				// TODO Validate NUMBER?
				// TODO Validate PRExxx?
				for (AssociatedPrereqObject assoc : mtl.getListFor(ab))
				{
					StringBuilder sb = new StringBuilder();
					sb.append(ab.getLSTformat());
					if (assoc.hasPrerequisites())
					{
						for (Prerequisite prereq : assoc.getPrerequisiteList())
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
