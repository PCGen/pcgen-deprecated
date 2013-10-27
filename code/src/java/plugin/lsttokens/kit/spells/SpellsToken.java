/*
 * SpellsToken.java
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

package plugin.lsttokens.kit.spells;

import java.util.ArrayList;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.cdom.kit.CDOMKitSpells;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Globals;
import pcgen.core.kit.KitSpells;
import pcgen.persistence.lst.KitSpellsLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * SPELLS token for KitSpells
 */
public class SpellsToken implements KitSpellsLstToken,
		CDOMSecondaryToken<CDOMKitSpells>
{
	private static final Class<CDOMSpell> SPELL_CLASS = CDOMSpell.class;
	private static final Class<CDOMAbility> ABILITY_CLASS = CDOMAbility.class;

	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "SPELLS";
	}

	/**
	 * parse
	 * 
	 * @param kitSpells
	 *            KitSpells
	 * @param value
	 *            String
	 * @return boolean
	 */
	public boolean parse(KitSpells kitSpells, String value)
	{
		Logging.errorPrint("Ignoring second SPELLS tag \"" + value
				+ "\" in Kit.");
		return false;
	}

	public Class<CDOMKitSpells> getTokenClass()
	{
		return CDOMKitSpells.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitSpells kitSpell,
			String value)
	{
		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
		while (aTok.hasMoreTokens())
		{
			String field = aTok.nextToken();
			if (field.startsWith("SPELLBOOK="))
			{
				if (kitSpell.getSpellBook() != null)
				{
					Logging.errorPrint("Cannot reset SPELLBOOK in SPELLS: "
							+ value);
					return false;
				}
				kitSpell.setSpellBook(field.substring(10));
			}
			else if (field.startsWith("CLASS="))
			{
				if (kitSpell.getCastingClass() != null)
				{
					Logging.errorPrint("Cannot reset CLASS" + " in SPELLS: "
							+ value);
					return false;
				}
				kitSpell.setCastingClass(context.ref.getCDOMReference(
						CDOMPCClass.class, field.substring(6)));
			}
			else
			{
				int count = 1;
				int equalLoc = field.indexOf("=");
				if (equalLoc != -1)
				{
					String countStr = field.substring(equalLoc + 1);
					try
					{
						count = Integer.parseInt(countStr);
					}
					catch (NumberFormatException e)
					{
						Logging.errorPrint("Expected an Integer COUNT,"
								+ " but found: " + countStr + " in " + value);
						return false;
					}
					field = field.substring(0, equalLoc);
				}
				StringTokenizer subTok = new StringTokenizer(field, "[]");
				String spellName = subTok.nextToken();
				CDOMSingleRef<CDOMSpell> spell = context.ref.getCDOMReference(
						SPELL_CLASS, spellName);

				ArrayList<CDOMSingleRef<CDOMAbility>> featList = new ArrayList<CDOMSingleRef<CDOMAbility>>();
				while (subTok.hasMoreTokens())
				{
					String featName = subTok.nextToken();
					CDOMSingleRef<CDOMAbility> feat = context.ref
							.getCDOMReference(ABILITY_CLASS,
									CDOMAbilityCategory.FEAT, featName);
					featList.add(feat);
				}
				kitSpell.addSpell(spell, featList, count);
			}
		}
		if (kitSpell.getSpellBook() == null)
		{
			kitSpell.setSpellBook(Globals.getDefaultSpellBook());
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitSpells kitSkill)
	{
		return null;
	}
}
