package com.nimblewire.eml2pdf;

import com.openhtmltopdf.extend.ReplacedElementFactory;
import com.openhtmltopdf.pdfboxout.PdfBoxRenderer;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class RenderHTML2Pdf extends PdfRendererBuilder
{
    private final MailReader reader;

    public RenderHTML2Pdf(MailReader reader) {
        this.reader=reader;
    }

    public void convert(OutputStream out) throws Throwable
    {
        PDDocument doc = new PDDocument();
        MailResolver resolver=new MailResolver(reader,doc);
        //ImageCache cache=new ImageCache(reader);

        usePDDocument(doc);
        useCache(resolver);
        useUriResolver(resolver);
        Document document=tidy(reader.getBody());
        //preProcess(document);
        withW3cDocument(document,"@@server");
        toStream(out);
        PdfBoxRenderer renderer = null;
        try {
            renderer = this.buildPdfRenderer();
            /*
                ReplacedElementFactory factory=renderer.getSharedContext().getReplacedElementFactory();
                renderer.getSharedContext().setReplacedElementFactory(new PdfBoxReplacedElementFactoryEx(factory,request));
             */
            renderer.layout();
            renderer.createPDF();
        } finally {
            doc.close();
            if(renderer != null) {
                renderer.close();
            }
        }
    }

    private Document tidy(String body)
    {
        Tidy tidy = new Tidy();
        tidy.setWord2000(true);
        tidy.setQuiet(true);
        tidy.setXmlOut(true);
        tidy.setNumEntities(true);
        tidy.setTidyMark(false);
        //tidy.setQuoteNbsp(true);
        tidy.setMakeClean(true);
  //      tidy.setIndentContent(indent);
        tidy.setInputEncoding("UTF-8"); // this might not always be the case, should probably detect it
        tidy.setOutputEncoding("UTF-8");
        // make it silent:
        tidy.setShowWarnings(false);
        tidy.setShowErrors(0);
        return tidy.parseDOM(new ByteArrayInputStream(body.getBytes()),new ByteArrayOutputStream());
    }

}
