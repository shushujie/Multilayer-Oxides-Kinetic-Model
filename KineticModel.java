import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.nio.file.*;

public class KineticModel {
    public static double KineticModel(
            double DiffInOxide1,
            double DiffInOxide2,
            double DiffInOxide3,
            double DiffInOxide4,
            double DiffInW) throws IOException {


        ProfileUtilities tools = new ProfileUtilities();
        double dx = ConcentrationOfOxigen.dx;
        double dt = ConcentrationOfOxigen.dt;
        int depthIterations = ConcentrationOfOxigen.depthIterations;
        long timeIterations = ConcentrationOfOxigen.timeIterations;
        long sampling = ConcentrationOfOxigen.sampling;



        double SurfaceOxygen = tools.SurfaceOxygen;
        double InterfaceConcentration1 = tools.InterfaceConcentration1;//3.0;
        double InterfaceConcentration2 = tools.InterfaceConcentration2;//2.9;
        double InterfaceConcentration3 = tools.InterfaceConcentration3;//2.722;
        double InterfaceConcentration4 = tools.InterfaceConcentration4;//2.0;


        /**/
        /**/
        //if(ConcentrationOfOxigen.printProfile) {  }

        String testID = "0212_22";
        FileWriter fileWriter = new FileWriter("profile" + testID + ".txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        FileWriter mGain_Writer = new FileWriter("massGain"+testID+".txt");
        PrintWriter print_mGain = new PrintWriter(mGain_Writer);
        FileWriter CMFit_Writer = new FileWriter("CommonsMathFit"+testID+".txt");
        PrintWriter Print_CMFit = new PrintWriter(CMFit_Writer);

        FileWriter Ss_Writer = new FileWriter("InterfacePositions"+testID+".txt");
        PrintWriter print_Ss = new PrintWriter(Ss_Writer);


/*
////// Initial condition implement:
*/
        //Initialize interfaces at time = 0
        ArrayList<Double> OxygenConcentration = new ArrayList<>();//[Oxy] profile at a specific time; it's a temporary data container
        ArrayList<Double> Time = new ArrayList<>();
        ArrayList<Double> sqrtTime = new ArrayList<>();

        ArrayList<Double> MassGains = new ArrayList<>();

        int sIter_1 = 2;
        int sIter_2 = 4;
        int sIter_3 = 6;
        int sIter_4 = 8;

        tools.InitialCondition(OxygenConcentration, dx, depthIterations, SurfaceOxygen,
                InterfaceConcentration1, InterfaceConcentration2, InterfaceConcentration3, InterfaceConcentration4,
                sIter_1, sIter_2, sIter_3, sIter_4);

        //Add initial condition to the time list of profiles

/**/        if (ConcentrationOfOxigen.printProfile){
            DataPrinter.WriteData(OxygenConcentration, printWriter, 0, true);}/**/
        DataPrinter.WriteInterfacePosition(print_Ss, sIter_1, sIter_2, sIter_3, sIter_4);
        double massGain = tools.GetMassGain(sIter_1,sIter_2,sIter_3,sIter_4);
/*        if (ConcentrationOfOxigen.printMassGain){
        print_mGain.printf("%d ", 0); print_mGain.printf("%e\n", massGain);}*/
        //Time.add(0.0);
        //sqrtTime.add(Math.sqrt(Time.get(0)));



/*
////// Revolution:
*/
        ArrayList<Integer> sIterArray = new ArrayList<Integer>();
        sIterArray.add(sIter_1);
        sIterArray.add(sIter_2);
        sIterArray.add(sIter_3);
        sIterArray.add(sIter_4);
        SolveDiffusionODE solver = new SolveDiffusionODE();

        //As values at time j = 0 already initialized, kick off time evolution from j = 1:
        for (long j = 1; j <= timeIterations; j++) {//j for time loop
            //record the profile at previous time step
            ArrayList<Double> PreviousConcentration = new ArrayList<>(OxygenConcentration);
            OxygenConcentration = new ArrayList<>();//it functions as a profile container for each time iterator;
            boolean  IsFinish = solver.SolveODE(PreviousConcentration, OxygenConcentration, sIterArray, tools,  j,
                    DiffInOxide1,
                    DiffInOxide2,
                    DiffInOxide3,
                    DiffInOxide4,
                    DiffInW);

            //System.out.println("OxygenConcentration has dimension of : "+OxygenConcentration.size());
            if (!IsFinish) break;
            if(j%sampling == 0) {
                sIter_1 = sIterArray.get(0);
                sIter_2 = sIterArray.get(1);
                sIter_3 = sIterArray.get(2);
                sIter_4 = sIterArray.get(3);

/**/                if (ConcentrationOfOxigen.printProfile){
                DataPrinter.WriteData(OxygenConcentration, printWriter, j, true);}/**/

                DataPrinter.WriteInterfacePosition(print_Ss, sIter_1, sIter_2, sIter_3, sIter_4);
                massGain = tools.GetMassGain(sIter_1,sIter_2,sIter_3,sIter_4);
                double time = j*dt;
                if (ConcentrationOfOxigen.printMassGain){
                print_mGain.printf("%e ", time);
                print_mGain.printf("%e\n", massGain);}


                Time.add(time);
                sqrtTime.add(Math.sqrt(time));
                MassGains.add(massGain);

            }

        }//time iteration;

        //FittingCurve Curvefitter = new FittingCurve();
        //double[] coeffs = Curvefitter.fit(sqrtTime, MassGains);
        //double kp = coeffs[1]*coeffs[1];
        //System.out.println(testID + " has kp of: " + kp );

        /*for(int i = 0; i < coeffs.length; i++) {
            //System.out.println(i + "'s oder " + coeffs[i]);
            Print_CMFit.println(coeffs[i]);
        }*/
        printWriter.close();
        print_mGain.close();
        Print_CMFit.close();
        print_Ss.close();
        printWriter.close();




        return 0;


    }//main

}
