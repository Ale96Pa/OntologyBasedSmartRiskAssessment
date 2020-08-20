/**
 * This class represents the model for collecting information about edges in the
 * multi layer attack graph.
 */
package control.models;

import java.util.ArrayList;

public class Edge {
    
    // Attributes
    String layer;
    double lambda;
    ArrayList<String> descriptionId;
    
    // Constructor
    public Edge(String layer, double lambda, ArrayList<String> descriptionId) {
        this.layer = layer;
        this.lambda = lambda;
        this.descriptionId = descriptionId;
    }
    
    // Methods
    public String getLayer() {return layer;}

    public void setLayer(String layer) {this.layer = layer;}

    public double getLambda() {return lambda;}

    public void setLambda(double lambda) {this.lambda = lambda;}

    public ArrayList<String> getDescriptionId() {return descriptionId;}

    public void setDescriptionId(ArrayList<String> descriptionId) {this.descriptionId = descriptionId;}
}