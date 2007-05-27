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

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "VISIBLE";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.PCTemplateLstToken#parse(pcgen.core.PCTemplate,
	 *      java.lang.String)
	 */
	public boolean parse(PCTemplate template, String value)
	{
		if (value.startsWith("DISPLAY"))
		{
			// 514 abbreviation cleanup
//			if (!value.equals("DISPLAY"))
//			{
//				Logging.errorPrint(getErrorMsgPrefix(template, value)
//					+ "DISPLAY (exact String, upper case)");
//			}
			template.setVisibility(Visibility.DISPLAY);
		}
		else if (value.startsWith("EXPORT"))
		{
			// 514 abbreviation cleanup
//			if (!value.equals("EXPORT"))
//			{
//				Logging.errorPrint(getErrorMsgPrefix(template, value)
//					+ "EXPORT (exact String, upper case)");
//			}
			template.setVisibility(Visibility.EXPORT);
		}
		else if (value.startsWith("NO"))
		{
			// 514 abbreviation cleanup
//			if (!value.equals("NO"))
//			{
//				Logging.errorPrint(getErrorMsgPrefix(template, value)
//					+ "NO (exact String, upper case)");
//			}
			template.setVisibility(Visibility.NO);
		}
		else
		{
			// 514 abbreviation cleanup
//			if (!value.equals("ALWAYS") && !value.equals("YES"))
//			{
//				Logging.errorPrint(getErrorMsgPrefix(template, value)
//					+ "DISPLAY, EXPORT, NO, YES or ALWAYS "
//					+ "(exact String, upper case)");
//			}
			template.setVisibility(Visibility.YES);
		}
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		Visibility vis;
		if (value.equals("DISPLAY"))
		{
			vis = Visibility.DISPLAY;
		}
		else if (value.equals("EXPORT"))
		{
			vis = Visibility.EXPORT;
		}
		else if (value.equals("NO"))
		{
			vis = Visibility.NO;
		}
		else if (value.equals("YES"))
		{
			vis = Visibility.YES;
		}
		else
		{
			Logging.errorPrint("Can't understand Visibility: " + value);
			return false;
		}
		context.obj.put(template, ObjectKey.VISIBILITY, vis);
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate template)
	{
		Visibility vis = context.obj.getObject(template, ObjectKey.VISIBILITY);
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
		return new String[]{visString};
	}

	/**
	 * Produce the standard start of an error message for an invalid visible
	 * tag.
	 * 
	 * @param template
	 *            The template the tag is for.
	 * @param value
	 *            The value of the visible tag.
	 * @return The error message prefix
	 */
//	private String getErrorMsgPrefix(PCTemplate template, String value)
//	{
//		StringBuffer buff = new StringBuffer();
//		buff.append("In template ");
//		buff.append(template.getDisplayName());
//		buff.append(", token ");
//		buff.append(getTokenName());
//		buff.append(", use of '");
//		buff.append(value);
//		buff.append("' is not valid, please use ");
//		return buff.toString();
//	}
}
