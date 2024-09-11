package com.nimblewire.eml2pdf;

import com.openhtmltopdf.extend.FSCache;
import com.openhtmltopdf.extend.FSUriResolver;
import com.openhtmltopdf.pdfboxout.PdfBoxImage;
import com.openhtmltopdf.resource.ImageResource;
import com.openhtmltopdf.swing.AWTFSImage;
import com.openhtmltopdf.swing.FSCacheKey;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class MailResolver implements FSUriResolver, FSCache
{
    private final MailReader reader;
    private final PDDocument pdf;
    private final HashMap<FSCacheKey, Object> cache=new HashMap<>();

    public MailResolver(MailReader reader,PDDocument pdf) {
        this.reader = reader;
        this.pdf = pdf;
    }

    @Override
    public String resolveURI(String s, String s1) {
        return s1;
    }


    @Override
    public Object get(FSCacheKey fsCacheKey) {
        String uri=fsCacheKey.getUri();
        Object obj=cache.get(fsCacheKey);
        if (obj!=null || cache.containsKey(fsCacheKey))
        {
            return obj;
        }
        // see if it is in the reader
        ContentPart part = reader.findImage(uri);
        byte[] data=null;
        if (part!=null)
        {
            data=part.getContent();
        }
        else if (uri.toLowerCase().startsWith("http"))
        {
            part=IOUtil.getRemoteImage(uri);
            if (part!=null)
            {
                data=part.getContent();
            }
        }
        if (data!=null)
        {
            Class clazz = fsCacheKey.getClazz();
            if (clazz.getSimpleName().equals("AWTFSImage"))
            {
                try
                {
                    BufferedImage image = IOUtil.decode(data);
                    obj= AWTFSImage.createImage(image);
                }
                catch(IOException ioe)
                {
                    // ignore
                }
            }
            else
            {
                try
                {
                    PdfBoxImage img = new PdfBoxImage(data,uri);
                    img.setXObject(PDImageXObject.createFromByteArray(pdf,data,uri));
                    obj = new ImageResource(uri,img);
                }
                catch(IOException ioe)
                {
                    // ignore
                }
            }

        }
        cache.put(fsCacheKey,obj);
        return obj;

    }

    @Override
    public void put(FSCacheKey fsCacheKey, Object o) {
        cache.put(fsCacheKey,o);
    }
}
