package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with BONUSFEATS Token
 */
public class BonusfeatsToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "BONUSFEATS";
	}

	// number of additional feats to spend
	public boolean parse(PCTemplate template, String value)
	{
		Logging.errorPrint("Template token BONUSFEATS is not used - you should use a BONUS: token.  This may (or may not) be fixed in a later revision of PCGen");
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
}
