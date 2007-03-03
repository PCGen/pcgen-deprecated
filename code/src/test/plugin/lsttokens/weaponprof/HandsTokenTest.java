package plugin.lsttokens.weaponprof;

import org.junit.Test;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.WeaponProfLoader;
import plugin.lsttokens.AbstractIntegerTokenTestCase;

public class HandsTokenTest extends AbstractIntegerTokenTestCase<WeaponProf>
{

	static HandsToken token = new HandsToken();
	static WeaponProfLoader loader = new WeaponProfLoader();

	@Override
	public Class<WeaponProf> getCDOMClass()
	{
		return WeaponProf.class;
	}

	@Override
	public LstObjectFileLoader<WeaponProf> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<WeaponProf> getToken()
	{
		return token;
	}

	@Override
	public IntegerKey getIntegerKey()
	{
		return IntegerKey.HANDS;
	}

	@Override
	public boolean isNegativeAllowed()
	{
		return false;
	}

	@Override
	public boolean isZeroAllowed()
	{
		return true;
	}

	@Test
	public void testValidSpecialCase()
	{
		assertTrue(token.parse(primaryContext, primaryProf,
			"1IFLARGERTHANWEAPON"));
		assertEquals(Integer.valueOf(-1), primaryProf.get(IntegerKey.HANDS));
	}

	@Test
	public void testRoundRobinSpecialCase() throws PersistenceLayerException
	{
		runRoundRobin("1IFLARGERTHANWEAPON");
	}

	@Override
	public boolean isPositiveAllowed()
	{
		return true;
	}
}
