package plugin.lsttokens;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class SaLstTest extends AbstractGlobalTokenTestCase
{
	static GlobalLstToken token = new SaLst();
	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}

	PreClassParser preclass = new PreClassParser();
	PreClassWriter preclasswriter = new PreClassWriter();
	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(preclass);
		TokenRegistration.register(prerace);
		TokenRegistration.register(preclasswriter);
		TokenRegistration.register(preracewriter);
	}

	@Test
	public void testInvalidDoublePipe() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SA Number %||VarF"));
	}

	@Test
	public void testInvalidEndingPipe() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "SA Number|"));
	}

	@Test
	public void testInvalidStartingPipe() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "|Var"));
	}

	@Test
	public void testInvalidVarAfterPre() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SA % plus %|Var|PRECLASS:1,Fighter|Var2"));
	}

	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		runRoundRobin("SA Number One");
	}

	@Test
	public void testRoundRobinVariable() throws PersistenceLayerException
	{
		runRoundRobin("SA Number %|Variab");
	}

	@Test
	public void testRoundRobinPre() throws PersistenceLayerException
	{
		runRoundRobin("SA Number One|PRECLASS:1,Fighter=1");
	}

	@Test
	public void testRoundRobinDoublePre() throws PersistenceLayerException
	{
		runRoundRobin("SA Number One|PRECLASS:1,Fighter=1|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinVarDoublePre() throws PersistenceLayerException
	{
		runRoundRobin("SA Number % before %|Var|TwoVar|PRECLASS:1,Fighter=1|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinCompound() throws PersistenceLayerException
	{
		runRoundRobin(
			"SA Number % before %|Var|TwoVar|PRECLASS:1,Fighter=1|PRERACE:1,Human",
			"SA Number One|PRECLASS:1,Fighter=1");
	}

}
