package bu.edu.ec504.moleculedb_mxswy.Pubchem;

import bu.edu.ec504.moleculedb_mxswy.cli.Commandline;
import org.apache.commons.cli.ParseException;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

public class DownloadDataProcess {
    public static void main(String[] mainargs) throws IOException, ParseException, JSONException {
        // This path needs to be changed to the download location
        String current = "/Users/msimbirsky/Documents/Repos/EC504/moleculedb_mxswy/PubChemAPI/DownloadData";
        File dir = new File(current);//Dir name
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".txt")) {
                String path = current + "/" + file.getName();
                String[] args = {"-a", path};
                Commandline.main(args);
            }
        }
        System.out.print("All the Download Data move to Database.");
    }
}
