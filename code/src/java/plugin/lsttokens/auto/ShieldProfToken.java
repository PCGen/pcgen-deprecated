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
package plugin.lsttokens.auto;

import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.core.PObject;
import pcgen.core.ShieldProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AutoLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class ShieldProfToken implements AutoLstToken
{

	public String getTokenName()
	{
		return "SHIELDPROF";
	}

	public boolean parse(PObject target, String value)
	{
		target.addAutoArray(getTokenName(), value);
		return true;
	}

	public boolean parse(LoadContext context, PObject obj, String value)
	{
		String shieldProfs;
		String prereq = null; // Do not initialize, null is significant!

		// Note: May contain PRExxx
		if (value.indexOf("[") == -1)
		{
			shieldProfs = value;
		}
		else
		{
			int openBracketLoc = value.indexOf("[");
			shieldProfs = value.substring(0, openBracketLoc);
			if (!value.endsWith("]"))
			{
				Logging.errorPrint("Unresolved Prerequisite in "
					+ getTokenName() + " " + value + " in " + getTokenName());
			}
			prereq = value.substring(openBracketLoc + 1, value.length() - 2);
		}

		if (shieldProfs.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (shieldProfs.charAt(shieldProfs.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (shieldProfs.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}
		
		StringTokenizer tok = new StringTokenizer(shieldProfs, Constants.PIPE);

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
				CDOMReference<ShieldProf> ref =
						TokenUtilities.getObjectReference(context,
							ShieldProf.class, aProf);
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

	public String unparse(LoadContext context, PObject obj)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					ShieldProf.class);
		if (edges.isEmpty())
		{
			return null;
		}
		SortedSet<CDOMReference<ShieldProf>> set =
				new TreeSet<CDOMReference<ShieldProf>>(
					TokenUtilities.REFERENCE_SORTER);
		boolean needComma = false;
		for (PCGraphEdge edge : edges)
		{
			set.add((CDOMReference<ShieldProf>) edge.getSinkNodes().get(0));
		}
		StringBuilder sb =
				new StringBuilder().append(getTokenName()).append('|');
		for (CDOMReference<ShieldProf> ref : set)
		{
			if (needComma)
			{
				sb.append(Constants.PIPE);
			}
			needComma = true;
			sb.append(ref.getLSTformat());
		}
		return sb.toString();
	}
}
