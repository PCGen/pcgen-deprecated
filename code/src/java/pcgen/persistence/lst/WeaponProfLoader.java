/*
 * WeaponProfLoader.java
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * 
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class WeaponProfLoader extends GenericLstLoader<WeaponProf>
{
	/** Creates a new instance of WeaponProfLoader */
	public WeaponProfLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject,
	 *      java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public void parseLine(WeaponProf prof, String lstLine,
		CampaignSourceEntry source) throws PersistenceLayerException
	{
		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(WeaponProfLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (Exception e)
			{
				Logging.errorPrint("Unhandled Exception: " + e);
				// TODO Handle Exception
			}
			WeaponProfLstToken token = (WeaponProfLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, prof, value);
				if (!token.parse(prof, value))
				{
					Logging.errorPrint("Error parsing WeaponProf "
						+ prof.getDisplayName() + ':' + source.getURI() + ':'
						+ colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(prof, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Illegal weapon proficiency info '"
					+ lstLine + "' in " + source.toString());
			}
		}

		// WeaponProfs are one line each;
		// finish the object
		completeObject(source, prof);
	}

	/**
	 * Get the weapon prof object with key aKey
	 * 
	 * @param aKey
	 * @return PObject
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectKeyed(java.lang.String)
	 */
	@Override
	protected WeaponProf getObjectKeyed(String aKey)
	{
		return Globals.getWeaponProfKeyed(aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	@Override
	protected void performForget(final WeaponProf objToForget)
	{
		Globals.removeWeaponProfKeyed(objToForget.getKeyName());
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#addGlobalObject(pcgen.core.PObject)
	 */
	@Override
	protected void addGlobalObject(final PObject pObj)
	{
		Globals.addWeaponProf((WeaponProf) pObj);
		// TODO - What exactly is this doing? Why would we set that it is not
		// a new item when we just added it?
		pObj.setNewItem(false);
	}

	@Override
	public Class<WeaponProf> getLoadClass()
	{
		return WeaponProf.class;
	}

	@Override
	public Class<? extends CDOMCompatibilityToken<WeaponProf>> getCompatibilityTokenClass()
	{
		//TODO Need to specify this
		return null;
	}

	@Override
	public Class<WeaponProfLstToken> getTokenClass()
	{
		return WeaponProfLstToken.class;
	}
}
