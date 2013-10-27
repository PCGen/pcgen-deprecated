/* 
 * DeityToken.java
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

package plugin.lsttokens.kit.deity;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.kit.CDOMKitDeity;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.kit.KitDeity;
import pcgen.persistence.lst.KitDeityLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * DEITY token for KitDeity
 */
public class DeityToken extends AbstractToken implements KitDeityLstToken,
		CDOMSecondaryToken<CDOMKitDeity>
{

	public boolean parse(KitDeity kitDeity, String value)
	{
		Logging.errorPrint("Ignoring second DEITY tag \"" + value
				+ "\" in Kit.");
		return false;
	}

	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "DEITY";
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
		if (isEmpty(value))
		{
			return false;
		}
		CDOMSingleRef<CDOMDeity> ref = context.ref.getCDOMReference(
				CDOMDeity.class, value);
		kitDeity.setDeity(ref);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitDeity kitDeity)
	{
		CDOMReference<CDOMDeity> race = kitDeity.getDeity();
		if (race == null)
		{
			return null;
		}
		return new String[] { race.getLSTformat() };
	}
}
