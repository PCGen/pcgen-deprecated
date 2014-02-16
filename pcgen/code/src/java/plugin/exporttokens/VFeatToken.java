package plugin.exporttokens;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbilityToken;

/**
 * <code>VFeatToken</code> deals with VFEAT output token.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author karianna
 * @version $Revision$
 */
public class VFeatToken extends AbilityToken
{

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "VFEAT";
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
						   ExportHandler eh)
	{
		setVisibility(ABILITY_ALL);
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		final String fString = aTok.nextToken();

		return getTokenForCategory(tokenSource, pc, eh, aTok, fString,
								   AbilityCategory.FEAT);
	}

	/**
	 * @see pcgen.io.exporttoken.AbilityToken#getAbilityList(pcgen.core.PlayerCharacter, pcgen.core.AbilityCategory)
	 */
	@Override
	protected MapToList<Ability, CNAbility> getAbilityList(PlayerCharacter pc,
										   final AbilityCategory aCategory)
	{
		final MapToList<Ability, CNAbility> listOfAbilities = new HashMapToList<Ability, CNAbility>();
		Collection<AbilityCategory> allCats =
				SettingsHandler.getGame().getAllAbilityCategories();
		for (AbilityCategory aCat : allCats)
		{
			if (aCat.getParentCategory().equals(aCategory))
			{
				for (CNAbility cna : pc.getCNAbilities(aCat, Nature.VIRTUAL))
				{
					listOfAbilities.addToListFor(cna.getAbility(), cna);
				}
			}
		}
		return listOfAbilities;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Nature getTargetNature()
	{
		return Nature.VIRTUAL;
	}

}
