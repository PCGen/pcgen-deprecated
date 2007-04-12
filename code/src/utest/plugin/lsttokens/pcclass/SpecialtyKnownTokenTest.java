package plugin.lsttokens.pcclass;

import pcgen.persistence.lst.PCClassLevelLstToken;

public class SpecialtyKnownTokenTest extends AbstractSpellCastingTokenTestCase
{
	static SpecialtyknownToken token = new SpecialtyknownToken();

	@Override
	public PCClassLevelLstToken getToken()
	{
		return token;
	}

}
