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
package plugin.lsttokens.template;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.ChoiceSet;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.PCTemplate;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with WEAPONBONUS Token
 */
public class WeaponbonusToken implements PCTemplateLstToken
{

	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

	public String getTokenName()
	{
		return "WEAPONBONUS";
	}

	public boolean parse(PCTemplate template, String value)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		while (aTok.hasMoreTokens())
		{
			template.addWeaponProfBonus(aTok.nextToken());
		}

		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		return parseWeaponBonus(context, template, value);
	}

	public boolean parseWeaponBonus(LoadContext context, CDOMObject obj,
		String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		ChoiceSet<CDOMReference<WeaponProf>> cl =
				new ChoiceSet<CDOMReference<WeaponProf>>(1, tok.countTokens());
		while (tok.hasMoreTokens())
		{
			CDOMReference<WeaponProf> ref =
					TokenUtilities.getTypeOrPrimitive(context,
						WEAPONPROF_CLASS, tok.nextToken());
			if (ref == null)
			{
				return false;
			}
			cl.addChoice(ref);
		}
		context.graph.linkObjectIntoGraph(getTokenName(), obj, cl);
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Set<PCGraphEdge> choiceEdges =
				context.graph.getChildLinksFromToken(getTokenName(), pct,
					ChoiceSet.class);
		if (choiceEdges == null || choiceEdges.isEmpty())
		{
			return null;
		}
		if (choiceEdges.size() > 1)
		{
			context.addWriteMessage(getTokenName()
				+ " may only have one ChoiceSet linked in the Graph");
			return null;
		}
		Set<CDOMReference<?>> set =
				new TreeSet<CDOMReference<?>>(TokenUtilities.REFERENCE_SORTER);
		PCGraphEdge edge = choiceEdges.iterator().next();
		set.addAll(((ChoiceSet<CDOMReference<?>>) edge.getSinkNodes().get(0))
			.getSet());
		return new String[]{ReferenceUtilities.joinLstFormat(set,
			Constants.PIPE)};
	}
}
