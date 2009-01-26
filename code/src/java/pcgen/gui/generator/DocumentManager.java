/*
 * DocumentManager.java
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute buildIterator and/or
 * modify buildIterator under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that buildIterator will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Jan 22, 2009, 5:29:49 PM
 */
package pcgen.gui.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pcgen.core.SettingsHandler;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DocumentManager implements EntityResolver
{

    private static class SingletonHolder
    {

        private static DocumentManager INSTANCE = new DocumentManager();
    }

    public static DocumentManager getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private final Map<String, Document> documentMap;
    private final SAXBuilder builder;
    private final XMLOutputter outputter;

    private DocumentManager()
    {
        documentMap = new HashMap<String, Document>();
        builder = new SAXBuilder();
        builder.setEntityResolver(this);
        builder.setReuseParser(false);
        outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
    }

    public InputSource resolveEntity(final String publicId, String systemId) throws SAXException, IOException
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

    public Document getDocument(URI uri) throws JDOMException, IOException, IllegalReferenceException
    {
        String systemId = uri.toString();
        Document document = documentMap.get(systemId);
        if (document == null)
        {
            document = createDocument(systemId);
            documentMap.put(systemId, document);
        }
        return document;
    }

    public void outputDocument(Document document) throws IOException
    {
        document = (Document) document.clone();
        unresoveReferences(document);
        outputter.output(document,
                         new FileOutputStream(new File(URI.create(document.getBaseURI()))));
    }

    private Document createDocument(String systemId) throws JDOMException, IOException, IllegalReferenceException
    {
        Document document = builder.build(systemId);
        resolveReferences(document);
        validate(document);
        return document;
    }

    private void unresoveReferences(Document document)
    {
        Iterator it = document.getDescendants(new ElementFilter("EXT_GENERATOR"));
        while (it.hasNext())
        {
            Element element = (Element) it.next();
            element.removeContent();
        }
    }

    private void resolveReferences(Document document) throws IllegalReferenceException, JDOMException, IOException
    {
        Iterator it = document.getDescendants(new ElementFilter("EXT_GENERATOR"));
        while (it.hasNext())
        {
            Element ext = (Element) it.next();
            try
            {
                final String generatorName = ext.getAttributeValue("name");
                URI uri = new URI(ext.getAttributeValue("uri"));
                URI baseuri = URI.create(document.getBaseURI());
                uri = baseuri.resolve(uri);
                Document extdocument = getDocument(uri);
                Filter filter = new Filter()
                {

                    public boolean matches(Object obj)
                    {
                        if (obj instanceof Element)
                        {
                            Element element = (Element) obj;
                            if (!element.getName().equals("EXT_GENERATOR"))
                            {
                                String value = element.getAttributeValue("name");
                                if (value != null && value.equals(generatorName))
                                {
                                    return true;
                                }
                                value = element.getAttributeValue("catagory");
                                if (value != null && value.equals(generatorName))
                                {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }

                };
                Iterator extit = extdocument.getDescendants(filter);
                if (extit.hasNext())
                {
                    Element element = (Element) extit.next();
                    ext.addContent((Element) element.clone());
                }
                else
                {
                    throw new IllegalReferenceException("Generator not found in " +
                                                        document.getBaseURI());
                }
            }
            catch (URISyntaxException ex)
            {
                throw new IllegalReferenceException("Malformed uri:" +
                                                    ex.getInput() + "in " +
                                                    document.getBaseURI(), ex);
            }
        }
    }

    private void validate(Document document) throws IllegalReferenceException
    {
        Iterator buildIterator = document.getDescendants(new ElementFilter("CHARACTER_BUILD"));
        while (buildIterator.hasNext())
        {
            Element element = (Element) buildIterator.next();
            @SuppressWarnings("unchecked")
            Iterator<Element> childIterator = element.getChildren().iterator();
            childIterator.next(); // The gender generator does not matter
            element = childIterator.next();
            if (element.getName().equals("EXT_GENERATOR") &&
                    element.getChild("ALIGNMENT_GENERATOR") == null)
            {
                throw new IllegalReferenceException("EXT_GENERATOR does not contain" +
                                                    " a reference to a ALIGNMENT_GENERATOR : " +
                                                    document.getBaseURI());
            }
            element = childIterator.next();
            if (element.getName().equals("EXT_GENERATOR") &&
                    element.getChild("RACE_GENERATOR") == null)
            {
                throw new IllegalReferenceException("EXT_GENERATOR does not contain" +
                                                    " a reference to a RACE_GENERATOR : " +
                                                    document.getBaseURI());
            }
            element = childIterator.next();
            if (element.getName().equals("EXT_GENERATOR") &&
                    element.getChild("CLASS_GENERATOR") == null)
            {
                throw new IllegalReferenceException("EXT_GENERATOR does not contain" +
                                                    " a reference to a CLASS_GENERATOR : " +
                                                    document.getBaseURI());
            }
            element = childIterator.next();
            if (element.getName().equals("EXT_GENERATOR") &&
                    element.getChild("STANDARDMODE_GENERATOR") == null &&
                    element.getChild("PURCHASEMODE_GENERATOR") == null)
            {
                throw new IllegalReferenceException("EXT_GENERATOR does not contain" +
                                                    " a reference to a stat generator : " +
                                                    document.getBaseURI());
            }
            element = childIterator.next();
            if (element.getName().equals("EXT_GENERATOR") &&
                    element.getChild("SKILL_GENERATOR") == null)
            {
                throw new IllegalReferenceException("EXT_GENERATOR does not contain" +
                                                    " a reference to a SKILL_GENERATOR : " +
                                                    document.getBaseURI());
            }
            element = childIterator.next();
            if (element.getName().equals("EXT_GENERATOR") &&
                    element.getChild("ABILITY_BUILD") == null)
            {
                throw new IllegalReferenceException("EXT_GENERATOR does not contain" +
                                                    " a reference to a ABILITY_BUILD : " +
                                                    document.getBaseURI());
            }
            element = childIterator.next();
            if (element.getName().equals("EXT_GENERATOR") &&
                    element.getChild("EQUIPMENT_GENERATOR") == null)
            {
                throw new IllegalReferenceException("EXT_GENERATOR does not contain" +
                                                    " a reference to a EQUIPMENT_GENERATOR : " +
                                                    document.getBaseURI());
            }
            element = childIterator.next();
            if (element.getName().equals("EXT_GENERATOR") &&
                    element.getChild("SPELL_GENERATOR") == null)
            {
                throw new IllegalReferenceException("EXT_GENERATOR does not contain" +
                                                    " a reference to a SPELL_GENERATOR : " +
                                                    document.getBaseURI());
            }
            element = childIterator.next();
            if (element.getName().equals("EXT_GENERATOR") &&
                    element.getChild("TEMPLATE_GENERATOR") == null)
            {
                throw new IllegalReferenceException("EXT_GENERATOR does not contain" +
                                                    " a reference to a TEMPLATE_GENERATOR : " +
                                                    document.getBaseURI());
            }
        }
    }

}
