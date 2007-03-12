package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.CDOMToken;

public class AltDamageTokenTest extends AbstractDamageTokenTestCase
{

	public static AltdamageToken token = new AltdamageToken();

	@Override
	public CDOMToken<Equipment> getToken()
	{
		return token;
	}

}
