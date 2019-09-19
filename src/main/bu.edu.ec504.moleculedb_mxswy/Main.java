package bu.edu.ec504.moleculedb_mxswy;

import bu.edu.ec504.moleculedb_mxswy.api.CompoundService;
import bu.edu.ec504.moleculedb_mxswy.api.CompoundServiceImpl;
import bu.edu.ec504.moleculedb_mxswy.exceptions.InvalidFormatException;
import bu.edu.ec504.moleculedb_mxswy.search.SearchTypes;
import bu.edu.ec504.moleculedb_mxswy.storage.model.Compound;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Initializing server.");
        // Configure Spark
        port(8080);

        get("/", (req, res) -> renderStaticFile("/index.html"));
        get("*", (req, res) -> {
            try {
                return renderStaticFile(req.pathInfo());
            } catch (FileNotFoundException fnf) {
                res.status(HttpURLConnection.HTTP_NOT_FOUND);
                res.body("file not found");
            } catch (IOException | URISyntaxException e) {
                res.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
                res.body(e.getMessage());
            }
            return res.body();
        });

        // add CompoundService
        CompoundService myService = new CompoundServiceImpl();

        post("/findCompound", (req, res)->{
            try {
                JSONObject parser = new JSONObject(req.body());
                Compound compoundData = myService.loadFromFile(parser.getString("compoundData"));
                SearchTypes searchType = SearchTypes.valueOf(parser.getString("searchMode"));
                List<Integer> matchingIDs = myService.findCompound(compoundData, searchType);
                if (matchingIDs.size() > 0){
                    return "Matching compound IDs: " + matchingIDs.toString();
                } else {
                    return "No matches found";
                }
            } catch (InvalidFormatException e) {
                res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return "STATUS: " + HttpURLConnection.HTTP_BAD_REQUEST + "\n" + "Invalid search type.";
            } catch (JSONException e) {
                res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return "STATUS: " + HttpURLConnection.HTTP_BAD_REQUEST + "\nBadly formatted JSON.";
            } catch (Exception e) {
                res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return "STATUS: " + HttpURLConnection.HTTP_BAD_REQUEST + "\n" + e.getMessage();
            }
        });

        post("/deleteCompound", (req, res)->{
            // parse JSON
            JSONObject parser;
            try {
                parser = new JSONObject(req.body());
                int compoundID = parser.getInt("compoundID");
                myService.deleteCompound(compoundID);
                return "Compound deleted.";
            } catch (JSONException e) {
                res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return "STATUS: " + HttpURLConnection.HTTP_BAD_REQUEST + "\nBadly formatted JSON.";
            } catch (Exception e) {
                res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return "STATUS: " + HttpURLConnection.HTTP_BAD_REQUEST + "\n" + e.getMessage();
            }
        });

        post("/modifyCompound", (req, res)->{
            try {
                JSONObject parser = new JSONObject(req.body());
                Compound compoundData = myService.loadFromFile(parser.getString("compoundData"));
                int compoundID = parser.getInt("compoundID");
                myService.modifyCompound(compoundID, compoundData);
                return "Compound modified.";

            } catch (JSONException e) {
                res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return "STATUS: " + HttpURLConnection.HTTP_BAD_REQUEST + "\nBadly formatted JSON.";
            } catch (InvalidFormatException e) {
                res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return "STATUS: " + HttpURLConnection.HTTP_BAD_REQUEST + "\n" + e.getMessage();
            } catch (Exception e) {
                res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return "STATUS: " + HttpURLConnection.HTTP_BAD_REQUEST + "\n" + e.getMessage();
            }
        });

        post("/saveCompound", (req, res)->{
            try {
                JSONObject parser = new JSONObject(req.body());
                Compound compoundData = myService.loadFromFile(parser.getString("compoundData"));
                int compoundID = myService.saveCompound(compoundData);
                if (compoundID == -1){
                    return "Compound already found in database. Refusing to create duplicate.";
                } else {
                    return "Compound saved to index " + compoundID;
                }
            } catch (JSONException e) {
                res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return "STATUS: " + HttpURLConnection.HTTP_BAD_REQUEST + "\nBadly formatted JSON.";
            } catch (InvalidFormatException e) {
                res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return "STATUS: " + HttpURLConnection.HTTP_BAD_REQUEST + "\n" + e.getMessage();
            } catch (Exception e) {
                res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return "STATUS: " + HttpURLConnection.HTTP_BAD_REQUEST + "\n" + e.getMessage();
            }
        });

        System.out.println("Server running. Point your browser to http://localhost:8080 to access the Web UI.");

    }

    private static String renderStaticFile(String file) throws IOException, URISyntaxException {
        URL url = Main.class.getResource(file);
        if (url == null) {
            System.out.println("ERROR: STATIC FILE NOT FOUND.");
            throw new FileNotFoundException(file);
        }

        Path path = Paths.get(url.toURI());
        return new String(Files.readAllBytes(path), Charset.defaultCharset());
    }
}
