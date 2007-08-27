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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.core.Language;
import pcgen.core.PObject;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class LangautoLst extends AbstractToken implements GlobalLstToken
{

	private static final Class<Language> LANGUAGE_CLASS = Language.class;

	@Override
	public String getTokenName()
	{
		return "LANGAUTO";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		final StringTokenizer tok = new StringTokenizer(value, ",");

		while (tok.hasMoreTokens())
		{
			obj.addLanguageAuto(tok.nextToken());
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}

		boolean firstToken = true;
		boolean foundAny = false;
		boolean foundOther = false;

		final StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);

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
				context.getGraphContext().removeAll(getTokenName(), obj);
			}
			else
			{
				CDOMReference<Language> ref;
				if (Constants.LST_ALL.equals(tokText))
				{
					foundAny = true;
					ref = context.ref.getCDOMAllReference(LANGUAGE_CLASS);
				}
				else
				{
					foundOther = true;
					ref =
							TokenUtilities.getTypeOrPrimitive(context,
								LANGUAGE_CLASS, tokText);
				}
				if (ref == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
						+ getTokenName());
					return false;
				}
				context.getGraphContext().grant(getTokenName(), obj, ref);
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

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		AssociatedChanges<Language> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					obj, LANGUAGE_CLASS);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		StringBuilder sb = new StringBuilder();
		boolean needComma = false;
		if (changes.includesGlobalClear())
		{
			sb.append(Constants.LST_DOT_CLEAR);
			needComma = true;
		}
		else if (added.isEmpty())
		{
			// Zero indicates no Token (and no global clear, so nothing to do)
			return null;
		}
		for (LSTWriteable lw : added)
		{
			if (needComma)
			{
				sb.append(Constants.COMMA);
			}
			needComma = true;
			sb.append(lw.getLSTformat());
		}
		return new String[]{sb.toString()};
	}
}
