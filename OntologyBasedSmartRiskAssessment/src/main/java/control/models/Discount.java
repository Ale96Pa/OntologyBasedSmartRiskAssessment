/**
 * This class represents the model for collecting information about discount
 * factor, dividing positive and negative parts of such an element.
 */
package control.models;

public class Discount {
    
    //Attributes
    double disPositive;
    int numElemPositive;
    double disNegative;
    int numElemNegative;
    
    //Constructor
    public Discount(double disPositive, int numElemPositive, double disNegative, int numElemNegative) {
        this.disPositive = disPositive;
        this.numElemPositive = numElemPositive;
        this.disNegative = disNegative;
        this.numElemNegative = numElemNegative;
    }

    // Methods
    public double getDisPositive() {return disPositive;}

    public void setDisPositive(double disPositive) {this.disPositive = disPositive;}

    public double getDisNegative() {return disNegative;}

    public void setDisNegative(double disNegative) {this.disNegative = disNegative;}

    public int getNumElemPositive() {return numElemPositive;}

    public void setNumElemPositive(int numElemPositive) {this.numElemPositive = numElemPositive;}

    public int getNumElemNegative() {return numElemNegative;}

    public void setNumElemNegative(int numElemNegative) {this.numElemNegative = numElemNegative;}
}
