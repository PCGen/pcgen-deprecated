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

import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.Logging;
import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.modifier.ChangeProf;
import pcgen.core.PObject;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 * 
 */
public class ChangeprofLst extends AbstractToken implements GlobalLstToken
{

	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

	@Override
	public String getTokenName()
	{
		return "CHANGEPROF";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		// value should be of the format:
		// Name1,TYPE.type1,Name3=Prof1|Name4,Name5=Prof2
		//
		// e.g.: TYPE.Hammer,Hand Axe=Simple|Urgosh,Waraxe=Martial
		//
		final StringTokenizer tok = new StringTokenizer(value, "|");

		while (tok.hasMoreTokens())
		{
			String entry = tok.nextToken();
			String newProf;
			final int indx = entry.indexOf('=');
			if (indx > 1)
			{
				newProf = entry.substring(indx + 1);
				entry = entry.substring(0, indx);

				final StringTokenizer bTok = new StringTokenizer(entry, ",");
				while (bTok.hasMoreTokens())
				{
					final String eqString = bTok.nextToken();
					obj.addChangeProf(eqString, newProf);
				}
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		// value should be of the format:
		// Name1,TYPE.type1,Name3=Prof1|Name4,Name5=Prof2
		//
		// e.g.: TYPE.Hammer,Hand Axe=Simple|Urgosh,Waraxe=Martial

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			int equalLoc = tokText.indexOf('=');
			if (equalLoc < 0)
			{
				Logging.errorPrint("Improper " + getTokenName()
					+ ": Expect format to be <Prof>,<Prof>=<Prof Type>.");
				Logging.errorPrint("  Token was: " + tokText);
				Logging.errorPrint("  Tag was: " + value);
				return false;
			}

			String newType = tokText.substring(equalLoc + 1);
			String[] val = {newType};

			CDOMGroupRef<WeaponProf> newTypeProf =
					context.ref.getCDOMTypeReference(WEAPONPROF_CLASS, val);

			String profs = tokText.substring(0, equalLoc);

			StringTokenizer pTok = new StringTokenizer(profs, Constants.COMMA);
			while (pTok.hasMoreTokens())
			{
				CDOMReference<WeaponProf> wp;
				if (tokText.startsWith(Constants.LST_TYPE)
					|| tokText.startsWith(Constants.LST_TYPE_OLD))
				{
					String[] val1 = {pTok.nextToken()};
					wp =
							context.ref.getCDOMTypeReference(WEAPONPROF_CLASS,
								val1);
				}
				else
				{
					wp =
							context.ref.getCDOMReference(WEAPONPROF_CLASS, pTok
								.nextToken());
				}
				context.graph.linkObjectIntoGraph(getTokenName(), obj,
					new ChangeProf(wp, newTypeProf));
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Set<PCGraphEdge> edgeList =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					ChangeProf.class);
		if (edgeList == null || edgeList.isEmpty())
		{
			return null;
		}
		HashMapToList<CDOMGroupRef<WeaponProf>, CDOMReference<WeaponProf>> m =
				new HashMapToList<CDOMGroupRef<WeaponProf>, CDOMReference<WeaponProf>>();
		for (PCGraphEdge edge : edgeList)
		{
			ChangeProf cp = (ChangeProf) edge.getNodeAt(1);
			CDOMReference<WeaponProf> source = cp.getSource();
			CDOMGroupRef<WeaponProf> result = cp.getResult();
			m.addToListFor(result, source);
		}

		StringBuilder sb = new StringBuilder();
		boolean needPipe = true;
		for (CDOMGroupRef<WeaponProf> result : m.getKeySet())
		{
			if (needPipe)
			{
				sb.append(Constants.PIPE);
			}
			needPipe = true;
			boolean needComma = false;
			for (CDOMReference<WeaponProf> source : m.getListFor(result))
			{
				if (needComma)
				{
					sb.append(Constants.COMMA);
				}
				needComma = true;
				sb.append(source);
			}
			sb.append(Constants.EQUALS).append(result);
		}
		return new String[]{sb.toString()};
	}
}
