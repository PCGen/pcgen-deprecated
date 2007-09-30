package pcgen.tokenruntimeload;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubRegion;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.core.Ability;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.pcclass.LevelsperfeatToken;
import plugin.lsttokens.template.AddLevelToken;
import plugin.lsttokens.template.BonusfeatsToken;
import plugin.lsttokens.template.BonusskillpointsToken;
import plugin.lsttokens.template.CrToken;
import plugin.lsttokens.template.FaceToken;
import plugin.lsttokens.template.FavoredclassToken;
import plugin.lsttokens.template.FeatToken;
import plugin.lsttokens.template.GenderlockToken;
import plugin.lsttokens.template.HandsToken;
import plugin.lsttokens.template.HdToken;
import plugin.lsttokens.template.HitdieToken;
import plugin.lsttokens.template.LangbonusToken;
import plugin.lsttokens.template.LegsToken;
import plugin.lsttokens.template.LevelToken;
import plugin.lsttokens.template.LeveladjustmentToken;
import plugin.lsttokens.template.NonppToken;
import plugin.lsttokens.template.RacesubtypeToken;
import plugin.lsttokens.template.RacetypeToken;
import plugin.lsttokens.template.ReachToken;
import plugin.lsttokens.template.RegionToken;
import plugin.lsttokens.template.RemovableToken;
import plugin.lsttokens.template.RepeatlevelToken;
import plugin.lsttokens.template.SizeToken;
import plugin.lsttokens.template.SubraceToken;
import plugin.lsttokens.template.SubregionToken;
import plugin.lsttokens.template.VisibleToken;
import plugin.lsttokens.template.WeaponbonusToken;
import plugin.lsttokens.testsupport.TokenRegistration;

public class TemplateTest extends AbstractIntegrationTestCase<PCTemplate>
{

	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
		URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(new AddLevelToken());
		TokenRegistration.register(new BonusfeatsToken());
		TokenRegistration.register(new BonusskillpointsToken());
		TokenRegistration.register(new CrToken());
		TokenRegistration.register(new FaceToken());
		TokenRegistration.register(new FavoredclassToken());
		TokenRegistration.register(new FeatToken());
		TokenRegistration.register(new GenderlockToken());
		TokenRegistration.register(new HandsToken());
		TokenRegistration.register(new HdToken());
		TokenRegistration.register(new HitdieToken());
		TokenRegistration.register(new LangbonusToken());
		TokenRegistration.register(new LegsToken());
		TokenRegistration.register(new LeveladjustmentToken());
		TokenRegistration.register(new LevelsperfeatToken());
		TokenRegistration.register(new LevelToken());
		TokenRegistration.register(new NonppToken());
		TokenRegistration.register(new RacesubtypeToken());
		TokenRegistration.register(new RacetypeToken());
		TokenRegistration.register(new ReachToken());
		TokenRegistration.register(new RegionToken());
		TokenRegistration.register(new RemovableToken());
		TokenRegistration.register(new RepeatlevelToken());
		TokenRegistration.register(new SizeToken());
		TokenRegistration.register(new SubraceToken());
		TokenRegistration.register(new SubregionToken());
		TokenRegistration.register(new VisibleToken());
		TokenRegistration.register(new WeaponbonusToken());
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

	@Test
	public void testImportBasicTemplate() throws PersistenceLayerException
	{
		verifyClean();
		primaryProf =
				loader.parseFullLine(primaryContext, 1,
					"TestTemplate\tHANDS:2\t"
						+ "SUBREGION:New South Wales\tVISIBLE:NO\tFEAT:Feat1",
					testCampaign);
		secondaryProf.put(IntegerKey.HANDS, Integer.valueOf(2));
		secondaryProf.put(ObjectKey.SUBREGION, SubRegion
			.valueOf("New South Wales"));
		secondaryProf.put(ObjectKey.VISIBILITY, Visibility.NO);
		secondaryProf.put(ObjectKey.SOURCE_URI, testCampaign.getURI());
		secondaryProf.setName("TestTemplate");
		PCGraphGrantsEdge edge =
				new PCGraphGrantsEdge(secondaryProf, secondaryContext.ref
					.getCDOMReference(Ability.class, AbilityCategory.FEAT,
						"Feat1"), "FEAT");
		edge
			.setAssociation(AssociationKey.ABILITY_NATURE, AbilityNature.NORMAL);
		edge.setAssociation(AssociationKey.SOURCE_URI, null);
		secondaryGraph.addEdge(edge);
		verifyClean();
	}
}
