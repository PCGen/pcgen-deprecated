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
package plugin.lsttokens.template;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.inst.CDOMLanguage;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.cdom.inst.LanguageList;
import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with LANGBONUS Token
 */
public class LangbonusToken extends AbstractToken implements PCTemplateLstToken, CDOMPrimaryToken<CDOMTemplate>
{
	private static final Class<CDOMLanguage> LANGUAGE_CLASS = CDOMLanguage.class;

	private static final Class<LanguageList> LANGUAGELIST_CLASS =
			LanguageList.class;

	@Override
	public String getTokenName()
	{
		return "LANGBONUS";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.setLanguageBonus(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMTemplate template, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);
		boolean foundAny = false;
		boolean foundOther = false;
		boolean firstToken = true;
		CDOMReference<LanguageList> swl =
				context.ref.getCDOMReference(LANGUAGELIST_CLASS, "*Starting");

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokText))
			{
				if (!firstToken)
				{
					Logging.errorPrint("Non-sensical situation was "
						+ "encountered while parsing " + getTokenName()
						+ ": When used, .CLEAR must be the first argument");
					return false;
				}
				context.getListContext().removeAllFromList(getTokenName(),
					template, swl);
			}
			else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				CDOMReference<CDOMLanguage> lang;
				String clearText = tokText.substring(7);
				if (Constants.LST_ALL.equals(clearText))
				{
					lang = context.ref.getCDOMAllReference(LANGUAGE_CLASS);
				}
				else
				{
					lang =
							TokenUtilities.getTypeOrPrimitive(context,
								LANGUAGE_CLASS, clearText);
				}
				if (lang == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
						+ getTokenName() + ": " + value
						+ " had an invalid .CLEAR. reference: " + clearText);
					return false;
				}
				context.getListContext().removeFromList(getTokenName(),
					template, swl, lang);
			}
			else
			{
				/*
				 * Note this is done one-by-one, because .CLEAR. token type
				 * needs to be able to perform the unlink. That could be
				 * changed, but the increase in complexity isn't worth it.
				 * (Changing it to a grouping object that didn't place links in
				 * the graph would also make it harder to trace the source of
				 * class skills, etc.)
				 */
				CDOMReference<CDOMLanguage> lang;
				if (Constants.LST_ALL.equals(tokText))
				{
					foundAny = true;
					lang = context.ref.getCDOMAllReference(LANGUAGE_CLASS);
				}
				else
				{
					foundOther = true;
					lang =
							TokenUtilities.getTypeOrPrimitive(context,
								LANGUAGE_CLASS, tokText);
				}
				if (lang == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
						+ getTokenName() + ": " + value
						+ " had an invalid reference: " + tokText);
					return false;
				}
				context.getListContext().addToList(getTokenName(), template,
					swl, lang);
			}
			firstToken = false;
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMTemplate pct)
	{
		CDOMReference<LanguageList> swl =
				context.ref.getCDOMReference(LANGUAGELIST_CLASS, "*Starting");
		AssociatedChanges<CDOMReference<CDOMLanguage>> changes =
				context.getListContext().getChangesInList(getTokenName(), pct,
					swl);
		if (changes == null)
		{
			// Legal if no Language was present in the race
			return null;
		}
		List<String> list = new ArrayList<String>();
		if (changes.hasRemovedItems())
		{
			if (changes.includesGlobalClear())
			{
				context.addWriteMessage("Non-sensical relationship in "
					+ getTokenName()
					+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			list.add(Constants.LST_DOT_CLEAR_DOT
				+ ReferenceUtilities.joinLstFormat(changes.getRemoved(),
					",.CLEAR."));
		}
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		if (changes.hasAddedItems())
		{
			list.add(ReferenceUtilities.joinLstFormat(changes.getAdded(),
				Constants.COMMA));
		}
		if (list.isEmpty())
		{
			// Zero indicates no add or global clear
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMTemplate> getTokenClass()
	{
		return CDOMTemplate.class;
	}
}
