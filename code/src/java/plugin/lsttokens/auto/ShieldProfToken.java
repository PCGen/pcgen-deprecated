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

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.core.PObject;
import pcgen.core.ShieldProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AutoLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
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

	public boolean parse(LoadContext context, CDOMObject obj, String value)
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

		StringTokenizer tok = new StringTokenizer(shieldProfs, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String aProf = tok.nextToken();
			if (aProf.startsWith(Constants.LST_TYPE)
				|| aProf.startsWith(Constants.LST_TYPE_OLD))
			{
				CDOMGroupRef<ShieldProf> ref =
						context.ref.getCDOMTypeReference(ShieldProf.class,
							aProf.substring(5).split("."));
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
			else if ("%LIST".equals(value))
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
				CDOMSimpleSingleRef<ShieldProf> ref =
						context.ref.getCDOMReference(ShieldProf.class, aProf);
				/*
				 * FIXME There is source consolidation that can be done once
				 * %LIST is figured out
				 */
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
				// Individual prefs
			}
		}

		return true;
	}

}
