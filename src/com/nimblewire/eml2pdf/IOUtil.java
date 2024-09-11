package com.nimblewire.eml2pdf;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class IOUtil {

    public static ContentPart getRemoteImage(String uri)
    {
        try
        {
            URL imageUrl = new URL(uri);

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537");

            // Set timeouts (in milliseconds)
            connection.setConnectTimeout(5000);  // 5 seconds for connection
            connection.setReadTimeout(5000);     // 5 seconds for reading the data


            // Check for a successful response code (200 OK)
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the input stream from the connection
                InputStream inputStream = connection.getInputStream();

                // Read the image into a BufferedImage
                BufferedImage image = ImageIO.read(inputStream);

                // Convert BufferedImage to a byte array
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //todo format from the url connection
                ImageIO.write(image, "jpg", byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                // Print the byte array length (or use the bytes as needed)
                System.out.println("Image downloaded, byte array size: " + imageBytes.length);

                // Close streams
                inputStream.close();
                byteArrayOutputStream.close();
                ContentPart content = new ContentPart();
                content.setFilename("");    //todo
                content.setContent(imageBytes);
                content.setContentType(""); //todo
                return content;
            }
        }
        catch(Throwable t)
        {
            //todo use a default image

        }
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
