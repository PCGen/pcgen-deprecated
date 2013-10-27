/*
 * SelectToken.java
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
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.kit.CDOMKitSelect;
import pcgen.core.Kit;
import pcgen.core.kit.KitSelect;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.BaseKitLoader;
import pcgen.persistence.lst.KitLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * SELECT for Kit
 */
public class SelectToken extends KitLstToken implements
		CDOMSecondaryToken<CDOMKitSelect>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "SELECT";
	}

	/**
	 * Handles the SELECT tag for a Kit.
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
		KitSelect kSelect = new KitSelect(colToken.nextToken());

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("SELECT:"))
			{
				Logging.errorPrint("Ignoring second SELECT tag \"" + colString
						+ "\" in SelectToken.parse");
			}
			else
			{
				if (BaseKitLoader.parseCommonTags(kSelect, colString, source) == false)
				{
					throw new PersistenceLayerException(
							"Unknown KitSelect info " + " \"" + colString
									+ "\"");
				}
			}
		}
		aKit.addObject(kSelect);
		return true;
	}

	public Class<CDOMKitSelect> getTokenClass()
	{
		return CDOMKitSelect.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitSelect kitSelect,
			String value)
	{
		kitSelect.setSelect(FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitSelect kitSelect)
	{
		Formula f = kitSelect.getSelect();
		if (f == null)
		{
			return null;
		}
		return new String[] { f.toString() };
	}

}
