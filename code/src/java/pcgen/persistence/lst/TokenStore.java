package pcgen.persistence.lst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.base.lang.UnreachableError;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.core.PObject;
import pcgen.util.Logging;

/**
 * A Store of LST tokens, has a map and list representation
 */
public class TokenStore
{
	private static TokenStore inst;
	private HashMap<Class<? extends LstToken>, Map<String, LstToken>> tokenTypeMap;
	private final List<Class<? extends LstToken>> tokenTypeList;
	private DoubleKeyMapToList<Class<? extends LstToken>, String, LstToken> tokenCompatibilityMap;
	private final List<Class<? extends LstToken>> tokenCompatibilityList;

	private TokenStore()
	{
		tokenTypeMap =
				new HashMap<Class<? extends LstToken>, Map<String, LstToken>>();
		tokenTypeList = new ArrayList<Class<? extends LstToken>>();
		tokenCompatibilityMap =
				new DoubleKeyMapToList<Class<? extends LstToken>, String, LstToken>();
		tokenCompatibilityList = new ArrayList<Class<? extends LstToken>>();
		populateTokenTypeList();
	}

	/**
	 * Create an instance of TokenStore and return it.
	 * 
	 * @return an instance of TokenStore and return it.
	 */
	public static TokenStore inst()
	{
		if (inst == null)
		{
			inst = new TokenStore();
		}
		return inst;
	}

	private void populateTokenTypeList()
	{
		// Campaign data
		tokenTypeList.add(GlobalLstToken.class);
		tokenTypeList.add(AbilityLstToken.class);
		tokenTypeList.add(CampaignLstToken.class);
		tokenTypeList.add(PCClassLstToken.class);
		tokenTypeList.add(PCClassUniversalLstToken.class);
		tokenTypeList.add(PCClassClassLstToken.class);
		tokenTypeList.add(PCClassLevelLstToken.class);
		tokenTypeList.add(CompanionModLstToken.class);
		tokenTypeList.add(DeityLstToken.class);
		tokenTypeList.add(DomainLstToken.class);
		tokenTypeList.add(EquipmentLstToken.class);
		tokenTypeList.add(EquipmentModifierLstToken.class);
		tokenTypeList.add(LanguageLstToken.class);
		tokenTypeList.add(RaceLstToken.class);
		tokenTypeList.add(PCTemplateLstToken.class);
		tokenTypeList.add(SkillLstToken.class);
		tokenTypeList.add(SpellLstToken.class);
		tokenTypeList.add(SourceLstToken.class);
		tokenTypeList.add(SubClassLstToken.class);
		tokenTypeList.add(SubstitutionClassLstToken.class);
		tokenTypeList.add(WeaponProfLstToken.class);
		// Kits
		tokenTypeList.add(KitLstToken.class);
		tokenTypeList.add(BaseKitLstToken.class);
		tokenTypeList.add(KitAbilityLstToken.class);
		tokenTypeList.add(KitClassLstToken.class);
		tokenTypeList.add(KitDeityLstToken.class);
		tokenTypeList.add(KitFundsLstToken.class);
		tokenTypeList.add(KitGearLstToken.class);
		tokenTypeList.add(KitLevelAbilityLstToken.class);
		tokenTypeList.add(KitProfLstToken.class);
		tokenTypeList.add(KitSkillLstToken.class);
		tokenTypeList.add(KitSpellsLstToken.class);
		tokenTypeList.add(KitStartpackLstToken.class);
		tokenTypeList.add(KitTableLstToken.class);

		// miscinfo.lst
		tokenTypeList.add(GameModeLstToken.class);
		tokenTypeList.add(AbilityCategoryLstToken.class);
		tokenTypeList.add(BaseDiceLstToken.class);
		tokenTypeList.add(EqSizePenaltyLstToken.class);
		tokenTypeList.add(RollMethodLstToken.class);
		tokenTypeList.add(TabLstToken.class);
		tokenTypeList.add(UnitSetLstToken.class);
		tokenTypeList.add(WieldCategoryLstToken.class);

		// statsandchecks.lst
		tokenTypeList.add(StatsAndChecksLstToken.class);
		tokenTypeList.add(PCAlignmentLstToken.class);
		tokenTypeList.add(BonusSpellLstToken.class);
		tokenTypeList.add(PCCheckLstToken.class);
		tokenTypeList.add(PCStatLstToken.class);

		// sizeAdjustment.ts
		tokenTypeList.add(SizeAdjustmentLstToken.class);

		// rules.js
		tokenTypeList.add(RuleCheckLstToken.class);

		// pointbuymethod.lst
		tokenTypeList.add(PointBuyLstToken.class);
		tokenTypeList.add(PointBuyMethodLstToken.class);
		tokenTypeList.add(PointBuyStatLstToken.class);

		// level.lst
		tokenTypeList.add(LevelLstToken.class);

		// equipmentslots.lst
		tokenTypeList.add(EquipSlotLstToken.class);

		// load.lst
		tokenTypeList.add(LoadInfoLstToken.class);

		// paperinfo.lst
		tokenTypeList.add(PaperInfoLstToken.class);

		// sponsors.lst
		tokenTypeList.add(SponsorLstToken.class);

		// subtokens
		tokenTypeList.add(EqModChooseLstToken.class);
		tokenTypeList.add(ChooseLstToken.class);
		tokenTypeList.add(ChooseCDOMLstToken.class);
		tokenTypeList.add(AutoLstToken.class);
		tokenTypeList.add(AddLstToken.class);
		tokenTypeList.add(RemoveLstToken.class);

		// compatibility
		tokenCompatibilityList.add(ChooseCompatibilityToken.class);
		tokenCompatibilityList.add(EqModChooseCompatibilityToken.class);
		tokenCompatibilityList.add(GlobalLstCompatibilityToken.class);
		tokenCompatibilityList.add(EquipmentLstCompatibilityToken.class);
		tokenCompatibilityList
			.add(EquipmentModifierLstCompatibilityToken.class);
		tokenCompatibilityList.add(PCClassClassLstCompatibilityToken.class);
		tokenCompatibilityList.add(PCClassLevelLstCompatibilityToken.class);
		tokenCompatibilityList.add(PCClassUniversalLstCompatibilityToken.class);
		tokenCompatibilityList.add(RaceLstCompatibilityToken.class);

		//install.lst
		tokenTypeList.add(InstallLstToken.class);
	}

	/**
	 * Add the new token to the token map
	 * 
	 * @param newToken
	 */
	public void addToTokenMap(LstToken newToken)
	{
		Class<? extends LstToken> newTokClass = newToken.getClass();
		for (Class<? extends LstToken> tokClass : tokenTypeList)
		{
			if (tokClass.isAssignableFrom(newTokClass))
			{
				Map<String, LstToken> tokenMap = getTokenMap(tokClass);
				LstToken test = tokenMap.put(newToken.getTokenName(), newToken);

				if (test != null)
				{
					Logging.errorPrint("More than one " + tokClass.getName()
						+ " has the same token name: '"
						+ newToken.getTokenName() + "'");
				}
			}
		}
		for (Class<? extends LstToken> tokClass : tokenCompatibilityList)
		{
			if (tokClass.isAssignableFrom(newToken.getClass()))
			{
				tokenCompatibilityMap.addToListFor(tokClass, newToken
					.getTokenName(), newToken);

				// TODO Someday need to test that it's a unique compatibility
				// level...
				// if (test != null)
				// {
				// Logging.errorPrint("More than one " + tokClass.getName()
				// + " has the same token name: '"
				// + newToken.getTokenName() + "'");
				// }
			}
		}
	}

	public void addToPrimitiveMap(PrimitiveToken<?> p)
	{
		Class<? extends PrimitiveToken> newTokClass = p.getClass();
		if (PrimitiveToken.class.isAssignableFrom(newTokClass))
		{
			primitiveMap
				.put(((PrimitiveToken) p).getReferenceClass(), p
					.getTokenName(), (Class<PrimitiveToken<?>>) newTokClass);
		}
	}
	
	public void addToQualifierMap(QualifierToken<?> p)
	{
		Class<? extends QualifierToken> newTokClass = p.getClass();
		if (ChooseLstQualifierToken.class.isAssignableFrom(newTokClass))
		{
			qualifierMap.put(((ChooseLstQualifierToken<?>) p)
				.getChoiceClass(), p.getTokenName(),
				(Class<ChooseLstQualifierToken<?>>) newTokClass);
		}
		if (ChooseLstGlobalQualifierToken.class.isAssignableFrom(newTokClass))
		{
			globalQualifierMap.put(p.getTokenName(),
				(Class<ChooseLstGlobalQualifierToken<?>>) newTokClass);
		}
	}
	
	/**
	 * Get the token map
	 * 
	 * @param tokInterface
	 * @return the token map
	 */
	public Map<String, LstToken> getTokenMap(
		Class<? extends LstToken> tokInterface)
	{
		Map<String, LstToken> tokenMap = tokenTypeMap.get(tokInterface);
		if (tokenMap == null)
		{
			tokenMap = new HashMap<String, LstToken>();
			tokenTypeMap.put(tokInterface, tokenMap);
		}
		return tokenMap;
	}

	public <T extends LstToken> T getToken(Class<T> name, String key)
	{
		Map<String, LstToken> tokenMap = tokenTypeMap.get(name);
		if (tokenMap == null)
		{
			return null;
		}
		return name.cast(tokenMap.get(key));
	}

	public <T extends LstToken> Collection<T> getCompatibilityToken(
		Class<T> name, String key)
	{
		return (Collection<T>) tokenCompatibilityMap.getListFor(name, key);
	}

	DoubleKeyMap<Class<?>, String, Class<PrimitiveToken<?>>> primitiveMap =
			new DoubleKeyMap<Class<?>, String, Class<PrimitiveToken<?>>>();

	public <T> PrimitiveToken<T> getPrimitive(Class<T> name, String tokKey)
	{
		Class<PrimitiveToken<?>> cptc = primitiveMap.get(name, tokKey);
		if (cptc == null)
		{
			return null;
		}
		try
		{
			return (PrimitiveToken<T>) cptc.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new UnreachableError("new Instance on " + cptc
				+ " should not fail in getPrimitive", e);
		}
		catch (IllegalAccessException e)
		{
			throw new UnreachableError("new Instance on " + cptc
				+ " should not fail due to access", e);
		}
	}

	DoubleKeyMap<Class<? extends PObject>, String, Class<ChooseLstQualifierToken<?>>> qualifierMap =
			new DoubleKeyMap<Class<? extends PObject>, String, Class<ChooseLstQualifierToken<?>>>();

	public <T extends PObject> ChooseLstQualifierToken<T> getChooseQualifier(
		Class<T> domain_class, String key)
	{
		Class<ChooseLstQualifierToken<?>> clqtc =
				qualifierMap.get(domain_class, key);
		if (clqtc == null)
		{
			return null;
		}
		try
		{
			return (ChooseLstQualifierToken<T>) clqtc.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new UnreachableError("new Instance on " + clqtc
				+ " should not fail in getChooseQualifier", e);
		}
		catch (IllegalAccessException e)
		{
			throw new UnreachableError("new Instance on " + clqtc
				+ " should not fail due to access", e);
		}
	}

	HashMap<String, Class<ChooseLstGlobalQualifierToken<?>>> globalQualifierMap =
			new HashMap<String, Class<ChooseLstGlobalQualifierToken<?>>>();

	public <T extends PObject> ChooseLstGlobalQualifierToken<T> getGlobalChooseQualifier(
		String key)
	{
		Class<ChooseLstGlobalQualifierToken<?>> clgqtc =
				globalQualifierMap.get(key);
		if (clgqtc == null)
		{
			return null;
		}
		try
		{
			return (ChooseLstGlobalQualifierToken<T>) clgqtc.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new UnreachableError("new Instance on " + clgqtc
				+ " should not fail in getGlobalChooseQualifier", e);
		}
		catch (IllegalAccessException e)
		{
			throw new UnreachableError("new Instance on " + clgqtc
				+ " should not fail due to access", e);
		}
	}
}
