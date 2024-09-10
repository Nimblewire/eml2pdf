import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;

public class TestTextToPdf {
    static void createPDF(String[] text) {
        // Create a new PDF document
        // TODO Create new pages as needed.
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
                contentStream.newLineAtOffset(25, 750); // Position at (x, y)

                // Gets the width of the page as a float
                float width = page.getCropBox().getWidth();

                for (String string : text) {

                    // Finds the width of the each string and compares it to the width of the page.
                    float textWidth = 0.0f;

                    // Check if there are any unknown characters in the current line. 
                    try {
                        textWidth = fontSize * font.getStringWidth(string) / 1000;

                    // If unknown characters are found, break the line into individual characters and replace the bad ones with "?" (Help from TIlman Hausherr on apache.org and Copilot AI)
                    } catch (IllegalArgumentException e) {
                        String sanitized = "";
                        String[] reviewChars = string.split("");
                        
                        for (String item : reviewChars) {
                            try {
                                font.getStringWidth(item);
                                sanitized += item;
                            
                            } catch (IllegalArgumentException e2) {
                                sanitized += "?";
                            }
                        }

                        // Swap the string with the unusable characters for the sanitized one
                        string = sanitized;
                        textWidth = fontSize * font.getStringWidth(string) / 1000;
                    }

                    // If the string is too big, split it up into words and add each word one at a time until it's the right size. (Help from Michael Woywod on stackoverflow.com)
                    if (textWidth > width - 50.0f) {
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
            document.save("TextToPDFSample.pdf");
            System.out.println("PDF created successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String[] fileData = InputFiles.getLines("samples\\Business Email Small.eml");
        createPDF(fileData);
    }
}