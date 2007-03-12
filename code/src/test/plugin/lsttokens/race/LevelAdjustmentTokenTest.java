package plugin.lsttokens.race;

import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.Race;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.RaceLoader;
import plugin.lsttokens.AbstractFormulaTokenTestCase;

public class LevelAdjustmentTokenTest extends
		AbstractFormulaTokenTestCase<Race>
{

	static LeveladjustmentToken token = new LeveladjustmentToken();
	static RaceLoader loader = new RaceLoader();

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public LstObjectFileLoader<Race> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Race> getToken()
	{
		return token;
	}

	@Override
	public FormulaKey getFormulaKey()
	{
		return FormulaKey.LEVEL_ADJUSTMENT;
	}
}
