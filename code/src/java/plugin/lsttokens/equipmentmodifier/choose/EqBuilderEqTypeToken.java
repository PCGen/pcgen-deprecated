/*
 * EqBuilderEqTypeToken.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on 12/01/2008
 *
 * $Id$
 */
package plugin.lsttokens.equipmentmodifier.choose;

import java.util.StringTokenizer;

import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.core.Constants;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.EqModChooseCompatibilityToken;
import pcgen.persistence.lst.EqModChooseLstToken;
import pcgen.util.Logging;

/**
 * <code>EqBuilderEqTypeToken</code> parses the EQ Builder specific choose
 * string to allow the selection of equipent types.
 * 
 * Last Editor: $Author$ Last Edited: $Date: 2008-01-14 00:09:14 -0500
 * (Mon, 14 Jan 2008) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class EqBuilderEqTypeToken extends AbstractToken implements
		EqModChooseLstToken, EqModChooseCompatibilityToken
{

	public boolean parse(EquipmentModifier po, String prefix, String value)
	{
		if (value == null)
		{
			po.setChoiceString(getTokenName());
			return true;
		}
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
		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			Logging
					.errorPrint("CHOOSE:" + getTokenName()
							+ " must have two or more | delimited arguments : "
							+ value);
			return false;
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		if (tok.countTokens() != 2)
		{
			Logging.errorPrint("COUNT:" + getTokenName()
					+ " requires two arguments: " + value);
			return false;
		}
		// New format: CHOOSE:EQBUILDER.EQTYPE|COUNT=ALL|TITLE=desired TYPE(s)
		String first = tok.nextToken();
		if (!first.startsWith("COUNT="))
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " must have COUNT= as its first argument : " + value);
			return false;
		}
		String second = tok.nextToken();
		if (!second.startsWith("TITLE="))
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " must have TITLE= as its second argument : " + value);
			return false;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(first).append('|').append(second.substring(6));
		sb.append("|TYPE=EQTYPES");
		// Old format: CHOOSE:COUNT=ALL|desired TYPE(s)|TYPE=EQTYPES
		po.setChoiceString(sb.toString());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "EQBUILDER.EQTYPE";
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

	public PrimitiveChoiceSet<?>[] parse(LoadContext context,
			EquipmentModifier mod, String value)
			throws PersistenceLayerException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
