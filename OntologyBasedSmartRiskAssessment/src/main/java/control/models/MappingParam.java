/**
 * This class represents the model for collecting information about the mapping
 * of controls mapped in layers, management, lifetime and assessment.
 */
package control.models;

public class MappingParam {
    
    // Attributes
    private String controlID;
    
    private double human;
    private double access;
    private double network;
    
    private double operational;
    private double compliance;
    
    private double runtime;
    private double designtime;
    
    private String assessment;

    // Methods
    public String getControlID() {return controlID;}

    public void setControlID(String controlID) {this.controlID = controlID;}

    public double getHuman() {return human;}

    public void setHuman(double human) {this.human = human;}

    public double getAccess() {return access;}

    public void setAccess(double access) {this.access = access;}

    public double getNetwork() {return network;}

    public void setNetwork(double network) {this.network = network;}

    public double getOperational() {return operational;}

    public void setOperational(double operational) {this.operational = operational;}

    public double getCompliance() {return compliance;}

    public void setCompliance(double compliance) {this.compliance = compliance;}

    public double getRuntime() {return runtime;}

    public void setRuntime(double runtime) {this.runtime = runtime;}

    public double getDesigntime() {return designtime;}

    public void setDesigntime(double designtime) {this.designtime = designtime;}
    
    public String getAssessment() { return assessment;}

    public void setAssessment(String assessment) {this.assessment = assessment;}
}
