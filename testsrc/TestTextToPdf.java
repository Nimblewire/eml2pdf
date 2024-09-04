import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.util.Base64;
import java.io.IOException;

public class TestTextToPdf {
    public static void main(String[] args) {
        // Define the text you want to write to the PDF
        String text = "Hello, PDFBox! This is a sample text being written to a PDF file.";
        Base64.Decoder decoder = Base64.getMimeDecoder();
        byte[] bite = decoder.decode("T25jZSB1cG9uIGEgdGltZSwgYW4gZWdnIGZlbGwgb2ZmIGEgd2FsbC4NCg0KVGhlbiwgZXZlcnlv\r\n" + //
                        "bmUgd2FzIHNhZCwgYW5kIHRoZW4gdGhlIGVnZyBicm9rZSwgYW5kIHRoZW4gdGhlIHN1biByb2Fz\r\n" + //
                        "dGVkIHRoZSBlZ2csIGFuZCB0aGVuIGV2ZXJ5b25lIGF0ZSBzY3JhbWJsZWQgZWdncywgYW5kIHNv\r\n" + //
                        "IHRoZXkgd2VyZW7igJl0IHNhZCBhbnltb3JlIQ0KDQpUaGlzIGlzIGEgZHJ5ZXI6DQpbY2lkOjYy\r\n" + //
                        "MTNBMUU4LUY2OEQtNEM0NS1BRTYxLUZBMkJFRDBCNDFFNF0NCg0KUmFuZG9tIHByb3RlY3RlZCBj\r\n" + //
                        "aGFyYWN0ZXJzOg0Ke31bXSgpPD58L1wjJV4qKz0uLOKAmeKAmeKAneKAnUAmJC06Ow0KDQpBbHNv\r\n" + //
                        "LCBoZXJl4oCZcyBhIC5qc29uIGZpbGUgZnJvbSBNaW5lY3JhZnQNCg==");
        String realText = new String(bite);
        System.out.println(realText);
        

        // Create a new PDF document
        try (PDDocument document = new PDDocument()) {
            // Create a new page in the PDF document
            PDPage page = new PDPage();
            document.addPage(page);

            // Create a content stream to write content to the page
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set font and font size
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                // Start writing text
                contentStream.beginText();
                contentStream.setLeading(14.5f); // Set line spacing
                contentStream.newLineAtOffset(25, 700); // Position at (x, y)

                // Write text to the PDF
                contentStream.showText(text);
                contentStream.newLine();

                // If you want to write another line, use `contentStream.showText()` and `contentStream.newLine()`
                String newText = realText.replace("\r", "");
                String[] finalText = newText.split("\n");

                //Gets the width of the page as a float
                float width = page.getCropBox().getWidth();

                for (String string : finalText) {

                    //Finds the width of the each string and compares it to the width of the page. (Help from Michael Woywod on stackoverflow.com)
                    PDType1Font font = PDType1Font.HELVETICA;
                    float textWidth = 12 * font.getStringWidth(string) / 1000;

                    if (textWidth >= width) {
                        //TODO Split up the lines that are longer than the page
                        contentStream.showText("Nerd");
                        contentStream.newLine();
                    }
                    else {
                        contentStream.showText(string);
                        contentStream.newLine();
                    }
                }

                // End text writing
                contentStream.endText();
            }

            
            // Save the document to a file
            document.save("TextToPDFSample.pdf");
            System.out.println("PDF created successfully.");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}