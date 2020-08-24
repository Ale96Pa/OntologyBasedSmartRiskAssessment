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
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiscountControl {
    
    /**
     * This file return an array of three Factors, one per each layer, with
     * information about the mapping factor
     * @return 
     */
    private ArrayList<Factor> calculateMatchingLayer(String pathMatchingISO, double weight){
        ArrayList<Factor> factors = new ArrayList(); 
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
                
                double gapLayer = Math.abs(human-network);
                
                //TODO: validare soglie in maniera più concreta
                double resultH=0, resultA=0, resultN=0;
                double valDiscountH = 0, valDiscountN=0, valDiscountA=0;
                if(human != 0.0){valDiscountH = ((gapLayer/2) + human)/2;}
                if(access != 0.0){valDiscountA = ((gapLayer/2) + access)/2;}
                if(network != 0.0){valDiscountN = ((gapLayer/2) + network)/2;}
                
                if(gapLayer < 0.35){
                    resultH = -valDiscountH;
                    resultA = -valDiscountA;
                    resultN = -valDiscountN;
                }
                else if (gapLayer >= 0.35){
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
     * This method pick information from the attack graph in order to calculate
     * the lambda factor for the formula. It returns the list of edges with
     * the lambda value attached.
     * @param discounts
     * @param layer
     * @return 
     */
    private ArrayList<Edge> calculateLambda(ArrayList<Discount> discounts, String layer,
            GraphDbControl gc, double weight){
                
        ArrayList<Edge> edgesWithDiscount = new ArrayList();        
        ArrayList<Double> lambdas = new ArrayList();
        ArrayList<Edge> edges = new ArrayList();
        
        if("human".equals(layer)){edges = gc.setHumanEdges();}
        else if("network".equals(layer)){edges = gc.setNetworkEdges();}
        else if("access".equals(layer)){edges = gc.setAccessEdges();}
        for(Edge e : edges){
            double lambda = e.getLambda();
            lambdas.add(lambda);
        }
        
        double max = Collections.max(lambdas);
        double min = Collections.min(lambdas);
        double threshold1 = (max-min)/3;
        double threshold2 = (max-min)*2/3;
        
        for(Edge e : edges){
            double lambda = e.getLambda();
            double disInLayerPlus = 0, disInLayerMinus = 0;
            double finalDis;
                    
            for(Discount dis: discounts){
                if(dis.getNumElemPositive() != 0){
                    disInLayerPlus += dis.getDisPositive()/dis.getNumElemPositive();
                }
                if(dis.getNumElemNegative() != 0){
                    disInLayerMinus += dis.getDisNegative()/dis.getNumElemNegative();
                }
            }
            
            if(lambda <= threshold1){
                double dis1 = disInLayerPlus;
                double dis2 = (disInLayerMinus-(weight*lambda))/2;
                finalDis = dis1 + dis2;
            }
            else if(lambda >= threshold2){
                double dis1 = (disInLayerPlus+(weight*lambda))/2;
                double dis2 = disInLayerMinus;
                finalDis = dis1 + dis2;
            }
            else{
                double dis1 = disInLayerPlus;
                double dis2 = disInLayerMinus;
                finalDis = dis1 + dis2;
            }
            edgesWithDiscount.add(new Edge(e.getLayer(), finalDis, e.getDescriptionId()));
        }
        return edgesWithDiscount;
    }
    
    /**
     * This method return an array of factors, one per each control with the
     * value of management and lifetime parameter attached.
     * @param pathFinalMapping
     * @return 
     */
    private ArrayList<Factor> calculateManagement(String pathFinalMapping, double weight){
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
    private ArrayList<Factor> calculateValidation(String alignmentISOPath, double weight){
        ValidationControl vc = new ValidationControl();
        ArrayList<MappingParam> mp = vc.parseValidationFile(alignmentISOPath);
        return vc.caluclateValidationFactor(mp, weight);
    }
    
    /**
     * This method normalizes the formula (with form of array of Factor) with the
     * value of coverage given by the assessment, returning the value of cv.
     * @param mappingFilePath
     * @param factorControls
     * @return 
     */
    private double normalizeWithAssessment(String mappingFilePath, ArrayList<Factor> factorControls){
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
            /*        
            if(numC >= numPC && numC >= numNC){cv=0.9;}
            else if(numPC > numC && numPC >= numNC){cv=0.4;}
            else{cv=0.1;}
            */        
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
     * This method return the positive and negative elements of Discount factor
     * (through apposite data structure) BEFORE the calculation of lambda (this
     * is the reason why positive and negative parts are separated).
     * @param matchingLayerFactor
     * @param mappingManagementFactors
     * @param validationFactors
     * @return 
     */
    public Discount calculateDiscount(Factor matchingLayerFactor, 
        ArrayList<Factor> mappingManagementFactors, ArrayList<Factor> validationFactors){
        
        Discount dis = new Discount(0,0,0,0);
        Factor f = new Factor(null, 0, null);
        
        for (Factor factManag : mappingManagementFactors) {
            if(factManag.getId().equals(matchingLayerFactor.getId())){
                for (Factor factValid : validationFactors) {
                    if(factValid.getId().equals(matchingLayerFactor.getId())){
                        double disPlus=0, disMinus = 0;
                        int counterPlus=0, counterMinus = 0;
                        
                        // Parse matching layer part
                        if(matchingLayerFactor.getValue() >= 0){
                            disPlus = disPlus + matchingLayerFactor.getValue();
                            counterPlus++;
                        } else {
                            disMinus = disMinus + matchingLayerFactor.getValue();
                            counterMinus++;
                        }
                        
                        // Parse matching management part
                        if(factManag.getValue() >= 0){
                            disPlus = disPlus + factManag.getValue();
                            counterPlus++;
                        } else {
                            disMinus = disMinus + factManag.getValue();
                            counterMinus++;
                        }
                        
                        // Parse validation part
                        if(factValid.getValue() >= 0){
                            disPlus = disPlus + factValid.getValue();
                            counterPlus++;
                        } else {
                            disMinus = disMinus + factValid.getValue();
                            counterMinus++;
                        }
                        
                        dis = new Discount(disPlus, counterPlus, disMinus, counterMinus);

                        f = new Factor(matchingLayerFactor.getId(), 0, "dis");
                    }
                }
            }
        }
        // Return only if the matching factor exists
        if(f.getId()!=null){return dis;}
        else {return null;}
    }
    
    /**
     * Final method for calculating the discount factor also including lambda
     */
    public void calculateFormula(String pathMatchingISO, String outputFile, GraphDbControl gc, 
            double weightMatch, double weightLambda, double weightManag, double weightValid){
        ArrayList<Factor> fLayers = calculateMatchingLayer(pathMatchingISO, weightMatch);
        ArrayList<Factor> fValid = calculateValidation(pathMatchingISO, weightValid);
        ArrayList<Factor> fManag = calculateManagement(pathMatchingISO, weightManag);
        
        ArrayList<Factor> factorsHuman = new ArrayList();
        ArrayList<Factor> factorsAccess = new ArrayList();
        ArrayList<Factor> factorsNetwork = new ArrayList();
        
        ArrayList<Discount> disH = new ArrayList();
        ArrayList<Discount> disA = new ArrayList();
        ArrayList<Discount> disN = new ArrayList();
        
        ArrayList<Edge> edgeH = new ArrayList();
        ArrayList<Edge> edgeA = new ArrayList();
        ArrayList<Edge> edgeN = new ArrayList();
        
        ArrayList<Edge> edgeFinalH = new ArrayList();
        ArrayList<Edge> edgeFinalA = new ArrayList();
        ArrayList<Edge> edgeFinalN = new ArrayList();
                 
        for (Factor factLayer : fLayers) {
            if("human".equals(factLayer.getType()) && factLayer.getValue()!= 0){
                if(calculateDiscount(factLayer, fValid, fManag) != null){
                    disH.add(calculateDiscount(factLayer, fValid, fManag));
                }
                factorsHuman.add(factLayer);
            }
            else if ("access".equals(factLayer.getType()) && factLayer.getValue()!= 0){
                if(calculateDiscount(factLayer, fValid, fManag) != null){
                    disA.add(calculateDiscount(factLayer, fValid, fManag));
                }
                factorsAccess.add(factLayer);
            }
            else if ("network".equals(factLayer.getType()) && factLayer.getValue()!= 0){
                if(calculateDiscount(factLayer, fValid, fManag) != null){
                    disN.add(calculateDiscount(factLayer, fValid, fManag));
                }
                factorsNetwork.add(factLayer);
            }
        }        
        double cvHuman = normalizeWithAssessment(pathMatchingISO, factorsHuman);
        double cvAccess = normalizeWithAssessment(pathMatchingISO, factorsAccess);
        double cvNetwork = normalizeWithAssessment(pathMatchingISO, factorsNetwork);

        edgeH = calculateLambda(disH, "human", gc, weightLambda);
        edgeA = calculateLambda(disA, "access", gc, weightLambda);
        edgeN = calculateLambda(disN, "network", gc, weightLambda);

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
                //System.out.println(e.getDescriptionId() + " discount factor: " + e.getLambda());
                fw.append("human;"+ e.getDescriptionId() +";"+e.getLambda()+"\n");
            }
            for(Edge e : edgeFinalA){
                //System.out.println(e.getDescriptionId() + " discount factor: " + e.getLambda());
                fw.append("access;"+ e.getDescriptionId() +";"+e.getLambda()+"\n");
            }
            for(Edge e : edgeFinalN){
                //System.out.println(e.getDescriptionId() + " discount factor: " + e.getLambda());
                fw.append("network;"+ e.getDescriptionId() +";"+e.getLambda()+"\n");
            }
        } catch (IOException ex) {
        } finally {
            try {fw.close();}
            catch (IOException ex) {}
        }
        
    }
}
