//import java.util.*;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//import java.lang.Math;

public class ConcentrationOfOxigen
{

    // Discrete parameters:
    public static boolean LockS = true, LockS2 = true, LockS3 = false, LockS4 = false;//true;

    public static long sampling = 3000000L;//3e4s; 1.2e6s=2week
    public static int depthIterations = 800;// count
    public static long timeIterations = 100L * sampling;// 1month
    public static double dx = 10e-9;//in [m], around 1nm
    public static double dt = 100e-9;// in [second] //1e-3s
    //typical diffusivity: 1.677e-13 m2/s
    public static  boolean printProfile = true, printMassGain = true, printFitParas = false;


    public static void main(String args[]) throws IOException {
        /*//read kp database
        ArrayList<Double> temperatures = new ArrayList<>();
        ArrayList<Double> targets = new ArrayList<>();
        IOUtilities.kpReader(temperatures, targets);*/

        FileWriter testkp_Writer = new FileWriter("testLog");
        PrintWriter print_kp = new PrintWriter(testkp_Writer);
        FileWriter fittedkp_Writer = new FileWriter("fittedKp");
        PrintWriter print_fittedkp = new PrintWriter(fittedkp_Writer);

        //


        double tolerance = 0.1;
        int[] i = {0,0};
        int upper = 6;/////?/////
        int upperEmCount = 0;///?////


        double dD = 1e-6;
        //
        //double UpperEm = 1.4, LowerEm = 1.22;//1.8-1.3ev
        double Em_wo2d9 = 1.4, Em_wo2d72 = 1.22;

//        for (int index = 12; index < 13; index++) {//read kp from input//targets.size()
            double temperature = 600 + 273;//temperatures.get(index);
            //double target = targets.get(index);

            //prepare diffusivity values:
            double DiffInOxide1_0 = ProfileUtilities.Diffusivity_0WO3;
            double DiffInOxide1 = DiffInOxide1_0 * Math.exp(-ProfileUtilities.MigrationEnergyInWO3 / (ProfileUtilities.Boltzmanns * temperature));

            double DiffInOxide2_0 = 6.8e-6;//i[0] * dD;
            double DiffInOxide2 = DiffInOxide2_0 * Math.exp(-Em_wo2d9 / (ProfileUtilities.Boltzmanns * temperature));
            double DiffInOxide3_0 = 6.8e-6;//i[1] * dD;
            double DiffInOxide3 = DiffInOxide3_0 * Math.exp(-Em_wo2d72 / (ProfileUtilities.Boltzmanns * temperature));

            double DiffInOxide4_0 = ProfileUtilities.Diffusivity_0WO2;
            double DiffInOxide4 = DiffInOxide4_0 * Math.exp(-ProfileUtilities.MigrationEnergyInWO2 / (ProfileUtilities.Boltzmanns * temperature));
            double DiffInW = ProfileUtilities.D0_InW * Math.exp(-ProfileUtilities.Em_InW / (ProfileUtilities.Boltzmanns * temperature));//ProfileUtilities.DiffInW;
            //System.out.println("DiffInW: "+DiffInW);



            //run the model:
            double Kp = KineticModel.KineticModel(DiffInOxide1, DiffInOxide2, DiffInOxide3, DiffInOxide4, DiffInW);
            if (Kp < 1e-4) Kp = 0;//too small kp is meaningless
            System.out.println("The result is: ");
            //String result = temperature + "// l2 D0: " + DiffInOxide2_0 + " Em: " + Em_wo2d9 + " l3 D0: " + DiffInOxide3_0 + " Em: " + Em_wo2d72 +" kp difference: "+ (Kp-target)/target;
            String result = temperature + " " + DiffInOxide2_0 + " " + Em_wo2d9 + " " + DiffInOxide3_0 + " " + Em_wo2d72;// +" kp difference: "+ (Kp-target)/target;

            //print_kp.println(result);
            System.out.println(result);








//                            }

//                        }
//                    }
//                }
//        }


        print_kp.close();
        print_fittedkp.close();

    }//main
}//class