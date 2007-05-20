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

import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.ChoiceSet;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.PCClassUniversalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.utils.TokenUtilities;
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

		StringTokenizer tok = new StringTokenizer(value, Constants.DOT);

		ChoiceSet<CDOMReference<Domain>> cl =
				new ChoiceSet<CDOMReference<Domain>>(1, tok.countTokens());
		while (tok.hasMoreTokens())
		{
			CDOMReference<Domain> ref =
					TokenUtilities.getTypeOrPrimitive(context, DOMAIN_CLASS,
						tok.nextToken());
			if (ref == null)
			{
				return false;
			}
			cl.addChoice(ref);
		}
		context.graph.grant(getTokenName(), po, cl);
		return true;
	}

	public String[] unparse(LoadContext context, PObject po)
	{
		GraphChanges<ChoiceSet> changes =
				context.graph.getChangesFromToken(getTokenName(), po,
					ChoiceSet.class);
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
		if (added.size() > 1)
		{
			context.addWriteMessage(getTokenName()
				+ " may only have one ChoiceSet linked in the Graph");
			return null;
		}
		ChoiceSet<CDOMReference<?>> cs =
				(ChoiceSet<CDOMReference<?>>) added.iterator().next();
		Set<CDOMReference<?>> set =
				new TreeSet<CDOMReference<?>>(TokenUtilities.REFERENCE_SORTER);
		set.addAll(cs.getSet());
		return new String[]{ReferenceUtilities
			.joinLstFormat(set, Constants.DOT)};
	}
}
