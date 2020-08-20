import config.Config;
import control.models.Alignment;
import control.DiscountControl;
import control.models.Edge;
import control.GraphDbControl;
import control.models.MappingParam;
import control.ParseAlignment;
import control.ValidationControl;
import java.util.ArrayList;
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

    public static void main(String[] args){
        
        /*
        ISO_Model IsoOnto = new ISO_Model();
        OntModel modelIso = IsoOnto.createISOModel();
        */
        
        /*
        NIST_Model nistOnto = new NIST_Model();
        OntModel modelNIST = nistOnto.createNISTModel();
        */
        
        /*
        AG_Model agOnto = new AG_Model();
        OntModel modelMLAG = agOnto.createAGModel();
        */
        
        /*
        ManagementLifetime_Model mngmOnto = new ManagementLifetime_Model();
        OntModel modelMngm = mngmOnto.createManagementModel();
        */
        
        /*
        ParseAlignment pa = new ParseAlignment();
        ArrayList<Alignment> alignment = pa.parseAlignment(
                "src\\main\\java\\dataset\\alignment\\test.rdf");
        pa.calculateMatching(modelIso, modelMLAG, alignment);
        */
        
        // OK
        /*
        ValidationControl vc = new ValidationControl();
        ArrayList<MappingParam> mp = vc.parseValidationFile();
        vc.caluclateValidationFactor(mp);
        */
        
        // OK
        /* 
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
        
        try {
            gc.close();
        } catch (Exception ex) {
            Logger.getLogger(GraphDbControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        
        /*
        DiscountControl dc = new DiscountControl();
        dc.calculateFormula();
        */
    }
}
