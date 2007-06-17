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
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.choice.AnyChooser;
import pcgen.cdom.choice.CompoundAndChooser;
import pcgen.cdom.choice.GrantedChooser;
import pcgen.cdom.choice.ReferenceChooser;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.Ability;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class FeatListToken extends AbstractToken implements ChooseLstToken
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
		return "FEATLIST";
	}

	public ChoiceSet<?> parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value);
			return null;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return null;
		}
		if (hasIllegalSeparator('|', value))
		{
			return null;
		}

		ChoiceSet<Ability> cs;
		if (Constants.LST_ANY.equals(value))
		{
			cs = new AnyChooser<Ability>(Ability.class, AbilityCategory.FEAT);
		}
		else
		{
			cs = getReferenceChooser(context, value);
			if (cs == null)
			{
				return null;
			}
		}
		CompoundAndChooser<Ability> chooser = new CompoundAndChooser<Ability>();
		GrantedChooser<Ability> pcChooser =
				new GrantedChooser<Ability>(Ability.class);
		chooser.addChoiceSet(cs, true);
		chooser.addChoiceSet(pcChooser, false);
		return chooser;
	}

	private ChoiceSet<Ability> getReferenceChooser(LoadContext context,
		String value)
	{
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		List<CDOMReference<Ability>> featList =
				new ArrayList<CDOMReference<Ability>>();
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			if (Constants.LST_ANY.equals(tokString))
			{
				Logging.errorPrint("Cannot use ANY and another qualifier: "
					+ value);
				return null;
			}
			else
			{
				CDOMReference<Ability> ref =
						TokenUtilities.getTypeOrPrimitive(context,
							Ability.class, AbilityCategory.FEAT, tokString);
				if (ref == null)
				{
					Logging.errorPrint("Invalid Reference: " + tokString
						+ " in CHOOSE:" + getTokenName() + ": " + value);
					return null;
				}
				featList.add(ref);
			}
		}
		return new ReferenceChooser<Ability>(featList);
	}

	public String unparse(LoadContext context, ChoiceSet<?> chooser)
	{
		return chooser.getLSTformat();
	}
}
