package plugin.lsttokens;

import java.util.List;

import org.junit.Test;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.AbstractTokenTestCase;

public abstract class AbstractTypeSafeListTestCase<T extends PObject> extends
		AbstractTokenTestCase<T>
{

	public abstract Object getConstant(String string);

	public abstract char getJoinCharacter();

	public abstract ListKey<?> getListKey();

	@Test
	public void testValidInputSimple() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(getToken().parse(primaryContext, primaryProf, "Rheinhessen"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Rheinhessen")));
	}

	@Test
	public void testValidInputNonEnglish() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Niederösterreich"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Niederösterreich")));
	}

	@Test
	public void testValidInputSpace() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(getToken()
			.parse(primaryContext, primaryProf, "Finger Lakes"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Finger Lakes")));
	}

	@Test
	public void testValidInputHyphen() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Languedoc-Roussillon"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
	}

	@Test
	public void testValidInputY() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(getToken()
			.parse(primaryContext, primaryProf, "Yarra Valley"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Yarra Valley")));
	}

	@Test
	public void testValidInputList() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Niederösterreich" + getJoinCharacter() + "Finger Lakes"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(2, coll.size());
		assertTrue(coll.contains(getConstant("Niederösterreich")));
		assertTrue(coll.contains(getConstant("Finger Lakes")));
	}

	@Test
	public void testValidInputMultList() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Niederösterreich" + getJoinCharacter() + "Finger Lakes"));
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Languedoc-Roussillon" + getJoinCharacter() + "Rheinhessen"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(4, coll.size());
		assertTrue(coll.contains(getConstant("Niederösterreich")));
		assertTrue(coll.contains(getConstant("Finger Lakes")));
		assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
		assertTrue(coll.contains(getConstant("Rheinhessen")));
	}

	//FIXME Someday, when PCGen doesn't write out crappy stuff into custom items
	//	@Test
	//	public void testInvalidListEmpty() throws PersistenceLayerException
	//	{
	//		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
	//		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
	//	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TestWP1" + getJoinCharacter()));
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getJoinCharacter() + "TestWP1"));
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP2");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TestWP2" + getJoinCharacter() + getJoinCharacter() + "TestWP1"));
	}

	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		runRoundRobin("Rheinhessen");
	}

	@Test
	public void testRoundRobinWithSpace() throws PersistenceLayerException
	{
		runRoundRobin("Finger Lakes");
	}

	@Test
	public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
	{
		runRoundRobin("Niederösterreich");
	}

	@Test
	public void testRoundRobinHyphen() throws PersistenceLayerException
	{
		runRoundRobin("Languedoc-Roussillon");
	}

	@Test
	public void testRoundRobinY() throws PersistenceLayerException
	{
		runRoundRobin("Yarra Valley");
	}

	@Test
	public void testRoundRobinThree() throws PersistenceLayerException
	{
		runRoundRobin("Rheinhessen" + getJoinCharacter() + "Yarra Valley"
			+ getJoinCharacter() + "Languedoc-Roussillon");
	}

	public static String[] getConstants()
	{
		return new String[]{"Niederösterreich", "Finger Lakes",
			"Languedoc-Roussillon", "Rheinhessen", "Yarra Valley"};
	}

}
