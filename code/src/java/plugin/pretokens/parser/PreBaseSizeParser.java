/*
 * PreBaseSizeParser.java
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
 * Created on 17-Dec-2003
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
import pcgen.core.prereq.PrerequisiteException;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * @author wardc
 *
 */
public class PreBaseSizeParser extends AbstractPrerequisiteParser implements
		PrerequisiteParserInterface
{
	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]{"BASESIZE", "BASESIZEEQ", "BASESIZEGT",
			"BASESIZEGTEQ", "BASESIZELT", "BASESIZELTEQ", "BASESIZENEQ"};
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#parse(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public Prerequisite parse(String kind, String formula,
		boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq =
				super.parse(kind, formula, invertResult, overrideQualify);
		try
		{
			prereq.setKind("basesize");

			// Get the comparator type BASESIZEGTEQ, BASESIZE, BASESIZENEQ etc.
			String compType = kind.substring(8);
			if (compType.length() == 0)
			{
				compType = "gteq";
			}
			prereq.setOperator(compType);

			prereq.setOperand(formula);
			if (invertResult)
			{
				prereq.setOperator(prereq.getOperator().invert());
			}
		}
		catch (PrerequisiteException pe)
		{
			throw new PersistenceLayerException(
				"Unable to parse the prerequisite :'" + kind + ":" + formula
					+ "'. " + pe.getLocalizedMessage());
		}
		return prereq;
	}
}
