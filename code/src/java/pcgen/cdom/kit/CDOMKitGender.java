package pcgen.cdom.kit;

import pcgen.cdom.enumeration.Gender;

public class CDOMKitGender extends AbstractCDOMKitObject
{
	private Gender gender;

	public Gender getGender()
	{
		return gender;
	}

	public void setGender(Gender gender)
	{
		this.gender = gender;
	}
}
