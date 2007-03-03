package plugin.lsttokens.skill;

import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.SkillLoader;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.AbstractTokenTestCase;

public class VisibleTokenTest extends AbstractTokenTestCase<Skill>
{

	static VisibleToken token = new VisibleToken();
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
	public void testInvalidOutput()
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryProf.put(ObjectKey.VISIBILITY, Visibility.QUALIFY);
		assertNull(token.unparse(primaryContext, primaryProf));
		assertFalse(primaryContext.getWriteMessageCount() == 0);
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		internalTestInvalidInputString(null);
	}

	@Test
	public void testInvalidInputStringSet() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "EXPORT"));
		assertEquals(Visibility.EXPORT, primaryProf.get(ObjectKey.VISIBILITY));
		internalTestInvalidInputString(Visibility.EXPORT);
	}

	public void internalTestInvalidInputString(Object val)
		throws PersistenceLayerException
	{
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(token.parse(primaryContext, primaryProf, "Always"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(token.parse(primaryContext, primaryProf, "String"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE.TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(token.parse(primaryContext, primaryProf, "ALL"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		//Note case sensitivity
		assertFalse(token.parse(primaryContext, primaryProf, "Display"));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "DISPLAY"));
		assertEquals(Visibility.DISPLAY, primaryProf.get(ObjectKey.VISIBILITY));
		assertTrue(token.parse(primaryContext, primaryProf, "EXPORT"));
		assertEquals(Visibility.EXPORT, primaryProf.get(ObjectKey.VISIBILITY));
		assertTrue(token.parse(primaryContext, primaryProf, "YES"));
		assertEquals(Visibility.YES, primaryProf.get(ObjectKey.VISIBILITY));
	}

	@Test
	public void testRoundRobinYes() throws PersistenceLayerException
	{
		runRoundRobin("YES");
	}

	@Test
	public void testRoundRobinDisplay() throws PersistenceLayerException
	{
		runRoundRobin("DISPLAY");
	}

	@Test
	public void testRoundRobinExport() throws PersistenceLayerException
	{
		runRoundRobin("EXPORT");
	}
}
