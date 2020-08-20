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
    
    // Path of file having the alignment of all the ontologies
    private String alignmentDataset = "src\\main\\java\\dataset\\alignment\\iso\\";
    private String alignmentIsoPath = alignmentDataset + "AlignmentISOTotal.csv";
    
    // Path of files for attack graph dataset
    private String attackGraphDataset = "src\\main\\java\\dataset\\AG\\";
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
    
    
    
}
