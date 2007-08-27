/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.persistence;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import pcgen.base.io.FileLocation;
import pcgen.base.io.FileLocationFactory;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.MapToList;
import pcgen.base.util.TreeMapToList;
import pcgen.base.util.TripleKeyMapToList;
import pcgen.base.util.WeightedCollection;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.inst.SimpleAssociatedObject;
import pcgen.persistence.lst.utils.TokenUtilities;

public class GraphContext
{

	private final TrackingGraphCommitStrategy edits =
			new TrackingGraphCommitStrategy();

	private final GraphCommitStrategy commit;

	public GraphContext()
	{
		commit = new TrackingGraphCommitStrategy();
	}

	public GraphContext(GraphCommitStrategy commitStrategy)
	{
		if (commitStrategy == null)
		{
			throw new IllegalArgumentException("Commit Strategy cannot be null");
		}
		commit = commitStrategy;
	}

	public URI getSourceURI()
	{
		return edits.getSourceURI();
	}

	public void setSourceURI(URI sourceURI)
	{
		edits.setSourceURI(sourceURI);
		commit.setSourceURI(sourceURI);
	}

	public URI getExtractURI()
	{
		return edits.getExtractURI();
	}

	public void setExtractURI(URI extractURI)
	{
		edits.setExtractURI(extractURI);
		commit.setExtractURI(extractURI);
	}

	public void setLine(int i)
	{
		edits.setLine(i);
		commit.setLine(i);
	}

	public AssociatedPrereqObject grant(String sourceToken, CDOMObject obj,
		PrereqObject pro)
	{
		return edits.grant(sourceToken, obj, pro);
	}

	public void remove(String sourceToken, CDOMObject obj, PrereqObject pro)
	{
		edits.remove(sourceToken, obj, pro);
	}

	public void removeAll(String tokenName, CDOMObject obj)
	{
		edits.removeAll(tokenName, obj);
	}

	public void commit()
	{
		for (String token : edits.globalRemoveSet.getKeySet())
		{
			for (CDOMObject owner : edits.globalRemoveSet
				.getSecondaryKeySet(token))
			{
				commit.removeAll(token, owner);
			}
		}
		for (String token : edits.removed.getKeySet())
		{
			for (CDOMObject owner : edits.removed.getSecondaryKeySet(token))
			{
				for (PrereqObject child : edits.removed.getTertiaryKeySet(
					token, owner))
				{
					commit.remove(token, owner, child);
				}
			}
		}
		for (String token : edits.added.getKeySet())
		{
			for (CDOMObject owner : edits.added.getSecondaryKeySet(token))
			{
				for (PrereqObject child : edits.added.getTertiaryKeySet(token,
					owner))
				{
					for (AssociatedPrereqObject assoc : edits.added.getListFor(
						token, owner, child))
					{
						AssociatedPrereqObject edge =
								commit.grant(token, owner, child);
						Collection<AssociationKey<?>> associationKeys =
								assoc.getAssociationKeys();
						for (AssociationKey<?> ak : associationKeys)
						{
							setAssoc(assoc, edge, ak);
						}
						edge.addAllPrerequisites(assoc.getPrerequisiteList());
					}
				}
			}
		}
		decommit();
	}

	private <T> void setAssoc(AssociatedPrereqObject assoc,
		AssociatedPrereqObject edge, AssociationKey<T> ak)
	{
		edge.setAssociation(ak, assoc.getAssociation(ak));
	}

	public void decommit()
	{
		edits.decommit();
	}

	public <T extends PrereqObject & LSTWriteable> AssociatedChanges<T> getChangesFromToken(
		String tokenName, CDOMObject pct, Class<T> name)
	{
		return commit.getChangesFromToken(tokenName, pct, name);
	}

	public class TrackingGraphCommitStrategy implements GraphCommitStrategy
	{

		private DoubleKeyMapToList<String, CDOMObject, URI> globalRemoveSet =
				new DoubleKeyMapToList<String, CDOMObject, URI>();

		private TripleKeyMapToList<String, CDOMObject, PrereqObject, AssociatedPrereqObject> added =
				new TripleKeyMapToList<String, CDOMObject, PrereqObject, AssociatedPrereqObject>();

		private TripleKeyMapToList<String, CDOMObject, PrereqObject, AssociatedPrereqObject> removed =
				new TripleKeyMapToList<String, CDOMObject, PrereqObject, AssociatedPrereqObject>();

		private URI sourceURI;

		private URI extractURI;

		private FileLocationFactory locFac = new FileLocationFactory();

		public void setSourceURI(URI source)
		{
			sourceURI = source;
			locFac.newFile();
		}

		public URI getSourceURI()
		{
			return sourceURI;
		}

		public void setExtractURI(URI uri)
		{
			extractURI = uri;
		}

		public URI getExtractURI()
		{
			return extractURI;
		}

		public AssociatedPrereqObject grant(String sourceToken, CDOMObject obj,
			PrereqObject pro)
		{
			SimpleAssociatedObject sao = new SimpleAssociatedObject();
			added.addToListFor(sourceToken, obj, pro, sao);
			sao.setAssociation(AssociationKey.SOURCE_URI, sourceURI);
			FileLocation loc = locFac.getFileLocation();
			sao.setAssociation(AssociationKey.FILE_LOCATION, loc);
			return sao;
		}

		public void remove(String sourceToken, CDOMObject obj, PrereqObject pro)
		{
			SimpleAssociatedObject sao = new SimpleAssociatedObject();
			removed.addToListFor(sourceToken, obj, pro, sao);
			sao.setAssociation(AssociationKey.SOURCE_URI, sourceURI);
			sao.setAssociation(AssociationKey.FILE_LOCATION, locFac
				.getFileLocation());
			sao.setAssociation(AssociationKey.RETIRED_BY, sourceURI);
		}

		public void removeAll(String tokenName, CDOMObject obj)
		{
			Set<PrereqObject> children =
					added.getTertiaryKeySet(tokenName, obj);
			for (PrereqObject child : children)
			{
				List<AssociatedPrereqObject> assoc =
						added.getListFor(tokenName, obj, child);
				for (AssociatedPrereqObject apo : assoc)
				{
					if (extractURI != null)
					{
						if (!extractURI.equals(apo
							.getAssociation(AssociationKey.SOURCE_URI)))
						{
							continue;
						}
					}
					apo.setAssociation(AssociationKey.IRRELEVANT, Boolean.TRUE);
				}
			}
			globalRemoveSet.addToListFor(tokenName, obj, sourceURI);
		}

		/*
		 * TODO This is basically only used for Aggregator cleanup - change Agg
		 * cleanup method?
		 */
		public Set<PCGraphEdge> getChildLinksFromToken(String tokenName,
			CDOMObject obj)
		{
			return null;
		}

		public <T extends PrereqObject & LSTWriteable> AssociatedChanges<T> getChangesFromToken(
			String tokenName, CDOMObject pct, Class<T> name)
		{
			return new EditorGraphChanges<T>(tokenName, pct, name);
		}

		public class EditorGraphChanges<T> implements AssociatedChanges<T>
		{

			private final String token;

			private final CDOMObject source;

			private final Class<T> childClass;

			public EditorGraphChanges(String tokenName, CDOMObject cdo,
				Class<T> name)
			{
				token = tokenName;
				childClass = name;
				source = cdo;
			}

			public Collection<LSTWriteable> getAdded()
			{
				return getSimple(added);
			}

			private Collection<LSTWriteable> getSimple(
				TripleKeyMapToList<String, CDOMObject, PrereqObject, AssociatedPrereqObject> mtl)
			{
				Collection<LSTWriteable> coll =
						new WeightedCollection<LSTWriteable>(
							TokenUtilities.WRITEABLE_SORTER);
				Set<PrereqObject> children =
						mtl.getTertiaryKeySet(token, source);
				if (children == null)
				{
					return coll;
				}
				for (PrereqObject child : children)
				{
					List<AssociatedPrereqObject> assocs =
							mtl.getListFor(token, source, child);
					if (!childClass.isAssignableFrom(child.getClass())
						&& !(child instanceof CDOMReference && ((CDOMReference) child)
							.getReferenceClass().equals(childClass)))
					{
						continue;
					}
					for (AssociatedPrereqObject assoc : assocs)
					{
						Boolean irrel =
								assoc.getAssociation(AssociationKey.IRRELEVANT);
						if (irrel != null && irrel.booleanValue())
						{
							continue;
						}
						if (extractURI != null)
						{
							if (!extractURI.equals(assoc
								.getAssociation(AssociationKey.SOURCE_URI)))
							{
								continue;
							}
						}
						coll.add((LSTWriteable) child);
					}
				}
				return coll;
			}

			public MapToList<LSTWriteable, AssociatedPrereqObject> getAddedAssociations()
			{
				TreeMapToList<LSTWriteable, AssociatedPrereqObject> coll =
						new TreeMapToList<LSTWriteable, AssociatedPrereqObject>(
							TokenUtilities.WRITEABLE_SORTER);
				Set<PrereqObject> children =
						added.getTertiaryKeySet(token, source);
				if (children == null)
				{
					return coll;
				}
				for (PrereqObject child : children)
				{
					List<AssociatedPrereqObject> assocs =
							added.getListFor(token, source, child);
					if (childClass != null
						&& !childClass.isAssignableFrom(child.getClass())
						&& !(child instanceof CDOMReference && ((CDOMReference) child)
							.getReferenceClass().equals(childClass)))
					{
						continue;
					}
					for (AssociatedPrereqObject assoc : assocs)
					{
						Boolean irrel =
								assoc.getAssociation(AssociationKey.IRRELEVANT);
						if (irrel != null && irrel.booleanValue())
						{
							continue;
						}
						if (extractURI != null)
						{
							if (!extractURI.equals(assoc
								.getAssociation(AssociationKey.SOURCE_URI)))
							{
								continue;
							}
						}
						coll.addToListFor((LSTWriteable) child, assoc);
					}
				}
				return coll;
			}

			public Collection<LSTWriteable> getRemoved()
			{
				return getSimple(removed);
			}

			public MapToList<LSTWriteable, AssociatedPrereqObject> getRemovedAssociations()
			{
				return null;
			}

			public boolean hasAddedItems()
			{
				return getPossess(added);
			}

			private boolean getPossess(
				TripleKeyMapToList<String, CDOMObject, PrereqObject, AssociatedPrereqObject> mtl)
			{
				Set<PrereqObject> children =
						mtl.getTertiaryKeySet(token, source);
				if (children == null)
				{
					return false;
				}
				for (PrereqObject child : children)
				{
					List<AssociatedPrereqObject> assocs =
							mtl.getListFor(token, source, child);
					if (!childClass.isAssignableFrom(child.getClass())
						&& !(child instanceof CDOMReference && ((CDOMReference) child)
							.getReferenceClass().equals(childClass)))
					{
						continue;
					}
					for (AssociatedPrereqObject assoc : assocs)
					{
						Boolean irrel =
								assoc.getAssociation(AssociationKey.IRRELEVANT);
						if (irrel != null && irrel.booleanValue())
						{
							continue;
						}
						if (extractURI != null)
						{
							if (!extractURI.equals(assoc
								.getAssociation(AssociationKey.SOURCE_URI)))
							{
								continue;
							}
						}
						return true;
					}
				}
				return false;
			}

			public boolean hasRemovedItems()
			{
				return getPossess(removed);
			}

			public boolean includesGlobalClear()
			{
				return globalRemoveSet
					.containsInList(token, source, extractURI);
			}
		}

		public void setLine(int i)
		{
			locFac.setLine(i);
		}

		public void decommit()
		{
			globalRemoveSet.clear();
			added.clear();
			removed.clear();
		}
	}
}
