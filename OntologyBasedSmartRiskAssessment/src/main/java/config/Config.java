/*
 * This file contains all the elements for configuration, in order to avoid 
 * hard-coded elements.
 */
package config;

/**
 * This class is a collection of elements that can be retrieved by all classes.
 * @author Alessandro
 */
public class Config {
    
    // Path of files having information to build the ontology (sources)
    private String attackGraphDataset = "src\\main\\java\\dataset\\AG\\";
    private String csvDataset = "src\\main\\java\\dataset\\";
    private String isoCsvPath = csvDataset+"ISO.csv";
    private String nistCsvPath = csvDataset+"NIST.csv";
    private String managementCsvPath = csvDataset+"managementLifetime.csv";
    
    // Path of files having information to build the ontology (destination)
    private String owlDataset = "src\\main\\java\\dataset\\ontologies\\";
    private String isoOwlPath = owlDataset+"ISO_ontology.owl";
    private String nistOwlPath = owlDataset+"NIST_ontology.owl";
    private String managementOwlPath = owlDataset+"ManagementLifetime_ontology.owl";
    private String agOwlPath = owlDataset+"AG_ontology.owl";
    
    // Configuration elements for ontologies
    private String formatOntology = "RDF/XML-ABBREV";
    private String uriIso = "http://thesisAP.com/iso#";
    private String uriNist = "http://thesisAP.com/nist#";
    private String uriManagement = "http://thesisAP.com/mng-lt#";
    private String uriAG = "http://thesisAP.com/mlag#";
    
    // Path of file having the alignment of all the ontologies
    private String alignmentDataset = "src\\main\\java\\dataset\\alignment\\iso\\";
    private String alignmentIsoPath = alignmentDataset + "AlignmentISOTotal.csv";
    
    // Path of files for attack graph dataset
    private String humanGraphPath = attackGraphDataset + "graphs\\humanLayerAttackGraphRepr.json";
    private String accessGraphPath = attackGraphDataset + "graphs\\interLayerAttackGraphRepr.json";
    private String networkGraphPath = attackGraphDataset + "graphs\\networkLayerAttackGraphRepr.json";
    
    private String humanVulnerabilityPath = attackGraphDataset + "humanVulnerability.json";
    private String networkVulnerabilityPath = attackGraphDataset + "networkVulnerability.json";
    
    // Configuration parameters for neo4j dataset
    private String uri = "bolt://localhost:7687";
    private String user = "admin";
    private String password = "admin";
    
    // Getter methods
    public String getAlignmentIsoPath() {
        return alignmentIsoPath;
    }

    public String getHumanGraphPath() {
        return humanGraphPath;
    }

    public String getAccessGraphPath() {
        return accessGraphPath;
    }

    public String getNetworkGraphPath() {
        return networkGraphPath;
    }

    public String getHumanVulnerabilityPath() {
        return humanVulnerabilityPath;
    }

    public String getNetworkVulnerabilityPath() {
        return networkVulnerabilityPath;
    }

    public String getUri() {
        return uri;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getAttackGraphDataset() {
        return attackGraphDataset;
    }

    public String getIsoCsvPath() {
        return isoCsvPath;
    }

    public String getNistCsvPath() {
        return nistCsvPath;
    }

    public String getManagementCsvPath() {
        return managementCsvPath;
    }

    public String getIsoOwlPath() {
        return isoOwlPath;
    }

    public String getNistOwlPath() {
        return nistOwlPath;
    }

    public String getManagementOwlPath() {
        return managementOwlPath;
    }

    public String getAgOwlPath() {
        return agOwlPath;
    }

    public String getFormatOntology() {
        return formatOntology;
    }

    public String getUriIso() {
        return uriIso;
    }

    public String getUriNist() {
        return uriNist;
    }

    public String getUriManagement() {
        return uriManagement;
    }

    public String getUriAG() {
        return uriAG;
    }
    
    
    
}
