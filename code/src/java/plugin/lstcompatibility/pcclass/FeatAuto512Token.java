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
 * Current Ver: $Revision: 3312 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-07-04 21:36:43 -0400 (Wed, 04 Jul 2007) $
 */
package plugin.lstcompatibility.pcclass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.util.Logging;

/**
 * Class deals with FEAT Token
 */
public class FeatAuto512Token extends AbstractToken implements
		CDOMCompatibilityToken<CDOMPCClass>
{
	public static final Class<CDOMAbility> ABILITY_CLASS = CDOMAbility.class;

	@Override
	public String getTokenName()
	{
		return "FEATAUTO";
	}

	public boolean parse(LoadContext context, CDOMPCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		String token = tok.nextToken();

		if (token.startsWith("PRE") || token.startsWith("!PRE"))
		{
			Logging.errorPrint("Cannot have only PRExxx subtoken in "
					+ getTokenName());
			return false;
		}

		List<CDOMReference<CDOMAbility>> abilityList = new ArrayList<CDOMReference<CDOMAbility>>();

		while (true)
		{
			CDOMSingleRef<CDOMAbility> ability = context.ref.getCDOMReference(
					ABILITY_CLASS, CDOMAbilityCategory.FEAT, token);
			abilityList.add(ability);

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				finish(context, pcc, abilityList, null);
				return true;
			}
			token = tok.nextToken();
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				return false;
			}
			if (token.contains(".CLEAR"))
			{
				return false;
			}
		}
	}

	private void finish(LoadContext context, CDOMObject obj,
			List<CDOMReference<CDOMAbility>> abilityList,
			List<Prerequisite> prereqs)
	{
		for (CDOMReference<CDOMAbility> ability : abilityList)
		{
			AssociatedPrereqObject edge = context.getGraphContext().grant(
					getTokenName(), obj, ability);
			edge.setAssociation(AssociationKey.ABILITY_NATURE,
					AbilityNature.AUTOMATIC);
			if (prereqs != null)
			{
				for (Prerequisite prereq : prereqs)
				{
					edge.addPrerequisite(prereq);
				}
			}
		}
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilitySubLevel()
	{
		return 12;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}
