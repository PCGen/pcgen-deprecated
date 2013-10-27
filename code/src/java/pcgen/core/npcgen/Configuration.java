/*
 * Configuration.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.core.npcgen;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.spell.Spell;
import pcgen.util.WeightedList;

/**
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class Configuration
{
	private static List<Configuration> theConfigurations = new ArrayList<Configuration>();
	private static Configuration theDefaultConfiguration = new Configuration();
	
	private GameMode theMode = null;
	
	private List<GeneratorOption> theGeneratorOptions = new ArrayList<GeneratorOption>();
	private Map<String, ClassData> theClassData = new HashMap<String, ClassData>();
	
	private static File optionsDir = new File(SettingsHandler.getPcgenSystemDir()
		+ File.separator + "npcgen"  //$NON-NLS-1$ 
		+ File.separator + "options"); //$NON-NLS-1$
	
//	private static File optionsDir = new File(Globals.getDefaultPath() 
//											+ File.separator + "system" //$NON-NLS-1$
//											+ File.separator + "npcgen"  //$NON-NLS-1$ 
//											+ File.separator + "options"); //$NON-NLS-1$

	private static File classDataDir = new File(SettingsHandler.getPcgenSystemDir()
		+ File.separator + "npcgen"  //$NON-NLS-1$ 
		+ File.separator + "classdata"); //$NON-NLS-1$
	
//	private static File classDataDir = new File(Globals.getDefaultPath() 
//			+ File.separator + "system" //$NON-NLS-1$
//			+ File.separator + "npcgen"  //$NON-NLS-1$ 
//			+ File.separator + "classdata"); //$NON-NLS-1$

	public static Configuration get( final GameMode aMode )
	{
		for ( final Configuration config : theConfigurations )
		{
			if ( config.theMode.equals( aMode ) )
			{
				return config;
			}
		}

		final Configuration config = new Configuration();
		config.theMode = aMode;
		
		try
		{
			final OptionsParser parser = new OptionsParser( aMode );
			
			final File[] fileNames = optionsDir.listFiles(new FilenameFilter() {
				public boolean accept(final File aDir, final String aName)
				{
					if (aName.toLowerCase().endsWith(".xml")) //$NON-NLS-1$
					{
						return true;
					}
					return false;
				}
			});
	
			for ( final File file : fileNames )
			{
				final List<GeneratorOption> options = parser.parse(file);
				config.theGeneratorOptions.addAll(options);
			}
			
			final ClassDataParser classParser = new ClassDataParser( aMode );
			
			final File[] classDataFiles = classDataDir.listFiles(new FilenameFilter() {
				public boolean accept(final File aDir, final String aName)
				{
					if (aName.toLowerCase().endsWith(".xml")) //$NON-NLS-1$
					{
						return true;
					}
					return false;
				}
			});
	
			for ( final File file : classDataFiles )
			{
				final List<ClassData> classData = classParser.parse(file);
				for ( final ClassData cd : classData )
				{
					config.theClassData.put(cd.getClassKey(), cd);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return theDefaultConfiguration;
		}
		
		theConfigurations.add(config);
		return config;
	}
	
	public List<AlignGeneratorOption> getAlignmentOptions()
	{
		final List<AlignGeneratorOption> ret = new ArrayList<AlignGeneratorOption>();
		
		for ( final GeneratorOption opt : theGeneratorOptions )
		{
			if ( opt instanceof AlignGeneratorOption )
			{
				ret.add((AlignGeneratorOption)opt);
			}
		}
		for (final PCAlignment align : SettingsHandler.getGame()
			.getUnmodifiableAlignmentList())
		{
			boolean included = false;
			for (AlignGeneratorOption option : ret)
			{
				if (option.getName().equals(align.getDisplayName()))
				{
					included = true;
					break;
				}
			}
			if (!align.getDisplayName().equals(Constants.s_NONE) && !included)
			{
				final AlignGeneratorOption opt = new AlignGeneratorOption();
				opt.setName(align.getDisplayName());
				opt.addChoice(1, align.getKeyName());
				ret.add(opt);
			}
		}
		return ret;
	}
	
	public List<RaceGeneratorOption> getRaceOptions()
	{
		final List<RaceGeneratorOption> ret = new ArrayList<RaceGeneratorOption>();
		
		for ( final GeneratorOption opt : theGeneratorOptions )
		{
			if ( opt instanceof RaceGeneratorOption )
			{
				ret.add((RaceGeneratorOption)opt);
			}
		}
		for ( final Race race : Globals.getAllRaces() )
		{
			final RaceGeneratorOption opt = new RaceGeneratorOption();
			opt.setName(race.getDisplayName());
			opt.addChoice(1, race.getKeyName());
			ret.add( opt );
		}
		return ret;
	}

	public List<GenderGeneratorOption> getGenderOptions()
	{
		final List<GenderGeneratorOption> ret = new ArrayList<GenderGeneratorOption>();
		
		for ( final GeneratorOption opt : theGeneratorOptions )
		{
			if ( opt instanceof GenderGeneratorOption )
			{
				ret.add((GenderGeneratorOption)opt);
			}
		}
		for ( final String gender : Globals.getAllGenders() )
		{
			final GenderGeneratorOption opt = new GenderGeneratorOption();
			opt.setName(gender);
			opt.addChoice(1, gender);
			ret.add(opt);
		}
		return ret;
	}

	public List<ClassGeneratorOption> getClassOptions()
	{
		final List<ClassGeneratorOption> ret = new ArrayList<ClassGeneratorOption>();
		
		for ( final GeneratorOption opt : theGeneratorOptions )
		{
			if ( opt instanceof ClassGeneratorOption )
			{
				ret.add((ClassGeneratorOption)opt);
			}
		}
		for ( final PCClass pcClass : Globals.getClassList() )
		{
			final ClassGeneratorOption opt = new ClassGeneratorOption();
			opt.setName(pcClass.getDisplayName());
			opt.addChoice(1, pcClass.getKeyName());
			ret.add(opt);
		}
		return ret;
	}

	public List<LevelGeneratorOption> getLevelOptions()
	{
		final List<LevelGeneratorOption> ret = new ArrayList<LevelGeneratorOption>();
		
		for ( final GeneratorOption opt : theGeneratorOptions )
		{
			if ( opt instanceof LevelGeneratorOption )
			{
				ret.add((LevelGeneratorOption)opt);
			}
		}
		for ( int i = 1; i <= 20; i++ )
		{
			final LevelGeneratorOption opt = new LevelGeneratorOption();
			opt.setName(String.valueOf(i));
			opt.addChoice(1, String.valueOf(i));
			ret.add(opt);
		}
		return ret;
	}
	
	public WeightedList<String> getStatWeights(final String aKey)
	{
		ClassData data = theClassData.get(aKey);
		if ( data == null )
		{
			data = new ClassData(Constants.EMPTY_STRING);
		}
		return data.getStatWeights();
	}
	
	public WeightedList<SkillChoice> getSkillWeights(final String aKey)
	{
		ClassData data = theClassData.get(aKey);
		if ( data == null )
		{
			data = new ClassData(Constants.EMPTY_STRING);
		}
		return data.getSkillWeights();
	}
	
	public WeightedList<Ability> getAbilityWeights( final String aKey, final AbilityCategory aCategory )
	{
		ClassData data = theClassData.get(aKey);
		if ( data == null )
		{
			data = new ClassData(Constants.EMPTY_STRING);
		}
		return data.getAbilityWeights(aCategory);
	}
	
	public WeightedList<Deity> getDeityWeights( final String aKey )
	{
		ClassData data = theClassData.get( aKey );
		if ( data == null )
		{
			data = new ClassData(Constants.EMPTY_STRING);
		}
		return data.getDeityWeights();
	}

	public WeightedList<Domain> getDomainWeights(final String aDeityKey, final String aClassKey ) 
	{
		ClassData data = theClassData.get( aClassKey );
		if ( data == null )
		{
			data = new ClassData(Constants.EMPTY_STRING);
		}
		return data.getDomainWeights(aDeityKey);
	}

	public WeightedList<Spell> getKnownSpellWeights(final String aClassKey, final int aLevel)
	{
		ClassData data = theClassData.get( aClassKey );
		if ( data == null )
		{
			data = new ClassData( Constants.EMPTY_STRING );
		}
		return data.getKnownSpellWeights(aLevel);
	}

	public WeightedList<Spell> getPreparedSpellWeights(final String aClassKey, final int aLevel)
	{
		ClassData data = theClassData.get( aClassKey );
		if ( data == null )
		{
			data = new ClassData( Constants.EMPTY_STRING );
		}
		return data.getPreparedSpellWeights(aLevel);
	}

	public WeightedList<String> getSubClassWeights(final String aClassKey)
	{
		ClassData data = theClassData.get( aClassKey );
		if ( data == null )
		{
			data = new ClassData( Constants.EMPTY_STRING );
		}
		return data.getSubClassWeights();
	}
}
