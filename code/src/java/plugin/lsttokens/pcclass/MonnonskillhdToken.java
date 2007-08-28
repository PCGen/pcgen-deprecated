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

import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.Changes;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.BonusLoader;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with MONNONSKILLHD Token
 */
public class MonnonskillhdToken extends AbstractToken implements
		PCClassLstToken, PCClassClassLstToken
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

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		BonusObj bonus =
				BonusLoader.getBonus(context, pcc, "MONNONSKILLHD", "NUMBER",
					st.nextToken());
		bonus.addPreReq(getPrerequisite("PRELEVELMAX:1"));
		bonus.setCreatorObject(pcc);
		while (st.hasMoreTokens())
		{
			bonus.addPreReq(getPrerequisite(st.nextToken()));
		}
		context.getObjectContext().addToList(pcc, ListKey.BONUSES, bonus);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Changes<BonusObj> changes =
				context.getObjectContext().getListChanges(pcc, ListKey.BONUSES);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (BonusObj b : changes.getAdded())
		{
			if (!pcc.equals(b.getCreatorObject()))
			{
				continue;
			}
			if (!"MONNONSKILLHD".equals(b.getBonusName()))
			{
				context.addWriteMessage(getTokenName()
					+ " must create BONUS of type MONNONSKILLHD");
				return null;
			}
			// TODO Validate NUMBER
			// TODO Validate PRExxx
			String value = b.getValue();
			set.add(value);
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[set.size()]);
	}
}
