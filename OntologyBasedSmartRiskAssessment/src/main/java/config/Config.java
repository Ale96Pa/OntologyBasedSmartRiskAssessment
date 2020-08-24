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
    private String rootDataset = "src\\main\\java\\dataset\\";
    private String rootOutput = "src\\main\\java\\dataset\\output\\";
    
    private String isoCsvPath = rootDataset+"ISO.csv";
    private String nistCsvPath = rootDataset+"NIST.csv";
    private String managementCsvPath = rootDataset+"managementLifetime.csv";
    
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
    
    // Path of file having the assessment
    private String assessmentIsoC = rootDataset + "AssessmentIsoC.csv";
    private String assessmentIsoPC = rootDataset + "AssessmentIsoPC.csv";
    private String assessmentIsoNC = rootDataset + "AssessmentIsoNC.csv";
    private String assessmentIsoReal = rootDataset + "AssessmentIsoReal.csv";
    
    private String assessmentNistC = rootDataset + "AssessmentNistC.csv";
    private String assessmentNistPC = rootDataset + "AssessmentNistPC.csv";
    private String assessmentNistNC = rootDataset + "AssessmentNistNC.csv";
    private String assessmentNistReal = rootDataset + "AssessmentNistReal.csv";
    
    // Path of file having the alignment of all the ontologies
    private String alignmentDataset = "src\\main\\java\\dataset\\alignment\\";
    private String alignmentIsoFinalPath = alignmentDataset + "iso\\MappingISO";
    private String alignmentIsoAgPath = alignmentDataset +"iso\\iso-ag-alignment.rdf";
    private String alignmentIsoManagementPath = alignmentDataset + "iso\\iso-management-alignment.rdf";
    
    private String alignmentNistFinalPath = alignmentDataset + "nist\\MappingNIST";
    private String alignmentNistAgPath = alignmentDataset +"nist\\nist-ag-alignment.rdf";
    private String alignmentNistManagementPath = alignmentDataset + "nist\\nist-management-alignment.rdf";
    
    // Path of files for attack graph dataset
    private String humanGraphPath = rootDataset + "graphs\\humanLayerAttackGraphRepr.json";
    private String accessGraphPath = rootDataset + "graphs\\interLayerAttackGraphRepr.json";
    private String networkGraphPath = rootDataset + "graphs\\networkLayerAttackGraphRepr.json";
    
    private String humanVulnerabilityPath = attackGraphDataset + "humanVulnerability.json";
    private String networkVulnerabilityPath = attackGraphDataset + "networkVulnerability.json";
    
    // Configuration parameters for neo4j dataset
    private String uri = "bolt://localhost:7687";
    private String user = "admin";
    private String password = "admin";
    
    // Wiegths for discount formula
    private double weightMatching = 1.0;
    private double weightLambda = 1.0;
    private double weightManagement = 1.0;
    private double weightValidation = 1.0;
    
    // Paths for output files
    private String outputIsoC = rootOutput + "outputIsoC.csv";
    private String outputIsoPC = rootOutput + "outputIsoPC.csv";
    private String outputIsoNC = rootOutput + "outputIsoNC.csv";
    private String outputIsoReal = rootOutput + "outputIsoReal.csv";
    
    private String outputNistC = rootOutput + "outputNistC.csv";
    private String outputNistPC = rootOutput + "outputNistPC.csv";
    private String outputNistNC = rootOutput + "outputNistNC.csv";
    private String outputNistReal = rootOutput + "outputNistReal.csv";
    
    // Getter methods
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

    public String getAlignmentIsoFinalPath() {
        return alignmentIsoFinalPath;
    }

    public String getAlignmentIsoAgPath() {
        return alignmentIsoAgPath;
    }

    public String getAlignmentIsoManagementPath() {
        return alignmentIsoManagementPath;
    }

    public String getAlignmentNistFinalPath() {
        return alignmentNistFinalPath;
    }

    public String getAlignmentNistAgPath() {
        return alignmentNistAgPath;
    }

    public String getAlignmentNistManagementPath() {
        return alignmentNistManagementPath;
    }

    public String getAssessmentIsoC() {
        return assessmentIsoC;
    }

    public String getAssessmentIsoPC() {
        return assessmentIsoPC;
    }

    public String getAssessmentIsoNC() {
        return assessmentIsoNC;
    }

    public String getAssessmentIsoReal() {
        return assessmentIsoReal;
    }

    public String getAssessmentNistC() {
        return assessmentNistC;
    }

    public String getAssessmentNistPC() {
        return assessmentNistPC;
    }

    public String getAssessmentNistNC() {
        return assessmentNistNC;
    }

    public String getAssessmentNistReal() {
        return assessmentNistReal;
    }

    public double getWeightMatching() {
        return weightMatching;
    }

    public double getWeightLambda() {
        return weightLambda;
    }

    public double getWeightManagement() {
        return weightManagement;
    }

    public double getWeightValidation() {
        return weightValidation;
    }

    public String getOutputIsoC() {
        return outputIsoC;
    }

    public String getOutputIsoPC() {
        return outputIsoPC;
    }

    public String getOutputIsoNC() {
        return outputIsoNC;
    }

    public String getOutputIsoReal() {
        return outputIsoReal;
    }

    public String getOutputNistC() {
        return outputNistC;
    }

    public String getOutputNistPC() {
        return outputNistPC;
    }

    public String getOutputNistNC() {
        return outputNistNC;
    }

    public String getOutputNistReal() {
        return outputNistReal;
    }
    
    
    
    
}
