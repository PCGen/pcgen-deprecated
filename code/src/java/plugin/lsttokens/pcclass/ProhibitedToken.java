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
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.inst.Aggregator;
import pcgen.core.PCClass;
import pcgen.core.SpellProhibitor;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.enumeration.ProhibitedSpellType;

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
		throws PersistenceLayerException
	{
		List<SpellProhibitor> spList = subParse(context, pcc, value);
		if (spList == null || spList.isEmpty())
		{
			return false;
		}
		Aggregator agg = new Aggregator(pcc, pcc, getTokenName());
		context.graph.grant(getTokenName(), pcc, agg);
		for (SpellProhibitor sp : spList)
		{
			context.graph.grant(getTokenName(), agg, sp);
		}
		return true;
	}

	public List<SpellProhibitor> subParse(LoadContext context, PCClass pcc,
		String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return null;
		}

		SpellProhibitor spSchool = new SpellProhibitor();
		spSchool.setType(ProhibitedSpellType.SCHOOL);
		SpellProhibitor spSubSchool = new SpellProhibitor();
		spSubSchool.setType(ProhibitedSpellType.SUBSCHOOL);

		StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);

		while (tok.hasMoreTokens())
		{
			String aValue = tok.nextToken();
			// TODO This is a String, should it be typesafe?
			spSchool.addValue(aValue);
			spSubSchool.addValue(aValue);
		}

		List<SpellProhibitor> list = new ArrayList<SpellProhibitor>(2);
		list.add(spSchool);
		list.add(spSubSchool);
		return list;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), pcc,
					Aggregator.class);
		if (edges == null || edges.isEmpty())
		{
			return null;
		}
		if (edges.size() != 1)
		{
			context.addWriteMessage("Can only have one Aggregator from "
				+ getTokenName());
			return null;
		}
		Aggregator agg = (Aggregator) edges.iterator().next().getNodeAt(1);
		Set<PCGraphEdge> aggEdges =
				context.graph.getChildLinksFromToken(getTokenName(), agg,
					SpellProhibitor.class);
		if (aggEdges == null || aggEdges.size() != 2)
		{
			context.addWriteMessage("Invalid Aggregator in " + getTokenName()
				+ " must have two children");
			return null;
		}
		String retString = null;
		for (PCGraphEdge aggEdge : aggEdges)
		{
			SpellProhibitor sp = (SpellProhibitor) aggEdge.getNodeAt(1);
			String st = StringUtil.join(sp.getValueSet(), Constants.COMMA);
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
