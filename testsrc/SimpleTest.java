import com.nimblewire.eml2pdf.MailToPdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class SimpleTest {

    public static void main(String[] args) throws Throwable {
        boolean single=false;

        String dir=System.getProperty("user.dir");
        File file = new File(dir);
        File folder = new File(file,"samples");
        File out = new File(file,"out");
        out.mkdirs();
        for (File f : folder.listFiles()) {
            if (f.isDirectory() || !f.getName().endsWith(".eml"))
            {
                continue;
            }
            if (single && !f.getName().contains("Different"))
            {
                continue;
            }
            FileInputStream fis = new FileInputStream(f);
            FileOutputStream fos = new FileOutputStream(new File(out,f.getName()+".pdf"));
            MailToPdf.convert(fis,fos);
        }
        System.out.println(folder.getAbsolutePath());

    }

    private static void single()
    {

    }
}
