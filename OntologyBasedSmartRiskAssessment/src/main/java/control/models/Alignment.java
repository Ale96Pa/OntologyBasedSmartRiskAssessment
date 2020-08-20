package control.models;

public class Alignment {
    
    // Attributes
    String sourceUri;
    String targetUri;
    float matchingValue;

    public Alignment(String sourceOntology, String targetOntology, float matchingValue) {
        this.sourceUri = sourceOntology;
        this.targetUri = targetOntology;
        this.matchingValue = matchingValue;
    }

    public String getSourceUri() {
        return sourceUri;
    }

    public void setSourceUri(String sourceOntology) {
        this.sourceUri = sourceOntology;
    }

    public String getTargetUri() {
        return targetUri;
    }

    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }

    public float getMatchingValue() {
        return matchingValue;
    }

    public void setMatchingValue(float matchingValue) {
        this.matchingValue = matchingValue;
    }
    
    
}
