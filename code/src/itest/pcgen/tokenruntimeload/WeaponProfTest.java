package pcgen.tokenruntimeload;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.WeaponProfLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.lsttokens.weaponprof.HandsToken;

public class WeaponProfTest extends AbstractIntegrationTestCase<WeaponProf>
{

	static HandsToken token = new HandsToken();
	static WeaponProfLoader loader = new WeaponProfLoader();

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
		URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(token);
	}

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

	@Test
	public void testImportBasicWeaponProf() throws PersistenceLayerException
	{
		verifyClean();
		primaryProf =
				loader.parseFullLine(primaryContext, 1, "TestWP1\tHANDS:2",
					testCampaign);
		secondaryProf.put(IntegerKey.HANDS, Integer.valueOf(2));
		secondaryProf.put(ObjectKey.SOURCE_URI, testCampaign.getURI());
		verifyClean();
	}
}
