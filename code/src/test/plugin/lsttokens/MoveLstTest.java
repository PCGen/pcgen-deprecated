package plugin.lsttokens;

import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class MoveLstTest extends AbstractGlobalTokenTestCase
{
	static GlobalLstToken token = new MoveLst();
	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, ""));
	}

	@Test
	public void testInvalidInputOneItem() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "Normal"));
	}

	@Test
	public void testInvalidInputNoValue() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "Normal,"));
	}

	@Test
	public void testInvalidInputOnlyValue() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, ",30"));
	}

	@Test
	public void testInvalidInputTwoComma() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "Normal,,30"));
	}

	@Test
	public void testInvalidInputThreeItems() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"Normal,30,Darkvision"));
	}

	@Test
	public void testInvalidInputNegativeMovement()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "Normal,-30"));
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("Walk,30");
	}

	@Test
	public void testRoundRobinZero() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision,0");
	}

	@Test
	public void testRoundRobinMultiple() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision,0,Walk,30");
	}

}
