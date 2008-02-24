package pcgen.tokenruntimeload;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.lsttokens.weaponprof.HandsToken;

public class WeaponProfTest extends AbstractIntegrationTestCase<CDOMWeaponProf>
{

	static HandsToken token = new HandsToken();
	static CDOMTokenLoader<CDOMWeaponProf> loader = new CDOMTokenLoader<CDOMWeaponProf>(
			CDOMWeaponProf.class);

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
			URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(token);
	}

	@Override
	public Class<CDOMWeaponProf> getCDOMClass()
	{
		return CDOMWeaponProf.class;
	}

	@Override
	public CDOMLoader<CDOMWeaponProf> getLoader()
	{
		return loader;
	}

	@Test
	public void testImportBasicWeaponProf() throws PersistenceLayerException
	{
		verifyClean();
		primaryProf = new CDOMWeaponProf();
		primaryProf.setName("TestWP1");
		loader.parseLine(primaryContext, primaryProf, "HANDS:2", testCampaign.getURI());
		secondaryProf.put(IntegerKey.HANDS, Integer.valueOf(2));
		secondaryProf.put(ObjectKey.SOURCE_URI, testCampaign.getURI());
		verifyClean();
	}
}
