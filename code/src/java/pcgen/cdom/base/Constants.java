/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.base;

public class Constants
{

	public static final String COMMA = ",";

	public static final String PIPE = "|";

	public static final String EQUALS = "=";

	public static final String DOT = ".";

	public static final String COLON = ":";

	public static final String LST_CLEAR = "CLEAR";

	public static final String LST_DOT_CLEAR = ".CLEAR";

	public static final String LST_DOT_CLEARALL = ".CLEARALL";

	public static final String LST_DOT_CLEAR_DOT = ".CLEAR.";

	public static final String LST_ALL = "ALL";

	public static final String LST_ANY = "ANY";

	public static final String LST_ADD = "ADD";

	public static final String LST_NONE = "NONE";

	public static final String LST_REMOVE = "REMOVE";

	public static final String LST_TYPE = "TYPE=";

	public static final String LST_TYPE_OLD = "TYPE.";

	public static final String LST_CLASS_DOT = "CLASS.";

	public static final String LST_CHOOSE = "CHOOSE:";

	public static final String LST_ADDCHOICE = "ADDCHOICE:";

	public static final int HANDS_SIZEDEPENDENT = -1;

	public static final String CATEGORY_START_TOKEN = "CATEGORY=";

	public static final String EMPTY_STRING = "";

	public static final String VT_EQ_HEAD = "EQ_HEAD";

	public static final char CHAR_ASTERISK = '*';

	public static final String PERCENT = "%";

	public static final String TAB = "\t";

	/** The String that separates individual objects */
	public static final String LST_LINE_SEPARATOR = "\r\n"; //$NON-NLS-1$

	/** Tag used to include an object */
	public static final String LST_INCLUDE_TAG = "INCLUDE"; //$NON-NLS-1$

	/** Tag used to exclude an object */
	public static final String LST_EXCLUDE_TAG = "EXCLUDE"; //$NON-NLS-1$

	/** The suffix used to indicate this is a copy operation */
	public static final String LST_COPY_SUFFIX = ".COPY"; //$NON-NLS-1$

	/** The suffix used to indicate this is a mod operation */
	public static final String LST_MOD_SUFFIX = ".MOD"; //$NON-NLS-1$

	/** The suffix used to indicate this is a forget operation */
	public static final String LST_FORGET_SUFFIX = ".FORGET"; //$NON-NLS-1$

	public static final String OPEN_PAREN = "(";

	public static final String CLOSE_PAREN = ")";

	public static final String LST_CLASS_IDENTIFIER = "CLASS:";

	public static final String LST_SUBCLASS_IDENTIFIER = "SUBCLASS:";

	public static final String LST_SUBSTITUTIONCLASS_IDENTIFIER =
			"SUBSTITUTIONCLASS:";

}
