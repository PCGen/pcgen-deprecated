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
import java.util.Collections;
import java.util.HashMap;
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

    public Document getDocument(URI uri) throws JDOMException, IOException
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
        outputter.output(document,
                         new FileOutputStream(new File(URI.create(document.getBaseURI()))));
    }

    private Document createDocument(String systemId) throws JDOMException, IOException
    {
        Document document = builder.build(systemId);
        checkReferences(document);
        return document;
    }

    private void checkReferences(Document document)
    {
        Element root = document.getRootElement();
        String name = root.getName();
        if (name.equals("BUILDSET"))
        {
            pruneInvalidReferences(document,
                                   root.getContent(new ElementFilter("CHARACTER_BUILD")));
            pruneInvalidReferences(document,
                                   root.getContent(new ElementFilter("ABILITY_BUILD")));
        }
        else if (name.equals("CHARACTER_BUILD") || name.equals("ABILITY_BUILD"))
        {
            pruneInvalidReferences(document,
                                   Collections.singletonList(root));
        }
    }

    private void pruneInvalidReferences(Document document, List elements)
    {
        loop:
            for (Object element : elements)
            {
                Element buildElement = (Element) element;
                List children = buildElement.getContent(refFilter);
                for (Object object : children)
                {
                    if (!isValidReference(document, (Element) object))
                    {
                        Logging.errorPrint("Invalid Reference in " +
                                           document.getBaseURI() + ", ignoring " +
                                           buildElement);
                        buildElement.detach();
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
            try
            {
                URI uri = new URI(generatorUri);
                URI baseuri = URI.create(document.getBaseURI());
                uri = baseuri.resolve(uri);
                document = getDocument(uri);
            }
            catch (Exception ex)
            {
                Logging.errorPrint("Error occured while checking references in " +
                                   document.getBaseURI(), ex);
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
