package plugin.lsttokens.skill;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PCStat;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.SkillLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class KeystatTokenTest extends AbstractTokenTestCase<Skill>
{

	static KeystatToken token = new KeystatToken();
	static SkillLoader loader = new SkillLoader();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		primaryContext.ref.constructCDOMObject(PCStat.class, "STR");
		secondaryContext.ref.constructCDOMObject(PCStat.class, "STR");
		primaryContext.ref.constructCDOMObject(PCStat.class, "INT");
		secondaryContext.ref.constructCDOMObject(PCStat.class, "INT");
	}

	@Override
	public Class<Skill> getCDOMClass()
	{
		return Skill.class;
	}

	@Override
	public LstObjectFileLoader<Skill> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Skill> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidNotAStat() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "NAN"));
	}

	@Test
	public void testInvalidMultipleStatComma() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "STR,INT"));
	}

	@Test
	public void testInvalidMultipleStatBar() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "STR|INT"));
	}

	@Test
	public void testInvalidMultipleStatDot() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "STR.INT"));
	}

	@Test
	public void testRoundRobinDisplay() throws PersistenceLayerException
	{
		runRoundRobin("STR");
	}

}
