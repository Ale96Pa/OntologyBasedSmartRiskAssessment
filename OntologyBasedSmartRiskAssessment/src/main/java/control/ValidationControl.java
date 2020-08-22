/**
 * This file collects methods to manage the calculation of the validation factor.
 * It practically implements the research in the paper "Toward a Context-Aware 
 * Methodology for Information Security Governance Assessment Validation" by 
 * Angelini M., Bonomi S., Ciccottelli C., Palma A.
 */
package control;

import control.models.Factor;
import control.models.MappingParam;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Double.parseDouble;
import java.util.ArrayList;

public class ValidationControl {
        
    /*
    This method parse the file containing the allignment with the elements:
    ID;H;A;N;Runtime;Designtime;Operational;Compliance;Assessment
    and put such information into a list of MappingParam.
    In this way the information are accessible without reading the file everytime.
    */
    public ArrayList<MappingParam> parseValidationFile(String alignmentISOPath){
        
        ArrayList<MappingParam> mappings = new ArrayList();
        
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(alignmentISOPath));
            br.readLine(); // skip the first line (header)
            String line;
            
            while((line = br.readLine()) != null) {
                
                String[] data = line.split(";");
                
                String idControl = data[0];
                String humanParam = data[1];
                String accessParam = data[2];
                String networkParam = data[3];
                String runtime = data[4];
                String designtime = data[5];
                String operational = data[6];
                String compliance = data[7];
                String assessment = data[8];
                
                MappingParam map = new MappingParam();
                map.setControlID(idControl);
                map.setHuman(rescalePercentageToFour(parseDouble(humanParam)));
                map.setAccess(rescalePercentageToFour(parseDouble(accessParam)));
                map.setNetwork(rescalePercentageToFour(parseDouble(networkParam)));
                map.setOperational(rescalePercentageToFour(parseDouble(operational)));
                map.setCompliance(rescalePercentageToFour(parseDouble(compliance)));
                map.setDesigntime(rescalePercentageToTwo(parseDouble(designtime)));
                map.setRuntime(rescalePercentageToTwo(parseDouble(runtime)));
                map.setAssessment(assessment);
                
                mappings.add(map);
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
        return mappings;        
    }
    
    /*
    This methd receive in input a percentage (value between 0 and 100) and outputs
    the corresponding value in a scale between 0 and 4 (according to paper 
    mentioned in the description of this file).
    */
    public int rescalePercentageToFour(double percentage){
        if(percentage > 20 && percentage <=40){return 1;}
        else if(percentage > 40 && percentage <=60){return 2;}
        else if(percentage > 60 && percentage <=80){return 3;}
        else if(percentage > 80){return 4;}
        else {return 0;}
    }
    
    /*
    This methd receive in input a percentage (value between 0 and 100) and outputs
    the corresponding value in a scale between 0 and 2 (according to paper 
    mentioned in the description of this file).
    */
    public int rescalePercentageToTwo(double percentage){
        if(percentage > 20 && percentage <= 60){return 1;}
        else if(percentage > 60){return 2;}
        else {return 0;}
    }
    
    /*
    This method receives in input a list of MappingParam containing the information
    in the alignment file and outputs the final validation factor according to 
    the formula reported in the paper.
    Moreover this factor is used for the calculation of discount factor.
    */
    public ArrayList<Factor> caluclateValidationFactor(ArrayList<MappingParam> mappings){
         
        ArrayList<Factor> factors = new ArrayList();
        
        double denominator = 14; // According to paper (4+4+4+2)
        double maxGap, Prc, lt, ml;
        double val;
        
        for (MappingParam mapping : mappings) {
            String id = mapping.getControlID();
            double hum = mapping.getHuman();
            double acc = mapping.getAccess();
            double net = mapping.getNetwork();
            
            // Reliability condition for reliability-coverage factor matrix
            String reliability;
            if(mapping.getRuntime() >= mapping.getDesigntime() && 
                    (mapping.getHuman()!=0 || mapping.getAccess() !=0 || 
                    mapping.getNetwork()!=0)){reliability="H";}
            else if(mapping.getRuntime() < mapping.getDesigntime() && 
                    (mapping.getHuman()!=0 || mapping.getAccess() !=0 || 
                    mapping.getNetwork()!=0)){reliability="M";}
            else{reliability="L";}
            
            // Coverage condition for reliability-coverage factor matrix
            String coverage = mapping.getAssessment();
            if("C".equals(coverage) && "H".equals(reliability)){Prc=4;}
            else if("C".equals(coverage) && "M".equals(reliability)){Prc=3;}
            else if("C".equals(coverage) && "L".equals(reliability)){Prc=1;}
            else if("PC".equals(coverage) && "H".equals(reliability)){Prc=3;}
            else if("PC".equals(coverage) && "M".equals(reliability)){Prc=2;}
            else if("PC".equals(coverage) && "L".equals(reliability)){Prc=1;}
            else if("NC".equals(coverage) && "H".equals(reliability)){Prc=1;}
            else if("NC".equals(coverage) && "M".equals(reliability)){Prc=1;}
            else{Prc=1;}
            
            lt = mapping.getRuntime();
            ml = mapping.getOperational();
            maxGap =(Math.max(hum, Math.max(acc, net))) - (Math.min(hum, Math.min(acc, net)));
            
            val = (lt+ml+Prc+maxGap)/denominator;
            
            //System.out.println(id + " has validation factor: " + val);
            Factor f = new Factor(id, val, "validation");
            factors.add(f);
        }
        return factors;
    }
}
