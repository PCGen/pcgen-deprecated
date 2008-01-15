/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens.equipmentmodifier.choose;

import pcgen.cdom.helper.NoChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.EqModChooseCompatibilityToken;
import pcgen.persistence.lst.EqModChooseLstToken;
import pcgen.util.Logging;

public class NoChoiceToken extends AbstractToken implements
		EqModChooseLstToken, EqModChooseCompatibilityToken
{

	public boolean parse(EquipmentModifier po, String prefix, String value)
	{
		if (value == null)
		{
			// No args - legal
			StringBuilder sb = new StringBuilder();
			if (prefix.length() > 0)
			{
				sb.append(prefix).append('|');
			}
			sb.append(getTokenName());
			po.setChoiceString(sb.toString());
			return true;
		}
		Logging.deprecationPrint("CHOOSE:" + getTokenName()
				+ " will ignore arguments: " + value);
		return false;
	}

	public String getTokenName()
	{
		return "NOCHOICE";
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

	public PrimitiveChoiceSet<?>[] parse(LoadContext context,
			EquipmentModifier mod, String value)
			throws PersistenceLayerException
	{
		if (!isEmpty(value))
		{
			return null;
		}
		return new PrimitiveChoiceSet<?>[] { new NoChoiceSet() };
	}
}
