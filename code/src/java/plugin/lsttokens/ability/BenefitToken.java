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
package plugin.lsttokens.ability;

import java.util.StringTokenizer;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Ability;
import pcgen.core.Constants;
import pcgen.core.Description;
import pcgen.io.EntityEncoder;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbilityLstToken;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * This class deals with the BENEFIT Token
 */
public class BenefitToken extends AbstractToken implements AbilityLstToken
{

	@Override
	public String getTokenName()
	{
		return "BENEFIT";
	}

	public boolean parse(Ability ability, String value)
	{
		if (value.startsWith(".CLEAR")) //$NON-NLS-1$
		{
			if (value.equals(".CLEAR")) //$NON-NLS-1$
			{
				ability.removeAllBenefits();
			}
			else
			{
				ability.removeBenefit(value.substring(7));
			}
			return true;
		}
		ability.addBenefit(parseBenefit(value));
		return true;
	}

	public boolean parse(LoadContext context, Ability ability, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		/*
		 * TODO This was changed in 5.x to be a DESC, not just a String - ugh!
		 */
		context.getObjectContext().put(ability, StringKey.BENEFIT, value);
		return true;
	}

	public String[] unparse(LoadContext context, Ability ability)
	{
		String benefit =
				context.getObjectContext()
					.getString(ability, StringKey.BENEFIT);
		if (benefit == null)
		{
			return null;
		}
		return new String[]{benefit};
	}

	/**
	 * Parses the BENEFIT tag into a Description object.
	 * 
	 * @param aDesc
	 *            The LST tag
	 * @return A <tt>Description</tt> object
	 */
	public Description parseBenefit(final String aDesc)
	{
		final StringTokenizer tok = new StringTokenizer(aDesc, Constants.PIPE);

		final Description desc =
				new Description(EntityEncoder.decode(tok.nextToken()));

		boolean isPre = false;
		while (tok.hasMoreTokens())
		{
			final String token = tok.nextToken();
			if (PreParserFactory.isPreReqString(token)) //$NON-NLS-1$
			{
				desc.addPrerequisite(getPrerequisite(token));
				isPre = true;
			}
			else
			{
				if (isPre)
				{
					Logging.errorPrint("Invalid " + getTokenName() + ": "
						+ aDesc);
					Logging
						.errorPrint("  PRExxx must be at the END of the Token");
					isPre = false;
				}
				desc.addVariable(token);
			}
		}

		return desc;
	}
}
