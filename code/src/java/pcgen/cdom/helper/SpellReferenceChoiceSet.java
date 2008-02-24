package pcgen.cdom.helper;

import java.util.Collection;

import pcgen.cdom.base.CDOMListObject;

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
