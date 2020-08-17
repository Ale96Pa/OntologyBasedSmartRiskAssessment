package control;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import static java.lang.Float.parseFloat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import models.ISO_Model;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecException;
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
    
    final String prefix = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
    final String prefixIso = prefix + "PREFIX myns: <http://thesisAP.com/iso#>";
    
    String query = "SELECT ?class ?prop ?a ?b "
                    + "WHERE {?prop rdfs:range myns:HVUL. "
                    + "?prop rdfs:domain ?class. "
                    + "?class ?a ?b.}";
    
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
    
    
    public void calculateMatching(OntModel srcM, OntModel targetM, ArrayList<Alignment> alignments){
        
        for (Alignment alignment : alignments) {
                        
            Individual source = srcM.getIndividual(alignment.getSourceUri());
            
            Property pp = srcM.getProperty("http://thesisAP.com/iso#hasVulnerabilty");
            System.out.println(source.getPropertyValue(pp));
            
            OntClass classSource = source.getOntClass();
            
            OntClass idClass = srcM.getOntClass("http://thesisAP.com/iso#hasVulnerabilty");
            
            //System.out.println(alignment.getSourceUri());
            //System.out.println(idClass);
            
            //makeQuery(srcM, query);

        }
    }
    
    public ArrayList<String> makeQuery(OntModel m, String query){
        ArrayList<String> resultsOfQuery = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String fullQuery = prefixIso + query;
        Query qry = QueryFactory.create(fullQuery);
        QueryExecution queryExec = QueryExecutionFactory.create(qry, m);
        ResultSet resultSet;
        resultSet = queryExec.execSelect();
        String[] vars = query.split("WHERE")[0].split("\\?");
        int numVar = vars.length;
        String varsStringPrint="";
        for(int i=1; i<numVar; i++){
            varsStringPrint = varsStringPrint + "|" + vars[i].replace(" ", "");
        }
        resultsOfQuery.add("RESULT in format: ["+varsStringPrint+"]\n");
        while(resultSet.hasNext()){
            QuerySolution sol = resultSet.nextSolution();
            String[] results = new String[numVar-1];
            for(int i=1; i<numVar; i++){
                RDFNode res = sol.get(vars[i].replace(" ", ""));
                results[i-1] = res.toString();//.split("#")[1];
            }
            // Print the result
            System.out.println(Arrays.toString(results));
            resultsOfQuery.add(Arrays.toString(results));
        }
        queryExec.close();
        
        return resultsOfQuery;
    }
}
