import java.util.Scanner;
import java.io.File;

public class InputFiles {
    static String getAltData() {
        /*
        String rawData = "Content-Type: multipart/mixed; boundary=\"0000000000002affc906214e4624\"\r\n" +
            "--0000000000002affc906214e4624\r\n" +
            "Content-Type: multipart/related; boundary=\"0000000000002affc806214e4623\"\r\n" +
            "--0000000000002affc806214e4623\r\n" +
            "Content-Type: multipart/alternative; boundary=\"0000000000002affc706214e4622\"\r\n" +    // This is the alternative boundary id
            "--0000000000002affc706214e4622\r\n" +
            "Content-Type: text/plain; charset=\"UTF-8\"\r\n" +
            "Content-Transfer-Encoding: quoted-printable\r\n" +
            "\n\r\n" +
            "Once upon a time, an egg fell off a wall.\r\n" +    // Start of desired data
            "\n\r\n" +
            "Then, everyone was sad, and then the egg broke, and then the sun roasted\r\n" +
            "the egg, and then everyone ate scrambled eggs, and so they weren=E2=80=99t =\r\n" +
            "sad\r\n" +
            "anymore!\r\n" +
            "\n\r\n" +
            "This is a dryer:\r\n" +
            "\n\r\n" +
            "Random protected characters:\r\n" +
            "{}[]()<>|/\\#%^*+=3D.,=E2=80=99=E2=80=99=E2=80=9D=E2=80=9D@&$-:;\r\n"+
            "\n\r\n" +
            "Also, here=E2=80=99s a .json file from Minecraft\r\n" +
            "\n\r\n" +
            "--0000000000002affc706214e4622\r\n" +    // This is the alternative boundary id - End of desired data
            "Content-Type: text/html; charset=\"UTF-8\"\r\n" +
            "Content-Transfer-Encoding: quoted-printable\r\n";
        */

        // TODO Read the file more efficiently
        try {
            File eml = new File("samples\\Lookit this test!-quoted.eml");
            Scanner reader = new Scanner(eml);
            String rawData = "";
            String line = "";

            while (reader.hasNextLine()) {
                line = reader.nextLine();
                rawData += line;
                System.out.println(line);
            }
            reader.close();

            // Search for the key header that indicates the wanted data is begininng then go to the start of the data and pull it out
            // Use the boundary UID as the end index for the data substring
            String data = "Fail :(";
            String startKey = "Content-Type: text/plain";
            String idKey = "Content-Type: multipart/alternative; boundary=\"";
            String target = "\n\r\n";

            // Get the boundary UID by going to the header, then pulling a substring from between the quotation marks
            int sIdI = rawData.indexOf(idKey) + idKey.length();
            int eIdI = rawData.indexOf("\"", sIdI);
            String id = "--" + rawData.substring(sIdI, eIdI);

            int startI = rawData.indexOf(startKey);
            if (startI != -1) {
                int startIndex = rawData.indexOf(target, startI) + target.length();
                int endIndex = rawData.indexOf(id, startIndex);
                data = rawData.substring(startIndex, endIndex).trim();
            }
            return data;
        } catch (Exception e) {
            return "Fail :(\n" + e;
        }
        
    }    
}
