package control;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class DiscountControl {
    
    final String pathMatchingISO = "src\\main\\java\\dataset\\alignment\\iso\\AlignmentISOTotal.csv";
    
    /*
    Devo parsare il file totale
    */
    public ArrayList<Factor> calculateMatching(){
        
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
                
                if(gapLayer < 20){
                    resultH = -valDiscountH;
                    resultA = -valDiscountA;
                    resultN = -valDiscountN;
                }
                else if (gapLayer >= 35){
                    resultH = valDiscountH;
                    resultA = valDiscountA;
                    resultN = valDiscountN;
                }
                
                Factor factH = new Factor(controlId, resultH, "human");
                Factor factA = new Factor(controlId, resultA, "access");
                Factor factN = new Factor(controlId, resultN, "network");
                
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
    
    /*
    Dovrei tornare arco(src-dest-tutte info) + layer a cui appartiene + lambda
    */
    public ArrayList<Edge> calculateLambda(ArrayList<Discount> discounts, String layer){
                
        ArrayList<Edge> edgesWithDiscount = new ArrayList();
        int counter=0;
        
        GraphDbControl gc = new GraphDbControl("bolt://localhost:7687", "admin", "admin");
        ArrayList<Double> lambdas = new ArrayList();
        ArrayList<Edge> edges = new ArrayList();
        if("human".equals(layer)){edges = gc.setHumanEdges();}
        else if("network".equals(layer)){edges = gc.setNetworkEdges();}
        //else if("access".equals(layer)){edges = gc.setAccessEdges();}
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
            double finalDis=0;
                    
            for(Discount dis: discounts){
                disInLayerPlus += dis.getDisPositive()/dis.getNumElemPositive();
                disInLayerMinus += dis.getDisNegative()/dis.getNumElemNegative();
            }
            
            if(lambda <= threshold1){
                //System.out.println("CASE 1");
                double dis1 = disInLayerPlus/100;
                double dis2 = ((disInLayerMinus/100)-lambda)/2;
                finalDis = dis1 + dis2;

                //System.out.println(finalDis);
            }
            else if(lambda >= threshold2){
                //System.out.println("CASE 3");
                double dis1 = ((disInLayerPlus/100)+lambda)/2;
                double dis2 = disInLayerMinus/100;
                finalDis = dis1 + dis2;

                //System.out.println(finalDis);
            }
            else{
                //System.out.println("CASE 2");
                double dis1 = disInLayerPlus/100;
                double dis2 = disInLayerMinus/100;
                finalDis = dis1 + dis2;

                //System.out.println(finalDis);
            }
            edgesWithDiscount.add(new Edge(e.getLayer(), finalDis, e.getDescriptionId()));
            counter++;
        }
        System.out.println("counter: " + counter);
        return edgesWithDiscount;
    }
    
    public ArrayList<Factor> calculateManagement(){
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
                double runtime = Double.parseDouble(data[4]);
                double designtime = Double.parseDouble(data[5]);
                double operational = Double.parseDouble(data[6]);
                double compliance = Double.parseDouble(data[7]);
                
                double result = 0;
                
               
                if(runtime > designtime){
                    if(operational > compliance){result = 50;}
                    else if(operational < compliance){result = 25;}
                }
                else if (runtime < designtime){
                    if(operational > compliance){result = 25;}
                    else if(operational < compliance){result = -25;}
                }
                
                Factor fact = new Factor(controlId, result, "management");

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
    
    /*
    Dalla classe ValidationControl posso avere ArrayList di tipo 
    <idControl, factorValidation>
    */
    public ArrayList<Factor> calculateValidation(){
        ValidationControl vc = new ValidationControl();
        ArrayList<MappingParam> mp = vc.parseValidationFile();
        return vc.caluclateValidationFactor(mp);
    }
    
    public double normalizeWithAssessment(/*ArrayList<Edge> edges,*/ ArrayList<Factor> factorControls){
        double cv =0;
        int numC=0, numPC=0, numNC=0;
        
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(pathMatchingISO));
            br.readLine(); // skip the first line (header)
            String line;
            
            while((line = br.readLine()) != null) {
                
                String[] data = line.split(";");
                
                // Elements in matching csv dataset
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
            System.out.println("C: " + numC + " PC: " + numPC + " NC: " + numNC + "----" + (numC+numPC+numNC));
            if(numC >= numPC && numC >= numNC){cv=0.9;}
            else if(numPC > numC && numPC >= numNC){cv=0.4;}
            else{cv=0.1;}
            System.out.println("cv: " + cv);
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
    
    public Discount calculateDiscount(Factor f11, ArrayList<Factor> f2, ArrayList<Factor> f3){
        
        Discount dis = new Discount(0,0,0,0);
        Factor f = new Factor(null, 0, null);
        
        for (Factor f22 : f2) {
            if(f22.getId().equals(f11.getId())){
                for (Factor f33 : f3) {
                    if(f33.getId().equals(f11.getId())){
                        double disPlus=0, disMinus = 0;
                        int counterPlus=0, counterMinus = 0;
                        if(f11.getValue() >= 0){
                            disPlus = disPlus + f11.getValue();
                            counterPlus++;
                        } else {
                            disMinus = disMinus + f11.getValue();
                            counterMinus++;
                        }
                        if(f22.getValue() >= 0){
                            disPlus = disPlus + f22.getValue();
                            counterPlus++;
                        } else {
                            disMinus = disMinus + f22.getValue();
                            counterMinus++;
                        }
                        if(f33.getValue() >= 0){
                            disPlus = disPlus + f33.getValue();
                            counterPlus++;
                        } else {
                            disMinus = disMinus + f33.getValue();
                            counterMinus++;
                        }

                        dis = new Discount(disPlus, counterPlus, disMinus, counterMinus);

                        f = new Factor(f11.getId(), 0, "dis");
                    }
                }
            }
        }
        if(f.getId()!=null){
            return dis;
        } else {
            return null;
        }
    }
    
    public void calculateFormula(){
        ArrayList<Factor> f1 = calculateMatching();
        ArrayList<Factor> f2 = calculateValidation();
        ArrayList<Factor> f3 = calculateManagement();
        
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
        
        
        //ArrayList<Factor> result = new ArrayList();
        
        for (Factor f11 : f1) {
            if("human".equals(f11.getType()) && f11.getValue()!= 0){
                if(calculateDiscount(f11, f2, f3) != null){
                    disH.add(calculateDiscount(f11, f2, f3));
                }
                factorsHuman.add(f11);
                
            }
            else if ("access".equals(f11.getType()) && f11.getValue()!= 0){
                if(calculateDiscount(f11, f2, f3) != null){
                    disA.add(calculateDiscount(f11, f2, f3));
                }
                factorsAccess.add(f11);
            }
            else if ("network".equals(f11.getType()) && f11.getValue()!= 0){
                if(calculateDiscount(f11, f2, f3) != null){
                    disN.add(calculateDiscount(f11, f2, f3));
                }
                factorsNetwork.add(f11);
            }
        }
        
        double cvHuman = normalizeWithAssessment(factorsHuman);
        double cvAccess = normalizeWithAssessment(factorsAccess);
        double cvNetwork = normalizeWithAssessment(factorsNetwork);
        
        
        
        edgeH = calculateLambda(disH, "human");
        //edgeA = calculateLambda(disA, "access");
        edgeN = calculateLambda(disN, "network");
        
        /*
        System.out.println("HUMAN---------------------------------");
        for(Discount d : disH){
            System.out.println(d.getDisPositive() + " " + d.getDisNegative());
        }
        System.out.println("ACCESS---------------------------------");
        for(Discount d : disA){
            System.out.println(d.getDisPositive() + " " + d.getDisNegative());
        }
        System.out.println("NETWORK---------------------------------");
        for(Discount d : disN){
            System.out.println(d.getDisPositive() + " " + d.getDisNegative());
        }
       */
        for(Edge e: edgeH){
            edgeFinalH.add(new Edge(e.getLayer(), e.getLambda()*cvHuman, e.getDescriptionId()));
        }
        /*
        for(Edge e: edgeA){
            edgeFinalH.add(new Edge(e.getLayer(), e.getLambda()*cvAccess, e.getDescriptionId()));
        }
        */
        for(Edge e: edgeN){
            edgeFinalN.add(new Edge(e.getLayer(), e.getLambda()*cvNetwork, e.getDescriptionId()));
        }
        
        for(Edge e : edgeFinalH){
            System.out.println(e.getDescriptionId() + " discount factor: " + e.getLambda());
        }
        for(Edge e : edgeFinalN){
            System.out.println(e.getDescriptionId() + " discount factor: " + e.getLambda());
        }
        
    }
}
