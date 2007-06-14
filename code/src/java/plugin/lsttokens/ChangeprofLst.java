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
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.Logging;
import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.modifier.ChangeProf;
import pcgen.core.PObject;
import pcgen.core.WeaponProf;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;

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
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		// value should be of the format:
		// Name1,TYPE.type1,Name3=Prof1|Name4,Name5=Prof2
		//
		// e.g.: TYPE.Hammer,Hand Axe=Simple|Urgosh,Waraxe=Martial

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		List<ChangeProf> list = new ArrayList<ChangeProf>();

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			int equalLoc = tokText.indexOf('=');
			if (equalLoc < 0)
			{
				Logging.errorPrint("Improper " + getTokenName()
					+ ": No = found. "
					+ "Expect format to be <Prof>,<Prof>=<Prof Type>");
				Logging.errorPrint("  Token was: " + tokText);
				Logging.errorPrint("  Tag was: " + value);
				return false;
			}
			else if (equalLoc != tokText.lastIndexOf('='))
			{
				Logging.errorPrint("Improper " + getTokenName()
					+ ": Two = found.  "
					+ "Expect format to be <Prof>,<Prof>=<Prof Type>");
				Logging.errorPrint("  Token was: " + tokText);
				Logging.errorPrint("  Tag was: " + value);
				return false;
			}

			String newType = tokText.substring(equalLoc + 1);
			if (newType.length() == 0)
			{
				Logging.errorPrint("Improper " + getTokenName()
					+ ": Empty Result Type.  "
					+ "Expect format to be <Prof>,<Prof>=<Prof Type>");
				Logging.errorPrint("  Token was: " + tokText);
				Logging.errorPrint("  Tag was: " + value);
				return false;
			}
			if (newType.indexOf(".") != -1)
			{
				Logging
					.errorPrint("Improper "
						+ getTokenName()
						+ ": Invalid (Compound) Result Type: cannot contain a period (.)  "
						+ "Expect format to be <Prof>,<Prof>=<Prof Type>");
				Logging.errorPrint("  Token was: " + tokText);
				Logging.errorPrint("  Tag was: " + value);
				return false;
			}
			String[] val = {newType};

			CDOMGroupRef<WeaponProf> newTypeProf =
					context.ref.getCDOMTypeReference(WEAPONPROF_CLASS, val);

			String profs = tokText.substring(0, equalLoc);
			if (profs.length() == 0)
			{
				Logging.errorPrint("Improper " + getTokenName()
					+ ": Empty Source Prof.  "
					+ "Expect format to be <Prof>,<Prof>=<Prof Type>");
				Logging.errorPrint("  Token was: " + tokText);
				Logging.errorPrint("  Tag was: " + value);
				return false;
			}

			StringTokenizer pTok = new StringTokenizer(profs, Constants.COMMA);
			while (pTok.hasMoreTokens())
			{
				CDOMReference<WeaponProf> wp =
						TokenUtilities.getTypeOrPrimitive(context,
							WEAPONPROF_CLASS, pTok.nextToken());
				list.add(new ChangeProf(wp, newTypeProf));
			}
		}
		for (ChangeProf cp : list)
		{
			context.graph.grant(getTokenName(), obj, cp);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		GraphChanges<ChangeProf> changes =
				context.graph.getChangesFromToken(getTokenName(), obj,
					ChangeProf.class);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		HashMapToList<CDOMGroupRef<WeaponProf>, CDOMReference<WeaponProf>> m =
				new HashMapToList<CDOMGroupRef<WeaponProf>, CDOMReference<WeaponProf>>();
		for (LSTWriteable lw : added)
		{
			ChangeProf cp = (ChangeProf) lw;
			CDOMReference<WeaponProf> source = cp.getSource();
			CDOMGroupRef<WeaponProf> result = cp.getResult();
			m.addToListFor(result, source);
		}

		SortedSet<CDOMReference<WeaponProf>> set =
				new TreeSet<CDOMReference<WeaponProf>>(
					TokenUtilities.REFERENCE_SORTER);
		Set<String> returnSet = new TreeSet<String>();
		for (CDOMGroupRef<WeaponProf> result : m.getKeySet())
		{
			StringBuilder sb = new StringBuilder();
			boolean needComma = false;
			set.clear();
			set.addAll(m.getListFor(result));
			for (CDOMReference<WeaponProf> source : set)
			{
				if (needComma)
				{
					sb.append(Constants.COMMA);
				}
				needComma = true;
				sb.append(source.getLSTformat());
			}
			sb.append(Constants.EQUALS).append(result.getPrimitiveFormat());
			returnSet.add(sb.toString());
		}
		return new String[]{StringUtil.join(returnSet, Constants.PIPE)};
	}
}
