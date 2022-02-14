import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class IOUtilities {
    public static void kpReader(ArrayList<Double> temperatures, ArrayList<Double> targets) {
        int index = 0;

        try {
            File myObj = new File("./kpdatabase");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String [] content = data.split("\t");
                temperatures.add(Double.parseDouble(content[0]));
                targets.add(Double.parseDouble(content[1]));
                //System.out.println(temperatures.get(index));
                //System.out.println(targets.get(index));
                index++;
            }
            myReader.close();
        } catch (
                FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void fittedKpReader(Map<String[], List<Double>> Diffs2Temp) {
        int index = 0;

        try {
            File myObj = new File("./fittedKp");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String [] content = data.split(" ");
                String [] DiffString = new String [content.length - 1];
                System.arraycopy(content, 1, DiffString, content.length - 2, content.length - 3);

                if (!Diffs2Temp.containsKey(DiffString)) {
                    List<Double> list = new ArrayList<Double>();
                    list.add(Double.parseDouble(content[0]));
                    Diffs2Temp.put(DiffString, list);
                }

                
                index++;
            }
            myReader.close();
        } catch (
                FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }


}
