package control;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import config.Config;
import control.models.Alignment;
import control.models.Factor;
import control.models.MappingParam;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Float.parseFloat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParseAlignment {

    
    public ArrayList<Alignment> parseAlignment(String alignmentPath){
         
        ArrayList<Alignment> totalAlignment = new ArrayList();
        
        try {
            File fXmlFile = new File(alignmentPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Cell");
            
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element cellElement = (Element) nNode;

                    NodeList entity1Tag = cellElement.getElementsByTagName("entity1");
                    Element entitySrc = (Element) entity1Tag.item(0);
                    String entitySrcName = entitySrc.getAttribute("rdf:resource");

                    NodeList entity2Tag = cellElement.getElementsByTagName("entity2");
                    Element entityDest = (Element) entity2Tag.item(0);
                    String entityDestName = entityDest.getAttribute("rdf:resource");

                    String matching = cellElement.getElementsByTagName("measure").item(0).getTextContent();
                    
                    Alignment alignmet = new Alignment(entitySrcName, entityDestName, parseFloat(matching));
                    totalAlignment.add(alignmet);
                   
                }
            }
        } catch (Exception e) {}
        
        return totalAlignment;
    }
    
    public void initializeFinalMapping(){
        Config conf = new Config();
        BufferedReader br = null;
        FileWriter fw = null;
        try {
            br = new BufferedReader(new FileReader(conf.getIsoCsvPath()));
            br.readLine(); // skip the first line (header)
            String line;
            
            fw = new FileWriter(conf.getAlignmentIsoFinalPath());
            fw.append("ID;H;A;N;Runtime;Designtime;Operational;Compliance;Assessment\n");

            while((line = br.readLine()) != null) {
                                
                String[] data = line.split(";");
                
                String idCsv = data[0];
                
                fw.append(idCsv+";NaN;NaN;NaN;NaN;NaN;NaN;NaN;NaN\n");
                
            }
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        finally {
            if (br != null){
                try {br.close();}
                catch (IOException e) {}
            }
            if(fw != null){
                try{fw.flush();fw.close();}
                catch (IOException e) {}
            }
        }
    }
    
    /*
    public void updateCSV(String fileToUpdate, String replace, int row, int col){

        CSVReader reader = null;
        try {
            File inputFile = new File(fileToUpdate);
            // Read existing file
            reader = new CSVReader(new FileReader(inputFile));
            List<String[]> csvBody = reader.readAll();
            for(String[] s : csvBody){
                for(String ss : s){
                    System.out.println(ss);
                }
            }
            
            // get CSV row column  and replace with by using row and column
            csvBody.get(row)[col] = replace;
            reader.close();
            // Write to CSV file which is open
            CSVWriter writer = new CSVWriter(new FileWriter(inputFile));
            writer.writeAll(csvBody);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParseAlignment.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParseAlignment.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvException ex) {
            Logger.getLogger(ParseAlignment.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(ParseAlignment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    */
    
    public ArrayList<Factor> buildFactor(Alignment alignment){
        ArrayList<Factor> factors = new ArrayList();
        Config conf = new Config();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(conf.getIsoCsvPath()));
            br.readLine(); // skip the first line (header)
            String line;
            
            while((line = br.readLine()) != null) {
                //String controlId = alignment.getSourceUri().split("#")[1].split(";")[0];
                String controlContent=alignment.getSourceUri().split(";")[1].replace("%20", " ");
                double matchingValue=alignment.getMatchingValue();
                String label =alignment.getTargetUri().split("#")[1].split(";")[0];
                String controlCsvId = line.split(";")[0];
                if(line.contains(controlContent)){
                    Factor fact = new Factor(controlCsvId, matchingValue, label);
                    factors.add(fact);
                }

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
        return factors;
    }
    
    public ArrayList<Factor> writeAllFactors(ArrayList<Alignment> alignments){
        ArrayList<Factor> factorsAll = new ArrayList();
        for(Alignment a : alignments){
            ArrayList<Factor> facts = buildFactor(a);
            for(Factor f : facts){
                factorsAll.add(f);
            }
        }
        return factorsAll;
    }
    
    /*
    public MappingParam setLabelToMapping(String label, double value){
        
        MappingParam mp = new MappingParam();
    
        if(null != label)switch (label) {
            case "human":
                mp.setHuman(value);
                break;
            case "access":
                mp.setAccess(value);
                break;
            case "network":
                mp.setNetwork(value);
                break;
            case "runtime":
                mp.setRuntime(value);
                break;
            case "designtime":
                mp.setDesigntime(value);
                break;
            case "operational":
                mp.setOperational(value);
                break;
            case "compliance":
                mp.setCompliance(value);
                break;
        }
    }
    */
    
    public Map<String, List<Factor>> collectData(ArrayList<Factor> factors){
        //ArrayList<MappingParam> rows = new ArrayList();
        
        

        Map<String, List<Factor>> map = new HashMap();

        for (Factor fact : factors) {
            String key  = fact.getId();
            if(map.containsKey(key)){
                List<Factor> list = map.get(key);
                list.add(fact);

            }else{
                List<Factor> list = new ArrayList();
                list.add(fact);
                map.put(key, list);
            }

        }
        return map;
    }
    
    public ArrayList<MappingParam> setRows(Map<String, List<Factor>> mapFactors){
        
        
        ArrayList<MappingParam> mappings = new ArrayList();
        
        Config conf = new Config();
        BufferedReader br = null;
        
            

                for (Map.Entry<String, List<Factor>> entry : mapFactors.entrySet()) {
                    //System.out.println(entry.getKey());

                    List<Factor> list = entry.getValue();
                    MappingParam mp = new MappingParam();
                    mp.setControlID(entry.getKey());
                    try {
                        br = new BufferedReader(new FileReader(conf.getAssessmentIsoPC()));

                        br.readLine(); // skip the first line (header)

                        String line;
                    
                        while((line = br.readLine()) != null) {
                            String[] data = line.split(";");
                            String controlCsv = data[0];
                            String assessment = data[1];

                            if(entry.getKey().equals(controlCsv)){
                                mp.setAssessment(assessment);
                            }
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
                    
                    for(Factor f : list){ 
                        String label = f.getType();
                        switch (label) {
                            case "human":
                                mp.setHuman(f.getValue());
                                break;
                            case "access":
                                mp.setAccess(f.getValue());
                                break;
                            case "network":
                                mp.setNetwork(f.getValue());
                                break;
                            case "runtime":
                                mp.setRuntime(f.getValue());
                                break;
                            case "designtime":
                                mp.setDesigntime(f.getValue());
                                break;
                            case "operational":
                                mp.setOperational(f.getValue());
                                break;
                            case "compliance":
                                mp.setCompliance(f.getValue());
                                break;
                        }
                        
                    }
                    
                    mappings.add(mp);
                }

        return mappings;
    }
    
    public void writeMapping(ArrayList<MappingParam> mappings){
        FileWriter fw = null;
        try {
            Config conf = new Config();
            fw = new FileWriter(conf.getAlignmentIsoFinalPath());
            fw.append("ID;H;A;N;Runtime;Designtime;Operational;Compliance;Assessment\n");
            
            for(MappingParam m : mappings){
                /*
                System.out.println(m.getControlID() + "-"+m.getHuman()+"-"+m.getAccess()+
                "-"+m.getNetwork()+"-"+m.getRuntime()+"-"+m.getDesigntime()+
                "-"+m.getOperational()+"-"+m.getCompliance()+"-"+m.getAssessment());
                */
                String id = m.getControlID();
                double h = m.getHuman();
                double a = m.getAccess();
                double n = m.getNetwork();
                double rt =m.getRuntime() ;
                double dt = m.getDesigntime();
                double op = m.getOperational();
                double compl = m.getCompliance();
                String assessment = m.getAssessment();
                fw.append(id+";"+h+";"+a+";"+n+";"+rt+";"+dt+";"+op+";"+compl+";"+assessment+"\n");
                
            }
        } catch (IOException ex) {
            Logger.getLogger(ParseAlignment.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ParseAlignment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

        
        /*
        for(Factor f1 : factors){
            MappingParam mp = new MappingParam();
            String labelFirst = f1.getType();
            if(null != labelFirst)switch (labelFirst) {
                case "human":
                    mp.setHuman(f1.getValue());
                    break;
                case "access":
                    mp.setAccess(f1.getValue());
                    break;
                case "network":
                    mp.setNetwork(f1.getValue());
                    break;
                case "runtime":
                    mp.setRuntime(f1.getValue());
                    break;
                case "designtime":
                    mp.setDesigntime(f1.getValue());
                    break;
                case "operational":
                    mp.setOperational(f1.getValue());
                    break;
                case "compliance":
                    mp.setCompliance(f1.getValue());
                    break;
            }
            for(Factor f2: factors){
                if(f1.getId().equals(f2.getId())){
                    String label = f2.getType();
                    if(null != label)switch (label) {
                        case "human":
                            mp.setHuman(f1.getValue());
                            break;
                        case "access":
                            mp.setAccess(f1.getValue());
                            break;
                        case "network":
                            mp.setNetwork(f1.getValue());
                            break;
                        case "runtime":
                            mp.setRuntime(f1.getValue());
                            break;
                        case "designtime":
                            mp.setDesigntime(f1.getValue());
                            break;
                        case "operational":
                            mp.setOperational(f1.getValue());
                            break;
                        case "compliance":
                            mp.setCompliance(f1.getValue());
                            break;
                    }
                }
            }
            rows.add(mp);
        }
        
        return rows;
    }
        */
 
    /*
    public void writeFactorInOutput(String file, Factor f){
        Config conf = new Config();
        int rowIndex = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(conf.getAlignmentIsoFinalPath()));
            br.readLine(); // skip the first line (header)
            String line;
            
            while((line = br.readLine()) != null) {
                
                
                
                String[] data = line.split(";");
                
                // Elements in ISO 27001:2013 dataset
                String idCsv = data[0].replace("\"", "");
                System.out.println(idCsv + " -- " + f.getId());
                if(f.getId().equals(idCsv)){
                    System.out.println("IN");
                         
                    String label = f.getType();
                    Double matching = f.getValue();
                    switch (label) {
                        case "human":
                            updateCSV(file, matching.toString(), rowIndex, 0);
                            //break;
                        case "access":
                            updateCSV(file, matching.toString(), rowIndex, 1);
                            //break;
                        case "network":
                            updateCSV(file, matching.toString(), rowIndex, 2);
                            //break;
                        case "runtime":
                            updateCSV(file, matching.toString(), rowIndex, 3);
                            //break;
                        case "designtime":
                            updateCSV(file, matching.toString(), rowIndex, 4);
                            //break;
                        case "compliance":
                            updateCSV(file, matching.toString(), rowIndex, 5);
                            //break;
                        case "operational":
                            updateCSV(file, matching.toString(), rowIndex, 6);
                            //break;
                                            
                    }
                    
                }
                rowIndex++;
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
        
    }
    */
    
    
    
    
    
    
    
    
}
