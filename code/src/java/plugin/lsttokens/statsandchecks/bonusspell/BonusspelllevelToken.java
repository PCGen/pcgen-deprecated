package plugin.lsttokens.statsandchecks.bonusspell;

import java.util.Map;

import pcgen.persistence.lst.BonusSpellLoader;
import pcgen.persistence.lst.BonusSpellLstToken;

/**
 * Class deals with BONUSSPELLLEVEL Token
 */
public class BonusspelllevelToken implements BonusSpellLstToken
{

	public String getTokenName()
	{
		return "BONUSSPELLLEVEL";
	}

	public boolean parse(Map<String, String> bonus, String value)
	{
		bonus.put(BonusSpellLoader.LEVEL, value);
		return true;
	}
	
	//This has no equivalent in CDOM - handled in the loader...
}
