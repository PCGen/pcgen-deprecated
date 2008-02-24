package pcgen.cdom.inst;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.character.CharacterDataStore;
import pcgen.core.character.WieldCategory;

public class CDOMEquipment extends CDOMObject
{

	List<EquipmentHead> heads = new ArrayList<EquipmentHead>();

	public EquipmentHead getEquipmentHead(int index)
	{
		EquipmentHead head;
		if (index <= 0)
		{
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}
		else
		{
			int headsIndex = index - 1;
			int currentSize = heads.size();
			if (headsIndex >= currentSize)
			{
				for (int i = 0; i < headsIndex - currentSize; i++)
				{
					heads.add(null);
				}
				head = new EquipmentHead(this, index);
				heads.add(head);
			}
			else
			{
				head = heads.get(headsIndex);
				if (head == null)
				{
					head = new EquipmentHead(this, index);
					heads.add(headsIndex, head);
				}
			}
		}
		return head;
	}

	public EquipmentHead getEquipmentHeadReference(int index)
	{
		if (index <= 0)
		{
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}
		else if (index <= heads.size())
		{
			return heads.get(index - 1);
		}
		return null;
	}

	public WieldCategory getEffectiveWieldCategory(CharacterDataStore character)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCode()
	{
		String name = this.getDisplayName();
		return name == null ? 0 : name.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CDOMEquipment)
		{
			CDOMEquipment other = (CDOMEquipment) o;
			return other.isCDOMEqual(this) && other.equalsPrereqObject(this);
		}
		return false;
	}
}
