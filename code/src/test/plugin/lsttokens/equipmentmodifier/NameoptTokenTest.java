package plugin.lsttokens.equipmentmodifier;

import org.junit.Test;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentModifierLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractTokenTestCase;

public class NameoptTokenTest extends AbstractTokenTestCase<EquipmentModifier>
{
	static NameoptToken token = new NameoptToken();
	static EquipmentModifierLoader loader = new EquipmentModifierLoader();

	@Override
	public Class<EquipmentModifier> getCDOMClass()
	{
		return EquipmentModifier.class;
	}

	@Override
	public LstObjectFileLoader<EquipmentModifier> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<EquipmentModifier> getToken()
	{
		return token;
	}

	@Test
	public void testBadInputNegative() throws PersistenceLayerException
	{
		try
		{
			boolean parse =
					getToken().parse(primaryContext, primaryProf, "INVALID");
			assertFalse(parse);
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	@Test
	public void testBadInputEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
	}

	@Test
	public void testBadInputPlainText() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "TEXT"));
	}

	@Test
	public void testBadInputEmptyText() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "TEXT="));
	}

	@Test
	public void testRoundRobinNormal() throws PersistenceLayerException
	{
		runRoundRobin("NORMAL");
	}

	@Test
	public void testRoundRobinNoList() throws PersistenceLayerException
	{
		runRoundRobin("NOLIST");
	}

	@Test
	public void testRoundRobinNoName() throws PersistenceLayerException
	{
		runRoundRobin("NONAME");
	}

	@Test
	public void testRoundRobinNothing() throws PersistenceLayerException
	{
		runRoundRobin("NOTHING");
	}

	@Test
	public void testRoundRobinSpell() throws PersistenceLayerException
	{
		runRoundRobin("SPELL");
	}

	@Test
	public void testRoundRobinText() throws PersistenceLayerException
	{
		runRoundRobin("TEXT=This is the text");
	}

}
