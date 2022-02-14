import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DataPrinter {

    public static void setUpFile() {

    }

    public static void WriteData(  List<Double> OxygenConcentration,
                                   PrintWriter printWriter,
                                   long j,
                                   boolean withXcoord) throws IOException {
        //print data:

        int tempDepthIter = ConcentrationOfOxigen.depthIterations;//replace depthIterations temporarily
        long tempTimeIter = ConcentrationOfOxigen.timeIterations;//timeIterations//0;//
        //PrintWriter printWriter2 = new PrintWriter(fileWriter);///writeDta////////////////


/*            if (j % ConcentrationOfOxigen.sampling != 0) {
                return;
            }*/
            //0th layer is just to mark the surface concentration of oxygen.
            //1st layer is the first(topmost) oxide
            //2nd layer is the second oxide, ... , etc.
            for (int i = 0; i <tempDepthIter; i++) {
                if (!withXcoord)
                printWriter.printf("%f ", (OxygenConcentration.get(i) ));
                else {
                    printWriter.printf("%d ", i);
                    printWriter.printf("%f\n", (OxygenConcentration.get(i)));
                }

            }

            printWriter.printf("\n");///writeDta////////////////

        //end of printing data


    }

    public static void WriteInterfacePosition(
                                    PrintWriter print_Ss, int sIter_1,
                                     int sIter_2,
                                     int sIter_3,
                                     int sIter_4
                                    ) throws IOException {

            double s_1 = sIter_1 * ConcentrationOfOxigen.dx;
            double s_2 = sIter_2 * ConcentrationOfOxigen.dx;
            double s_3 = sIter_3 * ConcentrationOfOxigen.dx;
            double s_4 = sIter_4 * ConcentrationOfOxigen.dx;

        print_Ss.printf("%e ", s_1);///writeDta////////////////;
        print_Ss.printf("%e ", s_2);///writeDta////////////////;
        print_Ss.printf("%e ", s_3);///writeDta////////////////;
        print_Ss.printf("%e\n", s_4);///writeDta////////////////;


    }

}