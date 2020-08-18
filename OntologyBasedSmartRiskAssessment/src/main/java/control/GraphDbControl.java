package control;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.ontology.Individual;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.neo4j.driver.AccessMode;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Bookmark;
import org.neo4j.driver.SessionConfig;

import static org.neo4j.driver.Values.parameters;
import static org.neo4j.driver.SessionConfig.builder;

import static org.neo4j.driver.Values.parameters;

public class GraphDbControl implements AutoCloseable{
    
    private final Driver driver;
    final String pathHumanGraph = "src\\main\\java\\graphs\\humanLayerAttackGraphRepr.json";
    final String pathHumanVulnerability = "src\\main\\java\\dataset\\AG\\humanVulnerability.json";
    final String pathAccessGraph = "src\\main\\java\\graphs\\interLayerAttackGraphRepr.json";
    final String pathNetworkGraph = "src\\main\\java\\graphs\\networkLayerAttackGraphRepr.json";

    public GraphDbControl( String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() throws Exception{driver.close();}

    
    public void addHumanNode(final String uuid, final String employeeId, final String privLevel) {
        try (Session session = driver.session()){
            session.writeTransaction(new TransactionWork<Void>(){
                
                @Override
                public Void execute(Transaction tx){
                    tx.run( "MERGE (h:HUMAN {uuid: $uuid, employee: $employeeId , privilege: $privLevel})", 
                    parameters( "uuid", uuid, "employeeId", employeeId, "privLevel", privLevel) 
                    );
                    return null;
                }
            });
        }
    }
    
    public void addHumanRelation(final String src, final String dest, final String vulnId, final String vulnType) {
        try (Session session = driver.session()){
            session.writeTransaction(new TransactionWork<Void>(){
                
                @Override
                public Void execute(Transaction tx){
                    tx.run( "MATCH (h1:HUMAN {uuid: $src }) " +
                    "MATCH (h2:HUMAN {uuid: $dest }) " +
                    "MERGE (h1)-[ww:work_with {vulnId:  $vulnId , explType:  $vulnType }]->(h2)", 
                    parameters("src", src, "dest", dest, "vulnId", vulnId, "vulnType", vulnType)
                    );
                    return null;
                }
            });
        }
    }
    
    public void attachLambdaHuman(final String uuid){
        String vulnId="";
        try (Session session = driver.session()){
            session.writeTransaction(new TransactionWork<Void>(){
                
                @Override
                public Void execute(Transaction tx){
                    Result result = tx.run( "MATCH (h1)-[ww:work_with]->(h2) RETURN ww.vulnId" );
                    while ( result.hasNext() )
                    {
                        Record record = result.next();
                        System.out.println( String.format( "%s", record.get( "ww.vulnId" ).asString()) );
                        
                        JSONParser parser = new JSONParser();
                        try {
                            Object obj = parser.parse(new FileReader(pathHumanVulnerability));
                            JSONObject jsonObject = (JSONObject)obj;
                            JSONArray vulnerabilityArray = (JSONArray)jsonObject.get("humanVulnerabilities");
                            for(Object vuln : vulnerabilityArray){
                                JSONObject vulnObj = (JSONObject) vuln;
                                String vulnJson = (String) vulnObj.get("id");
                                if(String.format( "%s", record.get( "ww.vulnId" ).asString()) == vulnJson){
                                    long av = (long) vulnObj.get("accessVectorScore");
                                    long ac = (long) vulnObj.get("attackComplexityScore");
                                    long labda = av*ac;
                                    System.out.println(labda);
                                }
                            }
                            
                        } catch(IOException | ParseException e){} 
                        
                    }
                    return null;
                }
            });
        }
    }
    
    public void addHumanLayer(){
        
        String uuid = "", emplId = "", privLevel="";
        String src = ""; String dest = ""; String vulnId = ""; String vulnType = "";
                
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(pathHumanGraph));
            JSONObject jsonObject = (JSONObject)obj;
            JSONArray nodesHuman = (JSONArray)jsonObject.get("nodes");
            JSONArray edgesHuman = (JSONArray)jsonObject.get("edges");

            for (Object node : nodesHuman) {
                // Parse JSON file
                JSONObject nodeObj = (JSONObject) node;
                uuid = (String) nodeObj.get("uuid");
                emplId = (String) nodeObj.get("employeeId");
                privLevel = (String) nodeObj.get("privLevel");
                
                addHumanNode(uuid, emplId, privLevel);
            }
            for (Object edge : edgesHuman){
                // Parse JSON file
                JSONObject edgeObj = (JSONObject) edge;
                src = (String) edgeObj.get("source");
                dest = (String) edgeObj.get("destination");
                JSONArray edgesHumanVuln = (JSONArray)edgeObj.get("vulnerabilities");
                for (Object vuln : edgesHumanVuln){
                    JSONObject vulnObj = (JSONObject) vuln;
                    vulnId = (String) vulnObj.get("vulnId");
                    vulnType = (String) vulnObj.get("exploitationType");
                    
                    addHumanRelation(src, dest, vulnId, vulnType);
                }
            }
        } catch(IOException | ParseException e){} 
    }
    
    
    
    // Match and display human layer relationships.
    public void printHuman(){
        try (Session session = driver.session()){
            
            session.writeTransaction(new TransactionWork<Void>()
            {
                @Override
                public Void execute(Transaction tx){
                    Result result = tx.run( "MATCH (a)-[:work_with]->(b) RETURN a.uuid, b.uuid" );
                    while ( result.hasNext() )
                    {
                        Record record = result.next();
                        System.out.println( String.format( "%s :ww: %s", record.get( "a.uuid" ).asString(), record.get( "b.uuid" ).toString() ) );
                    }
                    return null;
                }
            } );
        }
    }

   
}
