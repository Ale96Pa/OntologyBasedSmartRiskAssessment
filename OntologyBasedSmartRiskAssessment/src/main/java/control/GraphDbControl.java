package control;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.TransactionWork;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;


import static org.neo4j.driver.Values.parameters;

public class GraphDbControl implements AutoCloseable{
    
    private final Driver driver;
    final String pathHumanGraph = "src\\main\\java\\graphs\\humanLayerAttackGraphRepr.json";
    final String pathHumanVulnerability = "src\\main\\java\\dataset\\AG\\humanVulnerability.json";
    final String pathAccessGraph = "src\\main\\java\\graphs\\interLayerAttackGraphRepr.json";
    final String pathNetworkGraph = "src\\main\\java\\graphs\\networkLayerAttackGraphRepr.json";
    final String pathNetworkVulnerability = "src\\main\\java\\dataset\\AG\\networkVulnerability.json";

    public GraphDbControl( String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() throws Exception{driver.close();}
    
    private void flushGraph(){
        try (Session session = driver.session()){
            session.writeTransaction(new TransactionWork<Void>(){
                
                @Override
                public Void execute(Transaction tx){
                    tx.run( "MATCH (n) DETACH DELETE n");
                    return null;
                }
            });
        }
    }

    
    private void addHumanNode(final String uuid, final String employeeId, final String privLevel) {
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
    
    private void addHumanRelation(final String src, final String dest, final String vulnId, final String vulnType) {
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
    
    private void attachLambdaHuman(){
        try (Session session = driver.session()){
            session.writeTransaction(new TransactionWork<Void>(){
                
                @Override
                public Void execute(Transaction tx){
                    Result result = tx.run( "MATCH (h1)-[ww:work_with]->(h2) RETURN ww.vulnId" );
                    while ( result.hasNext() )
                    {
                        Record record = result.next();
                        //System.out.println( String.format( "%s", record.get( "ww.vulnId" ).asString()) );

                        JSONParser parser = new JSONParser();
                        try {
                            Object obj = parser.parse(new FileReader(pathHumanVulnerability));
                            JSONObject jsonObject = (JSONObject) obj;
                            JSONArray vulnerabilityArray = (JSONArray)jsonObject.get("humanVulnerabilities");
                            for(Object vuln : vulnerabilityArray){
                                JSONObject vulnObj = (JSONObject) vuln;
                                String vulnJson = (String) vulnObj.get("id");
                                String resultMatch = record.get("ww.vulnId").asString();
                                if(resultMatch.equals(vulnJson)){
                                    double av = (double) vulnObj.get("accessVectorScore");
                                    double ac = (double) vulnObj.get("attackComplexityScore");
                                    double lambda = av*ac;

                                    tx.run("MATCH (h1:HUMAN)-[ww:work_with]->(h2:HUMAN) " +
                                            "SET( " +
                                            "CASE ww.vulnId " +
                                            "WHEN $vulnerability THEN ww " +
                                            "END).lambda=$lambda", 
                                            parameters("vulnerability", vulnJson, "lambda", lambda));
                                }
                            }
                        } catch(IOException | ParseException e){} 
                    }
                    return null;
                }
            });
        }
    }
    
    private void addHumanLayer(){
        
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
            attachLambdaHuman();
        } catch(IOException | ParseException e){} 
    }
    
    
    private void addAccessNode(final String uuid, final String credentialType) {
        try (Session session = driver.session()){
            session.writeTransaction(new TransactionWork<Void>(){
                
                @Override
                public Void execute(Transaction tx){
                    tx.run( "MERGE (a:ACCESS {uuid: $uuid, credentialType: $credentialType})", 
                    parameters( "uuid", uuid, "credentialType", credentialType) 
                    );
                    return null;
                }
            });
        }
    }
    
    private void addHumanAccessRelation(final String src, final String dest) {
        try (Session session = driver.session()){
            session.writeTransaction(new TransactionWork<Void>(){
                
                @Override
                public Void execute(Transaction tx){
                    tx.run( "MATCH (h:HUMAN {uuid: $src}) " +
                            "MATCH (a:ACCESS {uuid: $dest}) " +
                            "MERGE (h)-[il:inter_layer]->(a)", 
                    parameters("src", src, "dest", dest)
                    );
                    return null;
                }
            });
        }
    }
    private void addAccessNetworkRelation(final String src, final String dest) {
        try (Session session = driver.session()){
            session.writeTransaction(new TransactionWork<Void>(){
                
                @Override
                public Void execute(Transaction tx){
                    tx.run( "MATCH (a:ACCESS {uuid: $src}) " +
                            "MATCH (n:NETWORK {uuid: $dest}) " +
                            "MERGE (a)-[il:inter_layer]->(n)", 
                    parameters("src", src, "dest", dest)
                    );
                    return null;
                }
            });
        }
    }
    
    private void addAccessLayer(){
        
        String uuid = "", credType="";
        String srcH = "", destH = "";
        String srcN = "", destN = "";
                
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(pathAccessGraph));
            JSONObject jsonObject = (JSONObject)obj;
            JSONArray nodesAccess = (JSONArray)jsonObject.get("nodes");
            JSONArray edgesHuman = (JSONArray)jsonObject.get("humanAccessEdges");
            JSONArray edgesNetwork = (JSONArray)jsonObject.get("accessNetworkEdges");

            for (Object node : nodesAccess) {
                // Parse JSON file
                JSONObject nodeObj = (JSONObject) node;
                uuid = (String) nodeObj.get("uuid");
                credType = (String) nodeObj.get("credentialType");
                
                addAccessNode(uuid, credType);
            }
            for (Object edgeH : edgesHuman){
                // Parse JSON file
                JSONObject edgeObj = (JSONObject) edgeH;
                srcH = (String) edgeObj.get("source");
                destH = (String) edgeObj.get("destination");
                    
                addHumanAccessRelation(srcH, destH);
            }
            for (Object edgeN : edgesNetwork){
                // Parse JSON file
                JSONObject edgeObj = (JSONObject) edgeN;
                srcN = (String) edgeObj.get("source");
                destN = (String) edgeObj.get("destination");
                    
                addAccessNetworkRelation(srcN, destN);
            }
            
        } catch(IOException | ParseException e){} 
    }
    
    private void addNetworkNode(final String uuid, final String deviceId, final String privLevel) {
        try (Session session = driver.session()){
            session.writeTransaction(new TransactionWork<Void>(){
                
                @Override
                public Void execute(Transaction tx){
                    tx.run( "MERGE (n:NETWORK {uuid: $uuid, deviceId: $deviceId, privLevel: $privLevel})", 
                    parameters( "uuid", uuid, "deviceId", deviceId, "privLevel", privLevel) 
                    );
                    return null;
                }
            });
        }
    }

    private void addNetworkRelation(final String src, final String dest, 
            final String cve, final String type, final long port, final String protocol) {
        try (Session session = driver.session()){
            session.writeTransaction(new TransactionWork<Void>(){
                
                @Override
                public Void execute(Transaction tx){
                    tx.run( "MATCH (n1:NETWORK {uuid: $src}) " +
                            "MATCH (n2:NETWORK {uuid: $dest}) " +
                            "MERGE (n1)-[c:communicate {cve: $cve, type: $type, " +
                            "port: $port, protocol: $protocol}]->(n2)", 
                    parameters("src", src, "dest", dest, "cve", cve, "type", type, "port", port, "protocol", protocol)
                    );
                    return null;
                }
            });
        }
    }
    
    private void addNetworkLayer(){
        
        String uuid = "", deviceId="", privLevel = "";
        String src = "", dest = "", cve ="", type = "", protocol = "";
        long port;
                
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(pathNetworkGraph));
            JSONObject jsonObject = (JSONObject)obj;
            JSONArray nodesNetwork = (JSONArray)jsonObject.get("nodes");
            JSONArray edgesNetwork = (JSONArray)jsonObject.get("edges");

            for (Object node : nodesNetwork) {
                // Parse JSON file
                JSONObject nodeObj = (JSONObject) node;
                uuid = (String) nodeObj.get("uuid");
                deviceId = (String) nodeObj.get("deviceId");
                privLevel = (String) nodeObj.get("privLevel");
                
                addNetworkNode(uuid, deviceId, privLevel);
            }
            for (Object edge : edgesNetwork){
                // Parse JSON file
                JSONObject edgeObj = (JSONObject) edge;
                src = (String) edgeObj.get("source");
                dest = (String) edgeObj.get("destination");
                
                JSONArray edgesNetworkVuln = (JSONArray)edgeObj.get("vulnerabilities");
                for (Object vuln : edgesNetworkVuln){
                    JSONObject vulnObj = (JSONObject) vuln;
                    cve = (String) vulnObj.get("CVE");
                    type = (String) vulnObj.get("type");
                    port = (long) vulnObj.get("port");
                    protocol = (String) vulnObj.get("protocol");
                    if(protocol == null){protocol = "";}
                    
                    addNetworkRelation(src, dest, cve, type, port, protocol);
                }
            }
            attachLambdaNetwork();
        } catch(IOException | ParseException e){} 
    }
    
    private void attachLambdaNetwork(){
        try (Session session = driver.session()){
            session.writeTransaction(new TransactionWork<Void>(){
                
                @Override
                public Void execute(Transaction tx){
                    Result result = tx.run( "MATCH (n1)-[c:communicate]->(n2) RETURN c.cve" );
                    while ( result.hasNext() )
                    {
                        Record record = result.next();
                        String resultMatch = record.get("c.cve").asString();

                        JSONParser parser = new JSONParser();
                        try {
                            Object obj = parser.parse(new FileReader(pathNetworkVulnerability));
                            JSONObject jsonObject = (JSONObject) obj;
                            JSONArray vulnerabilityArray = (JSONArray)jsonObject.get("vulnerabilities");
                            
                            for(Object vuln : vulnerabilityArray){
                                JSONObject vulnObj = (JSONObject) vuln;
                                String vulnId = (String) vulnObj.get("cveId");
                                
                                if(resultMatch.equals(vulnId)){
                                    
                                    JSONObject impactV2 = (JSONObject) vulnObj.get("impactV2");
                                    double av, ac, pr, cm, rc;
                                    double av2=0.0, ac2=0.0, pr2=0.0, cm2=0.0, rc2=0.0;
                                    double av3=0.0, ac3=0.0, pr3=0.0, cm3=0.0, rc3=0.0;
                                    
                                    if(impactV2.containsKey("accessVectorScore")){
                                        av2 = (double) impactV2.get("accessVectorScore");}
                                    if(impactV2.containsKey("accessComplexityScore")){
                                        ac2 = (double) impactV2.get("accessComplexityScore");}
                                    if(impactV2.containsKey("privilegesRequired")){
                                        pr2 = (double) impactV2.get("privilegesRequired");}
                                    if(impactV2.containsKey("explotabilityCodeMaturity")){
                                        cm2 = (double) impactV2.get("explotabilityCodeMaturity");}
                                    if(impactV2.containsKey("reportConfidence")){
                                        rc2 = (double) impactV2.get("reportConfidence");}
                                    
                                    if(vulnObj.containsKey("impactV3")){ 
                                        JSONObject impactV3 = (JSONObject) vulnObj.get("impactV3");
                                        //double av3=0.0, ac3=0.0, pr3=0.0, cm3=0.0, rc3=0.0;
                                        if(impactV3.containsKey("accessVectorScore")){
                                            av3 = (double) impactV3.get("accessVectorScore");}
                                        if(impactV3.containsKey("accessComplexityScore")){
                                            ac3 = (double) impactV3.get("accessComplexityScore");}
                                        if(impactV3.containsKey("privilegesRequired")){
                                            pr3 = (double) impactV3.get("privilegesRequired");}
                                        if(impactV3.containsKey("explotabilityCodeMaturity")){
                                            cm3 = (double) impactV3.get("explotabilityCodeMaturity");}
                                        if(impactV3.containsKey("reportConfidence")){
                                            rc3 = (double) impactV3.get("reportConfidence");}
                                    }
                                    
                                    av = Math.max(av2, av3);
                                    ac = Math.max(ac2, ac3);
                                    pr = Math.max(pr2, pr3);
                                    cm = Math.max(cm2, cm3);
                                    rc = Math.max(rc2, rc3);                           
                                    double lambda = av*ac*pr*cm*rc;

                                    tx.run("MATCH (n1:NETWORK)-[c:communicate]->(n2:NETWORK) " +
                                            "SET( " +
                                            "CASE c.cve " +
                                            "WHEN $cve THEN c " +
                                            "END).lambda=$lambda", 
                                            parameters("cve", vulnId, "lambda", lambda));
                                }
                            }
                        } catch(IOException | ParseException e){} 
                    }
                    return null;
                }
            });
        }
    }
    
    public void buldGraph(){
        flushGraph();
        addHumanLayer();
        addNetworkLayer();
        addAccessLayer();
        /*
        try {
            close();
        } catch (Exception ex) {
            Logger.getLogger(GraphDbControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }
    
    
    public ArrayList<Edge> setHumanEdges(){
        
        final String layer = "human";
        ArrayList<Edge> edges = new ArrayList();
        
        try (Session session = driver.session()){
            
            edges = session.writeTransaction(new TransactionWork<ArrayList<Edge>>(){
                
                @Override
                public ArrayList<Edge> execute(Transaction tx){
                    
                    ArrayList<Edge> humanEdges = new ArrayList();
                    
                    Result result = tx.run( "MATCH (a)-[w:work_with]->(b) RETURN w.vulnId, w.explType, a.uuid, b.uuid, w.lambda" );
                    while ( result.hasNext() ){
                        ArrayList<String> description = new ArrayList();
                        
                        Record record = result.next();
                        description.add("source:"+String.format(record.get( "a.uuid" ).asString()));
                        description.add("destination:"+String.format(record.get( "b.uuid" ).asString()));
                        description.add("vulnId:"+String.format(record.get( "w.vulnId" ).asString()));
                        description.add("explType:"+String.format(record.get( "w.explType" ).asString()));
                        double lambda = record.get( "w.lambda" ).asDouble();
                        
                        Edge e = new Edge(layer, lambda, description);
                        humanEdges.add(e);                        
                    }
                    return humanEdges;
                }
            });
        }
        return edges;
    }
    
    public ArrayList<Edge> setNetworkEdges(){
        
        final String layer = "network";
        ArrayList<Edge> edges = new ArrayList();
        
        try (Session session = driver.session()){
            
            edges = session.writeTransaction(new TransactionWork<ArrayList<Edge>>(){
                
                @Override
                public ArrayList<Edge> execute(Transaction tx){
                    
                    ArrayList<Edge> networkEdges = new ArrayList();

                    Result result = tx.run( "MATCH (a)-[c:communicate]->(b) "
                            + "RETURN a.uuid, b.uuid, c.lambda, c.cve, c.type, c.port, c.protocol" );
                    
                    while ( result.hasNext() ){
                        ArrayList<String> description = new ArrayList();
                        
                        Record record = result.next();
                        description.add("source:"+String.format(record.get( "a.uuid" ).asString()));
                        description.add("destination:"+String.format(record.get( "b.uuid" ).asString()));
                        description.add("cve:"+String.format(record.get( "c.cve" ).asString()));
                        description.add("type:"+String.format(record.get( "c.type" ).asString()));
                        description.add("port:"+record.get( "c.port" ).asInt());
                        description.add("protocol:"+String.format(record.get( "c.protocol" ).asString()));
                        double lambda = record.get( "c.lambda" ).asDouble();
                        
                        Edge e = new Edge(layer, lambda, description);
                        networkEdges.add(e);                        
                    }
                    return networkEdges;
                }
            });
        }
        return edges;
    }
}
