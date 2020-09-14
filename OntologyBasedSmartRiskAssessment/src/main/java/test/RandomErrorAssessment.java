package test;

import control.ParseAlignment;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

public class RandomErrorAssessment {
    
    public void buildRandomErrorAssessment(double error, String pathRealAssessment, String outputAssessment){
        
        ArrayList<String[]> rowAssessment = new ArrayList();
        BufferedReader br = null;
        int numControls = 0;
        
        try {
            br = new BufferedReader(new FileReader(pathRealAssessment));
            br.readLine(); // skip the first line (header)
            String line;
            
            while((line = br.readLine()) != null) {
                String[] data = line.split(";");
                rowAssessment.add(data);
                numControls++;
            }
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        finally {
            if (br != null){
                try {br.close();}
                catch (IOException e) {}
            }
        }
        
        int errorsToGenerate = (int) Math.round((error/100.0)*numControls);
        int counter = 0;
        Random rand = new Random();
        ArrayList<Integer> errors = new ArrayList();

        for(int i=0; i<errorsToGenerate; i++){
            int indexError = rand.nextInt(numControls);
            errors.add(indexError);
        }
        
        FileWriter fw = null;
        try {
            fw = new FileWriter(outputAssessment);
            fw.write(""); // Erase previous content
            fw.append("ID;Assessment\n");
            
            for(String[] row : rowAssessment){
                
                
                String id = row[0];
                String realAssessment = row[1];
                String newAssessment = realAssessment;
                
                if(errors.contains(counter)){
                    switch (realAssessment) {
                        case "C":
                            newAssessment = "PC";
                            break;
                        case "PC":
                            if(counter%2==0){newAssessment = "NC";}
                            else{newAssessment = "C";}
                            break;
                        case "NC":
                            newAssessment = "PC";
                            break;
                    }
                }
                
                fw.append(id+";"+newAssessment+"\n");
                counter++;
            }
        } catch (IOException ex) {
            Logger.getLogger(ParseAlignment.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {fw.close();}
            catch (IOException ex) {
                Logger.getLogger(ParseAlignment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
