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

import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMArmorProf;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.CDOMShieldProf;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with PROFICIENCY token
 */
public class ProficiencyToken extends AbstractToken implements
		EquipmentLstToken, CDOMPrimaryToken<CDOMEquipment>
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
			Logging.addParseMessage(Logging.LST_ERROR,
					"Equipment Token PROFICIENCY syntax "
							+ "without a Subtoken is deprecated: " + value);
			Logging.addParseMessage(Logging.LST_ERROR, "Please use: "
					+ "PROFICIENCY:<subtoken>|<prof>");
			eq.setProfName(value);
		}
		else
		{
			String subtoken = value.substring(0, pipeLoc);
			String prof = value.substring(pipeLoc + 1);
			if (prof == null || prof.length() == 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
						"PROFICIENCY cannot have " + "empty second argument: "
								+ value);
				return false;
			}
			if (prof.indexOf(Constants.PIPE) != -1)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
						"PROFICIENCY cannot have two | characters: " + value);
				return false;
			}
			if (prof.indexOf("[hands]") != -1)
			{
				Logging.errorPrint("PROFICIENCY cannot have the String"
						+ "[hands] in 5.14 format: feature is deprecated");
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
				Logging.addParseMessage(Logging.LST_ERROR,
						"Unknown Subtoken for PROFICIENCY: " + subtoken);
				Logging.addParseMessage(Logging.LST_ERROR,
						"  Subtoken must be " + "WEAPON, ARMOR or SHIELD");
				return false;
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMEquipment eq, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Equipment Token PROFICIENCY syntax "
							+ "without a Subtoken is invalid: " + value);
			return false;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " expecting only one '|', "
					+ "format is: SubToken|ProfName value was: " + value);
			return false;
		}
		String subtoken = value.substring(0, pipeLoc);
		String prof = value.substring(pipeLoc + 1);
		if (prof == null || prof.length() == 0)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"PROFICIENCY cannot have " + "empty second argument: "
							+ value);
			return false;
		}
		if (subtoken.equals("WEAPON"))
		{
			CDOMSingleRef<CDOMWeaponProf> wp = context.ref.getCDOMReference(
					CDOMWeaponProf.class, prof);
			context.getObjectContext().put(eq, ObjectKey.WEAPON_PROF, wp);
		}
		else if (subtoken.equals("ARMOR"))
		{
			CDOMSingleRef<CDOMArmorProf> wp = context.ref.getCDOMReference(
					CDOMArmorProf.class, prof);
			context.getObjectContext().put(eq, ObjectKey.ARMOR_PROF, wp);
		}
		else if (subtoken.equals("SHIELD"))
		{
			CDOMSingleRef<CDOMShieldProf> wp = context.ref.getCDOMReference(
					CDOMShieldProf.class, prof);
			context.getObjectContext().put(eq, ObjectKey.SHIELD_PROF, wp);
		}
		else
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Unknown Subtoken for PROFICIENCY: " + subtoken);
			Logging.addParseMessage(Logging.LST_ERROR, "  Subtoken must be "
					+ "WEAPON, ARMOR or SHIELD");
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMEquipment eq)
	{
		CDOMSingleRef<CDOMWeaponProf> wp = context.getObjectContext()
				.getObject(eq, ObjectKey.WEAPON_PROF);
		CDOMSingleRef<CDOMShieldProf> sp = context.getObjectContext()
				.getObject(eq, ObjectKey.SHIELD_PROF);
		CDOMSingleRef<CDOMArmorProf> ap = context.getObjectContext().getObject(
				eq, ObjectKey.ARMOR_PROF);
		if (wp == null)
		{
			if (sp == null)
			{
				if (ap == null)
				{
					return null;
				}
				return new String[] { "ARMOR|" + ap.getLSTformat() };
			}
			else
			{
				if (ap == null)
				{
					return new String[] { "SHIELD|" + sp.getLSTformat() };
				}
				context.addWriteMessage("Equipment may not have both "
						+ "ARMOR and SHIELD Proficiencies");
				return null;
			}
		}
		if (sp == null)
		{
			if (ap == null)
			{
				return new String[] { "WEAPON|" + wp.getLSTformat() };
			}
			context.addWriteMessage("Equipment may not have both "
					+ "ARMOR and WEAPON Proficiencies");
			return null;
		}
		else
		{
			context.addWriteMessage("Equipment may not have both "
					+ "WEAPON and SHIELD Proficiencies");
			return null;
		}
	}

	public Class<CDOMEquipment> getTokenClass()
	{
		return CDOMEquipment.class;
	}
}
