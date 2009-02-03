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

import java.awt.Component;
import java.beans.BeanDescriptor;
import java.beans.PropertyEditorSupport;
import java.beans.SimpleBeanInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import pcgen.gui.generator.stat.MutablePurchaseModeGenerator;
import pcgen.gui.generator.stat.MutableStandardModeGenerator;
import pcgen.gui.generator.stat.PurchaseModeGenerator;
import pcgen.gui.generator.stat.StandardModeGenerator;
import pcgen.gui.util.GenericListModel;
import pcgen.gui.util.GenericListModelWrapper;
import pcgen.gui.util.event.AbstractGenericListDataListener;
import pcgen.gui.util.event.GenericListDataEvent;
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
        public static final GeneratorType<WeightedGenerator<AlignmentFacade>> ALIGNMENT = new GeneratorType<WeightedGenerator<AlignmentFacade>>("AlignmentGenerator",
                                                                                                                                                   "ALIGNMENT_GENERATOR",
                                                                                                                                                   DefaultAlignmentGenerator.class,
                                                                                                                                                   null);
        public static final GeneratorType<InfoFacadeGenerator<SkillFacade>> SKILL = new GeneratorType<InfoFacadeGenerator<SkillFacade>>("SkillGenerator",
                                                                                                                                           "SKILL_GENERATOR",
                                                                                                                                           DefaultSkillGenerator.class,
                                                                                                                                           DefaultMutableSkillGenerator.class);
        public static final GeneratorType<StandardModeGenerator> STANDARDMODE = new GeneratorType<StandardModeGenerator>("StandardModeGenerator",
                                                                                                                            "STANDARDMODE_GENERATOR",
                                                                                                                            DefaultStandardModeGenerator.class,
                                                                                                                            DefaultMutableStandardModeGenerator.class);
        public static final GeneratorType<PurchaseModeGenerator> PURCHASEMODE = new GeneratorType<PurchaseModeGenerator>("PurchaseModeGenerator",
                                                                                                                            "PURCHASEMODE_GENERATOR",
                                                                                                                            DefaultPurchaseModeGenerator.class,
                                                                                                                            DefaultMutablePurchaseModeGenerator.class);
        public static final GeneratorType<InfoFacadeGenerator<RaceFacade>> RACE = new GeneratorType<InfoFacadeGenerator<RaceFacade>>("RaceGenerator",
                                                                                                                                        "RACE_GENERATOR",
                                                                                                                                        DefaultRaceGenerator.class,
                                                                                                                                        DefaultMutableRaceGenerator.class);
        public static final GeneratorType<InfoFacadeGenerator<ClassFacade>> CLASS = new GeneratorType<InfoFacadeGenerator<ClassFacade>>("ClassGenerator",
                                                                                                                                           "CLASS_GENERATOR",
                                                                                                                                           DefaultClassGenerator.class,
                                                                                                                                           DefaultMutableClassGenerator.class);
        public static final GeneratorType<AbilityBuild> ABILITYBUILD = new GeneratorType<AbilityBuild>("AbilityBuild",
                                                                                                          "ABILITY_BUILD",
                                                                                                          DefaultAbilityBuild.class,
                                                                                                          DefaultMutableAbilityBuild.class);
        private Class<? extends E> baseClass;
        private Class<? extends E> mutableClass;
        private String name;
        private String element;

        private GeneratorType(String name, String element,
                               Class<? extends E> baseClass,
                               Class<? extends E> mutableClass)
        {
            this.name = name;
            this.element = element;
            this.baseClass = baseClass;
            this.mutableClass = mutableClass;
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
    }

    @SuppressWarnings("unchecked")
    private void loadGenerator(GeneratorElement element, boolean mutable)
    {

        List<GeneratorElement> sourceElements = element.getChildren("SOURCE");
        if (sourceElements != null)
        {
            Set<String> sources = data.getSources();
            for (Element source : sourceElements)
            {
                if (!sources.contains(source.getText()))
                {
                    return;
                }
            }
        }
        List<GeneratorElement> refElements = element.getContent(refFilter);
        if (refElements != null)
        {
            Document basedoc = element.getDocument();
            for (GeneratorElement refElement : refElements)
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
                        catch (URISyntaxException ex)
                        {
                            Logging.log(Logging.XML_ERROR,
                                        "Invalid URI specified in:\n" +
                                        documentHandler.outputter.outputString(refElement) +
                                        "\nlocated in " +
                                        basedoc.getBaseURI(), ex);
                            return;
                        }
                        if (document == null)
                        {
                            Logging.log(Logging.XML_ERROR,
                                        "Unable to resolve reference to " + uri);
                            return;
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
                    return;
                }
            }
        }
        String name = element.getAttributeValue("name");
        String catagory = element.getAttributeValue("catagory");
        GeneratorType<?> type = GeneratorType.getGeneratorType(element.getName());
        try
        {
            Class<?> c;
            if (mutable)
            {
                c = type.mutableClass;
            }
            else
            {
                c = type.baseClass;
            }
            Object obj = c.getConstructor(GeneratorElement.class).newInstance(element);
            generatorMap.put(type, catagory, name, obj);
        }
        catch (Exception ex)
        {
            Logging.errorPrint("Unable to create generator",
                               ex);
        }
    }

    private <T> List<T> getGeneratorList(Document document,
                                          GeneratorType<T> type)
    {
        return null;
    }

    private final DataSetFacade data;
    // The second and third keys are "catagory" and "name" respectively
    private final TripleKeyMap<GeneratorType<?>, String, String, Object> generatorMap;

    public GeneratorManager(DataSetFacade data)
    {
        this.data = data;
        this.generatorMap = new TripleKeyMap<GeneratorType<?>, String, String, Object>();

        loadSingletonInfoFacadeGenerators(GeneratorType.RACE, data.getRaces());
        loadSingletonInfoFacadeGenerators(GeneratorType.CLASS, data.getClasses());
        loadSingletonInfoFacadeGenerators(GeneratorType.SKILL, data.getSkills());
        GameModeFacade gameMode = data.getGameMode();

        Document document = documentHandler.getDocument(gameMode.getGeneratorFile().toURI());
        if (document != null)
        {
            loadGenerators(document);
        }


        GenericListModelWrapper<AlignmentFacade> alignments = new GenericListModelWrapper<AlignmentFacade>(gameMode.getAlignments());
        for (AlignmentFacade alignmentFacade : alignments)
        {
            SingletonWeightedGenerator<AlignmentFacade> generator = new SingletonWeightedGenerator<AlignmentFacade>(alignmentFacade);
            generatorMap.put(GeneratorType.ALIGNMENT, null, generator.toString(),
                             generator);
        }
    }

    private class SingletonInfoFacadeGeneratorManager<E extends InfoFacade>
            extends AbstractGenericListDataListener<E>
    {

        private final GeneratorType<? extends Generator<E>> type;
        private final String catagoryName;

        public SingletonInfoFacadeGeneratorManager(GeneratorType<? extends Generator<E>> type,
                                                    AbilityCatagoryFacade catagory,
                                                    GenericListModel<E> model)
        {
            super(model);
            this.type = type;
            if (catagory != null)
            {
                catagoryName = catagory.getName();
            }
            else
            {
                catagoryName = null;
            }
            setModel(model);
            addData(wrapper);
        }

        private void addData(Collection<? extends E> data)
        {
            for (E infoFacade : data)
            {
                SingletonInfoFacadeGenerator<E> generator = new SingletonInfoFacadeGenerator<E>(infoFacade);
                generatorMap.put(type, catagoryName, generator.toString(),
                                 generator);
            }
        }

        private void removeData(Collection<? extends E> data)
        {
            for (E e : data)
            {
                generatorMap.remove(type, catagoryName, e.toString());
            }
        }

        public void intervalAdded(GenericListDataEvent<E> e)
        {
            addData(e.getData());
        }

        public void intervalRemoved(GenericListDataEvent<E> e)
        {
            removeData(e.getData());
        }

    }

    private <T extends InfoFacade> void loadSingletonInfoFacadeGenerators(GeneratorType<InfoFacadeGenerator<T>> type,
                                                                           GenericListModel<T> model)
    {
        loadSingletonInfoFacadeGenerators(type, null, model);
    }

    private <T extends InfoFacade> void loadSingletonInfoFacadeGenerators(GeneratorType<InfoFacadeGenerator<T>> type,
                                                                           AbilityCatagoryFacade catagory,
                                                                           GenericListModel<T> model)
    {
        GenericListModelWrapper<T> list = new GenericListModelWrapper<T>(model);
        for (T infoFacade : list)
        {
            SingletonInfoFacadeGenerator<T> generator = new SingletonInfoFacadeGenerator<T>(infoFacade);
            generatorMap.put(type, catagory.getName(), generator.toString(),
                             generator);
        }
    }

    public static final void main(String[] arg)
    {

        File file = new File("build/classes/generators/DefaultStandardGenerators.xml");
        System.out.println(GeneratorType.getGeneratorType("ABILITY_BUILD").name);
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

    public static MutablePurchaseModeGenerator createMutablePurchaseModeGenerator(String name,
                                                                                    DataSetFacade data,
                                                                                    PurchaseModeGenerator template)
    {
        Document document = getCustomGeneratorDocument(data,
                                                       "PurchaseModeGenerator");
        if (document != null)
        {
            GeneratorElement generatorElement = new GeneratorElement("GENERATOR");
            generatorElement.setAttribute("name", name).
                    setAttribute("points", "0");
            Element cost = new Element("COST");
            cost.setAttribute("score", "0").setText("0");
            document.getRootElement().addContent(generatorElement.addContent(cost));

            MutablePurchaseModeGenerator generator = new DefaultMutablePurchaseModeGenerator(generatorElement);
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
        return null;
    }

    public static MutableStandardModeGenerator createMutableStandardModeGenerator(String name,
                                                                                    DataSetFacade data,
                                                                                    StandardModeGenerator template)
    {
        Document document = getCustomGeneratorDocument(data,
                                                       "StandardModeGenerator");
        if (document != null)
        {
            GeneratorElement generatorElement = new GeneratorElement("GENERATOR");
            generatorElement.setAttribute("name", name).
                    setAttribute("assignable", "true");
            document.getRootElement().addContent(generatorElement);
            for (int x = 0; x < 6; x++)
            {
                generatorElement.addContent(new Element("STAT"));
            }

            MutableStandardModeGenerator generator = new DefaultMutableStandardModeGenerator(generatorElement);
            if (template != null)
            {
                generator.setAssignable(template.isAssignable());
                /* 
                 * Its safe to resuse the list since the expression 
                 * lists arn't modified after they are put into the generator
                 */
                generator.setDiceExpressions(template.getDiceExpressions());
            }
            return generator;
        }
        return null;
    }

    public static <T extends InfoFacade> MutableInfoFacadeGenerator<T> createMutableFacadeGenerator(GeneratorType<InfoFacadeGenerator<T>> type,
                                                                                                      String name,
                                                                                                      DataSetFacade data,
                                                                                                      MutableInfoFacadeGenerator<T> template)
    {
        Document document = getCustomGeneratorDocument(data,
                                                       type.name);
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

    private class DefaultCharacterBuild
    {
    }

    public static class DefaultCharacterBuildEditor extends PropertyEditorSupport
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

    private class AnyWeightedGenerator<E> extends AbstractWeightedGenerator<E>
    {

        public AnyWeightedGenerator(List<E> items)
        {
            super("Any");
            for (E object : items)
            {
                priorityMap.put(object, 1);
            }
        }

        @Override
        protected E getFacade(String name)
        {
            return null;
        }

    }

    private class AnyInfoFacadeGenerator<E extends InfoFacade> extends AbstractInfoFacadeGenerator<E>
    {

        public AnyInfoFacadeGenerator(List<E> items)
        {
            super("Any");
            for (E object : items)
            {
                priorityMap.put(object, 1);
            }
        }

        @Override
        protected E getFacade(String name)
        {
            return null;
        }

    }

    private class DefaultAlignmentGenerator extends AbstractWeightedGenerator<AlignmentFacade>
    {

        //private Queue<E> queue = null;
        public DefaultAlignmentGenerator(GeneratorElement element) throws MissingDataException
        {
            super(element);

        }

        @Override
        protected AlignmentFacade getFacade(String name)
        {
            return data.getGameMode().getAlignment(name);
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

        public AbstractWeightedGenerator(String name)
        {
            super(name);
            queue = new LinkedList<E>();
        }

        public AbstractWeightedGenerator(GeneratorElement element) throws MissingDataException
        {
            super(element);
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
        public AbstractInfoFacadeGenerator(GeneratorElement element) throws MissingDataException
        {
            super(element);
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

        public AbstractMutableInfoFacadeGenerator(AbstractInfoFacadeGenerator<E> generator) throws MissingDataException
        {
            super(generator.element);
            this.generator = generator;
        }

        public AbstractMutableInfoFacadeGenerator(GeneratorElement element) throws MissingDataException
        {
            super(element);
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
            return Collections.singletonList(item);
        }

        public int getWeight(E item)
        {
            if (item.equals(this.item))
            {
                return 1;
            }
            else
            {
                return 0;
            }
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

        public SingletonInfoFacadeGenerator(E item)
        {
            super(item);
        }

        public Set<String> getSources()
        {
            return Collections.singleton(item.getSource());
        }

        public boolean isRandomOrder()
        {
            return false;
        }

    }

    private static class DefaultPurchaseModeGenerator extends AbstractGenerator<Integer>
            implements PurchaseModeGenerator
    {

        protected Vector<Integer> costs;
        protected int points;
        protected int min;

        public DefaultPurchaseModeGenerator(GeneratorElement element)
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

    private static class DefaultMutablePurchaseModeGenerator extends DefaultPurchaseModeGenerator
            implements MutablePurchaseModeGenerator
    {

        private DefaultPurchaseModeGenerator generator = null;

        public DefaultMutablePurchaseModeGenerator(DefaultPurchaseModeGenerator generator)
        {
            super(generator.element);
            this.generator = generator;
        }

        public DefaultMutablePurchaseModeGenerator(GeneratorElement element)
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

    private static class DefaultStandardModeGenerator extends AbstractGenerator<Integer>
            implements StandardModeGenerator
    {

        protected boolean assignable;
        protected List<String> diceExpressions;

        public DefaultStandardModeGenerator(GeneratorElement element)
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

    private static class DefaultMutableStandardModeGenerator extends DefaultStandardModeGenerator
            implements MutableStandardModeGenerator
    {

        private DefaultStandardModeGenerator generator = null;

        public DefaultMutableStandardModeGenerator(DefaultStandardModeGenerator generator)
        {
            super(generator.element);
            this.generator = generator;
        }

        public DefaultMutableStandardModeGenerator(GeneratorElement element)
        {
            super(element);
        }

        public void setDiceExpressions(List<String> expressions)
        {
            diceExpressions = expressions;
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

    private class DefaultRaceGenerator extends AbstractInfoFacadeGenerator<RaceFacade>
    {

        public DefaultRaceGenerator(GeneratorElement element) throws MissingDataException
        {
            super(element);
        }

        @Override
        protected RaceFacade getFacade(String name)
        {
            return data.getRace(name);
        }

    }

    private class DefaultMutableRaceGenerator extends AbstractMutableInfoFacadeGenerator<RaceFacade>
    {

        public DefaultMutableRaceGenerator(GeneratorElement element) throws MissingDataException
        {
            super(element);
        }

        @Override
        protected RaceFacade getFacade(String name)
        {
            return data.getRace(name);
        }

        @Override
        protected String getValueName()
        {
            return "RACE";
        }

    }

    private class DefaultClassGenerator extends AbstractInfoFacadeGenerator<ClassFacade>
    {

        public DefaultClassGenerator(GeneratorElement element) throws MissingDataException
        {
            super(element);
        }

        @Override
        protected ClassFacade getFacade(String name)
        {
            return data.getClass(name);
        }

    }

    private class DefaultMutableClassGenerator extends AbstractMutableInfoFacadeGenerator<ClassFacade>
    {

        public DefaultMutableClassGenerator(GeneratorElement element) throws MissingDataException
        {
            super(element);
        }

        @Override
        protected ClassFacade getFacade(String name)
        {
            return data.getClass(name);
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

    public static class DefaultMutableClassGeneratorEditor extends PropertyEditorSupport
    {
    }

    private class DefaultSkillGenerator extends AbstractInfoFacadeGenerator<SkillFacade>
    {

        public DefaultSkillGenerator(GeneratorElement element) throws MissingDataException
        {
            super(element);
        }

        @Override
        protected SkillFacade getFacade(String name)
        {
            return data.getSkill(name);
        }

    }

    private class DefaultMutableSkillGenerator extends AbstractMutableInfoFacadeGenerator<SkillFacade>
    {

        public DefaultMutableSkillGenerator(GeneratorElement element) throws MissingDataException
        {
            super(element);
        }

        @Override
        protected SkillFacade getFacade(String name)
        {
            return data.getSkill(name);
        }

        @Override
        protected String getValueName()
        {
            return "SKILL";
        }

    }

    private class DefaultAbilityBuild implements AbilityBuild
    {

        private String name;
        protected Map<AbilityCatagoryFacade, InfoFacadeGenerator<AbilityFacade>> generatorMap;

        public DefaultAbilityBuild(GeneratorElement element) throws MissingDataException
        {
            this.name = element.getAttributeValue("name");
            this.generatorMap = new HashMap<AbilityCatagoryFacade, InfoFacadeGenerator<AbilityFacade>>();
            @SuppressWarnings("unchecked")
            List<GeneratorElement> children = element.getChildren("GENERATOR");
            for ( GeneratorElement element1 : children)
            {
                DefaultMutableAbilityGenerator generator = new DefaultMutableAbilityGenerator(element1);
                AbilityCatagoryFacade catagory = data.getAbilityCatagory(generator.toString());
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

    private class DefaultMutableAbilityBuild extends DefaultAbilityBuild
            implements MutableAbilityBuild
    {

        private Element element;

        public DefaultMutableAbilityBuild(GeneratorElement element) throws MissingDataException
        {
            super(element);
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
//            Collection<DefaultMutableAbilityGenerator> generators = generatorMap.values();
//            for ( Iterator<DefaultMutableAbilityGenerator> it = generators.iterator(); it.hasNext();)
//            {
//                DefaultMutableAbilityGenerator generator = it.next();
//                if (!generator.priorityMap.isEmpty())
//                {
//                    sources.addAll(generator.getSources());
//                    generator.saveChanges();
//                    element.addContent(generator.element);
//                }
//                else
//                {
//                    it.remove();
//                }
//            }
            for ( String string : sources)
            {
                Element sourceElement = new Element("SOURCE");
                sourceElement.setText(string);
                element.addContent(sourceElement);
            }
        //outputDocument(element.getDocument());
        }

        public void setGenerator(AbilityCatagoryFacade catagory,
                                  InfoFacadeGenerator<AbilityFacade> generator)
        {
            generatorMap.put(catagory, generator);
        }

    }

    private class DefaultMutableAbilityGenerator extends AbstractMutableInfoFacadeGenerator<AbilityFacade>
    {

        public DefaultMutableAbilityGenerator(GeneratorElement element) throws MissingDataException
        {
            super(element);
        }

        @Override
        protected AbilityFacade getFacade(String name)
        {

            return data.getAbility(data.getAbilityCatagory(this.toString()),
                                   name);
        }

        @Override
        protected String getValueName()
        {
            return "ABILITY";
        }

        @Override
        public String toString()
        {
            return element.getAttributeValue("catagory");
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
