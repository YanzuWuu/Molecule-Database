package bu.edu.ec504.moleculedb_mxswy.cli;

import bu.edu.ec504.moleculedb_mxswy.exceptions.InvalidFormatException;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.cli.*;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Commandline {
    private static String postRequest(String urlPath, JSONObject jsonData) throws IOException {
        // hardcode the server location to where bu.edu.ec504.moleculedb_mxswy.Main is running
        String AuthServer="http://localhost:8080";

        URL targetURL = new URL(AuthServer + urlPath);
        HttpURLConnection postConnection = (HttpURLConnection) targetURL.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setRequestProperty("Content-Type", "application/json");
        postConnection.setDoOutput(true);

        // send the data
        try {
        OutputStream urlStream = postConnection.getOutputStream();
        String jsonDataString = jsonData.toString();
        urlStream.write(jsonDataString.getBytes());
        urlStream.flush();
        urlStream.close(); } catch (ConnectException e) {
            throw new ConnectException("Could not connect to DB Server.");
        }

        // check the response
        int responseCode = postConnection.getResponseCode();
        String responseMessage = postConnection.getResponseMessage();

        // add logic to retry in case of connection error
        int retryAttempts = 0;
        while (retryAttempts < 5){
        if (responseCode == 200) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    postConnection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // print result
            return response.toString();
        } else if (responseCode == 400){
            System.out.println("Bad request: 400 ERROR");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    postConnection.getErrorStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            throw new InvalidFormatException(response.toString());
        } else {
            retryAttempts ++;
            System.out.println("Connection Error. Retrying attempt " + retryAttempts);
        }}
        // raise error if reaching this point
        throw new ConnectException("Could not connect to DB server.");
    }

    private static String readFile(String filePath) throws FileNotFoundException {
        File f = new File(filePath);
        Scanner scan = new Scanner(f); //read data from txt
        StringBuilder sb = new StringBuilder();
                while (scan.hasNextLine()) {
            sb.append(scan.nextLine()).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws ParseException, IOException, JSONException {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        // define the options
        options.addOption("h","help",false,"Show the help message and exit");
        options.addOption("a", "addMolecule", true,"add a new compound using file path ");
        options.addOption("d", "deleteMolecule",true,"Delete a compound from the database using #ID");

        // modify compound has two arguments
        Option modify = new Option("m", "modifyMolecule",true,"Update an existing compound using the compound number and the new data's file path");
        modify.setArgs(2);
        options.addOption(modify);

        // Search option has two arguments
        Option search = new Option("s", "searchMolecule", false, "Search for a compound in the database, takes 2 arguments: (filePath) (searchType), where searchType is one of: name, formula, isomorphism, exact_subgraph, fuzzy_subgraph");
        search.setArgs(2);
        options.addOption(search);

        // Pubchem operation
        //        options.addOption("loadID", "PubChemLoadMolecule",true,"Load molecule #ID from Pubchem");
        //        options.addOption("loadRand", "PubChemLoadRandom",true,"Load random #number molecules from Pubchem");
        //options.addOption("showMolecule","s",true,"Show molecules in database using Name");
        //options.addOption("countMolecule","c",false,"Count molecules");

        // now parse the options
        CommandLine commandLine;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e1) {
            e1.printStackTrace();
            throw new ParseException("Error parsing commandline");
        }

        // parse the options
        if (commandLine.hasOption("h")){
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("MoleculeDB",options);
        } else if (commandLine.hasOption("addMolecule")) {
            String s_compound = commandLine.getOptionValue("addMolecule");
            String s_data = readFile(s_compound);

            // build a JSON object
            JSONObject request = new JSONObject();
            request.put("compoundData", s_data);

            // send the request
            String returnMessage = postRequest("/saveCompound", request);
            System.out.println(returnMessage);

        } else if (commandLine.hasOption("deleteMolecule")) {
            String d_compound = commandLine.getOptionValue("deleteMolecule");
            int d = Integer.parseInt(d_compound);

            // build a JSON object
            JSONObject request = new JSONObject();
            request.put("compoundID", d);

            // send the request
            String returnMessage = postRequest("/deleteCompound", request);
            System.out.println(returnMessage);

        } else if (commandLine.hasOption("modifyMolecule")) {
            String[] m_compound = commandLine.getOptionValues("modifyMolecule");
            int m = Integer.parseInt(m_compound[0]);
            String newData = m_compound[1];

            // read txt file with given path s_compound
            String m_data = readFile(newData);

            // build a JSON object
            JSONObject request = new JSONObject();
            request.put("compoundID", m);
            request.put("compoundData", m_data);

            String returnMessage = postRequest("/modifyCompound", request);
            System.out.println(returnMessage);

        } else if (commandLine.hasOption("searchMolecule")) {
            String[] searchArgs = commandLine.getOptionValues("searchMolecule");
            String filePath = searchArgs[0];
            String searchMode = searchArgs[1];

            // load the data from file
            String fileData = readFile(filePath);

            // check that the searchtype is valid
            List<String> validSearchModes = Arrays.asList("name", "chemical_formula", "isomorphism", "exact_subgraph", "fuzzy_subgraph");
            if (!validSearchModes.contains(searchMode.toLowerCase())) {
                String err = "Invalid search mode specified.";
                err += "Search mode must be one of: ";
                err += "name, chemical_formula, isomorphism, exact_subgraph, fuzzy_subgraph";
                throw new IllegalArgumentException(err);
            }

            // build a JSON object
            JSONObject request = new JSONObject();
            request.put("compoundData", fileData);
            request.put("searchMode", searchMode.toUpperCase());

            String returnMessage = postRequest("/findCompound", request);
            System.out.println(returnMessage);

        }
    }
}
