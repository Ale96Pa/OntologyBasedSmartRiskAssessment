/**
 * This class represents the model for collecting information about the mapping
 * of controls mapped in layers, management, lifetime and assessment.
 */
package control.models;

public class MappingParam {
    
    // Attributes
    private String controlID;
    
    private int human;
    private int access;
    private int network;
    
    private int operational;
    private int compliance;
    
    private int runtime;
    private int designtime;
    
    private String assessment;

    // Methods
    public String getControlID() {return controlID;}

    public void setControlID(String controlID) {this.controlID = controlID;}

    public int getHuman() {return human;}

    public void setHuman(int human) {this.human = human;}

    public int getAccess() {return access;}

    public void setAccess(int access) {this.access = access;}

    public int getNetwork() {return network;}

    public void setNetwork(int network) {this.network = network;}

    public int getOperational() {return operational;}

    public void setOperational(int operational) {this.operational = operational;}

    public int getCompliance() {return compliance;}

    public void setCompliance(int compliance) {this.compliance = compliance;}

    public int getRuntime() {return runtime;}

    public void setRuntime(int runtime) {this.runtime = runtime;}

    public int getDesigntime() {return designtime;}

    public void setDesigntime(int designtime) {this.designtime = designtime;}
    
    public String getAssessment() { return assessment;}

    public void setAssessment(String assessment) {this.assessment = assessment;}
}
