/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens.auto;

import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.PObject;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AutoLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class WeaponProfToken implements AutoLstToken
{

	public String getTokenName()
	{
		return "WEAPONPROF";
	}

	public boolean parse(PObject target, String value)
	{
		target.addAutoArray(getTokenName(), value);
		return true;
	}

	public boolean parse(LoadContext context, PObject obj, String value)
	{
		String weaponProfs;
		String prereq = null; // Do not initialize, null is significant!

		// Note: May contain PRExxx
		if (value.indexOf("[") == -1)
		{
			weaponProfs = value;
		}
		else
		{
			int openBracketLoc = value.indexOf("[");
			weaponProfs = value.substring(0, openBracketLoc);
			if (!value.endsWith("]"))
			{
				Logging.errorPrint("Unresolved Prerequisite in "
					+ getTokenName() + " " + value + " in " + getTokenName());
			}
			prereq = value.substring(openBracketLoc + 1, value.length() - 2);
		}

		if (weaponProfs.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (weaponProfs.charAt(weaponProfs.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (weaponProfs.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}

		StringTokenizer tok = new StringTokenizer(weaponProfs, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String aProf = tok.nextToken();
			if ("%LIST".equals(value))
			{
				/*
				 * FIXME Need to figure out how to handle this!!!
				 */
				// for (Iterator<AssociatedChoice<String>> e =
				// getAssociatedList()
				// .iterator(); e.hasNext();) {
				// aList.add(e.next().getDefaultChoice());
				// }
			}
			else
			{
				CDOMReference<WeaponProf> ref =
						TokenUtilities.getObjectReference(context,
							WeaponProf.class, aProf);
				if (ref == null)
				{
					return false;
				}
				PCGraphGrantsEdge edge =
						context.graph.linkObjectIntoGraph(getTokenName(), obj,
							ref);
				if (prereq != null)
				{
					try
					{
						edge.addPreReq(PreParserFactory.getInstance().parse(
							prereq));
					}
					catch (PersistenceLayerException e)
					{
						Logging.errorPrint("Error generating Prerequisite "
							+ prereq + " in " + getTokenName());
					}
				}
			}
		}

		return true;
	}

	public String[] unparse(LoadContext context, PObject obj)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					WeaponProf.class);
		if (edges.isEmpty())
		{
			return null;
		}
		SortedSet<CDOMReference<WeaponProf>> set =
				new TreeSet<CDOMReference<WeaponProf>>(
					TokenUtilities.REFERENCE_SORTER);
		for (PCGraphEdge edge : edges)
		{
			set.add((CDOMReference<WeaponProf>) edge.getSinkNodes().get(0));
		}
		return new String[]{ReferenceUtilities.joinLstFormat(set,
			Constants.PIPE)};
	}

}
