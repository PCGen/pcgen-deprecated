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

import java.io.StringWriter;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.core.Domain;
import pcgen.core.DomainList;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.PCClassUniversalLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * Class deals with ADDDOMAINS Token
 */
public class AdddomainsToken extends AbstractToken implements PCClassLstToken,
		PCClassUniversalLstToken
{

	private static final Class<Domain> DOMAIN_CLASS = Domain.class;

	@Override
	public String getTokenName()
	{
		return "ADDDOMAINS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		final StringTokenizer aTok = new StringTokenizer(value, ".");

		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			String domainKey;
			String prereq = null; // Do not initialize, null is significant!

			// Note: May contain PRExxx
			if (aString.indexOf("[") == -1)
			{
				domainKey = aString;
			}
			else
			{
				int openBracketLoc = aString.indexOf("[");
				domainKey = aString.substring(0, openBracketLoc);
				if (!aString.endsWith("]"))
				{
					Logging.errorPrint("Unresolved Prerequisite on Domain "
						+ aString + " in " + getTokenName());
				}
				prereq =
						aString.substring(openBracketLoc + 1, aString.length()
							- openBracketLoc - 2);
			}

			Domain thisDomain = Globals.getDomainKeyed(domainKey);

			if (thisDomain == null)
			{
				Logging.errorPrint("Unresolved Domain " + domainKey + " in "
					+ getTokenName());
			}
			else
			{
				Domain clonedDomain = thisDomain.clone();
				if (prereq != null)
				{
					try
					{
						clonedDomain.addPreReq(PreParserFactory.getInstance()
							.parse(prereq));
					}
					catch (PersistenceLayerException e)
					{
						Logging.errorPrint("Error generating Prerequisite "
							+ prereq + " in " + getTokenName());
					}
				}
				pcclass.addAddDomain(level, clonedDomain);
			}
		}

		return true;
	}

	public boolean parse(LoadContext context, PObject po, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value) || hasIllegalSeparator('.', value))
		{
			return false;
		}

		CDOMReference<DomainList> allowedDomainList =
				context.ref.getCDOMReference(DomainList.class, "*Allowed");

		StringTokenizer tok = new StringTokenizer(value, Constants.DOT);
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			Prerequisite prereq = null; // Do not initialize, null is
			// significant!
			String domainKey;

			// Note: May contain PRExxx
			if (tokString.indexOf("[") == -1)
			{
				domainKey = tokString;
			}
			else
			{
				int openBracketLoc = tokString.indexOf("[");
				domainKey = tokString.substring(0, openBracketLoc);
				if (!tokString.endsWith("]"))
				{
					Logging.errorPrint("Unresolved Prerequisite on Domain "
						+ tokString + " in " + getTokenName());
					return false;
				}
				prereq =
						getPrerequisite(tokString.substring(openBracketLoc + 1,
							tokString.length() - openBracketLoc - 2));
			}
			AssociatedPrereqObject apo =
					context.list.addToList(getTokenName(), po,
						allowedDomainList, context.ref.getCDOMReference(
							DOMAIN_CLASS, domainKey));
			if (prereq != null)
			{
				apo.addPrerequisite(prereq);
			}
		}

		return true;
	}

	public String[] unparse(LoadContext context, PObject po)
	{
		CDOMReference<DomainList> allowedDomainList =
				context.ref.getCDOMReference(DomainList.class, "*Allowed");

		GraphChanges<Domain> changes =
				context.list.getChangesInList(getTokenName(), po,
					allowedDomainList);
		if (changes == null)
		{
			// Legal if no ADDDOMAIN was present
			return null;
		}
		if (changes.hasRemovedItems() || changes.includesGlobalClear())
		{
			context
				.addWriteMessage(getTokenName() + " does not support .CLEAR");
			return null;
		}
		if (changes.hasAddedItems())
		{
			PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (LSTWriteable domain : changes.getAdded())
			{
				if (!first)
				{
					sb.append('.');
				}
				first = false;
				sb.append(domain.getLSTformat());
				AssociatedPrereqObject assoc =
						changes.getAddedAssociation(domain);
				List<Prerequisite> prereqs = assoc.getPrerequisiteList();
				Prerequisite prereq;
				if (prereqs == null || prereqs.size() == 0)
				{
					prereq = null;
				}
				else if (prereqs.size() == 1)
				{
					prereq = prereqs.get(0);
				}
				else
				{
					context.addWriteMessage("Added Domain from "
						+ getTokenName() + " had more than one "
						+ "Prerequisite: " + prereqs.size());
					return null;
				}
				if (prereq != null)
				{
					sb.append('[');
					StringWriter swriter = new StringWriter();
					try
					{
						prereqWriter.write(swriter, prereq);
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage("Error writing Prerequisite: "
							+ e);
						return null;
					}
					sb.append(swriter.toString());
					sb.append(']');
				}
			}
			return new String[]{sb.toString()};
		}
		return new String[]{};
	}
}
