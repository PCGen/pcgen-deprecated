/*
 * User: John K. Watson
 * Date: Feb 6, 2002
 * Time: 10:10:32 PM
 *
 */
package pcgen.core;

import java.io.File;
import java.util.List;
import pcgen.AbstractCharacterTestCase;
import pcgen.gui.NameElement;

/**
 * Test name generation
 */
@SuppressWarnings("nls")
public class NamesTest extends AbstractCharacterTestCase
{
	/**
	 * Constructs a new <code>NamesTest</code>.
	 *
	 * @see AbstractCharacterTestCase#AbstractCharacterTestCase()
	 */
	public NamesTest()
	{
		// Do Nothing
	}

	/**
	 * Constructs a new <code>NamesTest</code> with the given <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see AbstractCharacterTestCase#AbstractCharacterTestCase(String)
	 */
	public NamesTest(final String name)
	{
		super(name);
	}

	/**
	 * Test generating an Arabic name.
	 */
	public void testArabic()
	{
		Names.getInstance().init(
			getNameElement(SettingsHandler.getPcgenSystemDir() + File.separator
				+ "bio" + File.separator + "names" + File.separator, "Arabic"),
			getCharacter());
		assertTrue("got a null name!",
			Names.getInstance().getRandomName() != null);
		assertTrue("got a zero-length name!", Names.getInstance()
			.getRandomName().length() > 0);
		System.out.println("random arabic name: "
			+ Names.getInstance().getRandomName());
	}

	/**
	 * @throws Exception
	 */
	public void testGettingNameFiles() throws Exception
	{
		assertTrue("got null back!", Names.findAllNamesFiles() != null);
		List<NameElement> nameList = Names.findAllNamesFiles();
		assertTrue("got empty array back!", nameList.size() > 0);

		for (int i = 0; i < nameList.size(); i++)
		{
			final NameElement e = nameList.get(i);
			System.out.println("s = " + e);
		}
	}

	/**
	 * Test female name
	 */
	public void testASFemale()
	{
		Names.getInstance().init(
			getNameElement(SettingsHandler.getPcgenSystemDir() + File.separator
				+ "bio" + File.separator + "names" + File.separator,
				"rw_Anglo_Saxon_Female"), getCharacter());
		assertTrue("got a null name!",
			Names.getInstance().getRandomName() != null);
		assertTrue("got a zero-length name!", Names.getInstance()
			.getRandomName().length() > 0);
		System.out.println("random Anglo Saxon Female name: "
			+ Names.getInstance().getRandomName());
	}

	/**
	 * Test random name
	 *
	 */
	public void testRandomName()
	{
		Names.getInstance().init(
			getNameElement(SettingsHandler.getPcgenSystemDir() + File.separator
				+ "bio" + File.separator + "names" + File.separator, "orc"),
			getCharacter());

		//    System.out.println("random orc name: " + Names.getInstance().getRandomName());
		assertTrue("got a null name!",
			Names.getInstance().getRandomName() != null);
		assertTrue("got a zero-length name!", Names.getInstance()
			.getRandomName().length() > 0);
	}

	/**
	 * Test names?
	 *
	 */
	public void testTheNames()
	{
		Names.getInstance().init(
			getNameElement(SettingsHandler.getPcgenSystemDir() + File.separator
				+ "bio" + File.separator + "names" + File.separator, "orc"),
			getCharacter());

		//    for (Iterator iterator = Names.getInstance().getRuleList().iterator(); iterator.hasNext();) {
		//      String rule = (String) iterator.next();
		//      System.out.println("rule = " + rule);
		//    }
		//    for (Iterator iterator = Names.getInstance().getSyllablesByName("[SYL1]").iterator(); iterator.hasNext();) {
		//      String syl1 = (String) iterator.next();
		//      System.out.println("syl1 = " + syl1);
		//    }
		assertTrue("I got null rules!", Names.getInstance()
			.getRuleDefinitions() != null);
		assertTrue("I didn't get any rules!", Names.getInstance()
			.getRuleDefinitions().length > 0);
		assertTrue("There was nothing in syl1", Names.getInstance()
			.getSyllablesByName("[SYL1]").length > 0);
	}

	/**
	 * Create a name element
	 * @param path
	 * @param name
	 * @return A <tt>NameElement</tt>
	 */
	public NameElement getNameElement(String path, String name)
	{
		return new NameElement(new File(path + name + ".nam"), name);
	}
}
