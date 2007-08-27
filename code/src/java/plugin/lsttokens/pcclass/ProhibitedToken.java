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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.ProhibitedSpellType;
import pcgen.cdom.enumeration.SpellSchool;
import pcgen.cdom.enumeration.SpellSubSchool;
import pcgen.cdom.inst.Aggregator;
import pcgen.core.PCClass;
import pcgen.core.SpellProhibitor;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with PROHIBITED Token
 */
public class ProhibitedToken extends AbstractToken implements PCClassLstToken,
		PCClassClassLstToken
{

	@Override
	public String getTokenName()
	{
		return "PROHIBITED";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		final StringTokenizer aTok = new StringTokenizer(value, ",");
		while (aTok.hasMoreTokens())
		{
			String prohibitedSchool = aTok.nextToken();
			if (!prohibitedSchool.equals(Constants.LST_NONE))
			{
				pcclass.addProhibitedSchool(prohibitedSchool);
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		List<SpellProhibitor<?>> spList = subParse(context, pcc, value);
		if (spList == null || spList.isEmpty())
		{
			return false;
		}
		Aggregator agg = new Aggregator(pcc, pcc, getTokenName());
		context.getGraphContext().grant(getTokenName(), pcc, agg);
		for (SpellProhibitor<?> sp : spList)
		{
			context.getGraphContext().grant(getTokenName(), agg, sp);
		}
		return true;
	}

	public List<SpellProhibitor<?>> subParse(LoadContext context, PCClass pcc,
		String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return null;
		}

		SpellProhibitor<SpellSchool> spSchool =
				new SpellProhibitor<SpellSchool>();
		spSchool.setType(ProhibitedSpellType.SCHOOL);
		SpellProhibitor<SpellSubSchool> spSubSchool =
				new SpellProhibitor<SpellSubSchool>();
		spSubSchool.setType(ProhibitedSpellType.SUBSCHOOL);

		StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);

		while (tok.hasMoreTokens())
		{
			String aValue = tok.nextToken();
			spSchool.addValue(SpellSchool.getConstant(aValue));
			spSubSchool.addValue(SpellSubSchool.getConstant(aValue));
		}

		List<SpellProhibitor<?>> list = new ArrayList<SpellProhibitor<?>>(2);
		list.add(spSchool);
		list.add(spSubSchool);
		return list;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		AssociatedChanges<Aggregator> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					pcc, Aggregator.class);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			return null;
		}
		if (added.size() != 1)
		{
			context.addWriteMessage("Can only have one Aggregator from "
				+ getTokenName());
			return null;
		}
		Aggregator agg = (Aggregator) added.iterator().next();

		AssociatedChanges<SpellProhibitor> aggChanges =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					agg, SpellProhibitor.class);
		if (aggChanges == null)
		{
			context.addWriteMessage("Invalid Aggregator in " + getTokenName()
				+ " must have changes");
			return null;
		}
		Collection<LSTWriteable> aggAdded = aggChanges.getAdded();
		if (aggAdded == null)
		{
			context.addWriteMessage("Invalid Aggregator in " + getTokenName()
				+ " must have added changes");
			return null;
		}
		if (aggAdded.size() != 2)
		{
			context.addWriteMessage("Invalid Aggregator in " + getTokenName()
				+ " must have two children");
			return null;
		}
		String retString = null;
		for (LSTWriteable lstw : aggAdded)
		{
			SpellProhibitor<?> sp = SpellProhibitor.class.cast(lstw);
			Set<?> valueSet = sp.getValueSet();
			Set<String> stringSet = new TreeSet<String>();
			for (Object o : valueSet)
			{
				stringSet.add(o.toString());
			}
			String st = StringUtil.join(stringSet, Constants.COMMA);
			if (retString == null)
			{
				retString = st;
			}
			else
			{
				if (!st.equals(retString))
				{
					context
						.addWriteMessage("Child Spell Prohibitors of Aggregator for "
							+ getTokenName() + " must prohibit the same items");
					return null;
				}
			}
		}
		return new String[]{retString};
	}
}
