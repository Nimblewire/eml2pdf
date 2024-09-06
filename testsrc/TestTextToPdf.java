import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.util.Base64;
import java.io.IOException;

public class TestTextToPdf {
    public static void main(String[] args) {

        // Define the text you want to write to the PDF
        Base64.Decoder decoder = Base64.getMimeDecoder();
        byte[] bite = decoder.decode("T25jZSB1cG9uIGEgdGltZSwgYW4gZWdnIGZlbGwgb2ZmIGEgd2FsbC4NCg0KVGhlbiwgZXZlcnlv\r\n" +
                        "bmUgd2FzIHNhZCwgYW5kIHRoZW4gdGhlIGVnZyBicm9rZSwgYW5kIHRoZW4gdGhlIHN1biByb2Fz\r\n" +
                        "dGVkIHRoZSBlZ2csIGFuZCB0aGVuIGV2ZXJ5b25lIGF0ZSBzY3JhbWJsZWQgZWdncywgYW5kIHNv\r\n" +
                        "IHRoZXkgd2VyZW7igJl0IHNhZCBhbnltb3JlIQ0KDQpUaGlzIGlzIGEgZHJ5ZXI6DQpbY2lkOjYy\r\n" +
                        "MTNBMUU4LUY2OEQtNEM0NS1BRTYxLUZBMkJFRDBCNDFFNF0NCg0KUmFuZG9tIHByb3RlY3RlZCBj\r\n" +
                        "aGFyYWN0ZXJzOg0Ke31bXSgpPD58L1wjJV4qKz0uLOKAmeKAmeKAneKAnUAmJC06Ow0KDQpBbHNv\r\n" +
                        "LCBoZXJl4oCZcyBhIC5qc29uIGZpbGUgZnJvbSBNaW5lY3JhZnQNCg==");
        String text = new String(bite);        

        // Create a new PDF document
        try (PDDocument document = new PDDocument()) {
            // Create a new page in the PDF document
            PDPage page = new PDPage();
            document.addPage(page);

            // Create a content stream to write content to the page
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set font and font size
                PDType1Font font = PDType1Font.HELVETICA;
                int fontSize = 12;
                contentStream.setFont(font, fontSize);

                // Start writing text
                contentStream.beginText();
                contentStream.setLeading(14.5f); // Set line spacing
                contentStream.newLineAtOffset(25, 700); // Position at (x, y)

                // Remove MIME formatting from the decoded "text/plain" content
                String newText = text.replace("\r", "");
                
                // Add some text cases then split the string
                newText += "\n1234567890987654321234567890987654321234567890987654321234567890987654321234567890987654321234567890987654321. Gee whiz mister, that\'s a lot of damage! Better get some Flex Tape and fix that sucker up reeeeaaaaal good!";
                String[] finalText = newText.split("\n");

                // Gets the width of the page as a float
                float width = page.getCropBox().getWidth();

                for (String string : finalText) {

                    // Finds the width of the each string and compares it to the width of the page.
                    // If the string is too big, split it up into words and add each word one at a time until it's the right size. (Help from Michael Woywod on stackoverflow.com)
                    float textWidth = fontSize * font.getStringWidth(string) / 1000;

                    if (textWidth >= width - 50.0f) {
                        String line = "";
                        String[] words = string.split(" ");
                        float lineWidth = 0.0f;
                        float wordWidth = 0.0f;

                        for (String word : words) {
                            lineWidth = fontSize * font.getStringWidth(line) / 1000;
                            wordWidth = fontSize * font.getStringWidth(word) / 1000;
                            
                            // If the width of an individual word (e.g. large number, token, fancy science term, etc...) exceeds the width of the page, break it into its
                            // characters and add them one at a time until it's the right size.
                            if (wordWidth > width - 50.0f) {
                                String[] chars = word.split("");
                                float thingWidth = 0.0f;
                                for (String thing : chars) {
                                    lineWidth = fontSize * font.getStringWidth(line) / 1000;
                                    thingWidth = fontSize * font.getStringWidth(thing) / 1000;

                                    if (lineWidth + thingWidth > width - 50.0f) {
                                        contentStream.showText(line);
                                        contentStream.newLine();
                                        line = "";
                                        line += thing;
                                        continue;
                                    }
                                    else {
                                        line += thing;
                                    }
                                }
                                line += " ";
                                continue;
                            }

                            else if (lineWidth + wordWidth > width - 50.0f) {
                                contentStream.showText(line);
                                contentStream.newLine();
                                line = "";
                                line += word + " ";
                                continue;
                            }
                            else {
                                line += word + " ";
                            }
                        }
                        contentStream.showText(line);
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
            //document.save("TextToPDFSample.pdf");
            //System.out.println("PDF created successfully.");
            System.out.println(InputFiles.getText("samples\\Danger Mail.eml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}