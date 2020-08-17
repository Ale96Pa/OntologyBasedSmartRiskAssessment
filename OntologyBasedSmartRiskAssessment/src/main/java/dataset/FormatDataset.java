package dataset;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This class adapt datasets with appropriate format
 * @author Alessandro
 */
public class FormatDataset {
    
    final String srcPath = "src\\main\\java\\dataset\\NIST-formatted5.csv";
    final String destPath = "src\\main\\java\\dataset\\NIST-formatted6.csv";
    
    public void startFormatNist(){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(srcPath));
            br.readLine(); // skip the first line (header)
            String line;
            
            FileWriter csvWriter = new FileWriter(destPath);
            csvWriter.append("Family");
            csvWriter.append(";");
            csvWriter.append("Name");
            csvWriter.append(";");
            csvWriter.append("Title");
            csvWriter.append(";");
            csvWriter.append("Priority");
            csvWriter.append(";");
            csvWriter.append("Impact");
            csvWriter.append(";");
            csvWriter.append("Description");
            csvWriter.append(";");
            csvWriter.append("SupplementarGuidance");
            csvWriter.append(";");
            csvWriter.append("Related");
            csvWriter.append("\n");
            
            
            while((line = br.readLine()) != null) {
                String[] data = line.split(";");
                int datalen = data.length;
                String fam1, name1, tit1, pri1, imp1, des1, sup1="", rel1="";        
                        
                fam1 = data[0];
                name1 = data[1];
                tit1 = data[2];
                pri1 = data[3];
                imp1 = data[4];
                des1 = data[5];
                if (datalen >= 8){
                    sup1 = data[6];
                    rel1 = data[7];
                }
                
                String lineNext;
                if ((lineNext = br.readLine()) != null){
                    String[] data2 = lineNext.split(";");
                    int datalen2 = data2.length;
                    String fam2, name2, tit2, pri2, imp2, des2, sup2="", rel2="";

                    fam2 = data2[0];
                    name2 = data2[1];
                    tit2 = data2[2];
                    pri2 = data2[3];
                    imp2 = data2[4];
                    des2 = data2[5];
                    if (datalen2 >= 8){
                        sup2 = data2[6];
                        rel2 = data2[7];
                    }

                    String newDes;
                    if(tit1.equals(tit2)){
                        newDes = des1.concat(" "+des2);

                        List<List<String>> rows = Arrays.asList(
                        Arrays.asList(fam1, name1, tit1, pri1, imp1, newDes, sup1, rel1)
                        );

                        for (List<String> rowData : rows) {
                        csvWriter.append(String.join(";", rowData));
                        csvWriter.append("\n");
                        }

                    } else {

                        List<List<String>> rows = Arrays.asList(
                        Arrays.asList(fam1, name1, tit1, pri1, imp1, des1, sup1, rel1),
                        Arrays.asList(fam2, name2, tit2, pri2, imp2, des2, sup2, rel2)
                        );

                        for (List<String> rowData : rows) {
                        csvWriter.append(String.join(";", rowData));
                        csvWriter.append("\n");
                        }
                    }
                    //csvWriter.flush();
                    //csvWriter.close();
                }
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
    }
}
