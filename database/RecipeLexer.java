import java.util.Collections;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeLexer
{

  public static boolean isNum(String str){
    if(str == null){
      return false;
    }

    try{
      int n = Integer.parseInt(str);
    } catch(NumberFormatException e){
      return false;
    }

    return true;
  }

  public static List<String> parse(String[] tmp){
    int index = 0;

    double quantity=0;
    String unit;
    double instances=0;
    String inst_unit="";
    String name="";

    List<String> returnList = new ArrayList<>();

    for(int i=0; i<tmp.length; i++){

      if(tmp[i].contains("(")){
        tmp[i] = tmp[i].replace("(", "");

        if (i != tmp.length - 1) {
          tmp[i+1] = tmp[i+1].replace(")", "");

          if(tmp[i].contains(".")){
            String[] dec = tmp[i].split("\\.");
            try{
              double whole = Double.parseDouble(dec[0]);
              dec[1] = "0." + dec[1];
              double point = Double.parseDouble(dec[1]);
              instances = whole + point;
            }
            catch(Exception e){
            }
          }
          else if(tmp[i].contains("/")){
            String[] frac = tmp[i].split("/");
            double num = Double.parseDouble(frac[0]);
            double denom = Double.parseDouble(frac[1]);
            quantity += (double)(num / denom);
            index += 1;
          }
          else{
            try{
              instances = Integer.parseInt(tmp[0]);
            }
            catch(Exception e){

            }
          }

          inst_unit = tmp[i+1];
          index += 2;
        } else {
          tmp[i] = tmp[i].replace(")", "");
        }
      }

      else if(tmp[i].contains(".")){
        try{
          String[] dec = tmp[i].split("\\.");
          double whole = Double.parseDouble(dec[0]);
          dec[1] = "0." + dec[1];
          double point = Double.parseDouble(dec[1]);
          quantity += whole + point;
          index += 1;
        }
        catch(Exception e){

        }
        
      }

      else if(isNum(tmp[i])){
        quantity += Integer.parseInt(tmp[i]);
        index += 1;
      }

      else if(tmp[i].contains("/")){
        try {
        String[] frac = tmp[i].split("/");
        double num = Double.parseDouble(frac[0]);
        double denom = Double.parseDouble(frac[1]);
        quantity += (double)(num / denom);
        index += 1;
        }
        catch(Exception e){

        }
      }
    }

    if(index == tmp.length){
      unit = tmp[index -1];
    }
    else{
      unit = tmp[index];
    }
    

    for(int i=index+1; i<tmp.length; i++){
      name += tmp[i];

      if(i != tmp.length-1){
        name += " ";
      }
    }

    if(instances == 0){
      returnList.add(Double.toString(quantity));
      returnList.add(unit);
      if (!name.isEmpty()) {
        returnList.add(name);
      }

    }

    else{
      quantity *= instances;
      returnList.add(Double.toString(quantity));
      returnList.add(inst_unit);
      returnList.add(name);
    }
    return returnList;
  }

  public static void main(String[] args)
  {

    String dirPath = "./Ingredients";

    File dir = new File(dirPath);
    File [] filesInDir = dir.listFiles();

//        System.out.println("size of filesInDir: " + filesInDir.length);

    for (File file : filesInDir) {
      // read file
      
      String fileName = file.getName();
      System.out.println(fileName);
      if(fileName.contains(".txt")) {
        StringBuffer sb = new StringBuffer();

        try{

          BufferedReader br = new BufferedReader(new FileReader(file));
          while(br.ready()) {
            sb.append(br.readLine());
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        if(file.length() == 0){
          continue;
        }
        
        String ingredientList = sb.toString().substring(1, sb.length() - 1);

        String[] ingredients = ingredientList.split("\", |', ");

        Map<String, List<String>> ingredientToAmt = new HashMap<>();

        for(int i = 0; i < ingredients.length; i++) {
          // for each ingredient in the list
          if (i != ingredients.length - 1) {
            ingredients[i] = ingredients[i].substring(1);
          } else {
            ingredients[i] = ingredients[i].substring(1, ingredients[i].length() - 1);
          }
          List<String> parsedIngred = parse(ingredients[i].split(" "));

          for (int count = 0;  count < parsedIngred.size() - 1; count++) {
            String str = parsedIngred.get(count);

            Collections.replaceAll(parsedIngred, str, "\"" + str + "\"");
          }
          int size = parsedIngred.size();

          List<String> quantity = new ArrayList<>();


          for (int j = 0; j < size - 1; j++) {
            quantity.add(parsedIngred.get(j));
          }

          ingredientToAmt.put(parsedIngred.get(size - 1), quantity);
        }

        File outputFile = new File("./Ingredients_output3/"+fileName);

        try{
          PrintWriter printWriter = new PrintWriter(new FileWriter(outputFile));
          
          List<String> newList = new ArrayList<>();
          for (String str : ingredientToAmt.keySet()) {
            newList.add(str);
          }

          for(int i = 0; i < ingredientToAmt.keySet().size(); i++) {
            String str = newList.get(i);
            String amt = String.join(",", ingredientToAmt.get(str));
            if (i == ingredientToAmt.keySet().size() - 1) {
              amt = ":[" + amt + "]";
            } else {
              amt = ":[" + amt + "],";
            }
            
            printWriter.print("\t\t\t\"" + str + "\"" + amt +"\n");

          }

          printWriter.close();

          boolean fileCreated = outputFile.createNewFile();
          assert(fileCreated);
        } catch (Exception e) {
          e.getStackTrace();
        }

      }

    }
  }
}



