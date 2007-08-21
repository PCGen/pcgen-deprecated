/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.template;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with LEVELADJUSTMENT Token
 */
public class LeveladjustmentToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "LEVELADJUSTMENT";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.setLevelAdjustment(value);
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		context.getObjectContext().put(template, FormulaKey.LEVEL_ADJUSTMENT,
			FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Formula f =
				context.getObjectContext().getFormula(pct,
					FormulaKey.LEVEL_ADJUSTMENT);
		if (f == null)
		{
			return null;
		}
		return new String[]{f.toString()};
	}
}
