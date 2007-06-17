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
package plugin.lsttokens.choose;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.choice.AnyChooser;
import pcgen.cdom.choice.RemovingChooser;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.filter.ObjectKeyFilter;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.util.Logging;

public class SpellListToken extends AbstractToken implements ChooseLstToken
{

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value);
			return false;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return false;
		}
		if (hasIllegalSeparator('|', value))
		{
			return false;
		}

		if (!value.equals("Y") && !value.equals("N") && !value.equals("1")
			&& !value.equals("0"))
		{
			Logging.errorPrint("COUNT:" + getTokenName()
				+ " argument was not Y or N");
			return false;
		}
		StringBuilder sb = new StringBuilder();
		if (prefix.length() > 0)
		{
			sb.append(prefix).append('|');
		}
		sb.append(getTokenName()).append('|').append(value);
		po.setChoiceString(sb.toString());
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "SPELLLIST";
	}

	public ChoiceSet<?> parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		// 1) Filter to get classes where SPELLBOOK = value
		AnyChooser<PCClass> ac = new AnyChooser<PCClass>(PCClass.class);
		ObjectKeyFilter<PCClass> spellBookFilter = new ObjectKeyFilter<PCClass>(PCClass.class);
		spellBookFilter.setObjectFilter(ObjectKey.SPELLBOOK, Boolean.TRUE);
		RemovingChooser<PCClass> rc = new RemovingChooser<PCClass>(ac, false);
		rc.addRemovingChoiceFilter(spellBookFilter, false);
		
		// TODO Auto-generated method stub

		// 2) Get spellList for classes from 1)
		// Solution = Class to ClassSpellList Transformer
		// (ObjectKeyTransformer?)
		// 3) get Known spells from that spelllist
		// Solution = ??? (Can't just do an AND, probably need to have a false
		// root tranverse occur in a GrantedChooser-like behavior)

		return null;
	}
}
