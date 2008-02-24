/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2005-2006 (C) Devon Jones
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
 * Current Ver: $Revision: 3059 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-05-27 14:26:01 -0400 (Sun, 27 May 2007) $
 */
package plugin.lstcompatibility.global;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class Spells514Lst extends AbstractToken implements
		CDOMCompatibilityToken<CDOMObject>
{
	@Override
	public String getTokenName()
	{
		return "SPELLS";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		// if (!(obj instanceof Campaign)) {
		return createSpellsList(context, obj, value);
		// }
		// return false;
	}

	/**
	 * SPELLS:<spellbook name>|[<optional parameters, pipe deliminated>] |<spell
	 * name>[,<formula for DC>] |<spell name2>[,<formula2 for DC>] |PRExxx
	 * |PRExxx
	 * 
	 * CASTERLEVEL=<formula> Casterlevel of spells TIMES=<formula> Cast Times
	 * per day, -1=At Will
	 * 
	 * @param sourceLine
	 *            Line from the LST file without the SPELLS:
	 * @return spells list
	 */
	private boolean createSpellsList(LoadContext context, CDOMObject obj,
			String sourceLine)
	{
		if (isEmpty(sourceLine) || hasIllegalSeparator('|', sourceLine))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(sourceLine, Constants.PIPE);
		String spellBook = tok.nextToken();
		// Formula casterLevel = null;
		String casterLevel = null;
		String times = "1"; // FormulaFactory.getFormulaFor("1");

		if (!tok.hasMoreTokens())
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ ": minimally requires a Spell Name");
			return false;
		}
		String token = tok.nextToken();

		if (token.startsWith("CASTERLEVEL="))
		{
			// casterLevel =
			// FormulaFactory.getFormulaFor(token.substring(12));
			casterLevel = token.substring(12);
			if (casterLevel.length() == 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
						"Error in Caster Level in " + getTokenName()
								+ ": argument was empty");
				return false;
			}
			if (!tok.hasMoreTokens())
			{
				Logging
						.addParseMessage(
								Logging.LST_ERROR,
								getTokenName()
										+ ": minimally requires a Spell Name (after CASTERLEVEL=)");
				return false;
			}
			token = tok.nextToken();
		}
		if (token.startsWith("TIMES="))
		{
			// times = FormulaFactory.getFormulaFor(token.substring(6));
			times = token.substring(6);
			if (times.length() == 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, "Error in Times in "
						+ getTokenName() + ": argument was empty");
				return false;
			}
			if (!tok.hasMoreTokens())
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
						+ ": minimally requires a Spell Name (after TIMES=)");
				return false;
			}
			token = tok.nextToken();
		}

		if (token.charAt(0) == ',')
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " Spell arguments may not start with , : " + token);
			return false;
		}
		if (token.charAt(token.length() - 1) == ',')
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " Spell arguments may not end with , : " + token);
			return false;
		}
		if (token.indexOf(",,") != -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " Spell arguments uses double separator ,, : " + token);
			return false;
		}

		DoubleKeyMap<CDOMReference<CDOMSpell>, AssociationKey<String>, String> dkm = new DoubleKeyMap<CDOMReference<CDOMSpell>, AssociationKey<String>, String>();
		while (true)
		{
			int commaLoc = token.indexOf(',');
			String name = commaLoc == -1 ? token : token.substring(0, commaLoc);
			CDOMReference<CDOMSpell> spell = context.ref.getCDOMReference(
					CDOMSpell.class, name);
			dkm.put(spell, AssociationKey.CASTER_LEVEL, casterLevel);
			dkm.put(spell, AssociationKey.TIMES_PER_UNIT, times);
			dkm.put(spell, AssociationKey.SPELLBOOK, spellBook);
			if (commaLoc != -1)
			{
				dkm.put(spell, AssociationKey.DC_FORMULA, token
						.substring(commaLoc + 1));
			}
			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				finish(context, obj, dkm, null);
				return true;
			}
			token = tok.nextToken();
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
		}

		List<Prerequisite> prereqs = new ArrayList<Prerequisite>();

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
						"   (Did you put spells after the "
								+ "PRExxx tags in SPELLS:?)");
				return false;
			}
			prereqs.add(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}

		finish(context, obj, dkm, prereqs);
		return true;
	}

	public void finish(
			LoadContext context,
			CDOMObject obj,
			DoubleKeyMap<CDOMReference<CDOMSpell>, AssociationKey<String>, String> dkm,
			List<Prerequisite> prereqs)
	{
		for (CDOMReference<CDOMSpell> spell : dkm.getKeySet())
		{
			AssociatedPrereqObject edge = context.getGraphContext().grant(
					getTokenName(), obj, spell);
			for (AssociationKey<String> ak : dkm.getSecondaryKeySet(spell))
			{
				edge.setAssociation(ak, dkm.get(spell, ak));
			}
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

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
