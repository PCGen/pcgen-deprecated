package pcgen.cdom.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.PrereqObject;
import pcgen.core.prereq.Prerequisite;

public class PCGraphRoot implements PrereqObject
{

	public void addAllPrerequisites(Prerequisite... prereqs)
	{
		throw new UnsupportedOperationException(
			"Cannot add Prerequisites to PCGraphRoot");
	}

	public void addPrerequisite(Prerequisite preReq)
	{
		throw new UnsupportedOperationException(
			"Cannot add Prerequisites to PCGraphRoot");
	}

	public void clearPrerequisiteList()
	{
		// Fine, it's always empty :)
	}

	public int getPrerequisiteCount()
	{
		return 0;
	}

	public List<Prerequisite> getPrerequisiteList()
	{
		return Collections.emptyList();
	}

	public boolean hasPrerequisiteOfType(String matchType)
	{
		return false;
	}

	public boolean hasPrerequisites()
	{
		return false;
	}

	public void setPrerequisiteListFrom(PrereqObject prereqObject)
	{
		throw new UnsupportedOperationException(
			"Cannot add Prerequisites to PCGraphRoot");
	}

	public void addAllPrerequisites(Collection<Prerequisite> prereqs)
	{
		throw new UnsupportedOperationException(
			"Cannot add Prerequisites to PCGraphRoot");
	}

	public Class<? extends PrereqObject> getReferenceClass()
	{
		return getClass();
	}
}
