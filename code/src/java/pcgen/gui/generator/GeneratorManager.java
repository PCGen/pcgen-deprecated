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
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
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
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class GeneratorManager
{

    public static final class GeneratorType<E>
    {

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
        }

    }

    private <T> List<T> getGeneratorList(Document document,
                                          GeneratorType<T> type)
    {
        return null;
    }

    private final DataSetFacade data;
    private final List<WeightedGenerator<AlignmentFacade>> alignmentGenerators;
    private final List<InfoFacadeGenerator<RaceFacade>> raceGenerators;
    private final List<InfoFacadeGenerator<ClassFacade>> classGenerators;
    private final List<InfoFacadeGenerator<SkillFacade>> skillGenerators;

    public GeneratorManager(DataSetFacade data)
    {
        this.data = data;
        this.alignmentGenerators = new ArrayList<WeightedGenerator<AlignmentFacade>>();
        this.raceGenerators = new ArrayList<InfoFacadeGenerator<RaceFacade>>();
        this.classGenerators = new ArrayList<InfoFacadeGenerator<ClassFacade>>();
        this.skillGenerators = new ArrayList<InfoFacadeGenerator<SkillFacade>>();
        GameModeFacade gameMode = data.getGameMode();

        Document document = DocumentManager.getDocument(gameMode.getGeneratorFile().toURI());
        if (document != null)
        {
            alignmentGenerators.addAll(buildGeneratorList(GeneratorType.ALIGNMENT,
                                                      document));
        }

        GenericListModelWrapper<AlignmentFacade> alignments = new GenericListModelWrapper<AlignmentFacade>(gameMode.getAlignments());
        for (AlignmentFacade alignmentFacade : alignments)
        {
            alignmentGenerators.add(new SingletonWeightedGenerator<AlignmentFacade>(alignmentFacade));
        }
        addSingletonInfoFacadeGenerators(data.getRaces(), raceGenerators);
        addSingletonInfoFacadeGenerators(data.getClasses(), classGenerators);
        addSingletonInfoFacadeGenerators(data.getSkills(), skillGenerators);
    }

    private <T extends InfoFacade> void addSingletonInfoFacadeGenerators(GenericListModel<T> model,
                                                                          List<InfoFacadeGenerator<T>> generators)
    {
        GenericListModelWrapper<T> list = new GenericListModelWrapper<T>(model);
        for (T infoFacade : list)
        {
            generators.add(new SingletonInfoFacadeGenerator<T>(infoFacade));
        }
    }

    public static final void main(String[] arg)
    {

        File file = new File("build/classes/generators/DefaultStandardGenerators.xml");
        try
        {
            Integer.parseInt(null);
        }
        catch (NumberFormatException e)
        {
            System.out.println("failed");
        }

    }

    private <T> List<T> buildGeneratorList(GeneratorType<? extends T> type,
                                            Document document)
    {
        Element root = document.getRootElement();
        boolean mutable;
        if (root.getName().equals("BUILDSET"))
        {
            mutable = Boolean.parseBoolean(root.getAttributeValue("mutable"));
        }
        else
        {
            mutable = false;
        }
        List<T> generators = new ArrayList<T>();
        Set<String> sources = data.getSources();
        Iterator it = document.getDescendants(new ElementFilter(type.element));
        generatorLoop:
            while (it.hasNext())
            {
                Object element = it.next();
                @SuppressWarnings("unchecked")
                List<Element> sourceElements = ((Element) element).getChildren("SOURCE");
                if (sourceElements != null)
                {
                    for ( Element source : sourceElements)
                    {
                        if (!sources.contains(source.getText()))
                        {
                            continue generatorLoop;
                        }
                    }
                }
                try
                {
                    Class<? extends T> c;
                    if (mutable)
                    {
                        c = type.mutableClass;
                    }
                    else
                    {
                        c = type.baseClass;
                    }
                    T generator = (T) c.getConstructor(Element.class).newInstance(element);
                    generators.add(generator);
                }
                catch ( Exception ex)
                {
                    Logging.errorPrint("Unable to create generator",
                                       ex);
                }
            }
        return generators;
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
            Element alignmentElement = (Element) element.getContent(0);
            if (alignmentElement.getName().equals("ALIGNMENT_GENERATOR"))
            {
                alignmentGenerator = new DefaultAlignmentGenerator(alignmentElement);
            }
            else
            {

            }
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

    private class DefaultAlignmentGenerator extends AbstractWeightedGenerator<AlignmentFacade>
    {

        //private Queue<E> queue = null;
        public DefaultAlignmentGenerator(Element element) throws MissingDataException
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
        protected Element element;

        public AbstractGenerator(String name)
        {
            this.name = name;
        }

        public AbstractGenerator(Element element)
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

        protected Map<E, Integer> priorityMap;
        private Queue<E> queue = null;

        public AbstractWeightedGenerator(Element element) throws MissingDataException
        {
            super(element);

            element.removeChildren("SOURCE");
            @SuppressWarnings("unchecked")
            List<Element> children = element.getChildren();
            for ( Element child : children)
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

        @SuppressWarnings("unchecked")
        public AbstractInfoFacadeGenerator(Element element) throws MissingDataException
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
            extends AbstractInfoFacadeGenerator<E> implements MutableInfoFacadeGenerator<E>
    {

        private AbstractInfoFacadeGenerator<E> generator = null;

        public AbstractMutableInfoFacadeGenerator(AbstractInfoFacadeGenerator<E> generator) throws MissingDataException
        {
            super(generator.element);
            this.generator = generator;
        }

        public AbstractMutableInfoFacadeGenerator(Element element) throws MissingDataException
        {
            super(element);
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

        private DefaultPurchaseModeGenerator generator = null;

        public DefaultMutablePurchaseModeGenerator(DefaultPurchaseModeGenerator generator)
        {
            super(generator.element);
            this.generator = generator;
        }

        public DefaultMutablePurchaseModeGenerator(Element element)
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

        private DefaultStandardModeGenerator generator = null;

        public DefaultMutableStandardModeGenerator(DefaultStandardModeGenerator generator)
        {
            super(generator.element);
            this.generator = generator;
        }

        public DefaultMutableStandardModeGenerator(Element element)
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

        public DefaultRaceGenerator(Element element) throws MissingDataException
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

        public DefaultMutableRaceGenerator(Element element) throws MissingDataException
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

        public DefaultClassGenerator(Element element) throws MissingDataException
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

        public DefaultMutableClassGenerator(Element element) throws MissingDataException
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

        public DefaultSkillGenerator(Element element) throws MissingDataException
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

        public DefaultMutableSkillGenerator(Element element) throws MissingDataException
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

        public DefaultAbilityBuild(Element element) throws MissingDataException
        {
            this.name = element.getAttributeValue("name");
            this.generatorMap = new HashMap<AbilityCatagoryFacade, InfoFacadeGenerator<AbilityFacade>>();
            @SuppressWarnings("unchecked")
            List<Element> children = element.getChildren("GENERATOR");
            for ( Element element1 : children)
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

        public DefaultMutableAbilityBuild(Element element) throws MissingDataException
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

        public DefaultMutableAbilityGenerator(Element element) throws MissingDataException
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
