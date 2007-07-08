package pcgen.persistence.lst;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.LoadContext;
import pcgen.util.Logging;

public class BonusLoader
{

	public static BonusObj getBonus(LoadContext context, CDOMObject target,
		String bonusName, String bonusInfo, String value)
	{
		int equalLoc = bonusName.indexOf('=');
		String key =
				equalLoc == -1 ? bonusName : bonusName.substring(0,
					equalLoc + 1);
		BonusObj bonus = Bonus.getBonusOfType(key);
		bonus.setBonusName(bonusName);
		bonus.setTypeOfBonus(Bonus.getBonusTypeFromName(key));
		bonus.setValue(value);
		if (equalLoc >= 0)
		{
			bonus.setVariable(bonusName.substring(equalLoc + 1));
		}

		StringTokenizer aTok = new StringTokenizer(bonusInfo, ",");

		while (aTok.hasMoreTokens())
		{
			final String token = aTok.nextToken();
			final boolean result = bonus.parseToken(token);

			if (!result)
			{
				Logging.debugPrint(new StringBuffer().append(
					"Could not parse token ").append(token).append(
					" from bonusInfo ").append(bonusInfo).append(
					" in BonusObj.newBonus.").toString());
			}
		}
		return bonus;
	}

}
