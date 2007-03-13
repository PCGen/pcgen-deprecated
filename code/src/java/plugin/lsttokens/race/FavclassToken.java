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
package plugin.lsttokens.race;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with FAVCLASS Token
 */
public class FavclassToken implements RaceLstToken
{
	public static final Class<PCClass> PCCLASS_CLASS = PCClass.class;

	public String getTokenName()
	{
		return "FAVCLASS";
	}

	public boolean parse(Race race, String value)
	{
		race.setFavoredClass(value);
		return true;
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		return parseFavoredClass(context, race, value);
	}

	public boolean parseFavoredClass(LoadContext context, CDOMObject cdo,
		String value)
	{
		/*
		 * FIXME This needs to be able to query the CDOMObject for its type...
		 */
		// Prerequisite prereq = getPrerequisite("!PRExxx:"
		// + cdo.get(StringKey.NAME));
		// if (prereq == null) {
		// Logging.errorPrint(" result of improper internal build in "
		// + getTokenName() + ": " + value);
		// return false;
		// }
		/*
		 * CONSIDER IS this sufficient for the PRE?
		 * 
		 * Or is this a PREMULT - what exactly are the rules?!? :)
		 */

		StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);

		while (tok.hasMoreTokens())
		{
			CDOMReference<PCClass> fc;
			String tokString = tok.nextToken();
			if (Constants.LST_ANY.equalsIgnoreCase(tokString))
			{
				// TODO Warn on lower case??
				fc = context.ref.getCDOMAllReference(PCCLASS_CLASS);
			}
			else
			{
				fc = context.ref.getCDOMReference(PCCLASS_CLASS, tokString);
			}
			// TODO FIXME need to uncomment this...
			// context.addPrerequisiteToContent(prereq, fc, XPPenalty.class);
		}
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
