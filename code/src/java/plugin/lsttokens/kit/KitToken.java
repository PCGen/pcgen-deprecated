/*
 * KitToken.java
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

package plugin.lsttokens.kit;

import java.net.URI;
import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.inst.CDOMKit;
import pcgen.cdom.kit.CDOMKitKit;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Kit;
import pcgen.core.kit.KitKit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.BaseKitLoader;
import pcgen.persistence.lst.KitLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * Handles the KIT tag for Kits. Allows Common tags for this Kit line as well.
 */
public class KitToken extends KitLstToken implements
		CDOMSecondaryToken<CDOMKitKit>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "KIT";
	}

	/**
	 * Handles the parsing of the KIT tag for Kits. Can also accept Common tags.
	 * 
	 * @param aKit
	 *            the Kit object to add this information to
	 * @param value
	 *            the token string
	 * @return true if parse OK
	 * @throws PersistenceLayerException
	 */
	@Override
	public boolean parse(Kit aKit, String value, URI source)
			throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(value,
				SystemLoader.TAB_DELIM);
		KitKit kKit = new KitKit(colToken.nextToken());

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("KIT:"))
			{
				Logging.errorPrint("Ignoring second KIT tag \"" + colString
						+ "\" in KitToken.parse");
			}
			else
			{
				if (BaseKitLoader.parseCommonTags(kKit, colString, source) == false)
				{
					throw new PersistenceLayerException("Unknown KitKit info "
							+ " \"" + colString + "\"");
				}
			}
		}
		aKit.addObject(kKit);
		return true;
	}

	public Class<CDOMKitKit> getTokenClass()
	{
		return CDOMKitKit.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitKit kitKit, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			CDOMSingleRef<CDOMKit> ref = context.ref.getCDOMReference(
					CDOMKit.class, tokText);
			kitKit.addKit(ref);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitKit kitKit)
	{
		Collection<CDOMReference<CDOMKit>> domains = kitKit.getKits();
		if (domains == null || domains.isEmpty())
		{
			return null;
		}
		return new String[] { ReferenceUtilities.joinLstFormat(domains,
				Constants.PIPE) };
	}
}
