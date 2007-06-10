/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens.choose;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.choice.CompoundOrChooser;
import pcgen.cdom.choice.ListChooser;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.SpellList;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.util.Logging;

public class SpellsToken implements ChooseLstToken
{

	private static final Class<SpellList> SPELLLIST_CLASS = SpellList.class;

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value);
			return false;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (!tokText.startsWith("CLASS=") && !tokText.startsWith("DOMAIN="))
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " argument must start with CLASS= or DOMAIN= : "
					+ tokText);
				Logging.errorPrint("  Entire Token was: " + value);
				return false;
			}
		}
		StringBuilder sb = new StringBuilder();
		if (prefix.length() > 0)
		{
			sb.append(prefix).append('|');
		}
		sb.append(getTokenName()).append('|').append(value);
		po.setChoiceString(sb.toString());
		return true;
	}

	public String getTokenName()
	{
		return "SPELLS";
	}

	public ChoiceSet<?> parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value);
			return null;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return null;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not start with | : " + value);
			return null;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not end with | : " + value);
			return null;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments uses double separator || : " + value);
			return null;
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		CompoundOrChooser<Spell> chooser = new CompoundOrChooser<Spell>();
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (!tokText.startsWith("CLASS=") && !tokText.startsWith("DOMAIN="))
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " argument must start with CLASS= or DOMAIN= : "
					+ tokText);
				Logging.errorPrint("  Entire Token was: " + value);
				return null;
			}
			String listName = tokText.substring(tokText.indexOf('=') + 1);
			CDOMSimpleSingleRef<SpellList> ref =
					context.ref.getCDOMReference(SPELLLIST_CLASS, listName);
			ListChooser<Spell> listChooser = new ListChooser<Spell>(ref);
			chooser.addChoiceSet(listChooser);
		}
		return chooser;
	}
}
