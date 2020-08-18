import control.Alignment;
import control.GraphDbControl;
import control.MappingParam;
import control.ParseAlignment;
import control.ValidationControl;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.AG_Model;
import models.ISO_Model;
import models.ManagementLifetime_Model;
import models.NIST_Model;
import org.apache.jena.ontology.OntModel;


public class OntoAGMapping {

    public static void main(String[] args){
        /*
        ISO_Model IsoOnto = new ISO_Model();
        OntModel modelIso = IsoOnto.createISOModel();
        
        NIST_Model nistOnto = new NIST_Model();
        OntModel modelNIST = nistOnto.createNISTModel();
        
        AG_Model agOnto = new AG_Model();
        OntModel modelMLAG = agOnto.createAGModel();
        
        ManagementLifetime_Model mngmOnto = new ManagementLifetime_Model();
        OntModel modelMngm = mngmOnto.createManagementModel();
        
        ParseAlignment pa = new ParseAlignment();
        ArrayList<Alignment> alignment = pa.parseAlignment(
                "src\\main\\java\\dataset\\alignment\\test.rdf");
        pa.calculateMatching(modelIso, modelMLAG, alignment);
        
        ValidationControl vc = new ValidationControl();
        ArrayList<MappingParam> mp = vc.parseValidationFile();
        vc.caluclateValidationFactor(mp);
        */
/*
        try ( GraphDbControl greeter = new GraphDbControl( "bolt://localhost:7687", "admin", "admin" ) )
        {
            greeter.printGreeting( "hello, world" );
        } catch(Exception e){}
      */  
        
        GraphDbControl gc = new GraphDbControl( "bolt://localhost:7687", "admin", "admin" );
        gc.addHumanLayer();
        //gc.printHuman();
        //gc.attachLambdaHuman();
        gc.addNetworkLayer();
        gc.addAccessLayer();
        gc.attachLambdaNetwork();
        try {
            gc.close();
        } catch (Exception ex) {
            Logger.getLogger(OntoAGMapping.class.getName()).log(Level.SEVERE, null, ex);
        }
                
  
    }
}
