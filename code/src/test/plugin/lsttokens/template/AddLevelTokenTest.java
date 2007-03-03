package plugin.lsttokens.template;

import org.junit.Test;

import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.AbstractTokenTestCase;

public class AddLevelTokenTest extends AbstractTokenTestCase<PCTemplate>
{
	static AddLevelToken token = new AddLevelToken();
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
	public void testInvalidInputNoPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter:3"));
	}

	@Test
	public void testInvalidInputNoClass() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|3"));
	}

	@Test
	public void testInvalidInputNoLevelCount() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter|"));
	}

	@Test
	public void testInvalidInputEmptyLevelCount()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter| "));
	}

	@Test
	public void testInvalidInputTwoPipes() throws PersistenceLayerException
	{
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, "Fighter|3|3"));
	}

	@Test
	public void testInvalidInputDecimalLevelCount()
		throws PersistenceLayerException
	{
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, "Fighter|3.5"));
	}

	@Test
	public void testInvalidInputNegativeLevelCount()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter|-5"));
	}

	@Test
	public void testInvalidInputZeroLevelCount()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter|0"));
	}

	@Test
	public void testInvalidInputNotAClass() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "NotAClass|3"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("Fighter|3");
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinMultiple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		primaryContext.ref.constructCDOMObject(PCClass.class, "Thief");
		runRoundRobin("Fighter|3", "Thief|4");
		assertTrue(primaryContext.ref.validate());
	}

}