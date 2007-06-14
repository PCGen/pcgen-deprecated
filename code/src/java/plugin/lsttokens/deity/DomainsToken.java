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
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.util.Logging;
import pcgen.base.util.PropertyFactory;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.DomainList;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.ListGraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.DeityLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;

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

		boolean foundAny = false;
		boolean foundOther = false;

		/*
		 * FIXME This isn't doing .CLEAR or .CLEAR. :(
		 */
		StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);
		List<CDOMReference<Domain>> list =
				new ArrayList<CDOMReference<Domain>>();
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			if (Constants.LST_ALL.equals(tokString))
			{
				foundAny = true;
				list.add(context.ref.getCDOMAllReference(DOMAIN_CLASS));
			}
			else
			{
				foundOther = true;
				list.add(context.ref.getCDOMReference(DOMAIN_CLASS, tokString));
			}
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		CDOMReference<DomainList> dl =
				context.ref.getCDOMReference(DomainList.class, "*Starting");
		for (CDOMReference<Domain> ref : list)
		{
			/*
			 * FIXME This isn't doing PREREQs :(
			 */
			AssociatedPrereqObject ao =
					context.list.addToList(getTokenName(), deity, dl, ref);
		}
		return true;
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		CDOMReference<DomainList> dl =
				context.ref.getCDOMReference(DomainList.class, "*Starting");
		ListGraphChanges<Domain> changes =
				context.list.getChangesInList(getTokenName(), deity, dl);
		if (changes == null)
		{
			// Legal if no Language was present in the race
			return null;
		}
		List<String> list = new ArrayList<String>();
		if (changes.hasRemovedItems() || changes.includesGlobalClear())
		{
			context
				.addWriteMessage(getTokenName() + " does not support .CLEAR");
			return null;
		}
		if (changes.hasAddedItems())
		{
			list.add(ReferenceUtilities.joinLstFormat(changes.getAdded(),
				Constants.COMMA));
		}
		/*
		 * FIXME This isn't doing PREREQs :(
		 */
		return list.toArray(new String[list.size()]);
	}
}
