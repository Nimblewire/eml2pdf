import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashSet;
import java.io.File;

public class InputFiles {
    // TODO handle quoted-printable encoded content
    static String decodeQuoted(String data, String contentType) {
        // Remove \r and trailing "="
        String prettyData = data.replace("\r", "");
        prettyData = prettyData.replace("=\n", "\n");

        // TODO Decode UTF-8 hex values
        // TODO Make sure there's not a security concern with decoding the protected characters
        HashSet<String> hexes = new HashSet<String>();    // A special type of array where every item is unique. This will prevent attempts to replace the same character twice

        // Use RegEx to find the UTF-8 hex characters in the data and add them to the HashSet.
        Pattern fHex = Pattern.compile("=F[a-zA-z0-9](=([a-zA-Z0-9]){2}){3}", Pattern.MULTILINE);
        Matcher fMatcher = fHex.matcher(prettyData);
        boolean fFound = fMatcher.find();

        while(fFound) {
            if (!fFound) {
                break;
            }
            String foundChar = prettyData.substring(fMatcher.start(), fMatcher.end());
            hexes.add(foundChar);
            fFound = fMatcher.find();
        }

        Pattern eHex = Pattern.compile("=E[a-zA-z0-9](=([a-zA-Z0-9]){2}){2}", Pattern.MULTILINE);
        Matcher eMatcher = eHex.matcher(prettyData);
        boolean eFound = eMatcher.find();

        while(eFound) {
            if (!eFound) {
                break;
            }
            String foundChar = prettyData.substring(eMatcher.start(), eMatcher.end());
            hexes.add(foundChar);
            eFound = eMatcher.find();
        }

        // Print statments for debugging. Remove before deployment
        for (String string : hexes) {
            System.out.println(string);
        }

        // TODO use string.replace() to swap out all the values in hexes

        System.out.println("\n**********PRETTY**********\n" + prettyData);

        return "";
    }

    // TODO handle base64 encoded content
    static String decodeBase64(String data, String contentType) {
        return "";
    }

    static String getText(String path) {
        try {
            File eml = new File(path);
            Scanner reader = new Scanner(eml);
            String rawData = "";
            String data = "";
            String line = "";
            String startKey = "Content-Type: text/plain;";
            String boundaryKey = "Content-Type: multipart/alternative;";
            String encodingKey = "Content-Transfer-Encoding:";
            String idKey = "boundary=\"";
            String id = "";
            String encoding = "";
            String contentType = "";
            boolean startFlag = false;
            boolean encodingFlag = false;

            // Move the Scanner past the boundaryKey then to the idKey line and get the ending id
            reader.findWithinHorizon(boundaryKey, 0);
            reader.findWithinHorizon(idKey, 0);
            String idLine = reader.nextLine();
            idLine = idLine.replace("\"", "");
            id = "--" + idLine;

            // Watch for the Content-Type and Content-Transfer-Encoding MIME headers and store their data
            while(reader.hasNextLine()) {
                line = reader.nextLine();
                if (reader.findInLine(startKey) != null) {
                    contentType = reader.nextLine().trim();    // Will hold both the type and the charset of the content
                    startFlag = true;
                }

                if (reader.findInLine(encodingKey) != null) {
                    encoding = reader.nextLine();
                    encodingFlag = true;
                }
                
                if (startFlag && encodingFlag) {
                    break;
                }
            }

            // Watch for where the wanted data starts after the MIME headers are done
            while (reader.hasNextLine()) {
                line = reader.nextLine();
                if (line.isEmpty()) {
                    break;
                }
            }

            // Add wanted data to a string and watch for the ending id
            while (reader.hasNextLine()) {
                if (reader.findInLine(id) == null) {
                    rawData += reader.nextLine() + "\r\n";
                }
                else {
                    break;
                }
            }
            data = rawData.trim();
            reader.close();

            String fileData = "";
            switch (encoding) {
                case "quoted-printable":
                    fileData = decodeQuoted(data, contentType);
                    break;
            
                case "base64":
                    fileData = decodeBase64(data, contentType);
                    break;

                default:
                    fileData = decodeQuoted(data, contentType);
                    break;
            }

            return fileData;

        } catch (Exception e) {
            return "Fail :(\n" + e;
        }
    }    
}
