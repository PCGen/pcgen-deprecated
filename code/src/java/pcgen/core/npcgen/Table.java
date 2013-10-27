package pcgen.core.npcgen;

import pcgen.core.Constants;
import pcgen.util.WeightedList;

public class Table 
{
	private WeightedList<TableEntry> theData = new WeightedList<TableEntry>();
	
	private String theId;
	private String theName = Constants.EMPTY_STRING;

	public Table( final String anId )
	{
		theId = anId;
	}

	public void setName( final String aName )
	{
		theName = aName;
	}
	
	public String getId()
	{
		return theId;
	}
	
	public TableEntry getEntry()
	{
		return theData.getRandomValue();
	}
	
	public void add( final int aWeight, final TableEntry anEntry )
	{
		theData.add( aWeight, anEntry );
	}
	
	public String toString()
	{
		return theName;
	}
}
