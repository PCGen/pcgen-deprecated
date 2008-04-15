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
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.ChoiceSetCompatibilityToken;
import pcgen.util.Logging;

public class ShieldProfToken extends AbstractToken implements ChooseLstToken,
		ChoiceSetCompatibilityToken<CDOMObject>
{

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value == null)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " requires additional arguments");
			return false;
		}
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

		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
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
		return "SHIELDPROF";
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

		List<CDOMReference<CDOMEquipment>> cs = new ArrayList<CDOMReference<CDOMEquipment>>();
		if (Constants.LST_ANY.equals(value))
		{
			cs.add(context.ref.getCDOMTypeReference(CDOMEquipment.class,
					"Shield"));
		}
		else
		{
			List<CDOMReference<CDOMEquipment>> rc = getReferenceChooser(
					context, value);
			if (rc == null)
			{
				return null;
			}
			cs.addAll(rc);
		}
		return new ReferenceChoiceSet<CDOMEquipment>(cs);
	}

	private List<CDOMReference<CDOMEquipment>> getReferenceChooser(
			LoadContext context, String rest)
	{
		StringTokenizer st = new StringTokenizer(rest, Constants.PIPE);
		List<CDOMReference<CDOMEquipment>> eqList = new ArrayList<CDOMReference<CDOMEquipment>>();
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
				CDOMReference<CDOMEquipment> ref;
				if (tokString.startsWith(Constants.LST_TYPE_OLD)
						|| tokString.startsWith(Constants.LST_TYPE))
				{
					ref = TokenUtilities.getTypeReference(context,
							CDOMEquipment.class, "Shield."
									+ tokString.substring(5));
				}
				else
				{
					/*
					 * TODO What if this isn't a shield??
					 */
					ref = context.ref.getCDOMReference(CDOMEquipment.class,
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

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
