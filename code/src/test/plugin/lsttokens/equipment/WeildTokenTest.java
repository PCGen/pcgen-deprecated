package plugin.lsttokens.equipment;

import org.junit.Test;

import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractTokenTestCase;

public class WeildTokenTest extends AbstractTokenTestCase<Equipment>
{
	static WieldToken token = new WieldToken();
	static EquipmentLoader loader = new EquipmentLoader();

	@Override
	public Class<Equipment> getCDOMClass()
	{
		return Equipment.class;
	}

	@Override
	public LstObjectFileLoader<Equipment> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Equipment> getToken()
	{
		return token;
	}

	@Test
	public void testBadInput() throws PersistenceLayerException
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
	public void testRoundRobinLight() throws PersistenceLayerException
	{
		runRoundRobin("Light");
	}

	@Test
	public void testRoundRobinOneHanded() throws PersistenceLayerException
	{
		runRoundRobin("OneHanded");
	}

	@Test
	public void testRoundRobinTwoHanded() throws PersistenceLayerException
	{
		runRoundRobin("TwoHanded");
	}
}
