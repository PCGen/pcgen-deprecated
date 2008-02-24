/*
 * DomainToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on March 3, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.deity;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.inst.CDOMDomain;
import pcgen.cdom.kit.CDOMKitDeity;
import pcgen.core.kit.KitDeity;
import pcgen.persistence.lst.KitDeityLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;

/**
 * DOMAIN Token for KitDeity
 */
public class DomainToken extends AbstractToken implements KitDeityLstToken,
		CDOMSecondaryToken<CDOMKitDeity>
{
	public boolean parse(KitDeity kitDeity, String value)
	{
		final StringTokenizer pTok = new StringTokenizer(value, "|");
		while (pTok.hasMoreTokens())
		{
			final String domain = pTok.nextToken();
			kitDeity.addDomain(domain);
		}
		return true;
	}

	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "DOMAIN";
	}

	public Class<CDOMKitDeity> getTokenClass()
	{
		return CDOMKitDeity.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitDeity kitDeity,
			String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);
		while (pipeTok.hasMoreTokens())
		{
			String tokString = pipeTok.nextToken();
			CDOMSingleRef<CDOMDomain> ref = context.ref.getCDOMReference(
					CDOMDomain.class, tokString);
			kitDeity.addDomain(ref);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitDeity kitDeity)
	{
		Collection<CDOMReference<CDOMDomain>> domains = kitDeity.getDomains();
		if (domains == null || domains.isEmpty())
		{
			return null;
		}
		return new String[] { ReferenceUtilities.joinLstFormat(domains,
				Constants.PIPE) };
	}
}
