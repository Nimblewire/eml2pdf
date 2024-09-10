package com.nimblewire.eml2pdf;

import java.io.InputStream;
import java.io.OutputStream;

public class MailToPdf {

    public static void convert(InputStream inputStream, OutputStream out) throws Throwable {
        MailReader reader=new MailReader();
        reader.load(inputStream);
        RenderHTML2Pdf toPdf=new RenderHTML2Pdf(reader);
        toPdf.convert(out);
    }
}
