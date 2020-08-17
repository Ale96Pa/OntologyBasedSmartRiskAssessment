package models;

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

public class ManagementLifetime_Model {
    /* Constant attributes */
    // Source file
    final String datasetPath = "src\\main\\java\\dataset\\managementLifetime.csv";
    
    // Destination file
    final String ontologyPath = "src\\main\\java\\dataset\\ManagementLifetime_ontology.owl";
    String formatFile = "RDF/XML-ABBREV"; // Format of the output file
    
    // Local namespace for entities
    final String uri = "http://thesisAP.com/mng-lt#" ;

    public String getUri() {
        return uri;
    }
    
    /*
    The method createISOModel creates the ontology of the management level
    taking in input a file with the following elements:
    Type;Example
    
    It writes the model into a file stored in the dataset package and it 
    returns the OntoModel.
    */
    public OntModel createManagementModel(){
        
        // Initialize the model for the ontology
        OntModel m = ModelFactory.createOntologyModel();
        
        /************
         * CLASSES  *
         ***********/        
        OntClass example = m.createClass(uri + "EXAMPLE");
        
        OntClass compliance = m.createClass(uri + "COMPLIANCE");
        
        OntClass operational = m.createClass(uri + "OPERATIONAL");
        
        OntClass runtime = m.createClass(uri + "RUNTIME");
        
        OntClass designtime = m.createClass(uri + "DESIGNTIME");
        
        
        /*******************
         * OBJECT PROPERTY *
         ******************/

        ObjectProperty hasExample = m.createObjectProperty(uri +"hasExample");
        hasExample.addRange(example);
        
       
        /***************
         * INDIVIDUALS *
         **************/
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(datasetPath));
            br.readLine(); // skip the first line (header)
            String line;
            
            while((line = br.readLine()) != null) {
                
                String[] data = line.split(";");
                
                // Elements in ISO 27001:2013 dataset
                String typeCsv = data[0];
                String exampleCsv = data[1];
                
                
                // Create instances of classes
                switch (typeCsv) {
                    case "Compliance":
                        {
                            Individual indCompl = m.createIndividual(uri + wellFormedCsv(exampleCsv), compliance);
                            indCompl.addLabel(exampleCsv, "");
                            break;
                        }
                    case "Operational":
                        {
                            Individual indOper = m.createIndividual(uri + wellFormedCsv(exampleCsv), operational);
                            indOper.addLabel(exampleCsv, "");
                            break;
                        }
                    case "Runtime":
                        {
                            Individual indRuntime = m.createIndividual(uri + wellFormedCsv(exampleCsv), runtime);
                            indRuntime.addLabel(exampleCsv, "");
                            break;
                        }
                    case "Designtime":
                        {
                            Individual indDesigntime = m.createIndividual(uri + wellFormedCsv(exampleCsv), designtime);
                            indDesigntime.addLabel(exampleCsv, "");
                            break;
                        }
                }
            }
            System.out.println("Read, parsed and inserted management and lifetime records");
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
