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
 * Current Ver: $Revision: 2959 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-05-20 00:02:34 -0400 (Sun, 20 May 2007) $
 */
package plugin.lstcompatibility.subclass;

import pcgen.cdom.content.CDOMSpellProhibitor;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.ProhibitedSpellType;
import pcgen.cdom.enumeration.SpellDescriptor;
import pcgen.cdom.enumeration.SpellSchool;
import pcgen.cdom.enumeration.SpellSubSchool;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.inst.CDOMSubClass;
import pcgen.core.Constants;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

public class Choice512Token implements CDOMCompatibilityToken<CDOMSubClass>,
		DeferredToken<CDOMSubClass>
{

	public String getTokenName()
	{
		return "CHOICE";
	}

	public boolean parse(LoadContext context, CDOMSubClass sc, String value)
	{
		if (value.indexOf(Constants.PIPE) == -1)
		{
			sc.put(StringKey.COMPAT_CHOICE, value);
			sc.put(StringKey.COMPAT_CHOICE_SOURCE, sc.getKeyName()
					+ " in file " + context.getGraphContext().getSourceURI());
			return true;
		}
		return false;
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
		return 12;
	}

	public Class<CDOMSubClass> getObjectClass()
	{
		return CDOMSubClass.class;
	}

	public boolean process(LoadContext context, CDOMSubClass sc)
	{
		String value = sc.get(StringKey.COMPAT_CHOICE);
		if (value == null)
		{
			return true;
		}
		boolean hasSchool = SpellSchool.containsConstantNamed(value);
		boolean hasSubSchool = SpellSubSchool.containsConstantNamed(value);
		boolean hasDescriptor = SpellDescriptor.containsConstantNamed(value);
		ProhibitedSpellType<?> pst;
		if (hasSchool)
		{
			if (hasSubSchool)
			{
				Logging.errorPrint("CHOICE is ambiguous: " + value
						+ " is both Spell School and SubSchool: "
						+ sc.get(StringKey.COMPAT_CHOICE_SOURCE));
				return false;
			}
			else if (hasDescriptor)
			{
				Logging.errorPrint("CHOICE is ambiguous: " + value
						+ " is both Spell School and Descriptor: "
						+ sc.get(StringKey.COMPAT_CHOICE_SOURCE));
				return false;
			}
			pst = ProhibitedSpellType.SCHOOL;
		}
		else
		{
			if (hasSubSchool)
			{
				if (hasDescriptor)
				{
					Logging.errorPrint("CHOICE is ambiguous: " + value
							+ " is both Spell SubSchool and Descriptor: "
							+ sc.get(StringKey.COMPAT_CHOICE_SOURCE));
					return false;
				}
				pst = ProhibitedSpellType.SUBSCHOOL;
			}
			else if (hasDescriptor)
			{
				pst = ProhibitedSpellType.DESCRIPTOR;
			}
			else
			{
				Logging.errorPrint("CHOICE is invalid: " + value
						+ " is not a Spell School, SubSchool or Descriptor: "
						+ sc.get(StringKey.COMPAT_CHOICE_SOURCE));
				return false;
			}
		}
		CDOMSpellProhibitor<?> sp = getSpellProhib(pst, value);
		context.getObjectContext().put(sc, ObjectKey.SELETED_SPELLS, sp);
		return true;
	}

	private <T> CDOMSpellProhibitor<T> getSpellProhib(ProhibitedSpellType<T> pst,
			String arg)
	{
		CDOMSpellProhibitor<T> spSchool = new CDOMSpellProhibitor<T>();
		spSchool.setType(pst);
		spSchool.addValue(pst.getTypeValue(arg));
		return spSchool;
	}

	public Class<CDOMSubClass> getTokenClass()
	{
		return CDOMSubClass.class;
	}
}
