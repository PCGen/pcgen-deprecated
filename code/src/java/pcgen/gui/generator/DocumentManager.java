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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DocumentManager implements EntityResolver
{

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

    private static class SingletonHolder
    {

        private static DocumentManager INSTANCE = new DocumentManager();
    }

    private static DocumentManager getInstance()
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

    public static Document getDocument(URI uri)
    {
        DocumentManager instance = getInstance();
        String systemId = uri.toString();
        Document document = instance.documentMap.get(systemId);
        if (document == null)
        {
            document = instance.createDocument(systemId);
            instance.documentMap.put(systemId, document);
        }
        return document;
    }

    public static void outputDocument(Document document) throws IOException
    {
        getInstance().outputter.output(document,
                                       new FileOutputStream(new File(URI.create(document.getBaseURI()))));
    }

    private Document createDocument(String systemId)
    {
        Document document = null;
        try
        {
            document = builder.build(systemId);
            checkValidity(document);
            checkReferences(document);
        }
        catch (JDOMException ex)
        {
            Logging.log(Logging.XML_ERROR, "Failed to created document", ex);
        }
        catch (IOException ex)
        {
            Logging.log(Logging.XML_ERROR, "Error occured while accessing file",
                        ex);
        }
        return document;
    }

    private void checkValidity(Document document)
    {
        @SuppressWarnings("unchecked")
        Iterator<Element> elementIterator = document.getDescendants(new ElementFilter());
        while (elementIterator.hasNext())
        {
            Element element = elementIterator.next();
            String value = element.getAttributeValue("score");
            if (value != null && !isValidInteger(document, element,
                                                 element.getText()))
            {
                //This must be a COST element for this to be true
                elementIterator.remove();
                continue;
            }
            if (value == null)
            {
                value = element.getAttributeValue("points");
            }
            if (value == null)
            {
                value = element.getAttributeValue("weight");
            }
            if (value != null && !isValidInteger(document, element, value))
            {
                elementIterator.remove();
                continue;
            }
        }


    }

    private boolean isValidInteger(Document document, Element element,
                                    String att)
    {
        try
        {
            Integer.parseInt(att);
        }
        catch (NumberFormatException e)
        {
            Logging.log(Logging.XML_ERROR,
                        "Invalid integer value in " +
                        document.getBaseURI() + ", ignoring " + element,
                        e);
            return false;
        }
        return true;
    }

    private void checkReferences(Document document)
    {
        Element root = document.getRootElement();
        String name = root.getName();
        if (name.equals("BUILDSET"))
        {
            pruneInvalidReferences(document,
                                   root.getContent(new ElementFilter("CHARACTER_BUILD")).iterator());
            pruneInvalidReferences(document,
                                   root.getContent(new ElementFilter("ABILITY_BUILD")).iterator());
        }
        else if (name.equals("CHARACTER_BUILD") || name.equals("ABILITY_BUILD"))
        {
            pruneInvalidReferences(document,
                                   Collections.singletonList(root).iterator());
        }
    }

    private void pruneInvalidReferences(Document document, Iterator elements)
    {
        loop:
            while (elements.hasNext())
            {
                Element buildElement = (Element) elements.next();
                List children = buildElement.getContent(refFilter);
                for (Object object : children)
                {
                    if (!isValidReference(document, (Element) object))
                    {
                        Logging.log(Logging.XML_ERROR, "Invalid Reference in " +
                                    document.getBaseURI() + ", ignoring " +
                                    buildElement);
                        elements.remove();
                        continue loop;
                    }
                }
            }
    }

    private boolean isValidReference(Document document, Element refElement)
    {
        String generatorUri = refElement.getAttributeValue("uri");
        if (generatorUri != null)
        {
            URI uri = null;
            try
            {
                uri = new URI(generatorUri);
                URI baseuri = URI.create(document.getBaseURI());
                uri = baseuri.resolve(uri);
                document = getDocument(uri);
            }
            catch (URISyntaxException ex)
            {
                Logging.log(Logging.XML_ERROR,
                            "Invalid URI specified in:\n" +
                            outputter.outputString(refElement) + "\nlocated in " +
                            document.getBaseURI(), ex);
                return false;
            }
            if (document == null)
            {
                Logging.log(Logging.XML_ERROR,
                            "Unable to resolve reference to " + uri);
                return false;
            }
        }
        return hasElement(document, refElement);
    }

    private boolean hasElement(Document document, Element refElement)
    {
        final String elementName = refElement.getName().replaceFirst("_REF", "");
        final String generatorName = refElement.getAttributeValue("name");
        final String generatorCatagory = refElement.getAttributeValue("catagory");
        Filter filter = new Filter()
        {

            public boolean matches(Object obj)
            {
                if (obj instanceof Element)
                {
                    Element element = (Element) obj;
                    if (elementName.equals(element.getName()) &&
                            generatorName.equals(element.getAttributeValue("name")) &&
                            (generatorCatagory == null ||
                            generatorCatagory.equals(element.getAttributeValue("catagory"))))
                    {
                        return true;
                    }
                }
                return false;
            }

        };
        return document.getDescendants(filter).hasNext();
    }

}
