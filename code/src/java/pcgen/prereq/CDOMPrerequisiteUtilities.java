package pcgen.prereq;

import java.util.List;

import pcgen.character.CharacterDataStore;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.RuleConstants;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.prereq.PrerequisiteTestFactory;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

public class CDOMPrerequisiteUtilities
{

	public static boolean passes(final Prerequisite prereq,
			final CharacterDataStore character, final PObject caller)
	{
		if (character == null)
		{
			return true;
		}
		final PrerequisiteTestFactory factory = PrerequisiteTestFactory
				.getInstance();
		final PrerequisiteTest test = factory.getTest(prereq.getKind());
		if (prereq.getLevelQualifier() >= 0 && (caller instanceof PCClass)
				&& ((PCClass) caller).getLevel() != prereq.getLevelQualifier())
			return true;

		if (test == null)
		{
			Logging
					.errorPrintLocalised(
							"PrereqHandler.Unable_to_find_implementation", prereq.toString()); //$NON-NLS-1$
			return false;
		}

		final boolean overrideQualify = prereq.isOverrideQualify();
		boolean autoQualifies = false;
		int total = 0;

		if ((caller != null) && character.checkQualifyList(caller)
				&& (!overrideQualify))
		{
			autoQualifies = true;
		}
		if (autoQualifies)
		{
			return true;
		}
		try
		{
			total = test.passesCDOM(prereq, character);
		}
		catch (PrerequisiteException pe)
		{
			Logging.errorPrintLocalised("PrereqHandler.Exception_in_test", pe); //$NON-NLS-1$
		}
		catch (Exception e)
		{
			Logging.errorPrint(
					"Problem encountered when testing PREREQ "
							+ String.valueOf(prereq)
							+ (caller != null ? (" for " + String
									.valueOf(caller)) : "")
							+ ". See following trace for details.", e);
		}
		return total > 0;
	}


	/**
	 * Test if the character passes the prerequisites for the caller. The caller
	 * is used to check if prereqs can be bypassed by either preferences or via
	 * Qualifies statements in templates or other objects applied to the
	 * character.
	 *
	 * @param prereqList The list of prerequisites to be tested.
	 * @param character The character to be checked.
	 * @param caller The object that we are testing qualification for.
	 * @return True if the character passes all prereqs.
	 */
	public static boolean passesAll(final List<Prerequisite> prereqList, final CharacterDataStore character, final PObject caller)
	{
		if (prereqList == null || prereqList.isEmpty())
		{
			return true;
		}

		if ((caller instanceof PCClass) && Globals.checkRule(RuleConstants.CLASSPRE)) //$NON-NLS-1$
		{
			return true;
		}
		if ((caller instanceof Ability) && Globals.checkRule(RuleConstants.FEATPRE)) //$NON-NLS-1$
		{
			return true;
		}

		if ((character != null) && (caller != null))
		{
			// Check for QUALIFY:
			if (character.checkQualifyList(caller))
			{
				return true;
			}
		}

		for (Object object : prereqList)
		{
			Prerequisite prereq;

			if (object instanceof String)
			{
				final String oString = (String)object;
				Logging.debugPrintLocalised("PrereqHandler.Why_not_already_parsed", object, "PrereqHandler.passesAll()"); //$NON-NLS-1$ //$NON-NLS-2$
				try
				{
					final PreParserFactory factory = PreParserFactory.getInstance();
					prereq = factory.parse( oString );
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple); //The message is now produced at a lower level, and thus has to be localised there.
					//Logging.errorPrintLocalised(PropertyFactory.getString("PrereqHandler.Unable_to_parse"), object); //$NON-NLS-1$
					return false;
				}
			}
			else
			{
				prereq = (Prerequisite) object;
			}


			if (!passes(prereq, character, caller))
			{
				return false;
			}
		}
		return true;
	}
}
