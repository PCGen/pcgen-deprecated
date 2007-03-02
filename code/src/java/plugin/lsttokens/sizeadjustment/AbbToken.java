package plugin.lsttokens.sizeadjustment;

import pcgen.cdom.mode.Size;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.lst.SizeAdjustmentLstToken;

/**
 * Class deals with ABB Token for size adjustment
 */
public class AbbToken implements SizeAdjustmentLstToken
{

	/**
	 * Get the token name
	 * @return token name
	 */
	public String getTokenName()
	{
		return "ABB";
	}

	private static int count = 1;
	
	/**
	 * Parse the abbreviation token
	 * @param sa 
	 * @param value true
	 * @return true
	 */
	public boolean parse(SizeAdjustment sa, String value)
	{
		Size.constructConstant(value, count++);
		sa.setAbbreviation(value);
		return true;
	}
}
