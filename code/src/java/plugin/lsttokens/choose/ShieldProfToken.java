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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.choice.CompoundAndChooser;
import pcgen.cdom.choice.RefSetChooser;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class ShieldProfToken implements ChooseLstToken
{

	private static final Class<Equipment> EQUIPMENT_CLASS = Equipment.class;

	public boolean parse(PObject po, String value)
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
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}
		po.setChoiceString(value);
		return true;
	}

	public String getTokenName()
	{
		return "SHIELDPROF";
	}

	public ChoiceSet<?> parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (value.indexOf('|') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain | : " + value);
			return null;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return null;
		}
		if (value.charAt(0) == '.')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not start with . : " + value);
			return null;
		}
		if (value.charAt(value.length() - 1) == '.')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not end with . : " + value);
			return null;
		}
		if (value.indexOf("..") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments uses double separator .. : " + value);
			return null;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		List<CDOMReference<Equipment>> eqList =
				new ArrayList<CDOMReference<Equipment>>();
		while (st.hasMoreTokens())
		{
			CDOMReference<Equipment> eq =
					TokenUtilities.getTypeOrPrimitive(context, EQUIPMENT_CLASS,
						st.nextToken());
			eqList.add(eq);
		}
		CompoundAndChooser<Equipment> chooser =
				new CompoundAndChooser<Equipment>();
		RefSetChooser<Equipment> setChooser = new RefSetChooser<Equipment>(eqList);
		chooser.addChoiceSet(setChooser);
		CDOMReference<Equipment> shield =
				TokenUtilities.getTypeReference(context, EQUIPMENT_CLASS,
					"Shield");
		chooser.addChoiceSet(new RefSetChooser<Equipment>(Collections
			.singletonList(shield)));
		return chooser;
	}
}
