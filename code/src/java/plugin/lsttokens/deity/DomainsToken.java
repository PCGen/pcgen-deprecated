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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.PropertyFactory;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.inst.CDOMDomain;
import pcgen.cdom.inst.DomainList;
import pcgen.core.Deity;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.DeityLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.MapToList;

/**
 * Class deals with DOMAINS Token
 */
public class DomainsToken extends AbstractToken implements DeityLstToken,
		CDOMPrimaryToken<CDOMDeity>
{
	private static final Class<CDOMDomain> DOMAIN_CLASS = CDOMDomain.class;

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
					final PreParserFactory factory = PreParserFactory
							.getInstance();
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

	public boolean parse(LoadContext context, CDOMDeity deity, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);
		StringTokenizer commaTok = new StringTokenizer(pipeTok.nextToken(),
				Constants.COMMA);
		CDOMReference<DomainList> dl = context.ref.getCDOMReference(
				DomainList.class, "*Starting");
		ArrayList<AssociatedPrereqObject> proList = new ArrayList<AssociatedPrereqObject>();

		boolean first = true;
		boolean foundAll = false;
		boolean foundOther = false;

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
				CDOMReference<CDOMDomain> ref;
				String clearText = tokString.substring(7);
				if (Constants.LST_ALL.equals(clearText))
				{
					ref = context.ref.getCDOMAllReference(DOMAIN_CLASS);
				}
				else
				{
					ref = context.ref.getCDOMReference(DOMAIN_CLASS, clearText);
				}
				context.getListContext().removeFromList(getTokenName(), deity,
						dl, ref);
			}
			else if (Constants.LST_ALL.equals(tokString))
			{
				CDOMGroupRef<CDOMDomain> ref = context.ref
						.getCDOMAllReference(DOMAIN_CLASS);
				proList.add(context.getListContext().addToList(getTokenName(),
						deity, dl, ref));
				foundAll = true;
			}
			else
			{
				CDOMSingleRef<CDOMDomain> ref = context.ref.getCDOMReference(
						DOMAIN_CLASS, tokString);
				proList.add(context.getListContext().addToList(getTokenName(),
						deity, dl, ref));
				foundOther = true;
			}
			first = false;
		}

		if (foundAll && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains ALL and a specific reference: " + value);
			return false;
		}

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
			for (AssociatedPrereqObject ao : proList)
			{
				ao.addAllPrerequisites(prereq);
			}
		}

		return true;
	}

	public String[] unparse(LoadContext context, CDOMDeity deity)
	{
		CDOMReference<DomainList> dl = context.ref.getCDOMReference(
				DomainList.class, "*Starting");
		AssociatedChanges<CDOMReference<CDOMDomain>> changes = context
				.getListContext().getChangesInList(getTokenName(), deity, dl);
		if (changes == null)
		{
			// Legal if no Language was present in the race
			return null;
		}
		List<String> list = new ArrayList<String>();
		Collection<LSTWriteable> removedItems = changes.getRemoved();
		if (changes.includesGlobalClear())
		{
			if (removedItems != null && !removedItems.isEmpty())
			{
				context.addWriteMessage("Non-sensical relationship in "
						+ getTokenName()
						+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			list.add(Constants.LST_DOT_CLEAR);
		}
		else if (removedItems != null && !removedItems.isEmpty())
		{
			list.add(Constants.LST_DOT_CLEAR_DOT
					+ ReferenceUtilities.joinLstFormat(removedItems,
							",.CLEAR."));
		}
		MapToList<LSTWriteable, AssociatedPrereqObject> mtl = changes
				.getAddedAssociations();
		if (mtl != null && !mtl.isEmpty())
		{
			MapToList<Set<Prerequisite>, LSTWriteable> m = new HashMapToList<Set<Prerequisite>, LSTWriteable>();
			for (LSTWriteable ab : mtl.getKeySet())
			{
				for (AssociatedPrereqObject assoc : mtl.getListFor(ab))
				{
					m.addToListFor(new HashSet<Prerequisite>(assoc
							.getPrerequisiteList()), ab);
				}
			}
			Set<String> set = new TreeSet<String>();
			for (Set<Prerequisite> prereqs : m.getKeySet())
			{
				Set<LSTWriteable> domainSet = new TreeSet<LSTWriteable>(
						TokenUtilities.WRITEABLE_SORTER);
				domainSet.addAll(m.getListFor(prereqs));
				StringBuilder sb = new StringBuilder(ReferenceUtilities
						.joinLstFormat(domainSet, Constants.COMMA));
				if (prereqs != null && !prereqs.isEmpty())
				{
					sb.append(Constants.PIPE);
					sb.append(getPrerequisiteString(context, prereqs));
				}
				set.add(sb.toString());
			}
			list.addAll(set);
		}
		if (list.isEmpty())
		{
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMDeity> getTokenClass()
	{
		return CDOMDeity.class;
	}
}
