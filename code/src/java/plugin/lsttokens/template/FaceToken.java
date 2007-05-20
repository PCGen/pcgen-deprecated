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

import java.math.BigDecimal;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Logging;

/**
 * Class deals with FACE Token
 */
public class FaceToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "FACE";
	}

	public boolean parse(PCTemplate template, String value)
	{
		int commaLoc = value.indexOf(",");
		if (commaLoc > -1)
		{
			double width;
			double height;
			try
			{
				width =
						Double.parseDouble(value.substring(0, commaLoc - 1)
							.trim());
			}
			catch (NumberFormatException nfe)
			{
				width = 5;
			}

			try
			{
				height =
						Double
							.parseDouble(value.substring(commaLoc + 1).trim());
			}
			catch (NumberFormatException ne)
			{
				height = 5;
			}
			template.setFace(width, height);
		}
		else
		{
			double width;
			try
			{
				width = Double.parseDouble(value);
			}
			catch (NumberFormatException nfe)
			{
				width = 5;
			}
			template.setFace(width, 0);
		}
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		return parseFace(context, template, value);
	}

	protected boolean parseFace(LoadContext context, PCTemplate fObj, String value)
	{
		int commaLoc = value.indexOf(Constants.COMMA);
		if (commaLoc != value.lastIndexOf(Constants.COMMA))
		{
			Logging.errorPrint(getTokenName() + " must be of the form: "
				+ getTokenName() + ":<num>[,<num>]");
			return false;
		}
		if (commaLoc > -1)
		{
			if (commaLoc == 0)
			{
				Logging.errorPrint(getTokenName()
					+ " should not start with a comma.  Must be of the form: "
					+ getTokenName() + ":<num>[,<num>]");
				return false;
			}
			if (commaLoc == value.length() - 1)
			{
				Logging.errorPrint(getTokenName()
					+ " should not end with a comma.  Must be of the form: "
					+ getTokenName() + ":<num>[,<num>]");
				return false;
			}
			BigDecimal width;
			BigDecimal height;
			try
			{
				String widthString = value.substring(0, commaLoc).trim();
				width = new BigDecimal(widthString);
				if (width.compareTo(BigDecimal.ZERO) < 0)
				{
					Logging.errorPrint("Cannot have negative width in "
						+ getTokenName() + ": " + value);
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Misunderstood Double Width in Tag: "
					+ value);
				return false;
			}

			try
			{
				String heightString = value.substring(commaLoc + 1).trim();
				height = new BigDecimal(heightString);
				if (height.compareTo(BigDecimal.ZERO) < 0)
				{
					Logging.errorPrint("Cannot have negative height in "
						+ getTokenName() + ": " + value);
					return false;
				}
			}
			catch (NumberFormatException ne)
			{
				Logging.errorPrint("Misunderstood Double Height in Tag: "
					+ value);
				return false;
			}
			context.obj.put(fObj, ObjectKey.FACE_WIDTH, width);
			context.obj.put(fObj, ObjectKey.FACE_HEIGHT, height);
		}
		else
		{
			BigDecimal width;
			try
			{
				String widthString = value;
				width = new BigDecimal(widthString);
				if (width.compareTo(BigDecimal.ZERO) < 0)
				{
					Logging.errorPrint("Cannot have negative width in "
						+ getTokenName() + ": " + value);
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Misunderstood Double in Tag: " + value);
				return false;
			}
			context.obj.put(fObj, ObjectKey.FACE_WIDTH, width);
			context.obj.put(fObj, ObjectKey.FACE_HEIGHT, BigDecimal.ZERO);
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		BigDecimal width = context.obj.getObject(pct, ObjectKey.FACE_WIDTH);
		BigDecimal height = context.obj.getObject(pct, ObjectKey.FACE_HEIGHT);
		if (width == null && height == null)
		{
			return null;
		}
		if (width == null || height == null)
		{
			context.addWriteMessage("Must have both width and height in "
				+ getTokenName() + ": " + width + " " + height);
			return null;
		}
		if (width.compareTo(BigDecimal.ZERO) < 0)
		{
			context.addWriteMessage("Cannot have negative width in "
				+ getTokenName() + ": " + width);
			return null;
		}
		if (height.compareTo(BigDecimal.ZERO) < 0)
		{
			context.addWriteMessage("Cannot have negative height in "
				+ getTokenName() + ": " + height);
			return null;
		}
		StringBuilder sb = new StringBuilder();
		BigDecimal w = BigDecimalHelper.trimBigDecimal(width);
		sb.append(w);
		if (height.compareTo(BigDecimal.ZERO) != 0)
		{
			BigDecimal h = BigDecimalHelper.trimBigDecimal(height);
			sb.append(',').append(h);
		}
		return new String[]{sb.toString()};
	}
}
