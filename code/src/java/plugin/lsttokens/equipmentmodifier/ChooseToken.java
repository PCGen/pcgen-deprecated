/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens.equipmentmodifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.core.EquipmentModifier;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class ChooseToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "CHOOSE";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		String key;
		String val = value;
		int activeLoc = 0;
		String count = null;
		String maxCount = null;
		List<String> prefixList = new ArrayList<String>(2);
		while (true)
		{
			int pipeLoc = val.indexOf(Constants.PIPE, activeLoc);
			if (pipeLoc == -1)
			{
				key = val;
				val = null;
			}
			else
			{
				key = val.substring(activeLoc, pipeLoc);
				val = val.substring(pipeLoc + 1);
			}
			if (key.startsWith("COUNT="))
			{
				if (count != null)
				{
					Logging
							.errorPrint("Cannot use COUNT more than once in CHOOSE: "
									+ value);
					return false;
				}
				prefixList.add(key);
				count = key.substring(6);
				if (count == null)
				{
					Logging.errorPrint("COUNT in CHOOSE must be a formula: "
							+ value);
					return false;
				}
				activeLoc += key.length() + 1;
			}
			else if (key.startsWith("NUMCHOICES="))
			{
				if (maxCount != null)
				{
					Logging
							.errorPrint("Cannot use NUMCHOICES more than once in CHOOSE: "
									+ value);
					return false;
				}
				prefixList.add(key);
				maxCount = key.substring(11);
				if (maxCount == null || maxCount.length() == 0)
				{
					Logging
							.errorPrint("NUMCHOICES in CHOOSE must be a formula: "
									+ value);
					return false;
				}
				activeLoc += key.length() + 1;
			}
			else
			{
				break;
			}
		}
		String prefixString = CoreUtility.join(prefixList, "|");
		if (ChooseLoader.isEqModChooseToken(key))
		{
			if (ChooseLoader.parseEqModToken(mod, prefixString, key, val))
			{
				return true;
			}
		}
		Logging
				.deprecationPrint("CHOOSE: in EqMod with Title as first argument is deprecated");
		Logging
				.deprecationPrint("  Please use CHOOSE:<subtoken>|<args>|TITLE=<title>");
		Logging.deprecationPrint("  Offending CHOOSE was: " + value);
		if (value.indexOf("TITLE=") != -1)
		{
			Logging
					.errorPrint("Unexpected Condition: Deprecated Syntax EqMod with Chooser Type and TITLE= "
							+ value);
			Logging
					.errorPrint("Please check error messages above for syntax errors or contact the PCGen team on the PCGenListFileHelp Yahoo Group for support");
			return false;
		}
		mod.setChoiceString(value);
		return true;
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
			String value) throws PersistenceLayerException
	{
		String token = null;
		String rest = value;
		String count = null;
		String maxCount = null;
		int pipeLoc = value.indexOf(Constants.PIPE);
		while (pipeLoc != -1)
		{
			token = rest.substring(0, pipeLoc);
			rest = rest.substring(pipeLoc + 1);
			if (token.startsWith("COUNT="))
			{
				if (count != null)
				{
					Logging
							.errorPrint("Cannot use COUNT more than once in CHOOSE: "
									+ value);
					return false;
				}
				count = token.substring(6);
				if (count == null)
				{
					Logging.errorPrint("COUNT in CHOOSE must be a formula: "
							+ value);
					return false;
				}
			}
			else if (token.startsWith("NUMCHOICES="))
			{
				if (maxCount != null)
				{
					Logging
							.errorPrint("Cannot use NUMCHOICES more than once in CHOOSE: "
									+ value);
					return false;
				}
				maxCount = token.substring(11);
				if (maxCount == null || maxCount.length() == 0)
				{
					Logging
							.errorPrint("NUMCHOICES in CHOOSE must be a formula: "
									+ value);
					return false;
				}
			}
			else
			{
				break;
			}
			pipeLoc = rest.indexOf(Constants.PIPE);
		}
		String title = null;
		if (rest != null)
		{
			int titleLoc = rest.indexOf("|TITLE=");
			if (titleLoc != -1)
			{
				if (rest.substring(titleLoc + 1).indexOf(Constants.PIPE) != -1)
				{
					Logging.errorPrint("CHOOSE: in EqMod must END with TITLE= . "
							+ "No additional arguments allowed after the title.  "
							+ "Offending value: " + value);
					return false;
				}
				title = rest.substring(titleLoc + 7);
				rest = rest.substring(0, titleLoc);
				pipeLoc = rest.indexOf(Constants.PIPE);
			}
		}
		String key;
		String val;
		if (pipeLoc == -1)
		{
			key = rest;
			val = null;
		}
		else
		{
			key = token;
			val = rest;
		}
		PrimitiveChoiceSet<?> chooser = ChooseLoader.parseEqModToken(context,
				mod, key, val);
		if (chooser == null)
		{
			// Yes, direct access, not through the context!!
			mod.put(StringKey.CHOOSE_BACKUP, value);
			return false;
		}
		ChooseActionContainer cac = mod.getChooseContainer();
		Formula maxFormula = maxCount == null ? FormulaFactory
				.getFormulaFor(Integer.MAX_VALUE) : FormulaFactory
				.getFormulaFor(maxCount);
		Formula countFormula = count == null ? FormulaFactory.getFormulaFor(1)
				: FormulaFactory.getFormulaFor(count);
		ChoiceSet<?> choiceSet = new ChoiceSet(Constants.CHOOSE, chooser);
		cac.setChoiceSet(choiceSet);
		cac.setAssociation(AssociationKey.CHOICE_COUNT, countFormula);
		cac.setAssociation(AssociationKey.CHOICE_MAXCOUNT, maxFormula);
		if (title != null)
		{
			cac.setAssociation(AssociationKey.CHOICE_TITLE, title);
		}
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		AssociatedChanges<ChoiceSet> changes = context.getGraphContext()
				.getChangesFromToken(getTokenName(), mod, ChoiceSet.class);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		return new String[] { ReferenceUtilities.joinLstFormat(added,
				Constants.PIPE) };
	}

}
