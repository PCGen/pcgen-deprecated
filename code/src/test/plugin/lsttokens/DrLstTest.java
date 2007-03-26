package plugin.lsttokens;

import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class DrLstTest extends AbstractGlobalTokenTestCase
{
	static GlobalLstToken token = new DrLst();
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
	public void testInvalidNoSlash() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "+1"));
	}

	@Test
	public void testInvalidNoReduction() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/"));
	}

	@Test
	public void testInvalidNoKey() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "/+3"));
	}

	@Test
	public void testInvalidTwoSlash() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/3/+3"));
	}

	@Test
	public void testInvalidNoOrSpace() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/+3 or"));
	}

	@Test
	public void testInvalidNoOrSuffix() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/+3 or "));
	}

	@Test
	public void testInvalidNoAndSpace() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/+3 and"));
	}

	@Test
	public void testInvalidNoAndSuffix() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/+3 and "));
	}

	@Test
	public void testInvalidNoSpaceOr() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/or +3"));
	}

	@Test
	public void testInvalidNoOrPrefix() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/ or +3"));
	}

	@Test
	public void testInvalidNoSpaceAnd() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/and +3"));
	}

	@Test
	public void testInvalidNoAndPrefix() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "10/ and +3"));
	}

	@Test
	public void testRoundRobinNormal() throws PersistenceLayerException
	{
		runRoundRobin("10/+1");
	}

	@Test
	public void testRoundRobinComplexOr() throws PersistenceLayerException
	{
		runRoundRobin("10/+1 or +2");
	}

	@Test
	public void testRoundRobinMultiple() throws PersistenceLayerException
	{
		runRoundRobin("10/+1", "5/+2");
	}
}
