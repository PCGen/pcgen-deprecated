package plugin.lsttokens.pcclass;

import pcgen.persistence.lst.PCClassLevelLstToken;

public class KnownTokenTest extends AbstractSpellCastingTokenTestCase
{
	static KnownToken token = new KnownToken();

	@Override
	public PCClassLevelLstToken getToken()
	{
		return token;
	}

}
