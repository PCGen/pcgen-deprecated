package pcgen.cdom.helper;

import java.util.Collection;

import pcgen.core.CDOMListObject;

public class SpellReferenceChoiceSet extends ReferenceChoiceSet
{
	public SpellReferenceChoiceSet(Collection col)
	{
		super(col);
	}

	@Override
	public Class getChoiceClass()
	{
		return CDOMListObject.class;
	}
}
