package plugin.lsttokens.template;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.AbstractTokenTestCase;
import plugin.lsttokens.TokenRegistration;
import plugin.pretokens.parser.PreHDParser;

public class HDTokenTest extends AbstractTokenTestCase<PCTemplate>
{

	static HdToken token = new HdToken();
	static PCTemplateLoader loader = new PCTemplateLoader();

	private static boolean classSetUpFired = false;

	@BeforeClass
	public static final void ltClassSetUp() throws PersistenceLayerException
	{
		TokenRegistration.register(new PreHDParser());
		classSetUpFired = true;
	}

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
		URISyntaxException
	{
		super.setUp();
		if (!classSetUpFired)
		{
			ltClassSetUp();
		}
	}

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
	public void testInvalidInputHDonly() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+"));
	}

	@Test
	public void testInvalidInputPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+|SR|3"));
	}

	@Test
	public void testInvalidInputOneColon() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+:SR|2"));
	}

	@Test
	public void testInvalidInputEmptyHD() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ":DR:3/+1"));
	}

	@Test
	public void testInvalidInputEmptySubtype() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "4+::3/+1"));
	}

	@Test
	public void testInvalidInputEmptyDR() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+:DR:"));
	}

	@Test
	public void testInvalidInputEmptyDRNoColon()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+:DR"));
	}

	@Test
	public void testInvalidInputNoSlashDR() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+:DR:1"));
	}

	@Test
	public void testInvalidInputTwoSlashDR() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"3+:DR:1/3/+4"));
	}

	@Test
	public void testInvalidInputEmptySR() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+:SR:"));
	}

	@Test
	public void testInvalidInputEmptySA() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+:SA:"));
	}

	@Test
	public void testInvalidInputEmptyCR() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+:CR:"));
	}

	@Test
	public void testInvalidInputEmptySRNoColon()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+:SR"));
	}

	@Test
	public void testInvalidInputEmptySANoColon()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+:SA"));
	}

	@Test
	public void testInvalidInputEmptyCRNoColon()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+:CR"));
	}

	@Test
	public void testInvalidInputNoAbbrs() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+:C:3"));
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+:D:1/+2"));
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+:CRA:3"));
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, "3+:DRA:1/+2"));
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"3+:SAA:Special"));
		assertFalse(getToken().parse(primaryContext, primaryProf, "3+:SRA:1"));
	}

	@Test
	public void testInvalidInputBadClear() throws PersistenceLayerException
	{
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, ".CLEARSTUFF"));
	}

	@Test
	public void testInvalidInputNoSpecificClear()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			".CLEAR.3+:CR:3"));
	}

	@Test
	public void testInvalidInputBadHDRangePlus()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"+3:SA:Special Abil"));
	}

	@Test
	public void testInvalidInputBadHDRangeMult()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"*3:SA:Special Abil"));
	}

	@Test
	public void testInvalidInputBadHDRangeTwoDash()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"1--3:SA:Special Abil"));
	}

	@Test
	public void testInvalidInputBadHDRangeEndDash()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"4-:SA:Special Abil"));
	}

	@Test
	public void testInvalidInputBadHDRangeUpTo()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"-4:SA:Special Abil"));
	}

	@Test
	public void testRoundRobinRangeDR() throws PersistenceLayerException
	{
		runRoundRobin("2-5:DR:1/+3");
	}

	@Test
	public void testRoundRobinRangeSRNumber() throws PersistenceLayerException
	{
		runRoundRobin("3-5:SR:25");
	}

	@Test
	public void testRoundRobinRangeSRFormula() throws PersistenceLayerException
	{
		runRoundRobin("3-6:SR:Formula");
	}

	@Test
	public void testRoundRobinRangeSA() throws PersistenceLayerException
	{
		runRoundRobin("5-7:SA:Special Ability, Man!");
	}

	@Test
	public void testRoundRobinRangeCRNumber() throws PersistenceLayerException
	{
		runRoundRobin("4-11:CR:3");
	}

	@Test
	public void testRoundRobinRangeCRFormula() throws PersistenceLayerException
	{
		runRoundRobin("4-9:CR:Formula");
	}

	@Test
	public void testRoundRobinMinimumDR() throws PersistenceLayerException
	{
		runRoundRobin("2+:DR:1/+3");
	}

	@Test
	public void testRoundRobinMinimumSRNumber()
		throws PersistenceLayerException
	{
		runRoundRobin("3+:SR:25");
	}

	@Test
	public void testRoundRobinMinimumSRFormula()
		throws PersistenceLayerException
	{
		runRoundRobin("3+:SR:Formula");
	}

	@Test
	public void testRoundRobinMinimumSA() throws PersistenceLayerException
	{
		runRoundRobin("5+:SA:Special Ability, Man!");
	}

	@Test
	public void testRoundRobinMinimumCRNumber()
		throws PersistenceLayerException
	{
		runRoundRobin("4+:CR:3");
	}

	@Test
	public void testRoundRobinMinimumCRFormula()
		throws PersistenceLayerException
	{
		runRoundRobin("4+:CR:Formula");
	}

}
