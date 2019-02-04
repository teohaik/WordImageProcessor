package gr.teohaik.wordimageprocessor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

/**
 *
 * @author tchaikalis
 */
public class WordReader {

    public static final String FILES_DIR = "C:\\Users\\tchaikalis\\Desktop\\eap-askisi2";
    public static final String IMAGE_DIR = FILES_DIR + "\\images";

    public static void main(String... args) {
        try {
            File dir = new File(FILES_DIR);
            Files.createDirectories(Paths.get(IMAGE_DIR, args));
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {

                for (File child : directoryListing) {
                    if (!child.getAbsolutePath().endsWith("docx")) {
                        continue;
                    }
                    System.out.println(child.getName());

                    //create file inputstream to read from a binary file
                    FileInputStream fs = new FileInputStream(child);
                    //create office word 2007+ document object to wrap the word file
                    XWPFDocument docx = new XWPFDocument(fs);
                    //get all images from the document and store them in the list piclist
                    List<XWPFPictureData> piclist = docx.getAllPictures();
                    //traverse through the list and write each image to a file
                    Iterator<XWPFPictureData> iterator = piclist.iterator();
                    int i = 0;
                    String imgBaseName = child.getName().replace(".docx", "_");
                    while (iterator.hasNext()) {
                        XWPFPictureData pic = iterator.next();
                        byte[] bytepic = pic.getData();
                        BufferedImage imag = ImageIO.read(new ByteArrayInputStream(bytepic));
                        if (imag != null) {
                            ImageIO.write(imag, "jpg", new File(IMAGE_DIR + imgBaseName + i + ".jpg"));
                        }
                        i++;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }
}
