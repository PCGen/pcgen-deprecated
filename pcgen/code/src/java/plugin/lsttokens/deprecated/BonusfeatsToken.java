package plugin.lsttokens.deprecated;

import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with BONUSFEATS Token
 */
public class BonusfeatsToken implements PCTemplateLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "BONUSFEATS";
	}

	// number of additional feats to spend
	public boolean parse(PCTemplate template, String value)
	{
		try
		{
			int featCount = Integer.parseInt(value);
			if (featCount <= 0)
			{
				Logging.errorPrint("Invalid integer in " + getTokenName()
					+ ": must be greater than zero");
				return false;
			}
			template.setBonusInitialFeats(featCount);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ ": must be an integer (greater than zero)");
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.DeprecatedToken#getMessage(pcgen.core.PObject, java.lang.String)
	 */
	@Override
	public String getMessage(PObject obj, String value)
	{
		return "Template token BONUSFEATS is not used - the tag will not do anything. Replaced by: BONUS:FEAT|POOL|<value>";
	}
}
