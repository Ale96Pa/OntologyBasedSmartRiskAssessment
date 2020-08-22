/**
 * This file contains methods to manage the whole process of the alignment. AML
 * tool provides an rdf file containing data about matching between two elements.
 * From such a file a finale csv dataset containing all the necessary info for
 * the calculation of discount is produced.
 */
package control;

import control.models.Alignment;
import control.models.Factor;
import control.models.MappingParam;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Float.parseFloat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParseAlignment {

    /**
     * This method gets in input the rdf file with the alignment and return the
     * same information collected in a suitable data structure.
     * @param alignmentPath
     * @return 
     */
    private ArrayList<Alignment> parseAlignment(String alignmentPathRdf){
        ArrayList<Alignment> totalAlignment = new ArrayList();
        
        try {
            // Elements for scanning RDF/XML file
            File fXmlFile = new File(alignmentPathRdf);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            // "Cell" is the tag inside which the information to be extracted is
            NodeList nList = doc.getElementsByTagName("Cell");
            
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element cellElement = (Element) nNode;

                    // entity1 is the source of the matching
                    NodeList entity1Tag = cellElement.getElementsByTagName("entity1");
                    Element entitySrc = (Element) entity1Tag.item(0);
                    String entitySrcName = entitySrc.getAttribute("rdf:resource");

                    // entity2 is the target of the matching
                    NodeList entity2Tag = cellElement.getElementsByTagName("entity2");
                    Element entityDest = (Element) entity2Tag.item(0);
                    String entityDestName = entityDest.getAttribute("rdf:resource");

                    // measure is the value of the matching
                    String matching = cellElement.getElementsByTagName("measure").item(0).getTextContent();
                    
                    Alignment alignmet = new Alignment(entitySrcName, entityDestName, parseFloat(matching));
                    totalAlignment.add(alignmet);
                }
            }
        } catch (Exception e) {}
        return totalAlignment;
    }
    
    /**
     * This method gets in input an alignment in order to extend the information
     * inside it through an array of Factor; in this way the total information
     * from the alignment is extracted.
     * @param alignment
     * @return 
     */
    private ArrayList<Factor> buildFactor(String pathStandardCsv, Alignment alignment){
        ArrayList<Factor> factors = new ArrayList();
        BufferedReader br = null;
        
        try {
            br = new BufferedReader(new FileReader(pathStandardCsv));
            br.readLine(); // skip the first line (header)
            String line;
            
            while((line = br.readLine()) != null) {
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
    
    /**
     * This method gets in input an array of alignments in order to extend the 
     * information inside it through an array of Factor; in this way the total 
     * information from the alignment is extracted.
     * @param pathStandardCsv
     * @param alignments
     * @return 
     */
    private ArrayList<Factor> writeAllFactors(String pathStandardCsv, ArrayList<Alignment> alignments){
        ArrayList<Factor> factorsAll = new ArrayList();
        for(Alignment a : alignments){
            ArrayList<Factor> facts = buildFactor(pathStandardCsv, a);
            for(Factor f : facts){
                factorsAll.add(f);
            }
        }
        return factorsAll;
    }
    
    /**
     * This method collect data about the array of all factors in order to group
     * the information basing on ID using an HashMap. In this way the info is
     * better organized.
     * @param factors
     * @return 
     */
    private Map<String, List<Factor>> collectData(ArrayList<Factor> factors){
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
    
    /**
     * This method collects information from an array of mapping factor in order
     * to set data that must be written in the same row in the final file.
     * @param pathAssessment
     * @param mapFactors
     * @return 
     */
    private ArrayList<MappingParam> setRows(String pathAssessment, Map<String, List<Factor>> mapFactors){
        ArrayList<MappingParam> mappings = new ArrayList();
        BufferedReader br = null;

        // Scan HashMap for getting info control by control from alignment
        for (Map.Entry<String, List<Factor>> entry : mapFactors.entrySet()) {
            List<Factor> list = entry.getValue();
            MappingParam mp = new MappingParam();
            mp.setControlID(entry.getKey());
            
            try {
                // Read Assessment file to get the coverage level of each control
                br = new BufferedReader(new FileReader(pathAssessment));
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

            // Scan Factors for each id in order to set parameters from alignment
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
    
    /**
     * This method write the output file of mapping picking information by an
     * array of MappingParam, one for each control.
     * @param finalMappingPath
     * @param mappings 
     */
    private void writeMapping(String finalMappingPath, ArrayList<MappingParam> mappings){
        FileWriter fw = null;
        try {
            fw = new FileWriter(finalMappingPath);
            fw.write(""); // Erase previous content
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
            try {fw.close();}
            catch (IOException ex) {
                Logger.getLogger(ParseAlignment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * This method organizes all above methods to write final output file
     * @param datasetCsv
     * @param alignmentWithAg
     * @param alignmentWithManagement
     * @param outputMapping
     * @param assessment
     */
    public void writeMappingFromAlignment(String datasetCsv, String alignmentWithAg, 
            String alignmentWithManagement, String assessment, String outputMapping){
        
        ArrayList<Alignment> alignmentAg = parseAlignment(alignmentWithAg);
        ArrayList<Alignment> alignmentManagement = parseAlignment(alignmentWithManagement);
        ArrayList<Alignment> alignmentAll = new ArrayList();
        ArrayList<Factor> factorsAll;
        Map<String, List<Factor>> mappings;
        ArrayList<MappingParam> rows;
        
        for(Alignment a : alignmentAg){alignmentAll.add(a);}
        for(Alignment a : alignmentManagement){alignmentAll.add(a);}
        
        factorsAll = writeAllFactors(datasetCsv, alignmentAll);
        
        mappings = collectData(factorsAll);
       
        rows = setRows(assessment, mappings);
        
        writeMapping(outputMapping, rows);
    }
    
}
