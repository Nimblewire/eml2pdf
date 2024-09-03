import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;

public class TestTextToPdf {
    public static void main(String[] args) {
        // Define the text you want to write to the PDF
        String text = "Hello, PDFBox! This is a sample text being written to a PDF file.";

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

                // End text writing
                contentStream.endText();
            }

            // Save the document to a file
            document.save("TextToPDFExample.pdf");
            System.out.println("PDF created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}