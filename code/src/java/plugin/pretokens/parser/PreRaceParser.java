/*
 * PreRaceParser.java
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package plugin.pretokens.parser;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteListParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * @author wardc
 *
 */
public class PreRaceParser extends AbstractPrerequisiteListParser implements
		PrerequisiteParserInterface
{
	public String[] kindsHandled()
	{
		return new String[]{"RACE"};
	}

	@Override
	public Prerequisite parse(String kind, String formula,
		boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{

		final Prerequisite prereq =
				super.parse(kind, formula, invertResult, overrideQualify);

		//
		// Negate the race names wrapped in []'s. Then need to bump up the required number of matches
		//
		if (formula.indexOf('[') >= 0)
		{
			negateRaceChoice(prereq);
		}

		return prereq;
	}

	private void negateRaceChoice(Prerequisite prereq)
	{
		int modified = 0;
		for (Prerequisite p : prereq.getPrerequisites())
		{
			if (p.getKind() == null) // PREMULT
			{
				negateRaceChoice(p);
			}
			else
			{
				String preKey = p.getKey();
				if (preKey.startsWith("[") && preKey.endsWith("]"))
				{
					preKey = preKey.substring(1, preKey.length() - 1);
					p.setKey(preKey);
					p.setOperator(p.getOperator().invert());
					++modified;
				}
			}
		}
		if (modified > 0)
		{
			String oper = prereq.getOperand();
			try
			{
				oper = Integer.toString(Integer.parseInt(oper) + modified);
			}
			catch (NumberFormatException nfe)
			{
				oper = "(" + oper + ")+" + Integer.toString(modified);
			}
			prereq.setOperand(oper);
		}
	}
}
