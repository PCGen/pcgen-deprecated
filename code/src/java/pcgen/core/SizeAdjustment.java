/*
 * SizeAdjustment.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on December 13, 2001, 4:24 PM
 */
package pcgen.core;

import java.util.List;

import pcgen.core.bonus.BonusObj;
import pcgen.core.utils.CoreUtility;

/**
 * <code>SizeAdjustment</code>.
 * 
 * @author Greg Bingleman <byngl@users.sourceforge.net>
 * @version $Revision$
 */
public final class SizeAdjustment extends PObject 
{
	private String abbreviation = ""; // should be 1-character long
	private boolean isDefaultSize = false;
	private static int ordinalCounter = 0;
	private final int ordinal;

	public SizeAdjustment()
	{
		ordinal = ordinalCounter++;
	}

	/**
	 * Set abbreviation
	 * 
	 * @param ab
	 */
	public void setAbbreviation(final String ab)
	{
		abbreviation = ab;
	}

	/**
	 * Get abbreviation
	 * 
	 * @return abbreviation
	 */
	public String getAbbreviation()
	{
		return abbreviation;
	}

	/**
	 * Activates (checks PrereqToUse) and returns list of BonusObj's
	 * 
	 * @param aPC
	 * @return active bonuses
	 */
	public List<BonusObj> getActiveBonuses(final PlayerCharacter aPC)
	{
		super.activateBonuses(aPC);

		return super.getActiveBonuses(aPC);
	}

	/**
	 * Set is default size to true or false
	 * 
	 * @param arg
	 */
	public void setIsDefaultSize(final boolean arg)
	{
		isDefaultSize = arg;
	}

	public String toString()
	{
		return "pcgen.core.SizeAdjustment{" + "abbreviation='" + abbreviation
			+ "'" + "}";
	}

	double getBonusTo(final PlayerCharacter aPC, final String bonusType,
		final List<String> typeList, double defaultValue)
	{
		for (String type : typeList)
		{
			/*
			 * TODO: The standard for these bonuses should probably be TYPE=,
			 * but the bonus objects only correctly match TYPE. The bonus
			 * objects probably need to be reevaluated to standardize this usage
			 */
			final double a = bonusTo(bonusType, "TYPE." + type, aPC, aPC);

			if (!CoreUtility.doublesEqual(a, 0.0))
			{
				defaultValue = a;

				break;
			}
		}

		return defaultValue;
	}

	boolean isDefaultSize()
	{
		return isDefaultSize;
	}

	public int getOrdinal()
	{
		return ordinal;
	}
}
