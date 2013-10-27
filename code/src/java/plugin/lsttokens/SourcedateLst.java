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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.SourceLoader;
import pcgen.persistence.lst.SourceLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author zaister
 * 
 */
public class SourcedateLst implements GlobalLstToken, SourceLstToken,
		CDOMPrimaryToken<CDOMObject>
{

	private static final DateFormat DATE_FORMATTER = new SimpleDateFormat(
			"yyyy-MM"); //$NON-NLS-1$

	public String getTokenName()
	{
		return "SOURCEDATE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		try
		{
			obj.getSourceEntry().getSourceBook().setDate(value);
		}
		catch (ParseException e)
		{
			Logging.errorPrint("Error parsing date", e);
			return false;
		}
		return true;
	}

	public boolean parse(Map<String, String> sourceMap, String value)
	{
		sourceMap.putAll(SourceLoader.parseSource("SOURCEDATE:" + value));
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		Date theDate;
		try
		{
			theDate = DATE_FORMATTER.parse(value);
		}
		catch (ParseException pe)
		{
			try
			{
				theDate = DateFormat.getDateInstance().parse(value);
			}
			catch (ParseException e)
			{
				Logging.addParseMessage(Logging.LST_ERROR, "Invalid Date: "
						+ value + " found in " + getTokenName());
				return false;
			}
		}
		context.obj.put(obj, ObjectKey.SOURCE_DATE, theDate);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Date date = context.getObjectContext().getObject(obj,
				ObjectKey.SOURCE_DATE);
		if (date == null)
		{
			return null;
		}
		return new String[] { DATE_FORMATTER.format(date) };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
