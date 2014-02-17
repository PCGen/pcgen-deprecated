package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

public class NoChoiceManager implements ChoiceManagerList<String>
{

	private final CDOMObject owner;
	private final int choicesPerUnitCost;
	private ChooseController<String> controller =
			new ChooseController<String>();
	private final ChooseInformation<String> info;

	private transient int preChooserChoices;

	public NoChoiceManager(CDOMObject cdo,
		ChooseInformation<String> chooseType, int cost)
	{
		owner = cdo;
		info = chooseType;
		choicesPerUnitCost = cost;
	}

    @Override
	public void getChoices(PlayerCharacter pc, List<String> availableList,
		List<String> selectedList)
	{
		availableList.addAll(info.getSet(pc));
		selectedList.addAll(pc.getAssociationList(owner));
		preChooserChoices = selectedList.size();
	}

    @Override
	public String typeHandled()
	{
		throw new UnsupportedOperationException();
	}

    @Override
	public boolean conditionallyApply(PlayerCharacter pc, String item)
	{
		throw new UnsupportedOperationException();
	}

    @Override
	public boolean applyChoices(PlayerCharacter pc, List<String> selected)
	{
		List<? extends String> oldSelections =
				info.getChoiceActor().getCurrentlySelected(owner, pc);
		int oldSelectionSize =
				(oldSelections == null) ? 0 : oldSelections.size();
		int newSelectionSize = selected.size();
		for (int i = oldSelectionSize; i > newSelectionSize; i--)
		{
			info.getChoiceActor().removeChoice(pc, owner,
				Constants.EMPTY_STRING);
		}
		for (int i = oldSelectionSize; i < newSelectionSize; i++)
		{
			info.getChoiceActor()
				.applyChoice(owner, Constants.EMPTY_STRING, pc);
		}
		adjustPool(selected);
		return oldSelectionSize != newSelectionSize;
	}

    @Override
	public List<String> doChooser(PlayerCharacter aPc,
		final List<String> availableList, final List<String> selectedList,
		final List<String> reservedList)
	{
		selectedList.add("NOCHOICE");
		return new ArrayList<String>(selectedList);
	}

    @Override
	public void doChooserRemove(PlayerCharacter aPC,
		List<String> availableList, List<String> selectedList,
		List<String> reservedList)
	{
		selectedList.remove(0);
		applyChoices(aPC, selectedList);
	}

	protected void adjustPool(List<String> selected)
	{
		controller.adjustPool(selected);
	}

    @Override
	public void setController(ChooseController<String> cc)
	{
		controller = cc;
	}

    @Override
	public int getChoicesPerUnitCost()
	{
		return choicesPerUnitCost;
	}

    @Override
	public int getPreChooserChoices()
	{
		return preChooserChoices;
	}

    @Override
	public void restoreChoice(PlayerCharacter pc, CDOMObject target,
		String choice)
	{
		info.restoreChoice(pc, target, info.decodeChoice(Globals.getContext(), choice));
	}

    @Override
	public int getNumEffectiveChoices(List<String> selectedList,
		List<String> reservedList, PlayerCharacter aPc)
	{
		return 0;
	}

	@Override
	public void removeChoice(PlayerCharacter pc, CDOMObject obj, String selection)
	{
		info.removeChoice(pc, obj, selection);
	}

	@Override
	public String decodeChoice(String choice)
	{
		return info.decodeChoice(Globals.getContext(), choice);
	}

	@Override
	public void applyChoice(PlayerCharacter pc, CDOMObject cdo,
		String selection)
	{
		info.getChoiceActor().applyChoice(cdo, Constants.EMPTY_STRING, pc);
	}
}
