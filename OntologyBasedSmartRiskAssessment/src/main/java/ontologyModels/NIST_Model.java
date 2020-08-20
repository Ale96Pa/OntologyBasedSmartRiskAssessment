/*
 * This file build and write the model for NIST sp 800-53 controls into a 
 * suitable ontology designed and developed by the author.

 * Author: Alessandro Palma
 * Master Thesis in Engineering in Computer Science
 * University of Rome "La Sapienza"
 */
package ontologyModels;

import config.Config;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;



public class NIST_Model {
    
    Config conf = new Config();
    // Source file
    final String datasetPath = conf.getNistCsvPath();
    // Destination file
    final String ontologyPath = conf.getNistOwlPath();
    // Format of the output file
    String formatFile = conf.getFormatOntology(); 
    // Local namespace for entities
    final String uri = conf.getUriNist();
    
    /*
    The method createNISTModel creates the ontology of the NIST 800-53 
    controls taking in input a file with the following elements:
    Family;Name;Title;Priority;Impact;Description;SupplementarGuidance;Related
    It writes the model into a file stored in the dataset package and it 
    returns the OntoModel.
    */
    public OntModel createNISTModel() {
        
        // Initialize the model for the ontology
        OntModel m = ModelFactory.createOntologyModel();
        
        /************
         * CLASSES  *
         ***********/
        OntClass ID = m.createClass(uri + "ID");

        OntClass family = m.createClass(uri + "FAMILY");
        
        OntClass name = m.createClass(uri + "NAME");
        
        OntClass priority = m.createClass(uri + "PRIORITY");
        
        OntClass impact = m.createClass(uri + "IMPACT");
        
        OntClass description = m.createClass(uri + "DESCRIPTION");
        
        OntClass guidance = m.createClass(uri + "GUIDANCE");
       
        
        /*******************
         * OBJECT PROPERTY *
         ******************/
        ObjectProperty hasName = m.createObjectProperty(uri +"hasName");
        hasName.addDomain(ID);
        hasName.addRange(name);
        
        ObjectProperty hasFamily = m.createObjectProperty(uri +"hasFamily");
        hasFamily.addDomain(ID);
        hasFamily.addRange(family);
        
        ObjectProperty hasPriority = m.createObjectProperty(uri +"hasPriority");
        hasPriority.addDomain(ID);
        hasPriority.addRange(priority);
        
        ObjectProperty hasImpact = m.createObjectProperty(uri +"hasImpact");
        hasImpact.addDomain(ID);
        hasImpact.addRange(impact);
        
        ObjectProperty hasDescription = m.createObjectProperty(uri +"hasDescription");
        hasDescription.addDomain(ID);
        hasDescription.addRange(description);
        
        ObjectProperty hasGuidance = m.createObjectProperty(uri +"hasGuidance");
        hasGuidance.addDomain(ID);
        hasGuidance.addRange(guidance);
        
        ObjectProperty relatedTo = m.createSymmetricProperty(uri + "relatedTo");
        

        /***************
         * INDIVIDUALS *
         **************/
        int counter = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(datasetPath));
            br.readLine(); // skip the first line (header)
            String line;
            
            while((line = br.readLine()) != null) {
                
                // Counter is used to stop while loop to not create too long file
                counter++;
//                if(counter == 450){break;} // First part of file
//                if(counter <= 450) {continue;} // Second part of file                
                
                String[] data = line.split(";");
                
                // Elements in NIST 800-53 dataset
                String idCsv = data[1];
                String nameCsv = data[2];
                String famCsv = data[0];
                String priCsv = data[3];
                String impCsv = data[4];
                String descrCsv = data[5];
                String guidCsv = data[6];
                String relCsv = data[7];
                
                // Create instances of classes
                Individual indId = ID.createIndividual(uri+ wellFormedCsv(idCsv));
                indId.addLabel(idCsv, "");
                
                Individual indName = name.createIndividual(uri+wellFormedCsv(idCsv+";"+nameCsv));
                indName.addLabel(idCsv+";"+nameCsv, "");
                
                Individual indFam = family.createIndividual(uri+ wellFormedCsv(idCsv+";"+famCsv));
                indFam.addLabel(idCsv+";"+famCsv, "");
                
                Individual indPri = priority.createIndividual(uri+ wellFormedCsv(idCsv+";"+priCsv));
                indPri.addLabel(idCsv+";"+priCsv, "");
                
                Individual indImp = impact.createIndividual(uri+ wellFormedCsv(idCsv+";"+impCsv));
                indImp.addLabel(idCsv+";"+impCsv, "");
                
                Individual indDescr = description.createIndividual(uri+ wellFormedCsv(idCsv+";"+descrCsv));
                indDescr.addLabel(idCsv+";"+descrCsv, "");
                
                Individual indGuid = guidance.createIndividual(uri+ wellFormedCsv(idCsv+";"+guidCsv));
                indGuid.addLabel(idCsv+";"+guidCsv, "");
                
                // Object Property assertions
                indId.addProperty(hasName, indName);
                indId.addProperty(hasFamily, indFam);
                indId.addProperty(hasPriority, indPri);
                indId.addProperty(hasImpact, indImp);
                indId.addProperty(hasDescription, indDescr);
                indId.addProperty(hasGuidance, indGuid);
                
                /*
                // Related controls
                if(!"NaN".equals(relCsv)){
                    String[] controls = relCsv.split(",");
                    for (String control : controls) {
                        Individual indControlRelated = ID.createIndividual(uri+ wellFormedCsv(control));
                        indId.addProperty(relatedTo, indControlRelated);
                    }
                }
                */
            }
            System.out.println("Read, parsed and inserted " + counter + " records");
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        finally {
            if (br != null){
                try {br.close();}
                catch (IOException e) {}
            }
        }
        
        // Write on file with format expressed with the attribute formatFile
        FileWriter out = null;
        try {
          out = new FileWriter(ontologyPath);
          m.write(out, formatFile);
        }
        catch (IOException ex) {Logger.getLogger(ISO_Model.class.getName()).log(Level.SEVERE, null, ex);}
        finally {
          if (out != null){
            try {out.close();}
            catch (IOException ex){}}
        }
        return m;
    }
        
    /**
     * These methods avoid misspelling due to format of csv for the ontology
     * @param str
     * @return well formed string for uri
     */
    private static String wellFormedCsv(String input) {
        if (input.contains("\"")) {
            input = input.replaceAll("\"", "");
        }
        if (input.contains(",")) {
            input = String.format("\"%s\"", input);
        }
        input = input.replace("\"", "");
        input = input.replace(" ", "%20");
        input = input.replace("[", "(");
        input = input.replace("]", ")");
        return input;
    }
}
