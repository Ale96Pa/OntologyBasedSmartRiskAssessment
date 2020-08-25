/**
 * This file contains the calculation of the discount factors in every edge of
 * the multi-layer attack graph. It is necessary that previous steps are completed:
 * -) There exists a final file containing the mapping of the form:
 * ID;H;A;N;Runtime;Designtime;Operational;Compliance;Assessment
 * -) There exists an instance of the multi-layer attack graph in neo4j
 */
package control;

import control.models.Discount;
import control.models.MappingParam;
import control.models.Edge;
import control.models.Factor;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class DiscountControl {
    
    /**
     * This method returns an array of Factors with the value of mapping factor;
     * there is a factor per each control.
     * @return 
     */
    private ArrayList<Factor> calculateMappingFactorPerControl(String pathMatchingISO, double weight){
        ArrayList<Factor> factors = new ArrayList(); 
        ArrayList<Double> layersMapping = new ArrayList();
        BufferedReader br = null;
        
        try {
            br = new BufferedReader(new FileReader(pathMatchingISO));
            br.readLine(); // skip the first line (header)
            String line;
            
            while((line = br.readLine()) != null) {
                String[] data = line.split(";");
                
                // Elements in matching csv dataset
                String controlId = data[0];
                double human = Double.parseDouble(data[1]);
                double access = Double.parseDouble(data[2]);
                double network = Double.parseDouble(data[3]);
                layersMapping.add(human);
                layersMapping.add(access);
                layersMapping.add(network);
                
                double max = Collections.max(layersMapping);
                double avg = (human+access+network)/3;
                double threshold = max-avg;
                
                double gapLayer = Math.abs(human-network);
                
                double resultH, resultA, resultN;
                double valDiscountH = 0, valDiscountN=0, valDiscountA=0;
                if(human != 0.0){valDiscountH = ((gapLayer/2) + human)/2;}
                if(access != 0.0){valDiscountA = ((gapLayer/2) + access)/2;}
                if(network != 0.0){valDiscountN = ((gapLayer/2) + network)/2;}
                
                if(gapLayer < threshold){
                    resultH = -valDiscountH;
                    resultA = -valDiscountA;
                    resultN = -valDiscountN;
                }
                else{
                    resultH = valDiscountH;
                    resultA = valDiscountA;
                    resultN = valDiscountN;
                }
                
                Factor factH = new Factor(controlId, weight*resultH, "human");
                Factor factA = new Factor(controlId, weight*resultA, "access");
                Factor factN = new Factor(controlId, weight*resultN, "network");
                
                factors.add(factH);
                factors.add(factA);
                factors.add(factN);
            }
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        finally {
            if (br != null){
                try {br.close();}
                catch (IOException e) {}
            }
        }
        return factors;
    }
    
    
    /**
     * This method returns an array of factors, one per each control with the
     * value of management and lifetime parameter attached.
     * @param pathFinalMapping
     * @return 
     */
    private ArrayList<Factor> calculateManagementFactorPerControl(String pathFinalMapping, double weight){
        ArrayList<Factor> factors = new ArrayList();
        BufferedReader br = null;
        
        try {
            br = new BufferedReader(new FileReader(pathFinalMapping));
            br.readLine(); // skip the first line (header)
            String line;
            
            while((line = br.readLine()) != null) {
                
                String[] data = line.split(";");
                // Elements in matching csv dataset
                String controlId = data[0];
                double runtime = Double.parseDouble(data[4]);
                double designtime = Double.parseDouble(data[5]);
                double operational = Double.parseDouble(data[6]);
                double compliance = Double.parseDouble(data[7]);
                double result = 0;
                      
                if(runtime > designtime){
                    if(operational > compliance){result = 0.5;}
                    else if(operational < compliance){result = 0.25;}
                }
                else if (runtime < designtime){
                    if(operational > compliance){result = 0.25;}
                    else if(operational < compliance){result = -0.25;}
                }
                
                Factor fact = new Factor(controlId, weight*result, "management");
                factors.add(fact);
            }
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        finally {
            if (br != null){
                try {br.close();}
                catch (IOException e) {}
            }
        }
        return factors;
    }
    
    
    /**
     * This method returns the validation factors following the formula studied
     * by the paper [1].
     * @return 
     */
    private ArrayList<Factor> calculateValidationFactorPerControl(String alignmentISOPath, double weight){
        ValidationControl vc = new ValidationControl();
        ArrayList<MappingParam> mp = vc.parseValidationFile(alignmentISOPath);
        return vc.caluclateValidationFactor(mp, weight);
    }
    
    
    /**
     * This method picks information from the attack graph in order to calculate
     * the lambda factor for the formula. It returns the list of edges with
     * the final discount value attached.
     * @param discounts
     * @param layer
     * @return 
     */
    private ArrayList<Edge> calculateDiscountWithLambda(ArrayList<Discount> disControlsInLayer, String layer,
            GraphDbControl gc, double weight){
        
        ArrayList<Edge> edgesWithDiscount = new ArrayList();        
        ArrayList<Edge> edges = new ArrayList();
        
        if("human".equals(layer)){edges = gc.setHumanEdges();}
        else if("network".equals(layer)){edges = gc.setNetworkEdges();}
        else if("access".equals(layer)){edges = gc.setAccessEdges();}
        
        for(Edge e : edges){
            double lambda = e.getLambda();
            double layerFactor=0;
            
            for(Discount dis: disControlsInLayer){
                double partialFactor1 =(dis.getMappingFactor() + dis.getManagementFactor() + 
                        dis.getValidationFactor())/3;              
                layerFactor = layerFactor + partialFactor1;                
            }
            
            layerFactor = layerFactor/disControlsInLayer.size(); // media    

            double discount = (layerFactor+(weight*lambda))/2;
            edgesWithDiscount.add(new Edge(e.getLayer(), discount, e.getDescriptionId()));
        }
        return edgesWithDiscount;
    }
    
    
    /**
     * This method returns the three elements of Discount factor per control
     * (through apposite data structure) BEFORE the calculation of lambda.
     * @param matchingLayerFactor
     * @param mappingManagementFactors
     * @param validationFactors
     * @return 
     */
    private Discount groupFactorsPerControl(Factor matchingLayerFactor, 
        ArrayList<Factor> mappingManagementFactors, ArrayList<Factor> validationFactors){
        
        Discount dis = null;
        
        for (Factor factManag : mappingManagementFactors) {
            if(factManag.getId().equals(matchingLayerFactor.getId())){
                for (Factor factValid : validationFactors) {
                    if(factValid.getId().equals(matchingLayerFactor.getId())){
                                               
                        dis = new Discount(matchingLayerFactor.getValue(), 
                                factManag.getValue(), factValid.getValue());
                    }
                }
            }
        }
        return dis;
    }
    
    
    /**
     * This method calculate the coverage (with form of array of Factor) given 
     * by the assessment, returning the value of cv.
     * @param mappingFilePath
     * @param factorControls: all Factors of controls in the same layer
     * @return 
     */
    private double calculateCoveragePerLayer(String mappingFilePath, ArrayList<Factor> factorControls){
        double cv =0;
        int numC=0, numPC=0, numNC=0;
        
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(mappingFilePath));
            br.readLine(); // skip the first line (header)
            String line;
            
            while((line = br.readLine()) != null) {
                String[] data = line.split(";");
                String controlId = data[0];
                String assessment = data[8];
                
                for(Factor fact : factorControls){
                    if(controlId.equals(fact.getId())){
                        if("C".equals(assessment)){numC++;}
                        if("PC".equals(assessment)){numPC++;}
                        if("NC".equals(assessment)){numNC++;}
                    }
                }
            }
            if((numC+numPC+numNC) == 0){
                cv = 0.1;
            } else {
                cv = ((0.9*numC) + (0.5*numPC) + (0.1*numNC))/(numC+numPC+numNC);
            }       
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        finally {
            if (br != null){
                try {br.close();}
                catch (IOException e) {}
            }
        }
        return cv;
    }
    
    
    /**
     * Final method for calculating the discount factor also including lambda
     * @param pathMatchingISO
     * @param outputFile
     * @param gc
     * @param weightMatch
     * @param weightLambda
     * @param weightManag
     * @param weightValid
     */
    public void calculateFormula(String pathMatchingISO, String outputFile, GraphDbControl gc, 
            double weightMatch, double weightLambda, double weightManag, double weightValid){
        
        ArrayList<Factor> fLayers = calculateMappingFactorPerControl(pathMatchingISO, weightMatch);
        ArrayList<Factor> fManag = calculateManagementFactorPerControl(pathMatchingISO, weightManag);
        ArrayList<Factor> fValid = calculateValidationFactorPerControl(pathMatchingISO, weightValid);

        ArrayList<Factor> factorsHuman = new ArrayList();
        ArrayList<Factor> factorsAccess = new ArrayList();
        ArrayList<Factor> factorsNetwork = new ArrayList();
        
        ArrayList<Discount> disH = new ArrayList();
        ArrayList<Discount> disA = new ArrayList();
        ArrayList<Discount> disN = new ArrayList();
        
        ArrayList<Edge> edgeH;
        ArrayList<Edge> edgeA;
        ArrayList<Edge> edgeN;
        
        ArrayList<Edge> edgeFinalH = new ArrayList();
        ArrayList<Edge> edgeFinalA = new ArrayList();
        ArrayList<Edge> edgeFinalN = new ArrayList();
                         
        for (Factor factLayer : fLayers) {
            if("human".equals(factLayer.getType()) && factLayer.getValue()!= 0){
                if(groupFactorsPerControl(factLayer, fValid, fManag) != null){
                    Discount dis = groupFactorsPerControl(factLayer, fManag, fValid);
                    disH.add(dis);
                }
                factorsHuman.add(factLayer);
            }
            else if ("access".equals(factLayer.getType()) && factLayer.getValue()!= 0){
                if(groupFactorsPerControl(factLayer, fValid, fManag) != null){
                    Discount dis = groupFactorsPerControl(factLayer, fManag, fValid);
                    disA.add(dis);
                }
                factorsAccess.add(factLayer);
            }
            else if ("network".equals(factLayer.getType()) && factLayer.getValue()!= 0){
                if(groupFactorsPerControl(factLayer, fValid, fManag) != null){
                    Discount dis = groupFactorsPerControl(factLayer, fManag, fValid);
                    disN.add(dis);
                }
                factorsNetwork.add(factLayer);
            }
        }
        
        edgeH = calculateDiscountWithLambda(disH, "human", gc, weightLambda);
        edgeA = calculateDiscountWithLambda(disA, "access", gc, weightLambda);
        edgeN = calculateDiscountWithLambda(disN, "network", gc, weightLambda);
        
        double cvHuman = calculateCoveragePerLayer(pathMatchingISO, factorsHuman);
        double cvAccess = calculateCoveragePerLayer(pathMatchingISO, factorsAccess);
        double cvNetwork = calculateCoveragePerLayer(pathMatchingISO, factorsNetwork);
        
        for(Edge e: edgeH){
            edgeFinalH.add(new Edge(e.getLayer(), e.getLambda()*cvHuman, e.getDescriptionId()));
        }
        
        for(Edge e: edgeA){
            edgeFinalA.add(new Edge(e.getLayer(), e.getLambda()*cvAccess, e.getDescriptionId()));
        }
        
        for(Edge e: edgeN){
            edgeFinalN.add(new Edge(e.getLayer(), e.getLambda()*cvNetwork, e.getDescriptionId()));
        }
        
        FileWriter fw = null;
        try {
            fw = new FileWriter(outputFile);
            fw.write(""); // Erase previous content
            fw.append("Layer;Edge;Discount\n");
            
            for(Edge e : edgeFinalH){
                fw.append("human;"+ e.getDescriptionId() +";"+e.getLambda()+"\n");
            }
            for(Edge e : edgeFinalA){
                fw.append("access;"+ e.getDescriptionId() +";"+e.getLambda()+"\n");
            }
            for(Edge e : edgeFinalN){
                fw.append("network;"+ e.getDescriptionId() +";"+e.getLambda()+"\n");
            }
        } catch (IOException ex) {
        } finally {
            try {fw.close();}
            catch (IOException ex) {}
        }
    }
}
