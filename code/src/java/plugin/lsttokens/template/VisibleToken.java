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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(PCTemplate template, String value)
	{
		if (value.startsWith("DISPLAY"))
		{
			if (!value.equals("DISPLAY"))
			{
				Logging.errorPrint("In " + getTokenName() + " Use of '" + value
					+ "' is not valid, please use DISPLAY "
					+ "(exact String, upper case)");
			}
			template.setVisibility(Visibility.DISPLAY);
		}
		else if (value.startsWith("EXPORT"))
		{
			if (!value.equals("EXPORT"))
			{
				Logging.errorPrint("In " + getTokenName() + " Use of '" + value
					+ "' is not valid, please use EXPORT "
					+ "(exact String, upper case)");
			}
			template.setVisibility(Visibility.EXPORT);
		}
		else if (value.startsWith("NO"))
		{
			if (!value.equals("NO"))
			{
				Logging.errorPrint("In " + getTokenName() + " Use of '" + value
					+ "' is not valid, please use NO "
					+ "(exact String, upper case)");
			}
			template.setVisibility(Visibility.NO);
		}
		else
		{
			if (!value.equals("ALWAYS") && !value.equals("YES"))
			{
				Logging.errorPrint("In " + getTokenName() + " Use of '" + value
					+ "' is not valid, please use YES or ALWAYS "
					+ "(exact String, upper case)");
			}
			template.setVisibility(Visibility.YES);
		}
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		if (value.equals("DISPLAY"))
		{
			template.put(ObjectKey.VISIBILITY, Visibility.DISPLAY);
		}
		else if (value.equals("EXPORT"))
		{
			template.put(ObjectKey.VISIBILITY, Visibility.EXPORT);
		}
		else if (value.equals("NO"))
		{
			template.put(ObjectKey.VISIBILITY, Visibility.NO);
		}
		else if (value.equals("YES"))
		{
			template.put(ObjectKey.VISIBILITY, Visibility.YES);
		}
		else
		{
			Logging.errorPrint("Can't understand Visibility: " + value);
			return false;
		}
		return true;
	}

	public String unparse(LoadContext context, PCTemplate template)
	{
		Visibility vis = template.get(ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		String visString;
		if (vis.equals(Visibility.YES))
		{
			visString = "YES";
		}
		else if (vis.equals(Visibility.DISPLAY))
		{
			visString = "DISPLAY";
		}
		else if (vis.equals(Visibility.EXPORT))
		{
			visString = "EXPORT";
		}
		else if (vis.equals(Visibility.NO))
		{
			visString = "NO";
		}
		else
		{
			context.addWriteMessage("Visibility " + vis
				+ " is not a valid Visibility for a PCTemplate");
			return null;
		}
		return new StringBuilder().append(getTokenName()).append(':').append(
			visString).toString();
	}
}
