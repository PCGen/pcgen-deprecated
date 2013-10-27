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
 * Current Ver: $Revision: 3890 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-08-26 23:42:42 -0400 (Sun, 26 Aug 2007) $
 */
package plugin.lsttokens.pcclass.level;

import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.inst.CDOMDomain;
import pcgen.cdom.inst.CDOMPCClassLevel;
import pcgen.cdom.list.DomainList;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ADDDOMAINS Token
 */
public class AdddomainsToken extends AbstractToken implements 
CDOMPrimaryToken<CDOMPCClassLevel>
{

	private static final Class<CDOMDomain> DOMAIN_CLASS = CDOMDomain.class;

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

	public boolean parse(LoadContext context, CDOMPCClassLevel po, String value)
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
			int openBracketLoc = tokString.indexOf('[');
			if (openBracketLoc == -1)
			{
				if (tokString.indexOf(']') != -1)
				{
					Logging.errorPrint("Invalid " + getTokenName()
						+ " must have '[' if it contains a PREREQ tag");
					return false;
				}
				domainKey = tokString;
			}
			else
			{
				if (tokString.indexOf(']') != tokString.length() - 1)
				{
					Logging.errorPrint("Invalid " + getTokenName()
						+ " must end with ']' if it contains a PREREQ tag");
					return false;
				}
				domainKey = tokString.substring(0, openBracketLoc);
				String prereqString =
						tokString.substring(openBracketLoc + 1, tokString
							.length() - 1);
				if (prereqString.length() == 0)
				{
					Logging.errorPrint(getTokenName()
						+ " cannot have empty prerequisite : " + value);
					return false;
				}
				prereq = getPrerequisite(prereqString);
			}
			AssociatedPrereqObject apo =
					context.getListContext().addToList(getTokenName(), po,
						allowedDomainList,
						context.ref.getCDOMReference(DOMAIN_CLASS, domainKey));
			if (prereq != null)
			{
				apo.addPrerequisite(prereq);
			}
		}

		return true;
	}

	public String[] unparse(LoadContext context, CDOMPCClassLevel po)
	{
		CDOMReference<DomainList> allowedDomainList =
				context.ref.getCDOMReference(DomainList.class, "*Allowed");

		AssociatedChanges<CDOMReference<CDOMDomain>> changes =
				context.getListContext().getChangesInList(getTokenName(), po,
					allowedDomainList);
		Collection<CDOMReference<CDOMDomain>> removedItems = changes.getRemoved();
		if (removedItems != null && !removedItems.isEmpty()
				|| changes.includesGlobalClear())
		{
			context
				.addWriteMessage(getTokenName() + " does not support .CLEAR");
			return null;
		}
		MapToList<CDOMReference<CDOMDomain>, AssociatedPrereqObject> mtl =
				changes.getAddedAssociations();
		if (mtl == null || mtl.isEmpty())
		{
			return null;
		}
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (CDOMReference<CDOMDomain> domain : mtl.getKeySet())
		{
			for (AssociatedPrereqObject assoc : mtl.getListFor(domain))
			{
				if (!first)
				{
					sb.append('.');
				}
				first = false;
				sb.append(domain.getLSTformat());
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
		}
		return new String[]{sb.toString()};
	}

	public Class<CDOMPCClassLevel> getTokenClass()
	{
		return CDOMPCClassLevel.class;
	}
}
