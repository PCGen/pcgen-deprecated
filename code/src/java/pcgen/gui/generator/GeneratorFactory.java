/*
 * GeneratorFactory.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pcgen.base.util.WeightedCollection;
import pcgen.core.SettingsHandler;
import pcgen.gui.facade.AbilityCatagoryFacade;
import pcgen.gui.facade.AbilityFacade;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.DataSetFacade;
import pcgen.gui.facade.InfoFacade;
import pcgen.gui.facade.RaceFacade;
import pcgen.gui.facade.SkillFacade;
import pcgen.gui.generator.ability.AbilityBuild;
import pcgen.gui.generator.ability.MutableAbilityBuild;
import pcgen.gui.generator.stat.MutablePurchaseModeGenerator;
import pcgen.gui.generator.stat.MutableStandardModeGenerator;
import pcgen.gui.generator.stat.PurchaseModeGenerator;
import pcgen.gui.generator.stat.StandardModeGenerator;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class GeneratorFactory implements EntityResolver
{

    public static final class GeneratorType<E>
    {

        public static final GeneratorType<SkillFacade> SKILL = new GeneratorType<SkillFacade>("SkillGenerator",
                                                                                                 DefaultSkillGenerator.class,
                                                                                                 DefaultMutableSkillGenerator.class);
        public static final GeneratorType<Integer> STANDARDMODE = new GeneratorType<Integer>("StandardModeGenerator",
                                                                                                DefaultStandardModeGenerator.class,
                                                                                                DefaultMutableStandardModeGenerator.class);
        public static final GeneratorType<Integer> PURCHASEMODE = new GeneratorType<Integer>("PurchaseModeGenerator",
                                                                                                DefaultPurchaseModeGenerator.class,
                                                                                                DefaultMutablePurchaseModeGenerator.class);
        public static final GeneratorType<RaceFacade> RACE = new GeneratorType<RaceFacade>("RaceGenerator",
                                                                                              DefaultRaceGenerator.class,
                                                                                              DefaultMutableRaceGenerator.class);
        public static final GeneratorType<ClassFacade> CLASS = new GeneratorType<ClassFacade>("ClassGenerator",
                                                                                                 DefaultClassGenerator.class,
                                                                                                 DefaultMutableClassGenerator.class);
        private Class<? extends Generator<E>> baseClass;
        private Class<? extends MutableGenerator<E>> mutableClass;
        private String name;

        private GeneratorType(String name,
                               Class<? extends Generator<E>> baseClass,
                               Class<? extends MutableGenerator<E>> mutableClass)
        {
            this.name = name;
            this.baseClass = baseClass;
            this.mutableClass = mutableClass;
        }

    }

    private static class GeneratorFactoryHolder
    {

        private static final GeneratorFactory INSTANCE = new GeneratorFactory();
    }

    private static GeneratorFactory getInstance()
    {
        return GeneratorFactoryHolder.INSTANCE;
    }

    private final SAXBuilder builder;
    private final XMLOutputter outputter;

    private GeneratorFactory()
    {
        builder = new SAXBuilder();
        builder.setEntityResolver(this);
        outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
    {
        if (publicId != null && publicId.equals("PCGEN-GENERATORS"))
        {
            String fileName = SettingsHandler.getPcgenSystemDir() +
                    File.separator +
                    "generators" +
                    File.separator +
                    new File(systemId).getName();
            return new InputSource(new FileInputStream(fileName));
        }
        else
        {
            return new InputSource(systemId);
        }
    }

    static Document buildDocument(File file)
    {
        try
        {
            return getInstance().builder.build(file);
        }
        catch (JDOMException ex)
        {
            Logging.errorPrint("Unable to parse XML file: " + file.getPath(), ex);
        }
        catch (IOException ex)
        {
            Logging.errorPrint("Unable to access file: " + file.getPath(), ex);
        }
        return null;
    }

    private static void outputDocument(Document document)
    {
        outputDocument(document, new File(URI.create(document.getBaseURI())));
    }

    private static void outputDocument(Document document, File outputFile)
    {
        FileOutputStream output = null;
        try
        {
            output = new FileOutputStream(outputFile);
            getInstance().outputter.output(document, output);
        }
        catch (IOException ex)
        {
            Logging.errorPrint("Error occured while outputting document", ex);
        }
        finally
        {
            try
            {
                output.close();
            }
            catch (IOException ex)
            {
                Logging.errorPrint("Unable to close output stream", ex);
            }
        }
    }

    public static final void main(String[] arg)
    {
        try
        {
            File file = new File("build/classes/generators/stat/DefaultStandardGenerators.xml");
            GeneratorFactory factory = new GeneratorFactory();
            Document doc = factory.builder.build(file);
            System.out.println(doc.getDocType().getSystemID());
        }
        catch (JDOMException ex)
        {
            Logger.getLogger(GeneratorFactory.class.getName()).log(Level.SEVERE,
                                                                   null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(GeneratorFactory.class.getName()).log(Level.SEVERE,
                                                                   null, ex);
        }
    }

    static List<Generator<Integer>> buildStatGeneratorList(GeneratorType<Integer> type,
                                                            Document document)
    {
        Element root = document.getRootElement();
        boolean mutable = Boolean.parseBoolean(root.getAttributeValue("mutable"));
        List<Generator<Integer>> generators = new ArrayList<Generator<Integer>>();
        for (Object element : root.getChildren())
        {
            try
            {
                Class<? extends Generator<Integer>> c;
                if (mutable)
                {
                    c = type.mutableClass;
                }
                else
                {
                    c = type.baseClass;
                }
                Generator<Integer> generator = (Generator<Integer>) c.getConstructor(Element.class).newInstance(element);
                generators.add(generator);
            }
            catch (Exception ex)
            {
                Logging.errorPrint("Unable to create stat generator",
                                   ex);
            }
        }
        return generators;
    }

    static <T extends InfoFacade> List<FacadeGenerator<T>> buildFacadeGeneratorList(GeneratorType<T> type,
                                                                                     Document document,
                                                                                     DataSetFacade data)
    {
        Element root = document.getRootElement();
        boolean mutable = Boolean.parseBoolean(root.getAttributeValue("mutable"));

        List<FacadeGenerator<T>> generators = new ArrayList<FacadeGenerator<T>>();
        Set<String> sources = data.getSources();
        generatorLoop:
            for (Object element : root.getChildren())
            {
                @SuppressWarnings("unchecked")
                List<Element> sourceElements = root.getChildren("SOURCE");
                for ( Element source : sourceElements)
                {
                    if (!sources.contains(source.getText()))
                    {
                        continue generatorLoop;
                    }
                }
                try
                {
                    Class<? extends Generator<T>> c;
                    if (mutable)
                    {
                        c = type.mutableClass;
                    }
                    else
                    {
                        c = type.baseClass;
                    }
                    FacadeGenerator<T> generator = (FacadeGenerator<T>) c.getConstructor(Element.class,
                                                                                         DataSetFacade.class).newInstance(element,
                                                                                                                          data);
                    generators.add(generator);
                }
                catch ( Exception ex)
                {
                    Logging.errorPrint("Unable to create Facade generator",
                                       ex);
                }
            }
        return generators;
    }

    public static <T extends InfoFacade> FacadeGenerator<T> createSingletonGenerator(T item)
    {
        return new SingletonGenerator<T>(item);
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
            document = buildDocument(file);
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
            Element generatorElement = new Element("GENERATOR");
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
            Element generatorElement = new Element("GENERATOR");
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

    public static <T extends InfoFacade> MutableFacadeGenerator<T> createMutableFacadeGenerator(GeneratorType<T> type,
                                                                                                  String name,
                                                                                                  DataSetFacade data,
                                                                                                  MutableFacadeGenerator<T> template)
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
                MutableFacadeGenerator<T> generator = (MutableFacadeGenerator<T>) type.mutableClass.getConstructor(Element.class,
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

    private abstract static class AbstractGenerator<E> implements Generator<E>
    {

        private String name;

        public AbstractGenerator(String name)
        {
            this.name = name;
        }

        public AbstractGenerator(Element element)
        {
            this.name = element.getAttributeValue("name");
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

    private abstract static class AbstractFacadeGenerator<E extends InfoFacade>
            extends AbstractGenerator<E> implements FacadeGenerator<E>
    {

        protected Map<E, Integer> priorityMap;
        protected boolean randomOrder;
        private Queue<E> queue = null;

        @SuppressWarnings("unchecked")
        public AbstractFacadeGenerator(Element element, DataSetFacade data) throws MissingDataException
        {
            super(element);
            this.priorityMap = new HashMap<E, Integer>();

            List<Element> children = element.getChildren(getValueName());
            for (Element child : children)
            {
                String elementName = child.getText();
                Integer weight = Integer.valueOf(child.getAttributeValue("weight"));
                E facade = getFacade(data, elementName);
                if (facade == null)
                {
                    throw new MissingDataException(elementName +
                                                   " not found in DataSetFacade");
                }
                priorityMap.put(facade, weight);
            }
            reset();
        }

        protected abstract E getFacade(DataSetFacade data, String name);

        protected abstract String getValueName();

        public boolean isRandomOrder()
        {
            return randomOrder;
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

        public int getWeight(E item)
        {
            return priorityMap.get(item);
        }

        public boolean isSingleton()
        {
            return false;
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
        public List<E> getAll()
        {
            return new ArrayList<E>(priorityMap.keySet());
        }

        @Override
        public void reset()
        {
            if (randomOrder)
            {
                queue = new RandomWeightedQueue();
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
                queue = new PriorityQueue<E>(priorityMap.size(), comparator);
                queue.addAll(priorityMap.keySet());
            }
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

    private abstract static class AbstractMutableFacadeGenerator<E extends InfoFacade>
            extends AbstractFacadeGenerator<E> implements MutableFacadeGenerator<E>
    {

        protected Element element;

        public AbstractMutableFacadeGenerator(Element element,
                                               DataSetFacade data) throws MissingDataException
        {
            super(element, data);
            this.element = element;
        }

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
            for (String source : getSources())
            {
                element.addContent(new Element("SOURCE").setText(source));
            }
            for (E skill : priorityMap.keySet())
            {
                Element skillElement = new Element(getValueName());
                skillElement.setAttribute("weight",
                                          priorityMap.get(skill).toString());
                skillElement.setText(skill.toString());
                element.addContent(skillElement);
            }
            outputDocument(element.getDocument());
        }

    }

    private static class SingletonGenerator<E extends InfoFacade> extends AbstractGenerator<E>
            implements FacadeGenerator<E>
    {

        private E item;

        public SingletonGenerator(E item)
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

        public boolean isSingleton()
        {
            return true;
        }

        public Set<String> getSources()
        {
            return Collections.singleton(item.getSource());
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

        public DefaultPurchaseModeGenerator(Element element)
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

        private Element element;

        public DefaultMutablePurchaseModeGenerator(Element element)
        {
            super(element);
            this.element = element;
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
            outputDocument(element.getDocument());
        }

    }

    private static class DefaultStandardModeGenerator extends AbstractGenerator<Integer>
            implements StandardModeGenerator
    {

        protected boolean assignable;
        protected List<String> diceExpressions;

        public DefaultStandardModeGenerator(Element element)
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

        private Element element;

        public DefaultMutableStandardModeGenerator(Element element)
        {
            super(element);
            this.element = element;
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
            outputDocument(element.getDocument());
        }

    }

    private static class DefaultRaceGenerator extends AbstractFacadeGenerator<RaceFacade>
    {

        public DefaultRaceGenerator(Element element, DataSetFacade data) throws MissingDataException
        {
            super(element, data);
        }

        @Override
        protected RaceFacade getFacade(DataSetFacade data, String name)
        {
            return data.getRace(name);
        }

        @Override
        protected String getValueName()
        {
            return "RACE";
        }

    }

    private static class DefaultMutableRaceGenerator extends AbstractMutableFacadeGenerator<RaceFacade>
    {

        public DefaultMutableRaceGenerator(Element element, DataSetFacade data) throws MissingDataException
        {
            super(element, data);
        }

        @Override
        protected RaceFacade getFacade(DataSetFacade data, String name)
        {
            return data.getRace(name);
        }

        @Override
        protected String getValueName()
        {
            return "RACE";
        }

    }

    private static class DefaultClassGenerator extends AbstractFacadeGenerator<ClassFacade>
    {

        public DefaultClassGenerator(Element element, DataSetFacade data) throws MissingDataException
        {
            super(element, data);
        }

        @Override
        protected ClassFacade getFacade(DataSetFacade data, String name)
        {
            return data.getClass(name);
        }

        @Override
        protected String getValueName()
        {
            return "CLASS";
        }

    }

    private static class DefaultMutableClassGenerator extends AbstractMutableFacadeGenerator<ClassFacade>
    {

        public DefaultMutableClassGenerator(Element element, DataSetFacade data) throws MissingDataException
        {
            super(element, data);
        }

        @Override
        protected ClassFacade getFacade(DataSetFacade data, String name)
        {
            return data.getClass(name);
        }

        @Override
        protected String getValueName()
        {
            return "CLASS";
        }

    }

    private static class DefaultSkillGenerator extends AbstractFacadeGenerator<SkillFacade>
    {

        public DefaultSkillGenerator(Element element, DataSetFacade data) throws MissingDataException
        {
            super(element, data);
        }

        @Override
        protected SkillFacade getFacade(DataSetFacade data, String name)
        {
            return data.getSkill(name);
        }

        @Override
        protected String getValueName()
        {
            return "SKILL";
        }

    }

    private static class DefaultMutableSkillGenerator extends AbstractMutableFacadeGenerator<SkillFacade>
    {

        public DefaultMutableSkillGenerator(Element element, DataSetFacade data) throws MissingDataException
        {
            super(element, data);
        }

        @Override
        protected SkillFacade getFacade(DataSetFacade data, String name)
        {
            return data.getSkill(name);
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
        protected Map<AbilityCatagoryFacade, DefaultMutableAbilityGenerator> generatorMap;

        public DefaultAbilityBuild(Element element, DataSetFacade data) throws MissingDataException
        {
            this.name = element.getAttributeValue("name");
            this.generatorMap = new HashMap<AbilityCatagoryFacade, DefaultMutableAbilityGenerator>();
            @SuppressWarnings("unchecked")
            List<Element> children = element.getChildren("GENERATOR");
            for ( Element element1 : children)
            {
                DefaultMutableAbilityGenerator generator = new DefaultMutableAbilityGenerator(element1,
                                                                                                     data);
                AbilityCatagoryFacade catagory = data.getAbilityCatagory(generator.toString());
                generatorMap.put(catagory, generator);
            }

        }

        public FacadeGenerator<AbilityFacade> getGenerator(AbilityCatagoryFacade catagory)
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

        private Element element;
        private DataSetFacade data;

        public DefaultMutableAbilityBuild(Element element, DataSetFacade data) throws MissingDataException
        {
            super(element, data);
            this.element = element;
            this.data = data;
        }

        @Override
        public MutableFacadeGenerator<AbilityFacade> getGenerator(AbilityCatagoryFacade catagory)
        {
            DefaultMutableAbilityGenerator generator = generatorMap.get(catagory);
            if (generator == null)
            {
                try
                {
                    Element generatorElement = new Element("GENERATOR");
                    generatorElement.setAttribute("catagory",
                                                  catagory.toString());
                    generator = new DefaultMutableAbilityGenerator(generatorElement,
                                                                   data);
                    generatorMap.put(catagory, generator);
                }
                catch (MissingDataException ex)
                {
                    Logging.errorPrint("Failed to create a blank MutableAbilityGenerator",
                                       ex);
                }
            }
            return generator;
        }

        public void saveChanges()
        {
            
        }

    }

    private static class DefaultMutableAbilityGenerator extends AbstractMutableFacadeGenerator<AbilityFacade>
    {

        public DefaultMutableAbilityGenerator(Element element,
                                               DataSetFacade data) throws MissingDataException
        {
            super(element.setAttribute("name",
                                       element.getAttributeValue("catagory")),
                  data);
        }

        @Override
        protected AbilityFacade getFacade(DataSetFacade data, String name)
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
        public void saveChanges()
        {
            element.removeContent();
            element.removeAttribute("name");
            element.setAttribute("type", randomOrder ? "random" : "ordered");
            for (AbilityFacade ability : priorityMap.keySet())
            {
                Element skillElement = new Element(getValueName());
                skillElement.setAttribute("weight",
                                          priorityMap.get(ability).toString());
                skillElement.setText(ability.toString());
                element.addContent(skillElement);
            }
        }

    }
}
