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
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
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
import pcgen.core.SettingsHandler;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.DataSetFacade;
import pcgen.gui.facade.InfoFacade;
import pcgen.gui.facade.SkillFacade;
import pcgen.gui.generator.skill.MutableSkillGenerator;
import pcgen.gui.generator.skill.SkillGenerator;
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

    static List<StandardModeGenerator> buildStandardModeGeneratorList(Document document)
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

    static List<PurchaseModeGenerator> buildPurchaseModeGeneratorList(Document document)
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
    static List<SkillGenerator> buildSkillGeneratorList(Document document,
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
                        generator.setWeight(skillName,
                                            template.getWeight(skillName));
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
            extends DefaultOrderedGenerator<E>
            implements FacadeGenerator<E>
    {

        protected Map<E, Integer> priorityMap;

        @SuppressWarnings("unchecked")
        public AbstractFacadeGenerator(Element element, DataSetFacade data) throws GeneratorParsingException
        {
            super(element);
            this.priorityMap = new HashMap<E, Integer>();

            this.items = new ArrayList<E>(priorityMap.keySet());

            List<Element> children = element.getChildren(getValueName());
            for (Element child : children)
            {
                String elementName = child.getText();
                Integer weight = Integer.valueOf(child.getAttributeValue("weight"));
                E facade = getFacade(data, elementName);
                if (facade == null)
                {
                    throw new GeneratorParsingException(elementName +
                                                        " not found in DataSetFacade");
                }
                priorityMap.put(facade, weight);
            }
            reset();
        }

        protected abstract E getFacade(DataSetFacade data, String name);

        protected abstract String getValueName();

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
        protected Queue<E> createQueue()
        {
            if (!randomOrder)
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

    private static class DefaultClassGenerator extends AbstractFacadeGenerator<ClassFacade>
    {

        public DefaultClassGenerator(Element element, DataSetFacade data) throws GeneratorParsingException
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
            implements SkillGenerator
    {

        @SuppressWarnings("unchecked")
        public DefaultSkillGenerator(Element element, DataSetFacade data) throws GeneratorParsingException
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

        public void setRandomOrder(boolean randomOrder)
        {
            this.randomOrder = randomOrder;
        }

        public void saveChanges()
        {
            element.removeContent();
            element.setAttribute("type", randomOrder ? "random" : "ordered");
            for (String source : getSources())
            {
                element.addContent(new Element("SOURCE").setText(source));
            }
            for (SkillFacade skill : priorityMap.keySet())
            {
                Element skillElement = new Element("SKILL");
                skillElement.setAttribute("weight",
                                          priorityMap.get(skill).toString());
                skillElement.setText(skill.toString());
                element.addContent(skillElement);
            }
            outputDocument(element.getDocument());
        }

        public void setWeight(SkillFacade item, int weight)
        {
            priorityMap.put(item, weight);
        }

    }
}
