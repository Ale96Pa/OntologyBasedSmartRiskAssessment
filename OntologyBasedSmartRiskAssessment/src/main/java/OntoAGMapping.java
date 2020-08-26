import config.Config;
import control.DiscountControl;
import control.GraphDbControl;
import control.ParseAlignment;
import ontologyModels.AG_Model;
import ontologyModels.ISO_Model;
import ontologyModels.ManagementLifetime_Model;
import ontologyModels.NIST_Model;
import org.apache.jena.ontology.OntModel;

/**
 * This is the main class managing the entire work-flow of the project, all done
 * with methods and classes in this project.
 * @author Alessandro
 */
public class OntoAGMapping {
    
    static Config conf = new Config();
    // Path of files having information to build the ontology (sources)
    private static  String agDataset = conf.getAttackGraphDataset();    
    private static  String isoCsvPath = conf.getIsoCsvPath();
    private static  String nistCsvPath = conf.getNistCsvPath();
    private static  String managementCsvPath = conf.getManagementCsvPath();
    
    // Path of files having information to build the ontology (destination)
    private static  String isoOwlPath = conf.getIsoOwlPath();
    private static  String nistOwlPath = conf.getNistOwlPath();
    private static  String managementOwlPath = conf.getManagementOwlPath();
    private static  String agOwlPath = conf.getAgOwlPath();
    
    // Configuration elements for ontologies
    private static  String formatOntology = conf.getFormatOntology();
    private static  String uriIso = conf.getUriIso();
    private static  String uriNist = conf.getUriNist();
    private static  String uriManagement = conf.getUriManagement();
    private static  String uriAG = conf.getUriAG();
    
    // Path of file having the assessment
    private static  String assessmentIsoC = conf.getAssessmentIsoC();
    private static  String assessmentIsoPC = conf.getAssessmentIsoPC();
    private static  String assessmentIsoNC = conf.getAssessmentIsoNC();
    private static  String assessmentIsoReal = conf.getAssessmentIsoReal();
    private static  String assessmentNistC = conf.getAssessmentNistC();
    private static  String assessmentNistPC = conf.getAssessmentNistPC();
    private static  String assessmentNistNC = conf.getAssessmentNistNC();
    private static  String assessmentNistReal = conf.getAssessmentNistReal();
    
    // Path of file having the alignment of all the ontologies
    private static  String alignmentIsoFinalPath = conf.getAlignmentIsoFinalPath();
    private static  String alignmentIsoAgPath = conf.getAlignmentIsoAgPath();
    private static  String alignmentIsoManagementPath = conf.getAlignmentIsoManagementPath();
    private static  String alignmentNistFinalPath = conf.getAlignmentNistFinalPath();
    private static  String alignmentNistAgPath = conf.getAlignmentNistAgPath();
    private static  String alignmentNistManagementPath = conf.getAlignmentNistManagementPath();
    
    // Path of files for attack graph dataset
    private static  String humanGraphPath = conf.getHumanGraphPath();
    private static  String accessGraphPath = conf.getAccessGraphPath();
    private static  String networkGraphPath = conf.getNetworkGraphPath();
    private static  String humanVulnerabilityPath = conf.getHumanVulnerabilityPath();
    private static  String networkVulnerabilityPath = conf.getNetworkVulnerabilityPath();
    
    // Configuration parameters for neo4j dataset
    private static String uri = conf.getUri();
    private static String user = conf.getUser();
    private static String password = conf.getPassword();
    
    // Wiegths for discount formula
    private static double weightMatching = conf.getWeightMatching();
    private static double weightLambda = conf.getWeightLambda();
    private static double weightManagement = conf.getWeightManagement();
    private static double weightValidation = conf.getWeightValidation();
    
    // Paths for output files
    private static String outputIsoC = conf.getOutputIsoC();
    private static String outputIsoPC = conf.getOutputIsoPC();
    private static String outputIsoNC = conf.getOutputIsoNC();
    private static String outputIsoReal = conf.getOutputIsoReal();
    
    private static String outputNistC = conf.getOutputNistC();
    private static String outputNistPC = conf.getOutputNistPC();
    private static String outputNistNC = conf.getOutputNistNC();
    private static String outputNistReal = conf.getOutputNistReal();
    
    public static void main(String[] args){
        
        // 1 - Build the ontologies
        ISO_Model IsoOnto = new ISO_Model();
        OntModel modelIso = IsoOnto.createISOModel(isoCsvPath,isoOwlPath,formatOntology,uriIso);

        NIST_Model nistOnto = new NIST_Model();
        OntModel modelNIST = nistOnto.createNISTModel(nistCsvPath,nistOwlPath,formatOntology,uriNist);
        
        ManagementLifetime_Model mngmOnto = new ManagementLifetime_Model();
        OntModel modelMngm = mngmOnto.createManagementModel(managementCsvPath,managementOwlPath,
                formatOntology,uriManagement);
        
        AG_Model agOnto = new AG_Model();
        OntModel modelMLAG = agOnto.createAGModel(agDataset,agOwlPath,formatOntology,uriAG);
        
        /* 2 - Load ontology files into AML for making alignments and produce alignment files.*/
        
        // 3 - Parse alignment files for creating the mapping output
        ParseAlignment pa = new ParseAlignment();
            //3.1: ISO all C
        pa.writeMappingFromAlignment(isoCsvPath, alignmentIsoAgPath, 
                alignmentIsoManagementPath, assessmentIsoC, alignmentIsoFinalPath+"C.csv", "iso");
            //3.2: ISO all PC
        pa.writeMappingFromAlignment(isoCsvPath, alignmentIsoAgPath, 
                alignmentIsoManagementPath, assessmentIsoPC, alignmentIsoFinalPath+"PC.csv", "iso");
            //3.3: ISO all NC
        pa.writeMappingFromAlignment(isoCsvPath, alignmentIsoAgPath, 
                alignmentIsoManagementPath, assessmentIsoNC, alignmentIsoFinalPath+"NC.csv", "iso");
      
            //3.4: NIST all C
        pa.writeMappingFromAlignment(nistCsvPath, alignmentNistAgPath, 
                alignmentNistManagementPath, assessmentNistC, alignmentNistFinalPath+"C.csv", "nist");
            //3.5: NIST all PC
        pa.writeMappingFromAlignment(nistCsvPath, alignmentNistAgPath, 
                alignmentNistManagementPath, assessmentNistPC, alignmentNistFinalPath+"PC.csv", "nist");
            //3.6: NIST all NC
        pa.writeMappingFromAlignment(nistCsvPath, alignmentNistAgPath, 
                alignmentNistManagementPath, assessmentNistNC, alignmentNistFinalPath+"NC.csv", "nist");
        
            // 3.7 & 3.8: ISO and NIST "real"/mixed assessment
        pa.writeMappingFromAlignment(isoCsvPath, alignmentIsoAgPath, 
                alignmentIsoManagementPath, assessmentIsoReal, alignmentIsoFinalPath+"Real.csv", "iso");
        pa.writeMappingFromAlignment(nistCsvPath, alignmentNistAgPath, 
                alignmentNistManagementPath, assessmentNistReal, alignmentNistFinalPath+"Real.csv", "nist");
       
       
        // 4 - Build the graph with lambda factors
        GraphDbControl gc = new GraphDbControl(uri, user, password);
        gc.buildGraph(humanGraphPath, humanVulnerabilityPath, accessGraphPath, 
                networkGraphPath, networkVulnerabilityPath);
        
        // 5 - Evaluate discount formula
        DiscountControl dc = new DiscountControl();
            //5.1: ISO all C
        dc.calculateFormula(alignmentIsoFinalPath+"C.csv", outputIsoC, gc, 
                weightMatching, weightLambda, weightManagement, weightValidation);
            //5.2: ISO all PC
        dc.calculateFormula(alignmentIsoFinalPath+"PC.csv", outputIsoPC, gc, 
                weightMatching, weightLambda, weightManagement, weightValidation);
            //5.3: ISO all NC
        dc.calculateFormula(alignmentIsoFinalPath+"NC.csv", outputIsoNC, gc, 
                weightMatching, weightLambda, weightManagement, weightValidation);

            //5.4: NIST all C
        dc.calculateFormula(alignmentNistFinalPath+"C.csv", outputNistC, gc, 
                weightMatching, weightLambda, weightManagement, weightValidation);
            //5.5: NIST all PC
        dc.calculateFormula(alignmentNistFinalPath+"PC.csv", outputNistPC, gc, 
                weightMatching, weightLambda, weightManagement, weightValidation);
            //5.6: NIST all NC
        dc.calculateFormula(alignmentNistFinalPath+"NC.csv", outputNistNC, gc, 
                weightMatching, weightLambda, weightManagement, weightValidation);

            // 5.7 & 5.8: ISO and NIST "real"/mixed assessment
        dc.calculateFormula(alignmentIsoFinalPath+"Real.csv", outputIsoReal, gc, 
                weightMatching, weightLambda, weightManagement, weightValidation);
        dc.calculateFormula(alignmentNistFinalPath+"Real.csv", outputNistReal, gc, 
                weightMatching, weightLambda, weightManagement, weightValidation);
            
        // Close connection
        gc.close(); // Close neo4j connection
        
    }
}
