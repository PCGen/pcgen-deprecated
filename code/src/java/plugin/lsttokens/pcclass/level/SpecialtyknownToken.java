/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.pcclass.level;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.CDOMPCClassLevel;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with SPECIALTYKNOWN Token
 */
public class SpecialtyknownToken extends AbstractToken implements
		PCClassLstToken, CDOMPrimaryToken<CDOMPCClassLevel>
{

	@Override
	public String getTokenName()
	{
		return "SPECIALTYKNOWN";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		StringTokenizer st = new StringTokenizer(value, ",");
		List<String> list = new ArrayList<String>(st.countTokens());

		while (st.hasMoreTokens())
		{
			list.add(st.nextToken());
		}

		pcclass.addSpecialtyKnown(level, list);
		return true;
	}

	public boolean parse(LoadContext context, CDOMPCClassLevel pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}

		StringTokenizer st = new StringTokenizer(value, Constants.COMMA);
		while (st.hasMoreTokens())
		{
			String tok = st.nextToken();
			try
			{
				if (Integer.parseInt(tok) < 0)
				{
					Logging.errorPrint("Invalid Spell Count: " + tok
							+ " is less than zero");
					return false;
				}
			}
			catch (NumberFormatException e)
			{
				// OK, it must be a formula...
			}
			context.obj.addToList(pcc, ListKey.SPECIALTYKNOWN, FormulaFactory
					.getFormulaFor(tok));
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMPCClassLevel pcc)
	{
		Changes<Formula> changes = context.obj.getListChanges(pcc,
				ListKey.SPECIALTYKNOWN);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[] { StringUtil.join(changes.getAdded(),
				Constants.COMMA) };
	}

	public Class<CDOMPCClassLevel> getTokenClass()
	{
		return CDOMPCClassLevel.class;
	}
}
