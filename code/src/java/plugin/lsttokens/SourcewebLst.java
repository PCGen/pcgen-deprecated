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

import java.net.URI;
import java.net.URISyntaxException;
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
 * @author djones4
 * 
 */
public class SourcewebLst implements GlobalLstToken, SourceLstToken,
		CDOMPrimaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "SOURCEWEB";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		obj.getSourceEntry().getSourceBook().setWebsite(value);
		return true;
	}

	public boolean parse(Map<String, String> sourceMap, String value)
	{
		sourceMap.putAll(SourceLoader.parseSource("SOURCEWEB:" + value));
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		try
		{
			context.obj.put(obj, ObjectKey.SOURCE_WEB, new URI(value));
			return true;
		}
		catch (URISyntaxException e)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Invalid Web site value: " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		URI uri = context.getObjectContext().getObject(obj,
				ObjectKey.SOURCE_WEB);
		if (uri == null)
		{
			return null;
		}
		return new String[] { uri.toString() };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
