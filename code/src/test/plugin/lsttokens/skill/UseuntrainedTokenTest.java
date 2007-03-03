package plugin.lsttokens.skill;

import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.SkillLoader;
import plugin.lsttokens.AbstractTokenTestCase;

public class UseuntrainedTokenTest extends AbstractTokenTestCase<Skill>
{

	static UseuntrainedToken token = new UseuntrainedToken();
	static SkillLoader loader = new SkillLoader();

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
	public void testInvalidInputString() throws PersistenceLayerException
	{
		internalTestInvalidInputString(null);
	}

	@Test
	public void testInvalidInputStringSet() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "YES"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.USE_UNTRAINED));
		internalTestInvalidInputString(Boolean.TRUE);
	}

	public void internalTestInvalidInputString(Object val)
		throws PersistenceLayerException
	{
		assertEquals(val, primaryProf.get(ObjectKey.USE_UNTRAINED));
		assertFalse(token.parse(primaryContext, primaryProf, "String"));
		assertEquals(val, primaryProf.get(ObjectKey.USE_UNTRAINED));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.USE_UNTRAINED));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE.TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.USE_UNTRAINED));
		assertFalse(token.parse(primaryContext, primaryProf, "ALL"));
		assertEquals(val, primaryProf.get(ObjectKey.USE_UNTRAINED));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "YES"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.USE_UNTRAINED));
		assertTrue(token.parse(primaryContext, primaryProf, "NO"));
		assertEquals(Boolean.FALSE, primaryProf.get(ObjectKey.USE_UNTRAINED));
		//We're nice enough to be case insensitive here...
		assertTrue(token.parse(primaryContext, primaryProf, "YeS"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.USE_UNTRAINED));
		assertTrue(token.parse(primaryContext, primaryProf, "Yes"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.USE_UNTRAINED));
		assertTrue(token.parse(primaryContext, primaryProf, "No"));
		assertEquals(Boolean.FALSE, primaryProf.get(ObjectKey.USE_UNTRAINED));
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
