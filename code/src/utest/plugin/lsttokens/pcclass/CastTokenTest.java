package plugin.lsttokens.pcclass;

import pcgen.persistence.lst.PCClassLevelLstToken;

public class CastTokenTest extends AbstractSpellCastingTokenTestCase
{
	static CastToken token = new CastToken();

	@Override
	public PCClassLevelLstToken getToken()
	{
		return token;
	}

}
