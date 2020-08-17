/*
 * This file build and write the model for an instance of the Multi-layer attack
 * graph into a suitable ontology.

 * Author: Alessandro Palma
 * Master Thesis in Engineering in Computer Science
 * University of Rome "La Sapienza"
 */
package models;

import java.io.File;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//D:\projects\TESI_project\SmartRiskAssessment\src\main\java\com\assessment\dataset\
public class AG_Model {
    
    /* Constant attributes */
    // Source file
    final String datasetPath = "src\\main\\java\\dataset\\AG\\";
    
    // Destination file
    final String ontologyPath = "src\\main\\java\\dataset\\AG_ontology.owl";
    String formatFile = "RDF/XML-ABBREV"; // Format of the output file
    
    // Local namespace for entities
    final String uri = "http://thesisAP.com/mlag#" ;
    
    /*
    The method createAGModel creates the ontology of the attack graph 
    taking in input the files containing the following information:
    TODO
    
    It writes the model into a file stored in the dataset package and it 
    returns the OntoModel.
    */
    
    public OntModel createAGModel(){
        
        // Initialize the model for the ontology
        OntModel m = ModelFactory.createOntologyModel();
        
        /************
         * CLASSES  *
         ***********/
        OntClass layer = m.createClass(uri + "LAYER");
       
        // Access layer
        OntClass accessLayer = m.createClass(uri + "ACCESS");
        OntClass credential = m.createClass(uri + "CREDENTIAL");
        OntClass privLevel = m.createClass(uri + "PRIVILEGE_LEVEL");
        OntClass privType = m.createClass(uri + "PRIVILEGE_TYPE");
        
        // Human layer
        OntClass humanLayer = m.createClass(uri + "HUMAN");
        OntClass employee = m.createClass(uri + "EMPLOYEE");
        OntClass position = m.createClass(uri + "E_POSITION");
        OntClass location = m.createClass(uri + "E_LOCATION");
        OntClass relType = m.createClass(uri + "RELATION");
        OntClass vulnerabilityHum = m.createClass(uri + "HUMAN_VULNERABILITY");
        OntClass name = m.createClass(uri + "HV_NAME");
        OntClass description = m.createClass(uri + "HV_DESCRIPTION");
        OntClass accessVector = m.createClass(uri + "ACCESS_VECTOR");
        OntClass score = m.createClass(uri + "AV-SCORE");
        OntClass postCondition = m.createClass(uri + "POST-CONDITION");
        OntClass preCondition = m.createClass(uri + "PRE-CONDITION");
        
        // Network layer
        OntClass networkLayer = m.createClass(uri + "NETWORK");
        OntClass device = m.createClass(uri + "DEVICE");
        OntClass type = m.createClass(uri + "TYPE");
        OntClass netIface = m.createClass(uri + "NETWORK_INTERFACE");
        OntClass version = m.createClass(uri + "VERSION");
        OntClass mask = m.createClass(uri + "MASK");
        OntClass port = m.createClass(uri + "PORT");
        OntClass number = m.createClass(uri + "NUMBER");
        OntClass state = m.createClass(uri + "STATE");
        OntClass protocol = m.createClass(uri + "PROTOCOL");
        OntClass service = m.createClass(uri + "PORT_SERVICE");
        OntClass cpe = m.createClass(uri + "CPE");
        OntClass cve = m.createClass(uri + "CVE");
        OntClass os = m.createClass(uri + "OPERATING_SYSTEM");
        OntClass osService = m.createClass(uri + "OS_SERVICE");
        OntClass vulnerabilityNet = m.createClass(uri + "NETWORK_VULNERABILITY");
        OntClass cwe = m.createClass(uri + "CWE");
        
        
        // Create classes hierarchy
        layer.addSubClass(accessLayer);
        layer.addSubClass(humanLayer);
        layer.addSubClass(networkLayer);
        
        accessLayer.addSubClass(credential);
        humanLayer.addSubClass(employee);
        humanLayer.addSubClass(vulnerabilityHum);
        networkLayer.addSubClass(device);
        
        
        /*******************
         * OBJECT PROPERTY *
         ******************/
        
        // Access layer
        ObjectProperty hasPrivLevel = m.createObjectProperty(uri +"hasPrivilegeLevel");
        hasPrivLevel.addDomain(credential);
        hasPrivLevel.addRange(privLevel);
        
        ObjectProperty hasPrivType = m.createObjectProperty(uri +"hasPrivilegeType");
        hasPrivType.addDomain(credential);
        hasPrivType.addRange(privType);
        
        // Human layer
        ObjectProperty hasPosition = m.createObjectProperty(uri +"hasPosition");
        hasPosition.addDomain(employee);
        hasPosition.addRange(position);
        
        ObjectProperty hasLocation = m.createObjectProperty(uri +"hasLocation");
        hasLocation.addDomain(employee);
        hasLocation.addRange(location);
        
        ObjectProperty hasRelation = m.createObjectProperty(uri +"hasRelation");
        hasRelation.addDomain(employee);
        hasRelation.addRange(relType);
        
        ObjectProperty hasName = m.createObjectProperty(uri +"hasName");
        hasName.addDomain(vulnerabilityHum);
        hasName.addRange(name);
        
        ObjectProperty hasDescription = m.createObjectProperty(uri +"hasDescription");
        hasDescription.addDomain(vulnerabilityHum);
        hasDescription.addRange(description);
        
        ObjectProperty hasAccessVector = m.createObjectProperty(uri +"hasAccessVector");
        hasAccessVector.addDomain(vulnerabilityHum);
        hasAccessVector.addRange(accessVector);
        
        ObjectProperty hasScore = m.createObjectProperty(uri +"hasScore");
        hasScore.addDomain(accessVector);
        hasScore.addRange(score);
        
        ObjectProperty hasPostCondition = m.createObjectProperty(uri +"hasPostCondition");
        hasPostCondition.addDomain(vulnerabilityHum);
        hasPostCondition.addRange(postCondition);
        
        ObjectProperty hasPreCondition = m.createObjectProperty(uri +"hasPreCondition");
        hasPreCondition.addDomain(vulnerabilityHum);
        hasPreCondition.addRange(preCondition);
        
        // Network layer
        ObjectProperty hasType = m.createObjectProperty(uri +"hasType");
        hasType.addDomain(device);
        hasType.addRange(type);
        
        ObjectProperty hasOs = m.createObjectProperty(uri +"hasOperatingSystem");
        hasOs.addDomain(device);
        hasOs.addRange(os);
        
        ObjectProperty hasOsService = m.createObjectProperty(uri +"hasOperatingSystemService");
        hasOsService.addDomain(os);
        hasOsService.addRange(osService);
        
        ObjectProperty hasIface = m.createObjectProperty(uri +"hasInterface");
        hasIface.addDomain(device);
        hasIface.addRange(netIface);
        
        ObjectProperty hasVersion = m.createObjectProperty(uri +"hasVersion");
        hasVersion.addDomain(netIface);
        hasVersion.addRange(version);
        
        ObjectProperty hasMask = m.createObjectProperty(uri +"hasMask");
        hasMask.addDomain(netIface);
        hasMask.addRange(mask);
        
        ObjectProperty hasPort = m.createObjectProperty(uri +"hasPort");
        hasPort.addDomain(netIface);
        hasPort.addRange(port);
        
        ObjectProperty hasNumber = m.createObjectProperty(uri +"hasNumber");
        hasNumber.addDomain(port);
        hasNumber.addRange(number);
        
        ObjectProperty hasState = m.createObjectProperty(uri +"hasState");
        hasState.addDomain(port);
        hasState.addRange(state);
        
        ObjectProperty hasProtocol = m.createObjectProperty(uri +"hasProtocol");
        hasProtocol.addDomain(port);
        hasProtocol.addRange(protocol);
        
        ObjectProperty hasService = m.createObjectProperty(uri +"hasService");
        hasService.addDomain(port);
        hasService.addRange(service);
        
        ObjectProperty hasCve = m.createObjectProperty(uri +"hasCve");
        hasCve.addDomain(port);
        hasCve.addRange(cve);
        
        ObjectProperty hasCpe= m.createObjectProperty(uri +"hasCpe");
        hasCpe.addDomain(port);
        hasCpe.addRange(cpe);
        
        ObjectProperty hasVulnerability = m.createObjectProperty(uri +"hasVulnerability");
        hasVulnerability.addDomain(device);
        hasVulnerability.addRange(vulnerabilityNet);
        
        ObjectProperty isCwe= m.createObjectProperty(uri +"isCwe");
        isCwe.addDomain(vulnerabilityNet);
        isCwe.addRange(cwe);
 
        
        /***************
         * INDIVIDUALS *
         **************/
        
        File directoryPath = new File(datasetPath);
        File filesList[] = directoryPath.listFiles(); // get all files of dataset
        
        JSONParser parser = new JSONParser();
        try {
            for(File file : filesList) {
                Object obj = parser.parse(new FileReader(datasetPath + file.getName()));
                JSONObject jsonObject = (JSONObject)obj;
                
                if("access.json".equals(file.getName())){                    
                    JSONArray credentialsJson = (JSONArray)jsonObject.get("credentials");
                    for (Object cred : credentialsJson) {
                        // Parse JSON file
                        JSONObject credentialObj = (JSONObject) cred;
                        String privTypeJson = (String) credentialObj.get("privilegeType");
                        String idJson = (String) credentialObj.get("id");
                        JSONArray destJson = (JSONArray)credentialObj.get("destination");
                        
                        // Write individuals on ontology
                        Individual indIdAccess = m.createIndividual(uri + wellFormedCsv(idJson), credential);
                        indIdAccess.addLabel(idJson, "");
                        
                        Individual indPrivType = m.createIndividual(uri + wellFormedCsv(privTypeJson), privType);
                        indPrivType.addLabel(privTypeJson, "");
                        
                        indIdAccess.addProperty(hasPrivType, indPrivType);
                        
                        for (Object dest : destJson) {
                            JSONObject destObject = (JSONObject) dest;
                            String privLevelJson =( String) destObject.get("privilegeLevel");
                            
                            Individual indPrivLevel = m.createIndividual(uri + wellFormedCsv(privLevelJson), privLevel);
                            indPrivLevel.addLabel(privLevelJson, "");
                            
                            indIdAccess.addProperty(hasPrivLevel, indPrivLevel);
                        }
                    }
                }
                if("humanEmployee.json".equals(file.getName())){
                    JSONArray employeeJson = (JSONArray)jsonObject.get("employees");
                    for (Object empl : employeeJson) {
                        
                        // Parse JSON file
                        JSONObject employeeObj = (JSONObject) empl;
                        String idEmployee = (String) employeeObj.get("id");
                        
                        JSONArray rolesjson = (JSONArray)employeeObj.get("roles");
                        JSONArray locationjson = (JSONArray)employeeObj.get("locations");
                        
                        // Write individuals on ontology
                        Individual indIdEmployee = m.createIndividual(uri + wellFormedCsv(idEmployee), employee);
                        indIdEmployee.addLabel(idEmployee, "");
                        
                        for (Object role : rolesjson) {
                            // Parse JSON file
                            JSONObject roleObj = (JSONObject) role;
                            String positionJson = (String) roleObj.get("position");
                            
                            // Write individuals on ontology
                            Individual indPosition = m.createIndividual(uri + wellFormedCsv(positionJson), position);
                            indPosition.addLabel(positionJson, "");
                            
                            indIdEmployee.addProperty(hasPosition, indPosition);
                        }
                        for (Object locat : locationjson) {
                            // Parse JSON file
                            JSONObject locatObj = (JSONObject) locat;
                            String locationJson =( String) locatObj.get("id");
                            
                            // Write individuals on ontology
                            Individual indLocation = m.createIndividual(uri + wellFormedCsv(locationJson), location);
                            indLocation.addLabel(locationJson, "");
                            
                            indIdEmployee.addProperty(hasLocation, indLocation);
                        }
                    }
                }
                if("humanPolicy.json".equals(file.getName())){
                    JSONArray policyJson = (JSONArray)jsonObject.get("humanPolicies");
                    for (Object policy : policyJson) {
                        
                        // Parse JSON file
                        JSONObject policyObj = (JSONObject) policy;
                        String src = (String) policyObj.get("sourceId");
                        String relationType = (String) policyObj.get("relationType");
                        
                        // Write individuals on ontology
                        Individual indSrc = m.getIndividual(uri+wellFormedCsv(src));
                        Individual indRelation = m.createIndividual(uri+wellFormedCsv(relationType), relType);
                        indRelation.addLabel(relationType, "");
                        
                        indSrc.addProperty(hasRelation, indRelation);
                    }
                }
                if("humanVulnerability.json".equals(file.getName())){
                    JSONArray humanVulnJson = (JSONArray)jsonObject.get("humanVulnerabilities");
                    for (Object vuln : humanVulnJson) {
                        
                        // Parse JSON file
                        JSONObject humanVulnObj = (JSONObject) vuln;
                        String vulnIdJson = (String) humanVulnObj.get("id");
                        String vulnNameJson = (String) humanVulnObj.get("name");
                        String vulnDescrJson = (String) humanVulnObj.get("description");
                        String vulnAVJson = (String) humanVulnObj.get("accessVector");
                        String vulnScoreJson = (String) humanVulnObj.get("accessVectorScore").toString();
                        String vulnPreJson = (String) humanVulnObj.get("preCondition");
                        String vulnPostJson = (String) humanVulnObj.get("postCondition");
   
                        // Write individuals on ontology
                        Individual indVulnId = m.createIndividual(uri+wellFormedCsv(vulnIdJson), vulnerabilityHum);
                        indVulnId.addLabel(vulnIdJson, "");
                        
                        Individual indVulnName = m.createIndividual(uri+wellFormedCsv(vulnNameJson), name);
                        indVulnName.addLabel(vulnNameJson, "");
                        
                        Individual indVulnDescr = m.createIndividual(uri+wellFormedCsv(vulnDescrJson), description);
                        indVulnDescr.addLabel(vulnDescrJson, "");
                        
                        Individual indAV = m.createIndividual(uri+wellFormedCsv(vulnAVJson), accessVector);
                        indAV.addLabel(vulnAVJson, "");
                        
                        Individual indAVScore = m.createIndividual(uri+wellFormedCsv(vulnScoreJson), score);
                        indAVScore.addLabel(vulnScoreJson, "");
                        
                        Individual indPreCond = m.createIndividual(uri+wellFormedCsv(vulnPreJson), preCondition);
                        indPreCond.addLabel(vulnPreJson, "");
                        
                        Individual indPostCond = m.createIndividual(uri+wellFormedCsv(vulnPostJson), postCondition);
                        indPostCond.addLabel(vulnPostJson, "");
                        
                        indVulnId.addProperty(hasName, indVulnName);
                        indVulnId.addProperty(hasDescription, indVulnDescr);
                        indVulnId.addProperty(hasAccessVector, indAV);
                        indAV.addProperty(hasScore, indAVScore);
                        indVulnId.addProperty(hasPreCondition, indPreCond);
                        indVulnId.addProperty(hasPostCondition, indPostCond);
                    }
                }
                if("networkDevice.json".equals(file.getName())){
                    JSONArray devicesJson = (JSONArray)jsonObject.get("devices");
                    for (Object dev : devicesJson) {
                        
                        // Parse JSON file
                        JSONObject deviceObj = (JSONObject) dev;
                        String devId = (String) deviceObj.get("id");
                        String devType = (String) deviceObj.get("type");
                         
                        JSONObject devOs = (JSONObject) deviceObj.get("operatingSystem");
                        JSONArray devIface = (JSONArray)deviceObj.get("networkInterfaces");
                        
                        // Write individuals on ontology
                        Individual indIdDevice = m.createIndividual(uri + wellFormedCsv(devId), device);
                        indIdDevice.addLabel(devId, "");
                        
                        Individual indDevType = m.createIndividual(uri + wellFormedCsv(devType), type);
                        indDevType.addLabel(devType, "");
                        
                        indIdDevice.addProperty(hasType, indDevType);
                        
                        // Parse JSON file
                        JSONObject osObj = (JSONObject) devOs;  
                        String familyOs = (String) osObj.get("family");
                        String vendorOs = (String) osObj.get("vendor");
                        JSONArray serviceOs = (JSONArray)osObj.get("localServices");
                        
                        // Write individuals on ontology
                        Individual indOs = m.createIndividual(uri + wellFormedCsv(familyOs) + "-" + wellFormedCsv(vendorOs), os);
                        indOs.addLabel(familyOs + "-" + vendorOs, "");

                        indIdDevice.addProperty(hasOs, indOs);

                        for(Object serviceOsJson : serviceOs){
                            // Parse JSON file
                            JSONObject osServObj = (JSONObject) serviceOsJson;
                            String serviceNameJson = (String) osServObj.get("name");

                            // Write individuals on ontology
                            Individual indService = m.createIndividual(uri + wellFormedCsv(serviceNameJson), osService);
                            indService.addLabel(serviceNameJson, "");

                            indOs.addProperty(hasService, indService);
                        }
                            
                        for (Object iface : devIface) {
                            // Parse JSON file
                            JSONObject ifaceObj = (JSONObject) iface;
                            String ifaceNameJson = (String) ifaceObj.get("name");
                            String maskJson = (String) ifaceObj.get("mask");
                            String versionJson = (String) ifaceObj.get("version");
                            JSONArray portJson = (JSONArray)ifaceObj.get("ports");
                            
                            // Write individuals on ontology
                            Individual indIfaceName = m.createIndividual(uri + wellFormedCsv(ifaceNameJson), netIface);
                            indIfaceName.addLabel(ifaceNameJson, "");
                            
                            Individual indMask = m.createIndividual(uri + wellFormedCsv(maskJson), mask);
                            indMask.addLabel(maskJson, "");
                            
                            Individual indVersion = m.createIndividual(uri + wellFormedCsv(versionJson), version);
                            indVersion.addLabel(versionJson, "");

                            indIfaceName.addProperty(hasMask, indMask);
                            indIfaceName.addProperty(hasVersion, indVersion);
                            
                            for (Object portJ : portJson) {
                                // Parse JSON file
                                JSONObject portObj = (JSONObject) portJ;
                                String numberJson = (String) portObj.get("number").toString();
                                String stateJson = (String) portObj.get("state");
                                String protocolJson = (String) portObj.get("transportProtocol");
                                
                                JSONObject portServiceJson = (JSONObject)portObj.get("service");

                                // Write individuals on ontology
                                Individual indPortNum = m.createIndividual(uri + "port-"+wellFormedCsv(numberJson), number);
                                indPortNum.addLabel("port-"+numberJson, "");

                                Individual indState = m.createIndividual(uri + "port-"+wellFormedCsv(stateJson), state);
                                indState.addLabel("port-"+stateJson, "");

                                Individual indProtocol = m.createIndividual(uri + wellFormedCsv(protocolJson), protocol);
                                indProtocol.addLabel(protocolJson, "");

                                indIfaceName.addProperty(hasPort, indPortNum);
                                indPortNum.addProperty(hasState, indState);
                                indPortNum.addProperty(hasProtocol, indProtocol);
                                
                                // Parse JSON file
                                String portServiceJ = (String) portServiceJson.get("description");

                                JSONArray portVulnJson = (JSONArray)portServiceJson.get("vulnerabilities");
                                JSONArray portCpeJson = (JSONArray)portServiceJson.get("cpe");

                                // Write individuals on ontology
                                Individual indPortService = m.createIndividual(uri + wellFormedCsv(portServiceJ), service);
                                indPortService.addLabel(portServiceJ, "");

                                indPortNum.addProperty(hasService, indPortService);

                                for (Object vulnJ : portVulnJson) {
                                    // Parse JSON file
                                    String vulnJson = vulnJ.toString();

                                    // Write individuals on ontology
                                    Individual indVulnPort = m.createIndividual(uri + wellFormedCsv(vulnJson), cve);
                                    indVulnPort.addLabel(vulnJson, "");

                                    indPortNum.addProperty(hasCve, indVulnPort);
                                }
                                for (Object cpeJ : portCpeJson) {
                                    // Parse JSON file
                                    String cpeJson = cpeJ.toString();

                                    // Write individuals on ontology
                                    Individual indCpe = m.createIndividual(uri + wellFormedCsv(cpeJson), cpe);
                                    indCpe.addLabel(cpeJson, "");

                                    indPortNum.addProperty(hasCpe, indCpe);
                                }
                            }
                        }
                    }
                }
                if("networkVulnerability.json".equals(file.getName())){
                    JSONArray vulnNetJson = (JSONArray)jsonObject.get("vulnerabilities");
                    for (Object vuln : vulnNetJson) {
                        
                        // Parse JSON file
                        JSONObject vulnObj = (JSONObject) vuln;
                        String vulnId = (String) vulnObj.get("cveId");
                        
                        JSONArray cweJson = (JSONArray)vulnObj.get("cwe");
                        
                        // Write individuals on ontology
                        Individual indVulnId = m.createIndividual(uri + wellFormedCsv(vulnId), vulnerabilityNet);
                        indVulnId.addLabel(vulnId, "");
                        
                        for (Object cweJ : cweJson) {
                        
                            // Parse JSON file
                            JSONObject cweObj = (JSONObject) cweJ;
                            String cweJs = (String) cweObj.get("cweId");

                            // Write individuals on ontology
                            Individual indCweId = m.createIndividual(uri + wellFormedCsv(cweJs), cwe);
                            indCweId.addLabel(cweJs, "");
                            
                            indVulnId.addProperty(isCwe, indCweId);
                        }
                    }       
                }
            }
            System.out.println("Parsed and loaded Multi-Layer Attack Graph");
        } catch(IOException | ParseException e) {}
        
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
