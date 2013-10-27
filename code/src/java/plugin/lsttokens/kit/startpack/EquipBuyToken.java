/*
 * EquipBuyToken.java
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

package plugin.lsttokens.kit.startpack;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.inst.CDOMKit;
import pcgen.core.Kit;
import pcgen.core.QualifiedObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.lst.KitStartpackLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * EQUIPBUY Token for KitStartpack
 */
public class EquipBuyToken extends AbstractToken implements
		KitStartpackLstToken, CDOMSecondaryToken<CDOMKit>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "EQUIPBUY";
	}

	/**
	 * parse
	 * 
	 * @param kit
	 *            Kit
	 * @param value
	 *            String
	 * @return boolean
	 */
	public boolean parse(Kit kit, String value)
	{
		kit.setBuyRate(value);
		return true;
	}

	public Class<CDOMKit> getTokenClass()
	{
		return CDOMKit.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKit kit, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		String token = tok.nextToken();

		if (token.startsWith("PRE") || token.startsWith("!PRE"))
		{
			Logging.errorPrint("Cannot have only PRExxx subtoken in "
					+ getTokenName());
			return false;
		}

		Formula f = FormulaFactory.getFormulaFor(token);
		List<Prerequisite> prereqs = new ArrayList<Prerequisite>();

		while (tok.hasMoreTokens())
		{
			token = tok.nextToken();
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put feats after the "
						+ "PRExxx tags in " + getTokenName() + ":?)");
				return false;
			}
			prereqs.add(prereq);
		}
		kit.setEquipBuy(new QualifiedObject<Formula>(f, prereqs));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKit kit)
	{
		QualifiedObject<Formula> qo = kit.getEquipBuy();
		if (qo == null)
		{
			return null;
		}
		Formula f = qo.getObject(null);
		List<Prerequisite> prereqs = qo.getPrereqs();
		String ab = f.toString();
		if (prereqs != null && !prereqs.isEmpty())
		{
			ab = ab + Constants.PIPE + getPrerequisiteString(context, prereqs);
		}
		return new String[] { ab };
	}

}
