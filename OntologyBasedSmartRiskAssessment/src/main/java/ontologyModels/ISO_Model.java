/*
 * This file build and write the model for ISO 27001:2013 controls into a 
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

public class ISO_Model {
    
    Config conf = new Config();
    // Source file
    final String datasetPath = conf.getIsoCsvPath();
    // Destination file
    final String ontologyPath = conf.getIsoOwlPath();
    // Format of the output file
    String formatFile = conf.getFormatOntology(); 
    // Local namespace for entities
    final String uri = conf.getUriIso();

    /*
    The method createISOModel creates the ontology of the ISO 27001:2013 
    controls taking in input a file with the following elements:
    ID;Name;Category;Sub-category;Objective;Decription;VonSolmsExample;
    MehariExample;MehariLabel;VulnerabiltyPanacea
    It writes the model into a file stored in the dataset package and it 
    returns the OntoModel.
    */
    public OntModel createISOModel(){
        
        // Initialize the model for the ontology
        OntModel m = ModelFactory.createOntologyModel();
        
        /************
         * CLASSES  *
         ***********/
        OntClass ID = m.createClass(uri + "ID");
        
        OntClass name = m.createClass(uri + "NAME");
        
        OntClass category = m.createClass(uri + "CATEGORY");
        
        OntClass subcategory = m.createClass(uri + "SUBCATEGORY");
        
        OntClass objective = m.createClass(uri + "OBJECTIVE");
        
        OntClass description = m.createClass(uri + "DESCRIPTION");
        
        OntClass VS_example = m.createClass(uri + "VS-EXAMPLE");
        
        OntClass M_example = m.createClass(uri + "M-EXAMPLE");
        
        OntClass M_label = m.createClass(uri + "M-LABEL");
        
        OntClass hvul = m.createClass(uri + "HVUL");
        
        // Create classes hierarchy
        category.addSubClass(subcategory);
        M_example.addSubClass(M_label);
        
        
        /*******************
         * OBJECT PROPERTY *
         ******************/
        ObjectProperty hasName = m.createObjectProperty(uri +"hasName");
        hasName.addDomain(ID);
        hasName.addRange(name);
        
        ObjectProperty isCategory = m.createObjectProperty(uri +"isCategory");
        isCategory.addDomain(ID);
        isCategory.addRange(category);
        
        ObjectProperty hasObjective = m.createObjectProperty(uri +"hasObjective");
        hasObjective.addDomain(ID);
        hasObjective.addRange(objective);
        
        ObjectProperty hasDescription = m.createObjectProperty(uri +"hasDescription");
        hasDescription.addDomain(ID);
        hasDescription.addRange(description);
        
        ObjectProperty exampleM = m.createSymmetricProperty(uri + "mehariExample"); // symmetric
        
        ObjectProperty exampleVS = m.createSymmetricProperty(uri + "vonSolmsExample"); // symmetric
        
        ObjectProperty hasVulnerability = m.createObjectProperty(uri +"hasVulnerability");
        hasVulnerability.addDomain(ID);
        hasVulnerability.addRange(hvul);
        
       
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
//                if(counter == 150){break;} // First part of file
//                if(counter <= 150) {continue;} // Second part of file
                
                String[] data = line.split(";");
                
                // Elements in ISO 27001:2013 dataset
                String idCsv = data[0];
                String nameCsv = data[1];
                String catCsv = data[2];
                String subcatCsv = data[3];
                String objCsv = data[4];
                String descrCsv = data[5];
                String vsExCsv = data[6];
                String mehExCsv = data[7];
                String mehLabel = data[8];
                String vulnPanaceaCsv = data[9];
                
                
                // Create instances of classes
                Individual indId = m.createIndividual(uri + wellFormedCsv(idCsv), ID);
                indId.addLabel(idCsv, "");
                
                Individual indName = m.createIndividual(uri + wellFormedCsv(nameCsv), name);
                indName.addLabel(nameCsv, "");
                
                Individual indCat = m.createIndividual(uri+ wellFormedCsv(catCsv), category);
                indCat.addLabel(catCsv, "");
                
                Individual indSubcat = m.createIndividual(uri+ wellFormedCsv(subcatCsv), subcategory);
                indSubcat.addLabel(subcatCsv, "");
                
                Individual indObj = m.createIndividual(uri+ wellFormedCsv(objCsv), objective);
                indObj.addLabel(objCsv, "");
                
                Individual indDescr = m.createIndividual(uri+ wellFormedCsv(descrCsv), description);
                indDescr.addLabel(descrCsv, "");
                
                Individual indVonSolms = m.createIndividual(uri+ wellFormedCsv(vsExCsv), VS_example);
                indVonSolms.addLabel(vsExCsv, "");
                
                Individual indMehari = m.createIndividual(uri+ wellFormedCsv(mehExCsv), M_example);
                indMehari.addLabel(mehExCsv, "");
                
                Individual indMLabel = m.createIndividual(uri+ wellFormedCsv(mehLabel), M_label);
                indMLabel.addLabel(mehLabel, "");
                
                Individual indPanacea = m.createIndividual(uri+ wellFormedCsv(vulnPanaceaCsv), hvul);
                indPanacea.addLabel(vulnPanaceaCsv, "");
                
                /***************************
                * OBJECT PROPERTY ASSERTON *
                ****************************/
                indId.addProperty(hasName, indName);
                indId.addProperty(isCategory, indCat);
                indId.addProperty(hasObjective, indObj);
                indId.addProperty(hasDescription, indDescr);
                indId.addProperty(exampleM, indMehari);
                indId.addProperty(exampleVS, indVonSolms);
                indId.addProperty(hasVulnerability, indPanacea);
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
     * This method avoid misspelling due to format in the ontology
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
