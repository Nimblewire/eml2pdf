package com.nimblewire.eml2pdf;


import com.sun.mail.util.QPDecoderStream;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.util.SharedByteArrayInputStream;
import java.io.*;
import java.util.*;

/**
 * This reads the text base message into its individual parts.  It assumes a body with potential attachments.
 */

public class MailReader {

    private final ArrayList<ContentPart> parts=new ArrayList<>();
    private String body;

    public MailReader() {
    }

    public ArrayList<ContentPart> getParts() {
        return parts;
    }

    public String getBody() {
        return body;
    }

    public ContentPart findImage(String id)
    {
        return null;
    }

    public void load(InputStream inputStream) throws MessagingException, IOException {
        MimeMessage message= new MimeMessage(null, inputStream);

        if (message.isMimeType("multipart/*"))
        {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++)
            {
                Part p = multipart.getBodyPart(i);
                String fileName = p.getFileName();
                if (fileName != null)
                {
                    ContentPart contentPart=new ContentPart();
                    contentPart.setFilename(fileName);
                    contentPart.setContent(read(p.getInputStream()));
                    contentPart.setContentType(p.getContentType());
                    parts.add(contentPart);
                }
            }
        }
        body = getText(message);
    }

    private String getText(Part p) throws MessagingException, IOException
    {
        if (p.isMimeType("text/*"))
        {
            try
            {
                Object o = p.getContent();
                String s;
                if (o instanceof QPDecoderStream)
                {
                    byte[] b=readRestrictedStream((QPDecoderStream)o,4*1024*1024);
                    s=fromUTF8Bytes(b);
                }
                else if (o instanceof SharedByteArrayInputStream)
                {
                    byte[] b=readRestrictedStream((SharedByteArrayInputStream)o,4*1024*1024);
                    s=fromUTF8Bytes(b);
                }
                else if (o instanceof String)
                {
                    s = (String)o;
                }
                else
                {
                    throw new IOException("Unknown part "+o.getClass().getName());
                }
                return s;
            }
            catch (UnsupportedEncodingException e)
            {
                return null;
            }
        }

        if (p.isMimeType("multipart/alternative"))
        {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++)
            {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain"))
                {
                    if (text == null)
                    {
                        text = getText(bp);
                    }
                }
                else if (bp.isMimeType("text/html"))
                {
                    String s = getText(bp);
                    if (s != null)
                    {
                        return s;
                    }
                }
                else
                {
                    return getText(bp);
                }
            }
            return text;
        }
        else if (p.isMimeType("multipart/*"))
        {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++)
            {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                {
                    return s;
                }
            }
        }

        return null;
    }


   private byte[] read(InputStream in) throws IOException {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            read(in, buf);
            return buf.toByteArray();
        }

    private void read(InputStream in, OutputStream out) throws IOException {
        if (in != null && out != null) {
            byte[] temp = new byte[4096];

            int len;
            while ((len = in.read(temp)) != -1) {
                out.write(temp, 0, len);
            }
        }
    }

    private byte[] readRestrictedStream(InputStream is, long maxSize) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (is != null) {
            BufferedInputStream bufIn = new BufferedInputStream(is);
            int bytesRead = 0;
            long totalRead = 0;
            byte[] buff = new byte[16384];

            while ( (bytesRead = bufIn.read(buff)) != -1) {
                bos.write(buff, 0, bytesRead);
                totalRead+=bytesRead;
                if (totalRead>maxSize)
                {
                    throw new IOException("Read size exceeded");
                }
            }
        }
        return bos.toByteArray();
    }

    private String fromUTF8Bytes(byte[] bytes)
    {
        if (null == bytes)
        {
            return null;
        }
        else
        {
            try
            {
                return new String(bytes, "UTF-8");
            }
            catch (Exception ex)
            {
                return new String(bytes);
            }
        }
    }

}
