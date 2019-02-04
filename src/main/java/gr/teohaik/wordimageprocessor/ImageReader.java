package gr.teohaik.wordimageprocessor;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.api.request.input.SearchClause;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.ClarifaiInputValue;
import clarifai2.dto.search.SearchInputsResult;
import clarifai2.internal.JSONObjectBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.poi.util.IOUtils;
import static gr.teohaik.wordimageprocessor.WordReader.FILES_DIR;

/**
 *
 * @author tchaikalis
 */
public class ImageReader {

    private static final String IMAGE_DIR = "C:\\Users\\tchaikalis\\Desktop\\eap-askisi2\\images\\";

    static ClarifaiClient client;

    static File clopyImage = new File(FILES_DIR + "\\clopy.jpg");

    public static void main(String... args) throws IOException {

        client = new ClarifaiBuilder("efab2d67a6b54615ac45d009b8345606")
                .buildSync();
        // client.deleteAllInputs().executeSync().get();
      //  readImages();

        findSimilarWith(clopyImage);

    }

    private static void printHelp() {
        System.out.println("");
    }

    public static void readImages() throws FileNotFoundException, IOException {
        File dir = new File(IMAGE_DIR);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File image : directoryListing) {
                try {
                    ClarifaiResponse<List<ClarifaiInput>> executeSync = uploadImageToClassier(image);
                    System.out.println("Response " + executeSync.rawBody());
                } catch (IOException | UnsupportedOperationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static ClarifaiResponse<List<ClarifaiInput>> uploadImageToClassier(File image) throws IOException {
        byte[] imageBytes = IOUtils.toByteArray(new FileInputStream(image));
        final JsonObject metadata = new JsonObject();
        metadata.addProperty("NAME", image.toString());
        ClarifaiInput input = ClarifaiInput.forImage(imageBytes);
        ClarifaiResponse<List<ClarifaiInput>> executeSync
                = client.addInputs()
                        .plus(input.withMetadata(metadata))
                        .executeSync();
        return executeSync;
    }

    private static void findSimilarWith(File clopyImage) {
        SearchInputsResult get = client
                .searchInputs(SearchClause.matchImageVisually(ClarifaiImage.of(clopyImage)))
                .getPage(1)
                .executeSync().get();

        get.searchHits().forEach(hit -> {
            float score = hit.score();
            JsonParser parser = new JsonParser();
            System.out.println("Score = "+score + " | file = "+hit.input().metadata());
        });
    }

}
