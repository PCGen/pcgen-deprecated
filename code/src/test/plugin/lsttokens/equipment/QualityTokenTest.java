package plugin.lsttokens.equipment;

import org.junit.Test;

import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractTokenTestCase;

public class QualityTokenTest extends AbstractTokenTestCase<Equipment>
{
	static QualityToken token = new QualityToken();
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
	public void testInvalidNoPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "NoPipe"));
	}

	@Test
	public void testInvalidTwoPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"One|Two|Three"));
	}

	@Test
	public void testInvalidDoublePipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Two||Pipe"));
	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
	}

	@Test
	public void testInvalidOnlyPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|"));
	}

	@Test
	public void testInvalidEmptyKey() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|Value"));
	}

	@Test
	public void testInvalidEmptyValue() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Key|"));
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("QualityName|QualityValue");
	}

	@Test
	public void testRoundRobinSpaces() throws PersistenceLayerException
	{
		runRoundRobin("Quality Name|Quality Value");
	}

	@Test
	public void testRoundRobinInternational() throws PersistenceLayerException
	{
		runRoundRobin("Niederösterreich Quality|Niederösterreich");
	}

	@Test
	public void testRoundRobinHyphen() throws PersistenceLayerException
	{
		runRoundRobin("Languedoc-Roussillon Quality|Languedoc-Roussillon");
	}

}
