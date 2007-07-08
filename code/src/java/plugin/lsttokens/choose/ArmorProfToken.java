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
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.helper.ReferenceChoiceSet;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.ChooseCompatibilityToken;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class ArmorProfToken extends AbstractToken implements ChooseLstToken,
		ChooseCompatibilityToken
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

		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " must have two or more | delimited arguments : " + value);
			return false;
		}
		String start = value.substring(0, pipeLoc);
		try
		{
			Integer.parseInt(start);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " first argument must be an Integer : " + value);
			return false;
		}
		StringTokenizer st =
				new StringTokenizer(value.substring(pipeLoc + 1),
					Constants.PIPE);
		while (st.hasMoreTokens())
		{
			String tokString = st.nextToken();
			int equalsLoc = tokString.indexOf("=");
			if (equalsLoc == tokString.length() - 1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments must have value after = : " + tokString);
				Logging.errorPrint("  entire token was: " + value);
				return false;
			}
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
		return "ARMORPROF";
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

	public PrimitiveChoiceSet<?> parse(LoadContext context, CDOMObject cdo,
		String value) throws PersistenceLayerException
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

		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " must have two or more | delimited arguments : " + value);
			return null;
		}
		/*
		 * TODO Need the count
		 */
		// int count;
		// try
		// {
		// count = Integer.parseInt(value.substring(0, pipeLoc));
		// }
		// catch (NumberFormatException nfe)
		// {
		// Logging.errorPrint("CHOOSE:" + getTokenName()
		// + " first argument must be an Integer : " + value);
		// return null;
		// }
		List<CDOMReference<Equipment>> cs =
				new ArrayList<CDOMReference<Equipment>>();
		String rest = value.substring(pipeLoc + 1);
		if (Constants.LST_ANY.equals(rest))
		{
			cs.add(context.ref.getCDOMTypeReference(Equipment.class, "Armor"));
		}
		else
		{
			List<CDOMReference<Equipment>> rc =
					getReferenceChooser(context, rest);
			if (rc == null)
			{
				return null;
			}
			cs.addAll(rc);
		}
		/*
		 * TODO How to add count with this structure of returning a
		 * PrimitiveChoiceSet??
		 */
		// chooser.setMaxSelections(FormulaFactory.getFormulaFor(count));
		return new ReferenceChoiceSet<Equipment>(cs);
	}

	private List<CDOMReference<Equipment>> getReferenceChooser(
		LoadContext context, String rest)
	{
		StringTokenizer st = new StringTokenizer(rest, Constants.PIPE);
		List<CDOMReference<Equipment>> eqList =
				new ArrayList<CDOMReference<Equipment>>();
		while (st.hasMoreTokens())
		{
			String tokString = st.nextToken();
			if (Constants.LST_ANY.equals(tokString))
			{
				Logging.errorPrint("In CHOOSE:" + getTokenName()
					+ ": Cannot use ANY and another qualifier: " + rest);
				return null;
			}
			else
			{
				CDOMReference<Equipment> ref;
				if (tokString.startsWith(Constants.LST_TYPE_OLD)
					|| tokString.startsWith(Constants.LST_TYPE))
				{
					ref =
							TokenUtilities.getTypeReference(context,
								Equipment.class, "Armor."
									+ tokString.substring(5));
				}
				else
				{
					/*
					 * TODO What if this isn't armor??
					 */
					ref =
							context.ref.getCDOMReference(Equipment.class,
								tokString);
				}
				if (ref == null)
				{
					Logging.errorPrint("Invalid Reference: " + tokString
						+ " in CHOOSE:" + getTokenName() + ": " + rest);
					return null;
				}
				eqList.add(ref);
			}
		}
		return eqList;
	}

}
