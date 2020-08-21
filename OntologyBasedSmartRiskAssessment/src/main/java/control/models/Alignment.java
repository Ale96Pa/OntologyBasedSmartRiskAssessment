/**
 * This class represents the model for collecting information about the ontology
 * alignment represented by triple <src, target, valueMatching>.
 */
package control.models;

public class Alignment {
    
    // Attributes
    String sourceUri;
    String targetUri;
    double matchingValue;

    // Constructor
    public Alignment(String sourceOntology, String targetOntology, double matchingValue) {
        this.sourceUri = sourceOntology;
        this.targetUri = targetOntology;
        this.matchingValue = matchingValue;
    }

    // Methods
    public String getSourceUri() {return sourceUri;}

    public void setSourceUri(String sourceOntology) {this.sourceUri = sourceOntology;}

    public String getTargetUri() {return targetUri;}

    public void setTargetUri(String targetUri) {this.targetUri = targetUri;}

    public double getMatchingValue() {return matchingValue;}

    public void setMatchingValue(double matchingValue) {this.matchingValue = matchingValue;}
}
