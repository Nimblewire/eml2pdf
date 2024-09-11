package com.nimblewire.eml2pdf;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class IOUtil {

    public static ContentPart getRemoteImage(String uri)
    {
        return null;
    }

    public static BufferedImage decode(byte[] imageData) throws java.io.IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(imageData);
        return decode(in);
    }

    /**
     * Reads an input stream to produce a Java compatible image.
     *
     * @param in stream to read image from
     * @since v4.0
     * @return buffered image
     * @throws java.io.IOException on codec or stream error
     */
    public static BufferedImage decode(InputStream in) throws java.io.IOException
    {
        try(ImageInputStream input = ImageIO.createImageInputStream(in))
        {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

            while (readers != null && readers.hasNext())
            {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(input);
                    return reader.read(0);
                } catch (Throwable e) {
                    reader.dispose();
                    // Try next reader, ignore.
                } finally {
                    reader.dispose();
                }
            }
            throw new IOException("Unsupported image type");
        }
    }
}
