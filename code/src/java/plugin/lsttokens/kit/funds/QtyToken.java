/*
 * QtyToken.java
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

package plugin.lsttokens.kit.funds;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.kit.CDOMKitFunds;
import pcgen.core.kit.KitFunds;
import pcgen.persistence.lst.KitFundsLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;

/**
 * QTY Token
 */
public class QtyToken implements KitFundsLstToken,
		CDOMSecondaryToken<CDOMKitFunds>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "QTY";
	}

	/**
	 * parse
	 * 
	 * @param kitFunds
	 *            KitFunds
	 * @param value
	 *            String
	 * @return boolean
	 */
	public boolean parse(KitFunds kitFunds, String value)
	{
		kitFunds.setQty(value);
		return true;
	}

	public Class<CDOMKitFunds> getTokenClass()
	{
		return CDOMKitFunds.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitFunds kitFunds, String value)
	{
		kitFunds.setQuantity(FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitFunds kitFunds)
	{
		Formula f = kitFunds.getQuantity();
		if (f == null)
		{
			return null;
		}
		return new String[] { f.toString() };
	}
}
