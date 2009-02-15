/*
 * GeneratorManager.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Sep 11, 2008, 5:22:51 PM
 */
package pcgen.gui.generator;

import java.awt.Color;
import java.awt.Component;
import java.beans.BeanDescriptor;
import java.beans.Customizer;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyEditorSupport;
import java.beans.SimpleBeanInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.DefaultJDOMFactory;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.AbstractFilter;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pcgen.base.util.TripleKeyMap;
import pcgen.base.util.WeightedCollection;
import pcgen.cdom.enumeration.Gender;
import pcgen.core.SettingsHandler;
import pcgen.gui.facade.AbilityCatagoryFacade;
import pcgen.gui.facade.AbilityFacade;
import pcgen.gui.facade.AlignmentFacade;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.DataSetFacade;
import pcgen.gui.facade.GameModeFacade;
import pcgen.gui.facade.InfoFacade;
import pcgen.gui.facade.RaceFacade;
import pcgen.gui.facade.SkillFacade;
import pcgen.gui.generator.ability.AbilityBuild;
import pcgen.gui.generator.ability.MutableAbilityBuild;
import pcgen.gui.tools.SelectionDialog;
import pcgen.gui.tools.SelectionModel;
import pcgen.gui.util.DefaultGenericListModel;
import pcgen.gui.util.GenericListModel;
import pcgen.gui.util.GenericListModelWrapper;
import pcgen.gui.util.event.AbstractGenericListDataWrapper;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class GeneratorManager
{

	public static final class GeneratorType<E>
	{

		private static final Map<String, GeneratorType<?>> typeMap = new HashMap<String, GeneratorType<?>>();
		public static final GeneratorType<WeightedGenerator<AlignmentFacade>> ALIGNMENT = new GeneratorType<WeightedGenerator<AlignmentFacade>>("ALIGNMENT_GENERATOR",
																																				   DefaultAlignmentGenerator.class,
																																				   null,
																																				   SingletonWeightedGenerator.class);
		public static final GeneratorType<WeightedGenerator<Gender>> GENDER = new GeneratorType<WeightedGenerator<Gender>>("GENDER_GENERATOR",
																															  null,
																															  null,
																															  SingletonWeightedGenerator.class);
		public static final GeneratorType<InfoFacadeGenerator<SkillFacade>> SKILL = new GeneratorType<InfoFacadeGenerator<SkillFacade>>("SKILL_GENERATOR",
																																		   DefaultSkillGenerator.class,
																																		   DefaultMutableSkillGenerator.class,
																																		   SingletonInfoFacadeGenerator.class);
		public static final GeneratorType<RollMethod> ROLLMETHOD = new GeneratorType<RollMethod>("ROLL_METHOD",
																									DefaultRollMethod.class,
																									DefaultMutableRollMethod.class,
																									DefaultRollMethod.class);
		public static final GeneratorType<PurchaseMethod> PURCHASEMETHOD = new GeneratorType<PurchaseMethod>("PURCHASE_METHOD",
																												DefaultPurchaseMethod.class,
																												DefaultMutablePurchaseMethod.class,
																												DefaultPurchaseMethod.class);
		public static final GeneratorType<InfoFacadeGenerator<RaceFacade>> RACE = new GeneratorType<InfoFacadeGenerator<RaceFacade>>("RACE_GENERATOR",
																																		DefaultRaceGenerator.class,
																																		DefaultMutableRaceGenerator.class,
																																		SingletonInfoFacadeGenerator.class);
		public static final GeneratorType<InfoFacadeGenerator<ClassFacade>> CLASS = new GeneratorType<InfoFacadeGenerator<ClassFacade>>("CLASS_GENERATOR",
																																		   DefaultClassGenerator.class,
																																		   DefaultMutableClassGenerator.class,
																																		   SingletonInfoFacadeGenerator.class);
		public static final GeneratorType<AbilityBuild> ABILITYBUILD = new GeneratorType<AbilityBuild>("ABILITY_BUILD",
																										  DefaultAbilityBuild.class,
																										  DefaultMutableAbilityBuild.class,
																										  null);
		public static final GeneratorType<InfoFacadeGenerator<AbilityFacade>> ABILITY = new GeneratorType<InfoFacadeGenerator<AbilityFacade>>("ABILITY_GENERATOR",
																																				 DefaultAbilityGenerator.class,
																																				 DefaultMutableAbilityGenerator.class,
																																				 SingletonInfoFacadeGenerator.class);
		private Class<? extends E> baseClass;
		private Class<? extends E> mutableClass;
		private Class<?> singletonClass;
		private String element;

		private GeneratorType(String element,
							   Class<? extends E> baseClass,
							   Class<? extends E> mutableClass,
							   Class<?> singletonClass)
		{
			this.element = element;
			this.baseClass = baseClass;
			this.mutableClass = mutableClass;
			this.singletonClass = singletonClass;
			typeMap.put(element, this);
		}

		public static GeneratorType<?> getGeneratorType(String element)
		{
			return typeMap.get(element);
		}

	}

	private static class DocumentHandler extends DefaultJDOMFactory implements EntityResolver
	{

		private final Map<String, Document> documentMap;
		private final SAXBuilder builder;
		private final XMLOutputter outputter;

		public DocumentHandler()
		{
			documentMap = new HashMap<String, Document>();
			builder = new SAXBuilder();
			builder.setEntityResolver(this);
			builder.setFactory(this);
			outputter = new XMLOutputter();
			outputter.setFormat(Format.getPrettyFormat());
		}

		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
		{
			if (publicId != null)
			{
				if (publicId.equals("PCGEN-GENERATORS"))
				{
					String fileName = SettingsHandler.getPcgenSystemDir() +
							File.separator +
							"generators" +
							File.separator +
							new File(systemId).getName();
					return new InputSource(new FileInputStream(fileName));
				}

			}
			return null;
		}

		public Document getDocument(URI uri)
		{
			String systemId = uri.toString();
			Document document = documentMap.get(systemId);
			if (document == null)
			{
				try
				{
					document = builder.build(systemId);
				}
				catch (JDOMException ex)
				{
					Logging.log(Logging.XML_ERROR, "Failed to created document",
								ex);
				}
				catch (IOException ex)
				{
					Logging.log(Logging.XML_ERROR,
								"Error occured while accessing file",
								ex);
				}
				documentMap.put(systemId, document);
			}
			return document;
		}

		@Override
		public Element element(String name)
		{
			return new GeneratorElement(name);
		}

	}
	private static DocumentHandler documentHandler = new DocumentHandler();
	private static AbstractFilter generatorFilter = new AbstractFilter()
	{

		public boolean matches(Object obj)
		{
			if (obj instanceof Element)
			{
				Element element = (Element) obj;
				String name = element.getName();
				return !name.matches("BUILD") &&
						GeneratorType.getGeneratorType(name) != null;
			}
			return false;
		}

	};
	private static AbstractFilter buildFilter = new AbstractFilter()
	{

		public boolean matches(Object obj)
		{
			if (obj instanceof Element)
			{
				Element element = (Element) obj;
				return element.getName().matches("BUILD");
			}
			return false;
		}

	};
	private static Filter refFilter = new Filter()
	{

		public boolean matches(Object obj)
		{
			if (obj instanceof Element)
			{
				Element element = (Element) obj;
				if (element.getName().endsWith("_REF"))
				{
					return true;
				}
			}
			return false;
		}

	};

	@SuppressWarnings("unchecked")
	private void loadGenerators(Document document)
	{
		if (loadedDocuments.contains(document))
		{
			return;
		}
		GeneratorElement root = (GeneratorElement) document.getRootElement();
		boolean mutable;
		if (root.getName().equals("BUILDSET"))
		{
			mutable = Boolean.parseBoolean(root.getAttributeValue("mutable"));
		}
		else
		{
			mutable = false;
		}

		Iterator<GeneratorElement> elementIterator = document.getDescendants(generatorFilter);
		while (elementIterator.hasNext())
		{
			GeneratorElement element = elementIterator.next();
			loadGenerator(element, mutable);
		}

		elementIterator = document.getDescendants(buildFilter);
		while (elementIterator.hasNext())
		{
			GeneratorElement element = elementIterator.next();
			loadGenerator(element, mutable);
		}
		loadedDocuments.add(document);
	}

	private boolean checkSources(GeneratorElement element)
	{
		@SuppressWarnings("unchecked")
		List<GeneratorElement> sourceElements = element.getChildren("SOURCE");
		if (sourceElements != null)
		{
			Set<String> sources = dataset.getSources();
			for ( Element source : sourceElements)
			{
				if (!sources.contains(source.getText()))
				{
					return false;
				}
			}
		}
		return true;
	}

	private boolean checkReferences(GeneratorElement element)
	{
		@SuppressWarnings("unchecked")
		List<GeneratorElement> refElements = element.getContent(refFilter);
		if (refElements != null)
		{
			Document basedoc = element.getDocument();
			for ( GeneratorElement refElement : refElements)
			{
				String name = refElement.getAttributeValue("name");
				String catagory = refElement.getAttributeValue("catagory");
				GeneratorType<?> type = GeneratorType.getGeneratorType(refElement.getName().replaceFirst("_REF",
																										 ""));
				if (!generatorMap.containsKey(type, catagory, name))
				{
					String generatorUri = refElement.getAttributeValue("uri");
					if (generatorUri != null)
					{
						URI uri = null;
						Document document = null;
						try
						{
							uri = new URI(generatorUri);
							URI baseuri = URI.create(basedoc.getBaseURI());
							uri = baseuri.resolve(uri);
							document = documentHandler.getDocument(uri);
						}
						catch ( URISyntaxException ex)
						{
							Logging.log(Logging.XML_ERROR,
										"Invalid URI specified in:\n" +
										documentHandler.outputter.outputString(refElement) +
										"\nlocated in " +
										basedoc.getBaseURI(), ex);
							return false;
						}
						if (document == null)
						{
							Logging.log(Logging.XML_ERROR,
										"Unable to resolve reference to " + uri);
							return false;
						}
						else
						{
							loadGenerators(document);
						}
					}
				}
				if (!generatorMap.containsKey(type, catagory, name))
				{
					Logging.log(Logging.XML_ERROR, "Invalid Reference in " +
								basedoc.getBaseURI() + ", ignoring " +
								documentHandler.outputter.outputString(element));
					return false;
				}
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private void loadGenerator(GeneratorElement element, boolean mutable)
	{
		String name = element.getAttributeValue("name");
		String catagory = element.getAttributeValue("catagory");
		GeneratorType<?> type = GeneratorType.getGeneratorType(element.getName());
		Class<?> c;
		if (checkSources(element) && checkReferences(element))
		{
			try
			{
				if (mutable)
				{
					c = type.mutableClass;
				}
				else
				{
					c = type.baseClass;
				}
				Object obj = null;
				try
				{
					obj = c.getConstructor(GeneratorManager.class,
										   GeneratorElement.class).newInstance(this,
																			   element);
				}
				catch (NoSuchMethodException ex)
				{
					obj = c.getConstructor(GeneratorElement.class).newInstance(element);
				}
				generatorMap.put(type, catagory, name, obj);
				return;
			}
			catch (Exception ex)
			{
				Logging.errorPrint("Unable to create generator",
								   ex);
			}
		}
		c = type.singletonClass;
		try
		{
			Object obj = null;
			try
			{
				obj = c.getConstructor(String.class).newInstance(name);
			}
			catch (NoSuchMethodException ex)
			{
				obj = c.getConstructor(GeneratorManager.class,
									   String.class).newInstance(this, name);
			}
			generatorMap.put(type, catagory, name, obj);
		}
		catch (Exception ex)
		{
			Logging.errorPrint("Unable to create null-type SingletonGenerator",
							   ex);
		}
	}

	private <T> List<T> getGeneratorList(Document document,
										  GeneratorType<T> type)
	{
		return null;
	}

	private final DataSetFacade dataset;
	// The second and third keys are "catagory" and "name" respectively
	private final TripleKeyMap<GeneratorType<?>, String, String, Object> generatorMap;
	private final Set<Document> loadedDocuments;

	public GeneratorManager(DataSetFacade dataset)
	{
		this.dataset = dataset;
		this.generatorMap = new TripleKeyMap<GeneratorType<?>, String, String, Object>();
		this.loadedDocuments = new HashSet<Document>();

		loadAnyGenerators();
		loadSingletonGenerators();

		GameModeFacade gameMode = dataset.getGameMode();
		Document document = documentHandler.getDocument(gameMode.getGeneratorFile().toURI());
		if (document != null)
		{
			loadGenerators(document);
		}
		for (File file : dataset.getGeneratorFiles())
		{
			document = documentHandler.getDocument(file.toURI());
			if (document != null)
			{
				loadGenerators(document);
			}
		}

	}

	private void loadAnyGenerators()
	{
		loadAnyWeightedGenerator(GeneratorType.ALIGNMENT,
								 dataset.getGameMode().getAlignments());
		loadAnyWeightedGenerator(GeneratorType.GENDER,
								 new DefaultGenericListModel<Gender>(Arrays.asList(Gender.values())));
		AbilityCatagoryManager catagoryManager = new AbilityCatagoryManager();
		catagoryManager.setModel(dataset.getAbilityCatagories());
		loadAnyInfoFacadeGenerator(GeneratorType.RACE, dataset.getRaces());
		loadAnyInfoFacadeGenerator(GeneratorType.CLASS, dataset.getClasses());
	}

	private <T> void loadAnyWeightedGenerator(GeneratorType<WeightedGenerator<T>> type,
											   GenericListModel<T> model)
	{
		AnyWeightedGenerator<T> generator = new AnyWeightedGenerator<T>(model);
		generatorMap.put(type, null, generator.toString(), generator);
	}

	private <T extends InfoFacade> void loadAnyInfoFacadeGenerator(GeneratorType<InfoFacadeGenerator<T>> type,
																	GenericListModel<T> model)
	{
		AnyInfoFacadeGenerator<T> generator = new AnyInfoFacadeGenerator<T>(model);
		generatorMap.put(type, null, generator.toString(), generator);
	}

	private void loadSingletonGenerators()
	{
		loadSingletonWeightedGenerators(GeneratorType.ALIGNMENT,
										dataset.getGameMode().getAlignments());
		loadSingletonWeightedGenerators(GeneratorType.GENDER,
										new DefaultGenericListModel<Gender>(Arrays.asList(Gender.values())));
		loadSingletonInfoFacadeGenerators(GeneratorType.RACE, dataset.getRaces());
		loadSingletonInfoFacadeGenerators(GeneratorType.CLASS,
										  dataset.getClasses());
	}

	private <T> void loadSingletonWeightedGenerators(GeneratorType<WeightedGenerator<T>> type,
													  GenericListModel<T> model)
	{
		SingletonWeightedGeneratorManager<T> manager = new SingletonWeightedGeneratorManager<T>(type);
		manager.setModel(model);
	}

	private <T extends InfoFacade> void loadSingletonInfoFacadeGenerators(GeneratorType<InfoFacadeGenerator<T>> type,
																		   GenericListModel<T> model)
	{
		SingletonInfoFacadeGeneratorManager<T> manager = new SingletonInfoFacadeGeneratorManager<T>(type);
		manager.setModel(model);
	}

	private class AbilityCatagoryManager extends AbstractGenericListDataWrapper<AbilityCatagoryFacade>
	{

		private final Map<AbilityCatagoryFacade, AnyInfoFacadeGenerator<AbilityFacade>> catagoryMap;

		public AbilityCatagoryManager()
		{
			this.catagoryMap = new HashMap<AbilityCatagoryFacade, AnyInfoFacadeGenerator<AbilityFacade>>();
		}

		@Override
		protected void addData(Collection<? extends AbilityCatagoryFacade> data)
		{
			for (AbilityCatagoryFacade abilityCatagoryFacade : data)
			{
				AnyInfoFacadeGenerator<AbilityFacade> generator = new AnyInfoFacadeGenerator<AbilityFacade>(dataset.getAbilities(abilityCatagoryFacade));
				generatorMap.put(GeneratorType.ABILITY,
								 abilityCatagoryFacade.getName(),
								 generator.toString(), generator);
				catagoryMap.put(abilityCatagoryFacade, generator);
			}
		}

		@Override
		protected void removeData(Collection<? extends AbilityCatagoryFacade> data)
		{
			for (AbilityCatagoryFacade abilityCatagoryFacade : data)
			{
				generatorMap.remove(GeneratorType.ABILITY,
									abilityCatagoryFacade.getName(),
									catagoryMap.remove(abilityCatagoryFacade).toString());
			}
		}

	}

	private class SingletonWeightedGeneratorManager<E> extends AbstractGenericListDataWrapper<E>
	{

		private final GeneratorType<? extends Generator<E>> type;

		public SingletonWeightedGeneratorManager(GeneratorType<? extends Generator<E>> type)
		{
			this.type = type;
		}

		@Override
		protected void addData(Collection<? extends E> data)
		{
			for (E e : data)
			{
				SingletonWeightedGenerator<E> generator = createGenerator(e);
				generatorMap.put(type, null, generator.toString(),
								 generator);
			}
		}

		protected SingletonWeightedGenerator<E> createGenerator(E item)
		{
			return new SingletonWeightedGenerator<E>(item);
		}

		@Override
		protected void removeData(Collection<? extends E> data)
		{
			for (E e : data)
			{
				generatorMap.remove(type, null, e.toString());
			}
		}

	}

	private class SingletonInfoFacadeGeneratorManager<E extends InfoFacade>
			extends SingletonWeightedGeneratorManager<E>
	{

		public SingletonInfoFacadeGeneratorManager(GeneratorType<? extends Generator<E>> type)
		{
			super(type);
		}

		@Override
		protected SingletonWeightedGenerator<E> createGenerator(E item)
		{
			return new SingletonInfoFacadeGenerator<E>(item);
		}

	}

	public static final void main(String[] arg)
	{

		File file = new File("build/classes/generators/DefaultStandardGenerators.xml");

	}

	public static <T extends InfoFacade> InfoFacadeGenerator<T> createSingletonGenerator(T item)
	{
		return new SingletonInfoFacadeGenerator<T>(item);
	}

	private static Document getCustomGeneratorDocument(DataSetFacade data,
														 String generatorType)
	{
		File file = new File(SettingsHandler.getPcgenCustomDir(),
							 data.getGameMode() + File.separator +
							 "custom" + generatorType + "s.xml");
		Document document = null;
		if (!file.exists())
		{
			Element root = new Element("GENERATORSET");
			root.setAttribute("mutable", "true");
			DocType type = new DocType("GENERATORSET", "PCGEN-GENERATORS",
									   generatorType + ".dtd");
			document = new Document(root, type, file.toURI().toString());
		}
		else
		{
		// document = buildDocument(file);
		}
		return document;
	}

	public static MutablePurchaseMethod createMutablePurchaseModeGenerator(String name,
																			 PurchaseMethod template)
	{
		GeneratorElement generatorElement = new GeneratorElement("GENERATOR");
		generatorElement.setAttribute("name", name).
				setAttribute("points", "0");
		Element cost = new Element("COST");
		cost.setAttribute("score", "0").setText("0");

		MutablePurchaseMethod generator = new DefaultMutablePurchaseMethod(generatorElement);
		if (template != null)
		{
			int min = template.getMinScore();
			int max = template.getMaxScore();
			generator.setMinScore(min);
			generator.setMaxScore(max);
			generator.setPoints(template.getPoints());
			for (int i = min; i <= max; i++)
			{
				generator.setScoreCost(i, template.getScoreCost(i));
			}
		}
		return generator;
	}

	public static MutableRollMethod createMutableStandardModeGenerator(String name,
																		 RollMethod template)
	{
		GeneratorElement generatorElement = new GeneratorElement("GENERATOR");
		generatorElement.setAttribute("name", name).setAttribute("assignable",
																 "true");
		for (int x = 0; x < 6; x++)
		{
			generatorElement.addContent(new Element("STAT"));
		}

		DefaultMutableRollMethod generator = new DefaultMutableRollMethod(generatorElement);
		if (template != null)
		{
			generator.setAssignable(template.isAssignable());
			Collections.copy(generator.diceExpressions,
							 template.getDiceExpressions());
		}
		return generator;
	}

	public static <T extends InfoFacade> MutableInfoFacadeGenerator<T> createMutableFacadeGenerator(GeneratorType<InfoFacadeGenerator<T>> type,
																									  String name,
																									  DataSetFacade data,
																									  MutableInfoFacadeGenerator<T> template)
	{
		Document document = null;// = getCustomGeneratorDocument(data,
		//type.name);
		if (document != null)
		{

			Element generatorElement = new Element("GENERATOR");
			generatorElement.setAttribute("name", name).
					setAttribute("type", "random");
			document.getRootElement().addContent(generatorElement);
			try
			{
				MutableInfoFacadeGenerator<T> generator = (MutableInfoFacadeGenerator<T>) type.mutableClass.getConstructor(Element.class,
																														   DataSetFacade.class).newInstance(generatorElement,
																																							data);
				if (template != null)
				{
					generator.setRandomOrder(template.isRandomOrder());
					for (T facade : template.getAll())
					{
						generator.setWeight(facade,
											template.getWeight(facade));
					}
				}
				return generator;
			}
			catch (Exception ex)
			{
				Logging.errorPrint("Unable to parse " + document.getBaseURI(),
								   ex);
			}
		}

		return null;
	}

	private class GeneratorSelectionModel<T> implements SelectionModel<T>
	{

		private final GeneratorType<T> type;
		private final Map<T, T> copyMap;
		private GeneratorBeanInfo info;
		private Customizer customizer;

		public GeneratorSelectionModel(GeneratorType<T> type)
		{

			this.type = type;
			this.copyMap = new HashMap<T, T>();

			try
			{
				this.info = (GeneratorBeanInfo) Introspector.getBeanInfo(type.baseClass);
				this.customizer = (Customizer) info.getBeanDescriptor().getCustomizerClass().newInstance();
			}
			catch (IntrospectionException ex)
			{
				Logging.errorPrint("GeneratorBeanInfo for " + type.baseClass +
								   " not found", ex);
				this.info = null;
				this.customizer = null;
			}
			catch (Exception ex)
			{
				Logging.errorPrint("Customizer for " + type.baseClass +
								   " not found", ex);
				this.customizer = null;
			}
		}

		@SuppressWarnings("unchecked")
		public GenericListModel<T> getAvailableList()
		{
			DefaultGenericListModel<T> model = new DefaultGenericListModel<T>();
			for (Object generator : generatorMap.values(type, null))
			{
				model.addElement((T) generator);
			}
			return model;
		}

		@SuppressWarnings("unchecked")
		public GenericListModel<T> getSelectedList()
		{
			DefaultGenericListModel<T> model = new DefaultGenericListModel<T>();
			String generatorNames = SettingsHandler.getSelectedGenerators(dataset.getGameMode() +
																		  "." +
																		  type.element.toLowerCase());
			for (String name : generatorNames.split("\\|"))
			{
				Object generator = generatorMap.get(type, null, name);
				if (generator == null)
				{
					try
					{
						generator = type.singletonClass.getConstructor(String.class).newInstance(name);
					}
					catch (Exception ex)
					{
						Logging.errorPrint("Unable to create null-type SingletonGenerator",
										   ex);
					}
				}
				if (generator != null)
				{
					model.addElement((T) generator);
				}
			}
			return model;
		}

		public void setAvailableList(GenericListModel<T> list)
		{
			for (Object generator : generatorMap.values(type, null))
			{
				generatorMap.remove(type, null, generator.toString());
			}
			for (int i = 0; i < list.getSize(); i++)
			{
				T generator = list.getElementAt(i);
				generatorMap.put(type, null, generator.toString(), generator);
			}
		}

		public void setSelectedList(GenericListModel<T> list)
		{
			String prop = dataset.getGameMode() + type.element.toLowerCase();
			String generators = StringUtils.join(new GenericListModelWrapper<T>(list),
												 "|");
			SettingsHandler.setSelectedGenerators(prop, generators);
		}

		@SuppressWarnings("unchecked")
		public Component getCustomizer(Component currentItemPanel,
										T selectedItem)
		{
			if (info.isNullType(selectedItem))
			{
				return null;
			}
			if (isMutable(selectedItem))
			{
				if (!copyMap.containsKey(selectedItem))
				{
					try
					{
						Class<?> c = type.mutableClass;
						T item = null;
						try
						{
							item = (T) c.getConstructor(type.baseClass).newInstance(selectedItem);
						}
						catch (NoSuchMethodException ex)
						{
							item = (T) c.getConstructor(GeneratorManager.class,
														type.baseClass).newInstance(GeneratorManager.this,
																					selectedItem);
						}
						copyMap.put(selectedItem, item);
					}
					catch (Exception ex)
					{
						Logging.errorPrint("Error while copying generator", ex);
					}
				}
				selectedItem = copyMap.get(selectedItem);
			}
			if (customizer != null)
			{
				customizer.setObject(selectedItem);
			}
			return (Component) customizer;
		}

		public T createMutableItem(SelectionDialog<T> selectionDialog,
									T templateItem)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public boolean isMutable(T item)
		{
			return item instanceof Mutable;
		}

		public boolean isAddable(T item)
		{
			return true;
		}

		public boolean isCopyable(T item)
		{
			return !info.isNullType(item);
		}

		public Color getItemColor(T item)
		{
			if (isMutable(item))
			{
				return Color.BLUE;
			}
			if (info.isNullType(item))
			{
				return Color.GRAY;
			}
			return Color.BLACK;
		}

		public Properties getDisplayProperties()
		{
			return info.getDisplayProperties();
		}

	}

	private abstract static class GeneratorBeanInfo extends SimpleBeanInfo
	{

		public abstract Properties getDisplayProperties();

		public abstract boolean isNullType(Object bean);

	}

	private class DefaultCharacterBuild
	{
	}

	public static class DefaultCharacterBuildEditor
			extends PropertyEditorSupport
	{

		@Override
		public Component getCustomEditor()
		{
			DefaultCharacterBuild build = (GeneratorManager.DefaultCharacterBuild) this.getValue();

			return super.getCustomEditor();
		}

	}

	private class DefaultCharacterBuilder implements CharacterBuild
	{

		private WeightedGenerator<AlignmentFacade> alignmentGenerator;

		public DefaultCharacterBuilder(Element element) throws MissingDataException
		{

		}

		public Generator<AlignmentFacade> getAlignmentGenerator()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public Generator<Gender> getGenderGenerator()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public Generator<Integer> getStatGenerator()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public InfoFacadeGenerator<RaceFacade> getRaceGenerator()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public InfoFacadeGenerator<ClassFacade> getClassGenerator()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public InfoFacadeGenerator<SkillFacade> getSkillGenerator()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public AbilityBuild getAbilityBuild()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public GeneratorManager getGeneratorManager()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private static class AnyWeightedGenerator<E> extends AbstractWeightedGenerator<E>
	{

		private final AbstractGenericListDataWrapper<E> listener = new AbstractGenericListDataWrapper<E>()
		{

			@Override
			protected void addData(Collection<? extends E> data)
			{
				for (E e : data)
				{
					priorityMap.put(e, 1);
				}
			}

			@Override
			protected void removeData(Collection<? extends E> data)
			{
				for (E e : data)
				{
					priorityMap.remove(e);
				}
			}

		};

		public AnyWeightedGenerator(GenericListModel<E> model)
		{
			super("Any");
			listener.setModel(model);
		}

		@Override
		protected E getFacade(String name)
		{
			return null;
		}

	}

	private static class AnyInfoFacadeGenerator<E extends InfoFacade> extends AnyWeightedGenerator<E>
			implements InfoFacadeGenerator<E>
	{

		public AnyInfoFacadeGenerator(GenericListModel<E> model)
		{
			super(model);
		}

		@Override
		protected E getFacade(String name)
		{
			return null;
		}

		public boolean isRandomOrder()
		{
			return true;
		}

		public Set<String> getSources()
		{
			return null;
		}

	}

	private static class DefaultAlignmentGenerator extends AbstractWeightedGenerator<AlignmentFacade>
	{

		//private Queue<E> queue = null;
		public DefaultAlignmentGenerator(GeneratorManager manager,
										  GeneratorElement element) throws MissingDataException
		{
			super(manager, element);
		}

		@Override
		protected AlignmentFacade getFacade(String name)
		{
			return manager.dataset.getGameMode().getAlignment(name);
		}

	}

	private abstract static class AbstractGenerator<E> implements Generator<E>
	{

		private String name;
		protected GeneratorElement element;

		public AbstractGenerator(String name)
		{
			this.name = name;
		}

		public AbstractGenerator(GeneratorElement element)
		{
			this.name = element.getAttributeValue("name");
			this.element = element;
		}

		public List<E> getAll()
		{
			return Collections.emptyList();
		}

		public void reset()
		{

		}

		@Override
		public String toString()
		{
			return name;
		}

	}

	private static abstract class AbstractWeightedGenerator<E> extends AbstractGenerator<E>
			implements WeightedGenerator<E>
	{

		protected Map<E, Integer> priorityMap = new HashMap<E, Integer>();
		private Queue<E> queue = null;
		protected GeneratorManager manager;

		public AbstractWeightedGenerator(String name)
		{
			super(name);
			queue = new LinkedList<E>();
		}

		public AbstractWeightedGenerator(GeneratorManager manager,
										  GeneratorElement element) throws MissingDataException
		{
			super(element);
			this.manager = manager;
			init(element);
		}

		@SuppressWarnings("unchecked")
		protected void init(GeneratorElement element) throws MissingDataException
		{

			element.removeChildren("SOURCE");

			List<Element> children = element.getChildren();
			for (Element child : children)
			{
				String elementName = child.getText();
				Integer weight = Integer.valueOf(child.getAttributeValue("weight"));
				E facade = getFacade(elementName);
				if (facade == null)
				{
					throw new MissingDataException(elementName +
												   " not found in DataSetFacade");
				}
				priorityMap.put(facade, weight);
			}
			queue = new LinkedList<E>();
		}

		protected abstract E getFacade(String name);

		@Override
		public final List<E> getAll()
		{
			return new ArrayList<E>(priorityMap.keySet());
		}

		public E getNext()
		{
			if (priorityMap.isEmpty())
			{
				return null;
			}
			if (queue.isEmpty())
			{
				reset();
			}
			return queue.poll();
		}

		public final int getWeight(E item)
		{
			return priorityMap.get(item);
		}

		public boolean isSingleton()
		{
			return false;
		}

		@Override
		public final void reset()
		{
			queue = createQueue();
		}

		protected Queue<E> createQueue()
		{
			return new RandomWeightedQueue();
		}

		private class RandomWeightedQueue extends WeightedCollection<E>
				implements Queue<E>
		{

			private E element = null;

			public RandomWeightedQueue()
			{
				for (E skill : priorityMap.keySet())
				{
					add(skill, priorityMap.get(skill));
				}
			}

			public boolean offer(E o)
			{
				return false;
			}

			public E poll()
			{
				if (isEmpty())
				{
					return null;
				}
				return remove();
			}

			public E remove()
			{
				E skill = element();
				remove(skill);
				element = null;
				return skill;
			}

			public E peek()
			{
				if (isEmpty())
				{
					return null;
				}
				return element();
			}

			public E element()
			{
				if (element == null)
				{
					element = getRandomValue();
				}
				return element;
			}

		}
	}

	private abstract static class AbstractInfoFacadeGenerator<E extends InfoFacade>
			extends AbstractWeightedGenerator<E> implements InfoFacadeGenerator<E>
	{

		protected boolean randomOrder;

		public AbstractInfoFacadeGenerator(String name)
		{
			super(name);
			this.randomOrder = true;
		}

		@SuppressWarnings("unchecked")
		public AbstractInfoFacadeGenerator(GeneratorManager manager,
											GeneratorElement element) throws MissingDataException
		{
			super(manager, element);
			this.randomOrder = element.getAttributeValue("type").equals("random");
		}

		public boolean isRandomOrder()
		{
			return randomOrder;
		}

		public Set<String> getSources()
		{
			Set<String> sources = new HashSet<String>();
			for (E facade : getAll())
			{
				sources.add(facade.getSource());
			}
			return sources;
		}

		@Override
		protected Queue<E> createQueue()
		{
			if (randomOrder)
			{
				return super.createQueue();
			}
			else
			{
				Comparator<E> comparator = new Comparator<E>()
				{

					public int compare(E o1,
										E o2)
					{
						// compare the numbers in reverse in order for the highest priority
						// Skills to be used first
						return priorityMap.get(o2).compareTo(priorityMap.get(o1));
					}

				};
				Queue<E> queue = new PriorityQueue<E>(priorityMap.size(),
													  comparator);
				queue.addAll(priorityMap.keySet());
				return queue;
			}
		}

	}

	private abstract static class AbstractMutableInfoFacadeGenerator<E extends InfoFacade>
			extends AbstractInfoFacadeGenerator<E> implements MutableInfoFacadeGenerator<E>,
															  ChangeListener
	{

		private AbstractInfoFacadeGenerator<E> generator = null;

		public AbstractMutableInfoFacadeGenerator(GeneratorManager manager,
												   AbstractInfoFacadeGenerator<E> generator) throws MissingDataException
		{
			super(manager, generator.element);
			this.generator = generator;
		}

		public AbstractMutableInfoFacadeGenerator(GeneratorManager manager,
												   GeneratorElement element) throws MissingDataException
		{
			super(manager, element);
			element.addChangeListener(this);
		}

		public void stateChanged(ChangeEvent e)
		{
			try
			{
				init(element);
			}
			catch (MissingDataException ex)
			{
				Logging.errorPrint("Unable to update " + this + "generator", ex);
			}
		}

		protected abstract String getValueName();

		public void setRandomOrder(boolean randomOrder)
		{
			this.randomOrder = randomOrder;
		}

		public void setWeight(E item, int weight)
		{
			priorityMap.put(item, weight);
		}

		public void saveChanges()
		{
			element.removeContent();
			element.setAttribute("type", randomOrder ? "random" : "ordered");
			for (E skill : priorityMap.keySet())
			{
				Element skillElement = new Element(getValueName());
				skillElement.setAttribute("weight",
										  priorityMap.get(skill).toString());
				skillElement.setText(skill.toString());
				element.addContent(skillElement);
			}
			for (String source : getSources())
			{
				element.addContent(new Element("SOURCE").setText(source));
			}
			if (generator != null)
			{
				generator.priorityMap = priorityMap;
				generator.randomOrder = randomOrder;
			}
		}

	}

	private static class SingletonWeightedGenerator<E> extends AbstractGenerator<E>
			implements WeightedGenerator<E>
	{

		protected E item;

		public SingletonWeightedGenerator(String name)
		{
			super(name);
			this.item = null;
		}

		public SingletonWeightedGenerator(E item)
		{
			super(item.toString());
			this.item = item;
		}

		public E getNext()
		{
			return item;
		}

		@Override
		public List<E> getAll()
		{
			if (item == null)
			{
				return null;
			}
			return Collections.singletonList(item);
		}

		public int getWeight(E item)
		{
			if (item != null && item.equals(this.item))
			{
				return 1;
			}
			return 0;
		}

		public boolean isSingleton()
		{
			return true;
		}

	}

	private static class SingletonInfoFacadeGenerator<E extends InfoFacade>
			extends SingletonWeightedGenerator<E>
			implements InfoFacadeGenerator<E>
	{

		public SingletonInfoFacadeGenerator(String name)
		{
			super(name);
		}

		public SingletonInfoFacadeGenerator(E item)
		{
			super(item);
		}

		public Set<String> getSources()
		{
			if (item == null)
			{
				return Collections.emptySet();
			}
			return Collections.singleton(item.getSource());
		}

		public boolean isRandomOrder()
		{
			return false;
		}

	}

	private static class DefaultPurchaseMethod extends AbstractGenerator<Integer>
			implements PurchaseMethod
	{

		protected Vector<Integer> costs;
		protected int points;
		protected int min;

		public DefaultPurchaseMethod(String name)
		{
			super(name);
			this.points = -1;
			this.min = 0;
			this.costs = new Vector<Integer>();
			costs.setSize(1);
		}

		public DefaultPurchaseMethod(GeneratorElement element)
		{
			super(element);
			this.points = Integer.parseInt(element.getAttributeValue("points"));
			TreeMap<Integer, Integer> costMap = new TreeMap<Integer, Integer>();
			@SuppressWarnings("unchecked")
			List<Element> children = element.getChildren();
			for ( Element child : children)
			{
				Integer score = Integer.valueOf(child.getAttributeValue("score"));
				Integer cost = Integer.valueOf(child.getText());
				costMap.put(score, cost);
			}
			this.min = costMap.firstKey();
			this.costs = new Vector<Integer>(costMap.values());
		}

		public Integer getNext()
		{
			return null;
		}

		public int getMinScore()
		{
			return min;
		}

		public int getMaxScore()
		{
			return min + costs.size() - 1;
		}

		public int getScoreCost(int score)
		{
			return costs.get(score - min);
		}

		public int getPoints()
		{
			return points;
		}

	}

	private static class DefaultMutablePurchaseMethod extends DefaultPurchaseMethod
			implements MutablePurchaseMethod
	{

		private DefaultPurchaseMethod generator = null;

		public DefaultMutablePurchaseMethod(DefaultPurchaseMethod generator)
		{
			super(generator.element);
			this.generator = generator;
		}

		public DefaultMutablePurchaseMethod(GeneratorElement element)
		{
			super(element);
		}

		public void setMaxScore(int score)
		{
			costs.setSize(score - min + 1);
		}

		public void setMinScore(int score)
		{
			this.min = score;
		}

		public void setPoints(int points)
		{
			this.points = points;
		}

		public void setScoreCost(int score, int cost)
		{
			costs.set(score - min, cost);
		}

		public void saveChanges()
		{
			element.removeContent();
			element.setAttribute("points", Integer.toString(points));
			int max = getMaxScore();
			for (int x = min; x <= max; x++)
			{
				Element costElement = new Element("COST");
				costElement.setAttribute("score", Integer.toString(x));
				costElement.setText(Integer.toString(getScoreCost(x)));
				element.addContent(costElement);
			}
			if (generator != null)
			{
				generator.costs = costs;
				generator.min = min;
				generator.points = points;
			}
		}

	}

	private static class DefaultRollMethod extends AbstractGenerator<Integer>
			implements RollMethod
	{

		protected boolean assignable;
		protected List<String> diceExpressions;

		public DefaultRollMethod(String name)
		{
			super(name);
			this.diceExpressions = null;
			this.assignable = false;
		}

		public DefaultRollMethod(GeneratorElement element)
		{
			super(element);
			this.assignable = Boolean.parseBoolean(element.getAttributeValue("assignable"));
			this.diceExpressions = new ArrayList<String>();
			@SuppressWarnings("unchecked")
			List<Element> children = element.getChildren();
			for ( Element child : children)
			{
				diceExpressions.add(child.getText());
			}
		}

		public Integer getNext()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public List<String> getDiceExpressions()
		{
			return diceExpressions;
		}

		public boolean isAssignable()
		{
			return assignable;
		}

	}

	private static class DefaultMutableRollMethod extends DefaultRollMethod
			implements MutableRollMethod
	{

		private DefaultRollMethod generator = null;

		public DefaultMutableRollMethod(DefaultRollMethod generator)
		{
			super(generator.element);
			this.generator = generator;
		}

		public DefaultMutableRollMethod(GeneratorElement element)
		{
			super(element);
		}

		public void setDiceExpression(int index, String expression)
		{
			diceExpressions.set(index, expression);
		}

		public void setAssignable(boolean assign)
		{
			assignable = assign;
		}

		public void saveChanges()
		{
			element.removeContent();
			element.setAttribute("assignable", Boolean.toString(assignable));
			for (String expression : diceExpressions)
			{
				element.addContent(new Element("STAT").setText(expression));
			}
			if (generator != null)
			{
				generator.assignable = assignable;
				generator.diceExpressions = diceExpressions;
			}
		}

	}

	private static class DefaultRaceGenerator extends AbstractInfoFacadeGenerator<RaceFacade>
	{

		public DefaultRaceGenerator(GeneratorManager manager,
									 GeneratorElement element) throws MissingDataException
		{
			super(manager, element);
		}

		@Override
		protected RaceFacade getFacade(String name)
		{
			return manager.dataset.getRace(name);
		}

	}

	private static class DefaultMutableRaceGenerator extends AbstractMutableInfoFacadeGenerator<RaceFacade>
	{

		public DefaultMutableRaceGenerator(GeneratorManager manager,
											GeneratorElement element) throws MissingDataException
		{
			super(manager, element);
		}

		@Override
		protected RaceFacade getFacade(String name)
		{
			return manager.dataset.getRace(name);
		}

		@Override
		protected String getValueName()
		{
			return "RACE";
		}

	}

	private static class DefaultClassGenerator extends AbstractInfoFacadeGenerator<ClassFacade>
	{

		public DefaultClassGenerator(GeneratorManager manager,
									  GeneratorElement element) throws MissingDataException
		{
			super(manager, element);
		}

		@Override
		protected ClassFacade getFacade(String name)
		{
			return manager.dataset.getClass(name);
		}

	}

	private static class DefaultMutableClassGenerator extends AbstractMutableInfoFacadeGenerator<ClassFacade>
	{

		public DefaultMutableClassGenerator(GeneratorManager manager,
											 GeneratorElement element) throws MissingDataException
		{
			super(manager, element);
		}

		@Override
		protected ClassFacade getFacade(String name)
		{
			return manager.dataset.getClass(name);
		}

		@Override
		protected String getValueName()
		{
			return "CLASS";
		}

	}

	public static class DefaultMutableClassGeneratorBeanInfo extends SimpleBeanInfo
	{

		@Override
		public BeanDescriptor getBeanDescriptor()
		{
			return new BeanDescriptor(DefaultMutableClassGenerator.class,
									  BasicGeneratorSelectionModel.class);
		}

	}

	private static class DefaultSkillGenerator extends AbstractInfoFacadeGenerator<SkillFacade>
	{

		public DefaultSkillGenerator(GeneratorManager manager,
									  GeneratorElement element) throws MissingDataException
		{
			super(manager, element);
		}

		@Override
		protected SkillFacade getFacade(String name)
		{
			return manager.dataset.getSkill(name);
		}

	}

	private static class DefaultMutableSkillGenerator extends AbstractMutableInfoFacadeGenerator<SkillFacade>
	{

		public DefaultMutableSkillGenerator(GeneratorManager manager,
											 GeneratorElement element) throws MissingDataException
		{
			super(manager, element);
		}

		@Override
		protected SkillFacade getFacade(String name)
		{
			return manager.dataset.getSkill(name);
		}

		@Override
		protected String getValueName()
		{
			return "SKILL";
		}

	}

	private static class DefaultAbilityBuild implements AbilityBuild
	{

		private String name;
		protected Map<AbilityCatagoryFacade, AbstractInfoFacadeGenerator<AbilityFacade>> generatorMap;

		public DefaultAbilityBuild(GeneratorManager manager,
									GeneratorElement element) throws MissingDataException
		{
			this.name = element.getAttributeValue("name");
			this.generatorMap = new HashMap<AbilityCatagoryFacade, AbstractInfoFacadeGenerator<AbilityFacade>>();
			@SuppressWarnings("unchecked")
			List<GeneratorElement> children = element.getChildren("GENERATOR");
			for ( GeneratorElement element1 : children)
			{
				DefaultMutableAbilityGenerator generator = new DefaultMutableAbilityGenerator(manager,
																							  element1);
				AbilityCatagoryFacade catagory = manager.dataset.getAbilityCatagory(generator.toString());
				generatorMap.put(catagory, generator);
			}

		}

		public InfoFacadeGenerator<AbilityFacade> getGenerator(AbilityCatagoryFacade catagory)
		{
			return generatorMap.get(catagory);
		}

		@Override
		public String toString()
		{
			return name;
		}

	}

	private static class DefaultMutableAbilityBuild extends DefaultAbilityBuild
			implements MutableAbilityBuild
	{

		private GeneratorElement element;

		public DefaultMutableAbilityBuild(GeneratorManager manager,
										   GeneratorElement element) throws MissingDataException
		{
			super(manager, element);
			this.element = element;
		}

		public void saveChanges()
		{
			@SuppressWarnings("unchecked")
			List<Element> children = new ArrayList<Element>(element.getChildren());
			for ( Element child : children)
			{
				child.detach();
			}
			Set<String> sources = new HashSet<String>();
			Collection<AbstractInfoFacadeGenerator<AbilityFacade>> generators = generatorMap.values();
			for ( AbstractInfoFacadeGenerator<AbilityFacade> abilityGenerator : generators)
			{
				sources.addAll(abilityGenerator.getSources());
				Element ref = new Element("ABILITY_GENERATOR_REF");
				Element e = abilityGenerator.element;
				ref.setAttribute((Attribute) e.getAttribute("name").clone());
				ref.setAttribute((Attribute) e.getAttribute("catagory").clone());
				if (element.getDocument() != e.getDocument())
				{
					String uri = e.getDocument().getBaseURI();
					ref.setAttribute("uri", uri);
				}
				element.addContent(ref);
			}
			for ( String string : sources)
			{
				Element sourceElement = new Element("SOURCE");
				sourceElement.setText(string);
				element.addContent(sourceElement);
			}
			element.fireChangeEvent();
		}

		public void setGenerator(AbilityCatagoryFacade catagory,
								  InfoFacadeGenerator<AbilityFacade> generator)
		{
			generatorMap.put(catagory,
							 (AbstractInfoFacadeGenerator<AbilityFacade>) generator);
		}

	}

	private static class DefaultAbilityGenerator extends AbstractInfoFacadeGenerator<AbilityFacade>
	{

		public DefaultAbilityGenerator(GeneratorManager manager,
										GeneratorElement element) throws MissingDataException
		{
			super(manager, element);
		}

		@Override
		protected AbilityFacade getFacade(String name)
		{
			return manager.dataset.getAbility(manager.dataset.getAbilityCatagory(element.getAttributeValue("catagory")),
											  name);
		}

	}

	private static class DefaultMutableAbilityGenerator extends AbstractMutableInfoFacadeGenerator<AbilityFacade>
	{

		public DefaultMutableAbilityGenerator(GeneratorManager manager,
											   GeneratorElement element) throws MissingDataException
		{
			super(manager, element);
		}

		@Override
		protected AbilityFacade getFacade(String name)
		{

			return manager.dataset.getAbility(manager.dataset.getAbilityCatagory(element.getAttributeValue("catagory")),
											  name);
		}

		@Override
		protected String getValueName()
		{
			return "ABILITY";
		}

		@Override
		public void saveChanges()
		{
			element.removeContent();
			element.removeAttribute("name");
			element.setAttribute("type", randomOrder ? "random" : "ordered");
			for (AbilityFacade ability : priorityMap.keySet())
			{
				Element abilityElement = new Element(getValueName());
				abilityElement.setAttribute("weight",
											priorityMap.get(ability).toString());
				abilityElement.setText(ability.toString());
				element.addContent(abilityElement);
			}
		}

	}
}
