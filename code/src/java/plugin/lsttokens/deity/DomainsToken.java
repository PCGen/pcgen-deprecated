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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.PropertyFactory;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.DomainList;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.Changes;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.DeityLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with DOMAINS Token
 */
public class DomainsToken extends AbstractToken implements DeityLstToken
{
	private static final Class<Domain> DOMAIN_CLASS = Domain.class;

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "DOMAINS";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.DeityLstToken#parse(pcgen.core.Deity,
	 *      java.lang.String)
	 */
	public boolean parse(Deity deity, String value)
		throws PersistenceLayerException
	{
		if (value.length() == 0)
		{
			return false;
		}

		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		String[] domains = tok.nextToken().split(",");

		ArrayList<Prerequisite> preReqs = new ArrayList<Prerequisite>();
		if (tok.hasMoreTokens())
		{
			while (tok.hasMoreTokens())
			{
				final String key = tok.nextToken();
				if (PreParserFactory.isPreReqString(key))
				{
					final PreParserFactory factory =
							PreParserFactory.getInstance();
					final Prerequisite r = factory.parse(key);
					preReqs.add(r);
				}
				else
				{
					throw new PersistenceLayerException(PropertyFactory
						.getFormattedString(
							"Errors.LstTokens.InvalidTokenFormat", //$NON-NLS-1$
							getClass().getName(), value));
				}
			}
		}

		deity.setDomainNameList(CoreUtility.arrayToList(domains), preReqs);
		return true;
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);
		StringTokenizer commaTok =
				new StringTokenizer(pipeTok.nextToken(), Constants.COMMA);
		List<CDOMReference<Domain>> list =
				new ArrayList<CDOMReference<Domain>>();
		CDOMReference<DomainList> dl =
				context.ref.getCDOMReference(DomainList.class, "*Starting");
		boolean first = true;
		while (commaTok.hasMoreTokens())
		{
			String tokString = commaTok.nextToken();
			if (tokString.startsWith("PRE") || tokString.startsWith("!PRE"))
			{
				Logging.errorPrint("Invalid " + getTokenName()
					+ ": PRExxx was comma delimited : " + value);
				return false;
			}
			if (Constants.LST_DOT_CLEAR.equals(tokString))
			{
				if (!first)
				{
					Logging.errorPrint("  Non-sensical " + getTokenName()
						+ ": .CLEAR was not the first list item: " + value);
					return false;
				}
				context.getListContext().removeAllFromList(getTokenName(),
					deity, dl);
			}
			else if (tokString.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				CDOMReference<Domain> ref;
				String clearText = tokString.substring(7);
				if (Constants.LST_ALL.equals(clearText))
				{
					ref = context.ref.getCDOMAllReference(DOMAIN_CLASS);
				}
				else
				{
					ref =
							TokenUtilities.getTypeOrPrimitive(context,
								DOMAIN_CLASS, clearText);
					if (ref == null)
					{
						Logging
							.errorPrint("  Error was encountered while parsing "
								+ getTokenName()
								+ ": "
								+ clearText
								+ " is not a valid reference: " + value);
						return false;
					}
				}
				context.getListContext().removeFromList(getTokenName(), deity,
					dl, ref);
			}
			else if (Constants.LST_ALL.equals(tokString))
			{
				list.add(context.ref.getCDOMAllReference(DOMAIN_CLASS));
			}
			else
			{
				list.add(context.ref.getCDOMReference(DOMAIN_CLASS, tokString));
			}
			first = false;
		}

		List<Prerequisite> prereqs = new ArrayList<Prerequisite>();
		while (pipeTok.hasMoreTokens())
		{
			String tokString = pipeTok.nextToken();
			Prerequisite prereq = getPrerequisite(tokString);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put items after the "
					+ "PRExxx tags in " + getTokenName() + ":?)");
				return false;
			}
			prereqs.add(prereq);
		}
		finish(context, deity, list, prereqs);
		return true;
	}

	private void finish(LoadContext context, Deity deity,
		List<CDOMReference<Domain>> list, Collection<Prerequisite> c)
	{
		CDOMReference<DomainList> dl =
				context.ref.getCDOMReference(DomainList.class, "*Starting");
		for (CDOMReference<Domain> ref : list)
		{
			AssociatedPrereqObject ao =
					context.getListContext().addToList(getTokenName(), deity,
						dl, ref);
			ao.addAllPrerequisites(c);
		}
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		CDOMReference<DomainList> dl =
				context.ref.getCDOMReference(DomainList.class, "*Starting");
		Changes<CDOMReference<Domain>> changes =
				context.getListContext().getChangesInList(getTokenName(),
					deity, dl);
		if (changes == null || changes.isEmpty())
		{
			// Legal if no Language was present in the race
			return null;
		}
		if (changes.hasRemovedItems() || changes.includesGlobalClear())
		{
			context
				.addWriteMessage(getTokenName() + " does not support .CLEAR");
			return null;
		}
		HashMapToList<List<Prerequisite>, CDOMReference<Domain>> map =
				new HashMapToList<List<Prerequisite>, CDOMReference<Domain>>();
		if (changes.hasAddedItems())
		{
			for (CDOMReference<Domain> ref : changes.getAdded())
			{
				AssociatedPrereqObject assoc = changes.getAddedAssociation(ref);
				List<Prerequisite> prereqs = assoc.getPrerequisiteList();
				map.addToListFor(prereqs, ref);
			}
		}
		Set<String> set = new TreeSet<String>();
		for (List<Prerequisite> prereqs : map.getKeySet())
		{
			Set<CDOMReference<Domain>> domainSet =
					new TreeSet<CDOMReference<Domain>>(
						TokenUtilities.REFERENCE_SORTER);
			domainSet.addAll(map.getListFor(prereqs));
			StringBuilder sb =
					new StringBuilder(ReferenceUtilities.joinLstFormat(
						domainSet, Constants.COMMA));
			if (prereqs != null && !prereqs.isEmpty())
			{
				sb.append(Constants.PIPE);
				sb.append(getPrerequisiteString(context, prereqs));
			}
			set.add(sb.toString());
		}
		return set.toArray(new String[set.size()]);
	}
}
