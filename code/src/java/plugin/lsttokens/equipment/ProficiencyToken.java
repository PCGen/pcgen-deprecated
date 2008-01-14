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
package plugin.lsttokens.equipment;

import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.util.Logging;

/**
 * Deals with PROFICIENCY token
 */
public class ProficiencyToken extends AbstractToken implements EquipmentLstToken
{

	@Override
	public String getTokenName()
	{
		return "PROFICIENCY";
	}

	public boolean parse(Equipment eq, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.deprecationPrint("Equipment Token PROFICIENCY syntax "
					+ "without a Subtoken is deprecated: " + value);
			Logging.deprecationPrint("Please use: "
					+ "PROFICIENCY:<subtoken>|<prof>");
			eq.setProfName(value);
		}
		else
		{
			String subtoken = value.substring(0, pipeLoc);
			String prof = value.substring(pipeLoc + 1);
			if (prof == null || prof.length() == 0)
			{
				Logging.errorPrint("PROFICIENCY cannot have "
						+ "empty second argument: " + value);
				return false;
			}
			if (prof.indexOf(Constants.PIPE) != -1)
			{
				Logging.errorPrint("PROFICIENCY cannot have two | characters: "
						+ value);
				return false;
			}
			if (subtoken.equals("WEAPON"))
			{
				eq.setWeaponProf(prof);
			}
			else if (subtoken.equals("ARMOR"))
			{
				eq.setArmorProf(prof);
			}
			else if (subtoken.equals("SHIELD"))
			{
				eq.setShieldProf(prof);
			}
			else
			{
				Logging.errorPrint("Unknown Subtoken for PROFICIENCY: "
						+ subtoken);
				Logging.errorPrint("  Subtoken must be "
						+ "WEAPON, ARMOR or SHIELD");
				return false;
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		/*
		 * BUG TODO This is a problem - it is not necessarily a WEAPON
		 * proficiency - could be Shield or Armor... :P
		 */
		CDOMSimpleSingleRef<WeaponProf> wp =
				context.ref.getCDOMReference(WeaponProf.class, value);
		context.getObjectContext().put(eq, ObjectKey.WEAPON_PROF, wp);
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		CDOMSimpleSingleRef<WeaponProf> wp =
				context.getObjectContext().getObject(eq, ObjectKey.WEAPON_PROF);
		if (wp == null)
		{
			return null;
		}
		return new String[]{wp.getLSTformat()};
	}
}
