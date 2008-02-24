package pcgen.cdom.inst;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import pcgen.cdom.base.CDOMObject;

public class CDOMSpellProgressionInfo extends CDOMObject
{

	/*
	 * FUTURETYPESAFETY Currently can't do better than String in knownMap,
	 * castMap and specialtyKnownMap, because each one of these can be a
	 * formula, or some special gunk for Psionicists (can we clean up the +d??)
	 */

	/**
	 * This is a Progression of KNOWN spells.
	 */
	private Progression knownProgression = null;

	/**
	 * This is a Progression KNOWN spells added by a specialty.
	 */
	private Progression specialtyKnownProgression = null;

	/**
	 * This is a Progression of CAST spells.
	 */
	private Progression castProgression = null;

	/**
	 * Sets the Known spells for the given class level for this
	 * SpellProgression. The given character level must be greater than or equal
	 * to one.
	 * 
	 * Note that this is a SET (not an ADD) and will therefore OVERWRITE a KNOWN
	 * spell progression for the given class level if one is already present
	 * within this SpellProgression.
	 * 
	 * @param iLevel
	 *            The class level for which the given known spell progression
	 *            applies.
	 * @param aList
	 *            The known spell progression for the given class level.
	 * @return The previously set KNOWN spell progression for the given class
	 *         level; null if no KNOWN spell progression was previously set.
	 */
	public List<String> setKnown(int iLevel, List<String> aList) {
		if (knownProgression == null) {
			knownProgression = new Progression();
		}
		return knownProgression.setProgression(iLevel, aList);
	}

	/**
	 * Returns true if this SpellProgression contains a KNOWN spell progression.
	 * (this is not required, e.g. OGL Wizards do not have a KNOWN spell
	 * progression, rather they are limited by what spells are in their
	 * spellbook(s))
	 * 
	 * @return True if this SpellProgression contains a known spell progression;
	 *         false otherwise.
	 */
	public boolean hasKnownProgression() {
		return knownProgression != null && knownProgression.hasProgression();
	}

	/**
	 * Returns the known spell progression for the given class level. If this
	 * SpellProgression does not contain a KNOWN spell progression or if the
	 * given class level is not high enough to have known spells, this method
	 * returns null.
	 * 
	 * This method is value-semantic. Ownership of the returned List is
	 * transferred to the calling object (The returned list can be modified
	 * without impacting the internal contents of this SpellProgression)
	 * 
	 * @param aInt
	 *            The class level for which the known spell progression should
	 *            be returned.
	 * @return The known spell progression for the given class level, or null if
	 *         there is no known spell progression for the given class level.
	 */
	public List<String> getKnownForLevel(int aLevel) {
		return knownProgression == null ? null : knownProgression
				.getProgressionForLevel(aLevel);
	}

	/**
	 * Sets the KNOWN SPECIALTY spells for the given class level for this
	 * SpellProgression. The given character level must be greater than or equal
	 * to one.
	 * 
	 * Note that this is a SET (not an ADD) and will therefore OVERWRITE a KNOWN
	 * SPECIALTY spell progression for the given class level if one is already
	 * present within this SpellProgression.
	 * 
	 * @param iLevel
	 *            The class level for which the given known specialty spell
	 *            progression applies.
	 * @param aList
	 *            The known specialty spell progression for the given class
	 *            level.
	 * @return The previously set KNOWN SPECIALTY spell progression for the
	 *         given class level; null if no KNOWN SPECIALTY spell progression
	 *         was previously set.
	 */
	public List<String> setSpecialtyKnown(int aLevel, List<String> aList) {
		if (specialtyKnownProgression == null) {
			specialtyKnownProgression = new Progression();
		}
		return specialtyKnownProgression.setProgression(aLevel, aList);
	}

	/**
	 * Returns true if this SpellProgression contains KNOWN SPECIALTY spell
	 * progressions. (this is not required, e.g. most 3.0SRD classes do not have
	 * a KNOWN SPECIALTY list)
	 * 
	 * @return True if this SpellProgression contains a known specialty spell
	 *         progression; false otherwise.
	 */
	public boolean hasSpecialtyKnownProgression() {
		return specialtyKnownProgression != null
				&& specialtyKnownProgression.hasProgression();
	}

	/**
	 * Returns the known specialty spell progression for the given class level.
	 * If this SpellProgression does not contain a KNOWN SPECIALTY spell
	 * progression or if the given class level is not high enough to have
	 * entered the known specialty spell progression, this method returns null.
	 * 
	 * This method is value-semantic. Ownership of the returned List is
	 * transferred to the calling object (The returned list can be modified
	 * without impacting the internal contents of this SpellProgression)
	 * 
	 * @param aInt
	 *            The class level for which the known specialty spell
	 *            progression should be returned.
	 * @return The known specialty spell progression for the given class level,
	 *         or null if there is no known specialty spell progression for the
	 *         given class level.
	 */
	public List<String> getSpecialtyKnownForLevel(int aLevel) {
		return specialtyKnownProgression == null ? null
				: specialtyKnownProgression.getProgressionForLevel(aLevel);
	}

	/**
	 * Sets the CAST spells for the given class level for this SpellProgression.
	 * The given character level must be greater than or equal to one.
	 * 
	 * Note that this is a SET (not an ADD) and will therefore OVERWRITE a CAST
	 * spell progression for the given class level if one is already present
	 * within this SpellProgression.
	 * 
	 * @param iLevel
	 *            The class level for which the given CAST spell progression
	 *            applies.
	 * @param aList
	 *            The CAST spell progression for the given class level.
	 * @return The previously set CAST spell progression for the given class
	 *         level; null if no CAST spell progression was previously set.
	 */
	public List<String> setCast(int aLevel, List<String> aList) {
		if (castProgression == null) {
			castProgression = new Progression();
		}
		return castProgression.setProgression(aLevel, aList);
	}

	/**
	 * Returns true if this SpellProgression contains CAST spell progressions.
	 * (this is not required, but would be a bit strange to be empty)
	 * 
	 * @return True if this SpellProgression contains a CAST spell progression;
	 *         false otherwise.
	 */
	public boolean hasCastProgression() {
		return castProgression != null && castProgression.hasProgression();
	}

	/**
	 * Returns the CAST spell progression for the given class level. If this
	 * SpellProgression does not contain a CAST spell progression or if the
	 * given class level is not high enough to have CAST spells, this method
	 * returns null.
	 * 
	 * This method is value-semantic. Ownership of the returned List is
	 * transferred to the calling object (The returned list can be modified
	 * without impacting the internal contents of this SpellProgression)
	 * 
	 * @param aInt
	 *            The class level for which the CAST spell progression should be
	 *            returned.
	 * @return The CAST spell progression for the given class level, or null if
	 *         there is no CAST spell progression for the given class level.
	 */
	public List<String> getCastForLevel(int aLevel) {
		return castProgression == null ? null : castProgression
				.getProgressionForLevel(aLevel);
	}

	/**
	 * Stores an individual Progression within this SpellProgressionInfo. Broken
	 * out as a separate class in order to maintain consistent behavior and
	 * avoid a ton of redundant code within SpellProgressionInfo.
	 */
	private static class Progression implements Cloneable {
		/**
		 * This is a Map of spells. The Integer key is the Class level, the
		 * value is a List of constants or Formula for each SpellLevel.
		 * 
		 * The progressionMap must not contain any null values.
		 */
		private TreeMap<Integer, List<String>> progressionMap = null;

		/**
		 * Sets the spells for the given class level for this Progression. The
		 * given character level must be greater than or equal to one.
		 * 
		 * Note that this is a SET (not an ADD) and will therefore OVERWRITE a
		 * spell progression for the given class level if one is already present
		 * within this Progression.
		 * 
		 * @param iLevel
		 *            The class level for which the given spell progression
		 *            applies.
		 * @param aList
		 *            The spell progression for the given class level.
		 * @return The previously set spell progression for the given class
		 *         level; null if no spell progression was previously set.
		 */
		public List<String> setProgression(int iLevel, List<String> aList) {
			if (iLevel < 1) {
				throw new IllegalArgumentException(
						"Level must be >= 1 in spell progression");
			}
			if (aList == null) {
				throw new IllegalArgumentException(
						"Cannot add null spell progression list to level "
								+ iLevel);
			}
			if (aList.isEmpty()) {
				throw new IllegalArgumentException(
						"Cannot add empty spell progression list to level "
								+ iLevel);
			}
			if (aList.contains(null)) {
				throw new IllegalArgumentException(
						"Cannot have null value in spell progrssion list in level "
								+ iLevel);
			}
			if (progressionMap == null) {
				progressionMap = new TreeMap<Integer, List<String>>();
			}
			return progressionMap.put(iLevel, new ArrayList<String>(aList));
		}

		/**
		 * Returns true if this Progression contains a spell progression.
		 * 
		 * @return True if this Progression contains a spell progression; false
		 *         otherwise.
		 */
		public boolean hasProgression() {
			return progressionMap != null;
		}

		/**
		 * Returns the spell progression for the given class level. If this
		 * Progression does not contain a spell progression or if the given
		 * class level is not high enough to have spells, this method returns
		 * null.
		 * 
		 * This method is value-semantic. Ownership of the returned List is
		 * transferred to the calling object (The returned list can be modified
		 * without impacting the internal contents of this Progression)
		 * 
		 * @param classLevel
		 *            The class level for which the spell progression should be
		 *            returned.
		 * @return The spell progression for the given class level, or null if
		 *         there is no spell progression for the given class level.
		 */
		public List<String> getProgressionForLevel(int classLevel) {
			List<String> spellProgression = null;
			boolean found = false;
			if(progressionMap != null) {
				Integer key = Integer.valueOf(classLevel);
				if (!progressionMap.containsKey(key)) {
					//No spellcasting at level key, check previous levels
					if (progressionMap.firstKey() < classLevel) {
						key = progressionMap.headMap(key).lastKey();
						found = true;
					}
				} else {
					found = true;
				}
				if(found) {
					List<String> list = progressionMap.get(key);
					spellProgression = new ArrayList<String>(list);
				}
			}
			return spellProgression;
			
		}

		/**
		 * Clones this Progression object. A semi-deep (or semi-shallow,
		 * depending on one's point of view) clone is performed, under the
		 * assumption that the cloned object should be allowed to have the
		 * Progression.set* method called without allowing either the original
		 * or the cloned Progression object to accidentally modify the other.
		 * 
		 * There is the assumption, however, that the Lists contained within the
		 * Progression object are never modified, and violation of that semantic
		 * rule either within Progression or by other objects which call the
		 * reference-semantic methods of Progression can render this clone
		 * insufficient.
		 * 
		 * @return A semi-shallow Clone of this Progression object.
		 * @throws CloneNotSupportedException
		 */
		@Override
		public Progression clone() throws CloneNotSupportedException {
			Progression p = (Progression) super.clone();
			if (progressionMap != null) {
				p.progressionMap = new TreeMap<Integer, List<String>>(
						progressionMap);
			}
			return p;
		}
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
		if (o instanceof CDOMSpellProgressionInfo)
		{
			CDOMSpellProgressionInfo other = (CDOMSpellProgressionInfo) o;
			//TODO Compare the progressions
			return other.isCDOMEqual(this) && other.equalsPrereqObject(this);
		}
		return false;
	}
	
}
