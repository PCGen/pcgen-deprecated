package plugin.lsttokens.template;

import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.AbstractTokenTestCase;

public class RemovableTokenTest extends AbstractTokenTestCase<PCTemplate>
{

	static RemovableToken token = new RemovableToken();
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
	public void testInvalidInputString()
	{
		internalTestInvalidInputString(null);
	}

	@Test
	public void testInvalidInputStringSet()
	{
		assertTrue(token.parse(primaryContext, primaryProf, "YES"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.REMOVABLE));
		internalTestInvalidInputString(Boolean.TRUE);
	}

	public void internalTestInvalidInputString(Object val)
	{
		assertEquals(val, primaryProf.get(ObjectKey.REMOVABLE));
		assertFalse(token.parse(primaryContext, primaryProf, "String"));
		assertEquals(val, primaryProf.get(ObjectKey.REMOVABLE));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.REMOVABLE));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE.TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.REMOVABLE));
		assertFalse(token.parse(primaryContext, primaryProf, "ALL"));
		assertEquals(val, primaryProf.get(ObjectKey.REMOVABLE));
	}

	@Test
	public void testValidInputs()
	{
		assertTrue(token.parse(primaryContext, primaryProf, "YES"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.REMOVABLE));
		assertTrue(token.parse(primaryContext, primaryProf, "NO"));
		assertEquals(Boolean.FALSE, primaryProf.get(ObjectKey.REMOVABLE));
		//We're nice enough to be case insensitive here...
		assertTrue(token.parse(primaryContext, primaryProf, "YeS"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.REMOVABLE));
		assertTrue(token.parse(primaryContext, primaryProf, "Yes"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.REMOVABLE));
		assertTrue(token.parse(primaryContext, primaryProf, "No"));
		assertEquals(Boolean.FALSE, primaryProf.get(ObjectKey.REMOVABLE));
	}

	@Test
	public void testRoundRobinDisplay() throws PersistenceLayerException
	{
		runRoundRobin("YES");
	}

	@Test
	public void testRoundRobinExport() throws PersistenceLayerException
	{
		runRoundRobin("NO");
	}
}
