package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.CDOMToken;

public class DamageTokenTest extends AbstractDamageTokenTestCase
{

	public static DamageToken token = new DamageToken();

	@Override
	public CDOMToken<Equipment> getToken()
	{
		return token;
	}

}
