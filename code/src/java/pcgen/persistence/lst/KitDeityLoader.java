/*
 * KitDeityLoader.java
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
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package pcgen.persistence.lst;

import java.net.URI;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Kit;
import pcgen.core.kit.KitDeity;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * Deals with loading DEITY for KIT
 */
public class KitDeityLoader
{
	/**
	 * parse the line 
	 * @param kit
	 * @param colString
	 * @throws PersistenceLayerException
	 */
	public static void parseLine(Kit kit, String colString, URI source)
		throws PersistenceLayerException
	{
		final StringTokenizer colToken =
				new StringTokenizer(colString, SystemLoader.TAB_DELIM);
		KitDeity kitDeity = null;
		String deityName = colToken.nextToken();
		kitDeity = new KitDeity(deityName);

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(KitDeityLstToken.class);
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken();

			// We will find the first ":" for the "controlling" line token
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				throw new PersistenceLayerException();
			}
			KitDeityLstToken token = (KitDeityLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, kit, value);
				if (!token.parse(kitDeity, value))
				{
					Logging.errorPrint("Error parsing Kit Ability tag "
						+ kitDeity.getObjectName() + ':' + colString + "\"");
				}
			}
			else if (BaseKitLoader.parseCommonTags(kitDeity, colString, source))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Unknown Kit Class info: \"" + colString
					+ "\"");
			}
		}
		kit.addObject(kitDeity);
	}

}
