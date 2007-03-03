package plugin.lsttokens.template;

import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.AbstractTokenTestCase;

public class BonusFeatsTokenTest extends AbstractTokenTestCase<PCTemplate>
{

	static BonusfeatsToken token = new BonusfeatsToken();
	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<PCTemplate> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInput() throws PersistenceLayerException
	{
		//Always ensure get is unchanged
		// since no invalid item should set or reset the value
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "TestWP"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "String"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TYPE=TestType"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TYPE.TestType"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "ALL"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "ANY"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "FIVE"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "4.5"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "1/2"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "1+3"));
		assertEquals(primaryGraph, secondaryGraph);
		//Require Integer greater than zero
		assertFalse(getToken().parse(primaryContext, primaryProf, "-1"));
		assertEquals(primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf, "0"));
		assertEquals(primaryGraph, secondaryGraph);
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "5"));

		assertTrue(getToken().parse(primaryContext, primaryProf, "1"));

	}

	@Test
	public void testRoundRobinFive() throws PersistenceLayerException
	{
		runRoundRobin("5");
	}

}
