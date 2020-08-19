package control;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Float.parseFloat;
import java.util.ArrayList;

public class ValidationControl {
    final String alignmentISOPath = "src\\main\\java\\dataset\\alignment\\iso\\";
    
    public ArrayList<MappingParam> parseValidationFile(){
        
        ArrayList<MappingParam> mappings = new ArrayList();
        
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(alignmentISOPath+"AlignmentISOTotal.csv"));
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
                map.setHuman(rescalePercentageToFour(parseFloat(humanParam)));
                map.setAccess(rescalePercentageToFour(parseFloat(accessParam)));
                map.setNetwork(rescalePercentageToFour(parseFloat(networkParam)));
                map.setOperational(rescalePercentageToFour(parseFloat(operational)));
                map.setCompliance(rescalePercentageToFour(parseFloat(compliance)));
                map.setDesigntime(rescalePercentageToTwo(parseFloat(designtime)));
                map.setRuntime(rescalePercentageToTwo(parseFloat(runtime)));
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
    
    public int rescalePercentageToFour(float percentage){
        if(percentage > 20 && percentage <=40){return 1;}
        else if(percentage > 40 && percentage <=60){return 2;}
        else if(percentage > 60 && percentage <=80){return 3;}
        else if(percentage > 80){return 4;}
        else {return 0;}
    }
    
    public int rescalePercentageToTwo(float percentage){
        if(percentage > 20 && percentage <= 60){return 1;}
        else if(percentage > 60){return 2;}
        else {return 0;}
    }
    
    public ArrayList<Factor> caluclateValidationFactor(ArrayList<MappingParam> mappings){
         
        ArrayList<Factor> factors = new ArrayList();
        
        double denominator = 14;
        
        for (MappingParam mapping : mappings) {
            String id = mapping.getControlID();
            int hum = mapping.getHuman();
            int acc = mapping.getAccess();
            int net = mapping.getNetwork();
            
            int maxGap =(Math.max(hum, Math.max(acc, net))) - (Math.min(hum, Math.min(acc, net)));
            
            String reliability;
            if(mapping.getRuntime() >= mapping.getDesigntime() && 
                    (mapping.getHuman()!=0 || mapping.getAccess() !=0 || mapping.getNetwork()!=0)){reliability="H";}
            else if(mapping.getRuntime() < mapping.getDesigntime() && 
                    (mapping.getHuman()!=0 || mapping.getAccess() !=0 || mapping.getNetwork()!=0)){reliability="M";}
            else{reliability="L";}
            
            int Prc;
            String coverage = mapping.getAssessment();
            if(coverage == "C" && reliability == "H"){Prc=4;}
            else if(coverage == "C" && reliability == "M"){Prc=3;}
            else if(coverage == "C" && reliability == "L"){Prc=1;}
            else if(coverage == "PC" && reliability == "H"){Prc=3;}
            else if(coverage == "PC" && reliability == "M"){Prc=2;}
            else if(coverage == "PC" && reliability == "L"){Prc=1;}
            else if(coverage == "NC" && reliability == "H"){Prc=1;}
            else if(coverage == "NC" && reliability == "M"){Prc=1;}
            else{Prc=1;}
            
            int lt = mapping.getRuntime();
            int ml = mapping.getOperational();
            
            double val = (lt+ml+Prc+maxGap)/denominator;
            
            //System.out.println(id + " -validation factor- " + val);
            Factor f = new Factor(id, val, "validation");
            factors.add(f);
        }
        
        return factors;
    }
}
