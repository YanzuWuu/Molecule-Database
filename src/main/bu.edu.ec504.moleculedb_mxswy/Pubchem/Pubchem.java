package bu.edu.ec504.moleculedb_mxswy.Pubchem;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Pubchem {


    public void read(String pythonScriptPath,String[]args) throws IOException {
        String[] cmd = new String[2 + args.length];
        cmd[0] = "python";
        cmd[1] = pythonScriptPath;
        for (int i = 0; i < args.length; i++) {
            cmd[i + 2] = args[i];
        }
// create runtime to execute external command
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(cmd);

// retrieve output from python script
        BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = "";
        while ((line = bfr.readLine()) != null) {
// display each output line form python script
            System.out.println(line);
        }
    }

    public void LoadFileToDatabase(String[] args) throws IOException {
        String current = new java.io.File(".").getCanonicalPath();
        String pythonScriptPath = current + "/PubChemAPI/LoadFileToDatabase.py";
//        System.out.println(pythonScriptPath);
        this.read(pythonScriptPath,args);
    }

    public void PubChemAPI(String[] args) throws IOException {
        String current = new java.io.File(".").getCanonicalPath();
        String pythonScriptPath = current + "/PubChemAPI/PubChem.py";
//        System.out.println(pythonScriptPath);
        this.read(pythonScriptPath,args);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String current = new java.io.File( "." ).getCanonicalPath();
        String pythonScriptPath = current+"/PubChemAPI/LoadFileToDatabase.py";
//        System.out.println(pythonScriptPath);
        Pubchem chem = new Pubchem();
        chem.read(pythonScriptPath,args);
    }

}

