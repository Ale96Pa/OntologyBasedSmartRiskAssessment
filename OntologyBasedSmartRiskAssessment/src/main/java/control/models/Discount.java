/**
 * This class represents the model for collecting information about discount
 * factor, dividing positive and negative parts of such an element.
 */
package control.models;

public class Discount {
    
    //Attributes
    double mappingFactor;
    double managementFactor;
    double validationFactor;
    double lambdaFactor;
    
    //Constructor
    public Discount(double mappingFactor, double managementFactor, double validationFactor) {
        this.mappingFactor = mappingFactor;
        this.managementFactor = managementFactor;
        this.validationFactor = validationFactor;
    }

    public Discount(double mappingFactor, double managementFactor, double validationFactor, double lambdaFactor) {
        this.mappingFactor = mappingFactor;
        this.managementFactor = managementFactor;
        this.validationFactor = validationFactor;
        this.lambdaFactor = lambdaFactor;
    }
    

    
    // Methods

    public double getMappingFactor() {
        return mappingFactor;
    }

    public void setMappingFactor(double mappingFactor) {
        this.mappingFactor = mappingFactor;
    }

    public double getManagementFactor() {
        return managementFactor;
    }

    public void setManagementFactor(double managementFactor) {
        this.managementFactor = managementFactor;
    }

    public double getValidationFactor() {
        return validationFactor;
    }

    public void setValidationFactor(double validationFactor) {
        this.validationFactor = validationFactor;
    }

    public double getLambdaFactor() {
        return lambdaFactor;
    }

    public void setLambdaFactor(double lambdaFactor) {
        this.lambdaFactor = lambdaFactor;
    }
    

}
