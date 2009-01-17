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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import pcgen.base.util.RandomUtil;
import pcgen.base.util.WeightedCollection;
import pcgen.cdom.enumeration.Gender;
import pcgen.core.SettingsHandler;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.DataSetFacade;
import pcgen.gui.facade.InfoFacade;
import pcgen.gui.facade.RaceFacade;
import pcgen.gui.facade.SkillFacade;
import pcgen.gui.facade.StatFacade;
import pcgen.gui.generator.skill.MutableSkillGenerator;
import pcgen.gui.generator.skill.SkillGenerator;
import pcgen.gui.generator.stat.MutablePurchaseModeGenerator;
import pcgen.gui.generator.stat.MutableStandardModeGenerator;
import pcgen.gui.generator.stat.PurchaseModeGenerator;
import pcgen.gui.generator.stat.StandardModeGenerator;
import pcgen.gui.util.GenericComboBoxModel;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class GeneratorFactory implements EntityResolver
{

    private static class GeneratorFactoryHolder
    {

        private static final GeneratorFactory INSTANCE = new GeneratorFactory();
    }

    private static GeneratorFactory getInstance()
    {
        return GeneratorFactoryHolder.INSTANCE;
    }

    private final Map<Document, List<Generator<?>>> documentMap;
    private final SAXBuilder builder;
    private final XMLOutputter outputter;

    private GeneratorFactory()
    {
        builder = new SAXBuilder();
        builder.setEntityResolver(this);
        outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        documentMap = new HashMap<Document, List<Generator<?>>>();
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

    private static Document buildDocument(File file)
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
            file = new File("build/classes/generators/stat/DefaultStandardGenerators2.xml");
            outputDocument(doc, file);
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

    private final class DefaultCharacterCreationManager implements CharacterCreationManager
    {

        private final PropertyChangeSupport support;
        private final Map<String, Boolean> validityMap;

        public DefaultCharacterCreationManager(DataSetFacade data)
        {
            support = new PropertyChangeSupport(this);
            validityMap = new HashMap<String, Boolean>();
            List<Generator<Integer>> statGenerators = new ArrayList<Generator<Integer>>();
            List<Generator<SkillFacade>> skillGenerators = new ArrayList<Generator<SkillFacade>>();
            Set<File> files = data.getGeneratorFiles();
            for (File file : files)
            {
                try
                {
                    Document document = builder.build(file);
                    DocType type = document.getDocType();
                    if (type.getElementName().equals("GENERATORSET"))
                    {
                        String systemid = type.getSystemID();
                        if (systemid.equals("StandardModeGenerator.dtd"))
                        {
                        // statGenerators.addAll(buildStandardModeGeneratorList(document));
                        }
                        else if (systemid.equals("PurchaseModeGenerator.dtd"))
                        {
                        // statGenerators.addAll(buildPurchaseModeGeneratorList(document));
                        }
                        else if (systemid.equals("SkillGenerator.dtd"))
                        {
                        // skillGenerators.addAll(buildSkillGeneratorList(document,
                        //                                                data));
                        }
                    }
                }
                catch (Exception ex)
                {
                    Logging.errorPrint(ex.getMessage(), ex);
                }
            }
        }

        public void addPropertyChangeListener(PropertyChangeListener l)
        {
            support.addPropertyChangeListener(l);
        }

        public void addPropertyChangeListener(String prop,
                                               PropertyChangeListener l)
        {
            support.addPropertyChangeListener(prop, l);
        }

        public void removePropertyChangeListener(PropertyChangeListener l)
        {
            support.removePropertyChangeListener(l);
        }

        public void removePropertyChangeListener(String prop,
                                                  PropertyChangeListener l)
        {
            support.removePropertyChangeListener(prop, l);
        }

        public boolean isCharacterValid()
        {
            return !validityMap.values().contains(Boolean.FALSE);
        }

        public boolean isCharacterNameValid()
        {
            return validityMap.get(NAME_VALIDITY);
        }

        public void setValidity(String prop, boolean valid)
        {
            boolean oldvalue = validityMap.get(prop);
            validityMap.put(prop, valid);
            support.firePropertyChange(prop, oldvalue, valid);
        }

        public GenericComboBoxModel<Generator<Integer>> getAlignmentGenerators()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public GenericComboBoxModel<Generator<Gender>> getGenderGenerators()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public GenericComboBoxModel<Generator<RaceFacade>> getRaceGenerators()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public GenericComboBoxModel<Generator<ClassFacade>> getClassGenerators()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public GenericComboBoxModel<Generator<Integer>> getStatGenerators()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public List<StatFacade> getStats()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getModForScore(int score)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public GenericComboBoxModel<Generator<Integer>> getClassLevelGenerators()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private static List<StandardModeGenerator> buildStandardModeGeneratorList(Document document)
    {
        Element root = document.getRootElement();
        boolean mutable = Boolean.parseBoolean(root.getAttributeValue("mutable"));
        List<StandardModeGenerator> generators = new ArrayList<StandardModeGenerator>();
        for (Object element : root.getChildren())
        {
            try
            {
                if (mutable)
                {
                    generators.add(new DefaultMutableStandardModeGenerator((Element) element));
                }
                else
                {
                    generators.add(new DefaultStandardModeGenerator((Element) element));
                }
            }
            catch (NumberFormatException e)
            {
                Logging.errorPrint(e.getMessage(), e);
            }
        }
        return generators;
    }

    private static List<PurchaseModeGenerator> buildPurchaseModeGeneratorList(Document document)
    {
        Element root = document.getRootElement();
        boolean mutable = Boolean.parseBoolean(root.getAttributeValue("mutable"));
        List<PurchaseModeGenerator> generators = new ArrayList<PurchaseModeGenerator>();
        for (Object element : root.getChildren())
        {
            try
            {
                if (mutable)
                {
                    generators.add(new DefaultMutablePurchaseModeGenerator((Element) element));
                }
                else
                {
                    generators.add(new DefaultPurchaseModeGenerator((Element) element));
                }
            }
            catch (NumberFormatException e)
            {
                Logging.errorPrint(e.getMessage(), e);
            }
        }
        return generators;
    }

    @SuppressWarnings("unchecked")
    private static List<SkillGenerator> buildSkillGeneratorList(Document document,
                                                                  DataSetFacade data)
    {
        Element root = document.getRootElement();
        boolean mutable = Boolean.parseBoolean(root.getAttributeValue("mutable"));

        List<SkillGenerator> generators = new ArrayList<SkillGenerator>();
        Set<String> sources = data.getSources();
        List<Element> children = root.getChildren();
        generatorLoop:
            for (Element child : children)
            {
                List<Element> sourceElements = root.getChildren("SOURCE");
                for (Element source : sourceElements)
                {
                    if (!sources.contains(source.getText()))
                    {
                        continue generatorLoop;
                    }
                }
                try
                {
                    if (mutable)
                    {
                        generators.add(new DefaultMutableSkillGenerator(child,
                                                                        data));
                    }
                    else
                    {
                        generators.add(new DefaultSkillGenerator(child, data));
                    }
                }
                catch (GeneratorParsingException ex)
                {
                    Logging.errorPrint("Unable to create Skill generator",
                                       ex);
                }
            }
        return generators;
    }

//    public static <T extends InfoFacade> MutableFacadeGenerator<T> createMutableFacadeGenerator(String name,
//                                                                                                  FacadeGenerator<T> template)
//    {
//        MutableFacadeGenerator<T> generatorElement = new DefaultMutableFacadeGenerator<T>(name);
//        if (template != null)
//        {
//            for (T obj : template.getAll())
//            {
//                generatorElement.add(obj);
//            }
//        }
//        return generatorElement;
//    }
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

    public static MutableSkillGenerator createMutableSkillGenerator(String name,
                                                                      DataSetFacade data,
                                                                      SkillGenerator template)
    {
        Document document = getCustomGeneratorDocument(data,
                                                       "SkillGenerator");
        if (document != null)
        {
            try
            {
                Element generatorElement = new Element("GENERATOR");
                generatorElement.setAttribute("name", name).
                        setAttribute("type", "random");
                document.getRootElement().addContent(generatorElement);
                MutableSkillGenerator generator = new DefaultMutableSkillGenerator(generatorElement,
                                                                                   data);
                if (template != null)
                {
                    generator.setRandomOrder(template.isRandomOrder());
                    for (SkillFacade skillName : template.getAll())
                    {
                        generator.setSkillPriority(skillName,
                                                   template.getSkillPriority(skillName));
                    }
                }
                return generator;
            }
            catch (GeneratorParsingException ex)
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
            extends AbstractGenerator<E>
            implements FacadeGenerator<E>
    {

        public AbstractFacadeGenerator(Element element)
        {
            super(element);
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

    }

    private abstract static class DefaultOrderedGenerator<E> extends AbstractGenerator<E>
            implements OrderedGenerator<E>
    {

        protected List<E> items;
        private Queue<E> queue = null;
        protected boolean randomOrder;

        @SuppressWarnings("unchecked")
        protected DefaultOrderedGenerator(Element element)
        {
            this(element, Collections.EMPTY_LIST);
        }

        public DefaultOrderedGenerator(Element element, List<E> items)
        {
            super(element);
            this.randomOrder = element.getAttribute("type").equals("random");
            this.items = items;
            reset();
        }

        public E getNext()
        {
            if (items.isEmpty())
            {
                return null;
            }
            if (queue.isEmpty())
            {
                reset();
            }
            return queue.poll();
        }

        public boolean isRandomOrder()
        {
            return randomOrder;
        }

        @Override
        public List<E> getAll()
        {
            return items;
        }

        protected Queue<E> createQueue()
        {
            LinkedList<E> temp = new LinkedList<E>(items);
            if (randomOrder)
            {
                Collections.shuffle(temp);
            }
            return temp;
        }

        @Override
        public void reset()
        {
            queue = createQueue();
        }

    }
//
//    private static class DefaultFacadeGenerator<E extends InfoFacade> extends AbstractFacadeGenerator<E>
//    {
//
//        protected List<E> list;
//
//        @SuppressWarnings("unchecked")
//        public DefaultFacadeGenerator(Element element)
//        {
//            super(element);
//            List<Element> children = element.getChildren();
//            for(Element child : children)
//            {
//                
//            }
//            list = new Vector<E>(items);
//        }
//
//        public E getNext()
//        {
//            if (list.isEmpty())
//            {
//                return null;
//            }
//            return list.get(RandomUtil.getRandomInt(list.size()));
//        }
//
//        @Override
//        public List<E> getAll()
//        {
//            return Collections.unmodifiableList(list);
//        }
//
//    }
//
//    private static class DefaultMutableFacadeGenerator<E extends InfoFacade>
//            extends DefaultFacadeGenerator<E> implements MutableFacadeGenerator<E>
//    {
//
//        @SuppressWarnings("unchecked")
//        public DefaultMutableFacadeGenerator(String name)
//        {
//            super(name, Collections.EMPTY_LIST);
//        }
//
//        public void add(E element)
//        {
//            list.add(element);
//        }
//
//        public void remove(E element)
//        {
//            list.remove(element);
//        }
//
//        public void saveChanges()
//        {
//            throw new UnsupportedOperationException("Not supported yet.");
//        }
//
//    }
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
        //element.
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

//        public DefaultStandardModeGenerator(String name, boolean assignable,
//                                             List<String> diceExpressions)
//        {
//            super(name);
//            this.assignable = assignable;
//            this.diceExpressions = diceExpressions;
//        }
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

//        @SuppressWarnings("unchecked")
//        public DefaultMutableStandardModeGenerator(String name)
//        {
//            super(name, false, Collections.EMPTY_LIST);
//        }
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
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private static class DefaultSkillGenerator extends DefaultOrderedGenerator<SkillFacade>
            implements SkillGenerator
    {

        protected Map<SkillFacade, Integer> priorityMap;

        @SuppressWarnings("unchecked")
        public DefaultSkillGenerator(Element element, DataSetFacade data) throws GeneratorParsingException
        {
            super(element);
            this.priorityMap = new HashMap<SkillFacade, Integer>();
            List<Element> children = element.getChildren("SKILL");
            for (Element child : children)
            {
                String skillName = child.getText();
                Integer weight = Integer.valueOf(child.getAttributeValue("weight"));
                SkillFacade skill = data.getSkill(skillName);
                if (skill == null)
                {
                    throw new GeneratorParsingException(skillName +
                                                        " skill not found in DataSetFacade");
                }
                priorityMap.put(skill, weight);
            }
            this.items = new ArrayList<SkillFacade>(priorityMap.keySet());
            reset();
        }

//        public DefaultSkillGenerator(String name,
//                                      Map<SkillFacade, Integer> priorityMap,
//                                      boolean randomOrder)
//        {
//            super(name, new ArrayList<SkillFacade>(priorityMap.keySet()),
//                  randomOrder);
//            this.priorityMap = priorityMap;
//        }
        public int getSkillPriority(SkillFacade skill)
        {
            return priorityMap.get(skill);
        }

        public boolean isSingleton()
        {
            return false;
        }

        public Set<String> getSources()
        {
            Set<String> sources = new HashSet<String>();
            for (SkillFacade skill : priorityMap.keySet())
            {
                sources.add(skill.getSource());
            }
            return sources;
        }

        @Override
        protected Queue<SkillFacade> createQueue()
        {
            if (!randomOrder)
            {
                Comparator<SkillFacade> comparator = new Comparator<SkillFacade>()
                {

                    public int compare(SkillFacade o1,
                                        SkillFacade o2)
                    {
                        // compare the numbers in reverse in order for the highest priority
                        // Skills to be used first
                        return priorityMap.get(o2).compareTo(priorityMap.get(o1));
                    }

                };
                Queue<SkillFacade> queue = new PriorityQueue<SkillFacade>(priorityMap.size(),
                                                                          comparator);
                queue.addAll(priorityMap.keySet());
                return queue;
            }
            return new RandomWeightedQueue();
        }

        private class RandomWeightedQueue extends WeightedCollection<SkillFacade>
                implements Queue<SkillFacade>
        {

            private SkillFacade element = null;

            public RandomWeightedQueue()
            {
                for (SkillFacade skill : priorityMap.keySet())
                {
                    add(skill, priorityMap.get(skill));
                }
            }

            public boolean offer(SkillFacade o)
            {
                return false;
            }

            public SkillFacade poll()
            {
                if (isEmpty())
                {
                    return null;
                }
                return remove();
            }

            public SkillFacade remove()
            {
                SkillFacade skill = element();
                remove(skill);
                element = null;
                return skill;
            }

            public SkillFacade peek()
            {
                if (isEmpty())
                {
                    return null;
                }
                return element();
            }

            public SkillFacade element()
            {
                if (element == null)
                {
                    element = getRandomValue();
                }
                return element;
            }

        }
    }

    private static class DefaultMutableSkillGenerator extends DefaultSkillGenerator
            implements MutableSkillGenerator
    {

        private Element element;

        public DefaultMutableSkillGenerator(Element element, DataSetFacade data) throws GeneratorParsingException
        {
            super(element, data);
            this.element = element;
        }

//        public DefaultMutableSkillGenerator(String name)
//        {
//            super(name, new HashMap<SkillFacade, Integer>(), false);
//            // This makes getNext() work correctly
//            this.items = Collections.singletonList(null);
//        }
        @Override
        public List<SkillFacade> getAll()
        {
            return new ArrayList<SkillFacade>(priorityMap.keySet());
        }

        public void setSkillPriority(SkillFacade skill, int priority)
        {
            priorityMap.put(skill, priority);
        }

        public void setRandomOrder(boolean randomOrder)
        {
            this.randomOrder = randomOrder;
        }

        public void saveChanges()
        {
            List<Element> sources = new ArrayList<Element>();
            for (String source : getSources())
            {
                sources.add(new Element("SOURCE").setText(source));
            }
            element.removeChildren("SOURCE");
            element.addContent(0, sources);
            outputDocument(element.getDocument());
        }

    }
}
