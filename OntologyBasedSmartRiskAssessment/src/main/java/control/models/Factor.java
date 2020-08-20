package control.models;

public class Factor {
    
    // Attributes
    String id;
    double value;
    String type;
    
    // Constructor
    public Factor(String id, double value, String type) {
        this.id = id;
        this.value = value;
        this.type = type;
    }
    
    
    
    // Methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    
}
