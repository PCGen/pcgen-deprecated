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
package plugin.lsttokens.deity;

import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.DeityLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with DOMAINS Token
 */
public class DomainsToken implements DeityLstToken
{
	private static final Class<Domain> DOMAIN_CLASS = Domain.class;

	public String getTokenName()
	{
		return "DOMAINS";
	}

	public boolean parse(Deity deity, String value)
	{
		if (value.length() > 0)
		{
			String[] domains = value.split(",");
			deity.setDomainNameList(CoreUtility.arrayToList(domains));
			return true;
		}
		return false;
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		if (value.charAt(0) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with , : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with , : " + value);
			return false;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);
		while (tok.hasMoreTokens())
		{
			CDOMReference<Domain> cdo;
			String tokString = tok.nextToken();
			if (Constants.LST_ALL.equals(tokString))
			{
				cdo = context.ref.getCDOMAllReference(DOMAIN_CLASS);
			}
			else
			{
				cdo = context.ref.getCDOMReference(DOMAIN_CLASS, tokString);
			}
			context.graph.linkObjectIntoGraph(getTokenName(), deity, cdo);
		}
		return true;
	}

	public String unparse(LoadContext context, Deity deity)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), deity,
					DOMAIN_CLASS);
		if (edges.isEmpty())
		{
			return null;
		}
		SortedSet<CDOMReference<Domain>> set =
				new TreeSet<CDOMReference<Domain>>(
					TokenUtilities.REFERENCE_SORTER);

		for (PCGraphEdge edge : edges)
		{
			set.add((CDOMReference<Domain>) edge.getNodeAt(1));
		}
		StringBuilder sb =
				new StringBuilder().append(getTokenName()).append(':');
		boolean needBar = false;
		for (CDOMReference<Domain> domain : set)
		{
			if (needBar)
			{
				sb.append(Constants.COMMA);
			}
			needBar = true;
			sb.append(domain.getLSTformat());
		}
		return sb.toString();
	}
}
