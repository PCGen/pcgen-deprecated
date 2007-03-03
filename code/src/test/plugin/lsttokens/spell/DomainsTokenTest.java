package plugin.lsttokens.spell;

import org.junit.Test;

import pcgen.core.Domain;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.SpellLoader;
import plugin.lsttokens.AbstractTokenTestCase;

public class DomainsTokenTest extends AbstractTokenTestCase<Spell>
{

	static DomainsToken token = new DomainsToken();
	static SpellLoader loader = new SpellLoader();

	@Override
	public Class<Spell> getCDOMClass()
	{
		return Spell.class;
	}

	@Override
	public LstObjectFileLoader<Spell> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Spell> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputClassOnly() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fire"));
	}

	@Test
	public void testInvalidInputLevelOnly() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3"));
	}

	@Test
	public void testInvalidInputChainClassOnly()
		throws PersistenceLayerException
	{
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, "Fire=3|Good"));
	}

	@Test
	public void testInvalidInputDoubleEquals() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fire==4"));
	}

	@Test
	public void testInvalidInputBadLevel() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fire=Good"));
	}

	@Test
	public void testInvalidInputNegativeLevel()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fire=-4"));
	}

	@Test
	public void testInvalidInputLeadingBar() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|Fire=4"));
	}

	@Test
	public void testInvalidInputTrailingBar() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fire=4|"));
	}

	@Test
	public void testInvalidInputDoublePipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Fire=3||Good=4"));
	}

	@Test
	public void testInvalidInputDoubleComma() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Fire,,Good=4"));
	}

	@Test
	public void testInvalidInputLeadingComma() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ",Fire=4"));
	}

	@Test
	public void testInvalidInputTrailingEquals()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fire=4="));
	}

	@Test
	public void testInvalidInputDoubleSet() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fire=4=3"));
	}

	@Test
	public void testInvalidInputTrailingComma()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fire,=4"));
	}

	@Test
	public void testInvalidInputEmptyType() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "TYPE.=4"));
	}

	@Test
	public void testInvalidInputEmptyPrerequisite()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fire=4[]"));
	}

	@Test
	public void testInvalidInputNotClass() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "Fire=4"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputNotClassCompound()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Domain.class, "Fire");
		assertTrue(getToken().parse(primaryContext, primaryProf, "Fire,Good=4"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		assertTrue(primaryContext.getWriteMessageCount() == 0);
		primaryContext.ref.constructCDOMObject(Domain.class, "Fire");
		runRoundRobin("Fire=4");
		assertTrue(primaryContext.ref.validate());
		assertEquals(0, primaryContext.getWriteMessageCount());
	}

	@Test
	public void testRoundRobinComma() throws PersistenceLayerException
	{
		assertTrue(primaryContext.getWriteMessageCount() == 0);
		primaryContext.ref.constructCDOMObject(Domain.class, "Fire");
		primaryContext.ref.constructCDOMObject(Domain.class, "Good");
		runRoundRobin("Fire,Good=4");
		assertTrue(primaryContext.ref.validate());
		assertEquals(0, primaryContext.getWriteMessageCount());
	}

	@Test
	public void testRoundRobinPipe() throws PersistenceLayerException
	{
		assertTrue(primaryContext.getWriteMessageCount() == 0);
		primaryContext.ref.constructCDOMObject(Domain.class, "Fire");
		primaryContext.ref.constructCDOMObject(Domain.class, "Good");
		runRoundRobin("Fire=3|Good=4");
		assertTrue(primaryContext.ref.validate());
		assertEquals(0, primaryContext.getWriteMessageCount());
	}

	@Test
	public void testRoundRobinCommaPipe() throws PersistenceLayerException
	{
		assertTrue(primaryContext.getWriteMessageCount() == 0);
		primaryContext.ref.constructCDOMObject(Domain.class, "Fire");
		primaryContext.ref.constructCDOMObject(Domain.class, "Good");
		primaryContext.ref.constructCDOMObject(Domain.class, "Sun");
		runRoundRobin("Fire,Good=3|Sun=4");
		assertTrue(primaryContext.ref.validate());
		assertEquals(0, primaryContext.getWriteMessageCount());
	}
}
