package plugin.lsttokens;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;

import pcgen.cdom.graph.PCGenGraph;
import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.TokenStore;

public abstract class AbstractTokenTestCase<T extends PObject> extends TestCase
{
	protected PCGenGraph primaryGraph;
	protected PCGenGraph secondaryGraph;
	protected LoadContext primaryContext;
	protected LoadContext secondaryContext;
	protected T primaryProf;
	protected T secondaryProf;

	private static boolean classSetUpFired = false;
	protected static CampaignSourceEntry testCampaign;

	@BeforeClass
	public static final void classSetUp() throws URISyntaxException
	{
		testCampaign =
				new CampaignSourceEntry(new Campaign(), new URI(
					"file:/Test%20Case"));
		classSetUpFired = true;
	}

	@Override
	@Before
	public void setUp() throws PersistenceLayerException,
		URISyntaxException
	{
		if (!classSetUpFired)
		{
			classSetUp();
		}
		// Yea, this causes warnings...
		TokenRegistration.register(getToken());
		primaryGraph = new PCGenGraph();
		secondaryGraph = new PCGenGraph();
		primaryContext = new LoadContext(primaryGraph);
		secondaryContext = new LoadContext(secondaryGraph);
		primaryProf =
				primaryContext.ref.constructCDOMObject(getCDOMClass(),
					"TestObj");
		secondaryProf =
				secondaryContext.ref.constructCDOMObject(getCDOMClass(),
					"TestObj");
	}

	public abstract Class<T> getCDOMClass();

	public static void addToken(LstToken tok)
	{
		TokenStore.inst().addToTokenMap(tok);
	}

	public void runRoundRobin(String... str) throws PersistenceLayerException
	{
		// Default is not to write out anything
		assertNull(getToken().unparse(primaryContext, primaryProf));
		// Ensure the graphs are the same at the start
		assertEquals(primaryGraph, secondaryGraph);

		// Set value
		for (String s : str)
		{
			assertTrue(getToken().parse(primaryContext, primaryProf, s));
		}
		// Get back the appropriate token:
		String unparsed = getToken().unparse(primaryContext, primaryProf);
		String expected;
		if (str.length == 1)
		{
			expected = getToken().getTokenName() + ":" + str[0];
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			boolean needTab = false;
			for (String s : str)
			{
				if (needTab)
				{
					sb.append('\t');
				}
				needTab = true;
				sb.append(getToken().getTokenName()).append(':').append(s);
			}
			expected = sb.toString();
		}
		assertEquals(expected, unparsed);

		// Do round Robin
		getLoader().parseLine(secondaryContext, secondaryProf,
			"TestObj\t" + unparsed, testCampaign);

		// Ensure the objects are the same
		assertEquals(primaryProf, secondaryProf);

		// Ensure the graphs are the same
		assertEquals(primaryGraph, secondaryGraph);

		// And that it comes back out the same again
		assertEquals(unparsed, getToken().unparse(secondaryContext,
			secondaryProf));
	}

	public abstract LstObjectFileLoader<T> getLoader();

	public abstract CDOMToken<T> getToken();

}
