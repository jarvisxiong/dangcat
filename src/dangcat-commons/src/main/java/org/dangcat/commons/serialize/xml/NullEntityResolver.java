package org.dangcat.commons.serialize.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class NullEntityResolver implements EntityResolver
{
    private static ByteArrayInputStream emptyInputStream = new ByteArrayInputStream(new byte[] {});

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
    {
        return new InputSource(emptyInputStream);
    }
}
