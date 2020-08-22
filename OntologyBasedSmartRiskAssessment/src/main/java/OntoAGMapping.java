import config.Config;
import control.models.Alignment;
import control.DiscountControl;
import control.models.Edge;
import control.GraphDbControl;
import control.models.MappingParam;
import control.ParseAlignment;
import control.ValidationControl;
import control.models.Factor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    Config conf = new Config();
    // Path of files having information to build the ontology (sources)
    private String agDataset = conf.getAttackGraphDataset();    
    private String isoCsvPath = conf.getIsoCsvPath();
    private String nistCsvPath = conf.getNistCsvPath();
    private String managementCsvPath = conf.getManagementCsvPath();
    
    // Path of files having information to build the ontology (destination)
    private String isoOwlPath = conf.getIsoOwlPath();
    private String nistOwlPath = conf.getNistOwlPath();
    private String managementOwlPath = conf.getManagementOwlPath();
    private String agOwlPath = conf.getAgOwlPath();
    
    // Configuration elements for ontologies
    private String formatOntology = conf.getFormatOntology();
    private String uriIso = conf.getUriIso();
    private String uriNist = conf.getUriNist();
    private String uriManagement = conf.getUriManagement();
    private String uriAG = conf.getUriAG();
    
    // Path of file having the assessment
    private String assessmentIsoC = conf.getAssessmentIsoC();
    private String assessmentIsoPC = conf.getAssessmentIsoPC();
    private String assessmentIsoNC = conf.getAssessmentIsoNC();
    private String assessmentIsoReal = conf.getAssessmentIsoReal();
    private String assessmentNistC = conf.getAssessmentNistC();
    private String assessmentNistPC = conf.getAssessmentNistPC();
    private String assessmentNistNC = conf.getAssessmentNistNC();
    private String assessmentNistReal = conf.getAssessmentNistReal();
    
    // Path of file having the alignment of all the ontologies
    private String alignmentIsoFinalPath = conf.getAlignmentIsoFinalPath();
    private String alignmentIsoAgPath = conf.getAlignmentIsoAgPath();
    private String alignmentIsoManagementPath = conf.getAlignmentIsoManagementPath();
    private String alignmentNistFinalPath = conf.getAlignmentNistFinalPath();
    private String alignmentNistAgPath = conf.getAlignmentNistAgPath();
    private String alignmentNistManagementPath = conf.getAlignmentNistManagementPath();
    
    // Path of files for attack graph dataset
    private String humanGraphPath = conf.getHumanGraphPath();
    private String accessGraphPath = conf.getAccessGraphPath();
    private String networkGraphPath = conf.getNetworkGraphPath();
    private String humanVulnerabilityPath = conf.getHumanVulnerabilityPath();
    private String networkVulnerabilityPath = conf.getNetworkVulnerabilityPath();
    
    // Configuration parameters for neo4j dataset
    private String uri = conf.getUri();
    private String user = conf.getUser();
    private String password = conf.getPassword();
    
    public static void main(String[] args){
        
/*
        // OK
        
        ISO_Model IsoOnto = new ISO_Model();
        OntModel modelIso = IsoOnto.createISOModel();

        // OK

        NIST_Model nistOnto = new NIST_Model();
        OntModel modelNIST = nistOnto.createNISTModel();

        // OK

        AG_Model agOnto = new AG_Model();
        OntModel modelMLAG = agOnto.createAGModel();

        // OK

        ManagementLifetime_Model mngmOnto = new ManagementLifetime_Model();
        OntModel modelMngm = mngmOnto.createManagementModel();

        //OK

        ParseAlignment pa = new ParseAlignment();
        pa.writeMappingFromAlignmentIso();

        // OK
 
        Config conf = new Config();
        GraphDbControl gc = new GraphDbControl(conf.getUri(), conf.getUser(), conf.getPassword());
        gc.buildGraph();
        ArrayList<Edge> ee = gc.setHumanEdges();
        for(Edge e : ee){
            System.out.println(e.getLayer() + " " + e.getLambda() + " " +e.getDescriptionId());
        }
        ArrayList<Edge> ee2 = gc.setNetworkEdges();
        for(Edge e : ee2){
            System.out.println(e.getLayer() + " " + e.getLambda() + " " +e.getDescriptionId());
        }
        ArrayList<Edge> ee3 = gc.setAccessEdges();
        for(Edge e : ee3){
            System.out.println(e.getLayer() + " " + e.getLambda() + " " +e.getDescriptionId());
        }
        try {
            gc.close();
        } catch (Exception ex) {
            Logger.getLogger(GraphDbControl.class.getName()).log(Level.SEVERE, null, ex);
        }

        //OK

        DiscountControl dc = new DiscountControl();
        dc.calculateFormula();
*/
        
    }
}
