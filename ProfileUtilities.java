import java.util.ArrayList;
import java.util.List;


public class ProfileUtilities {


    public static double Boltzmanns = 8.617e-5; //eV/K;
    public static double Diffusivity_0InW = 1e-6;//m^2/s The Diffusivity_0InW of Oxygen in Tungsten
    public static double Diffusivity_0InWO3 = 6.83e-6;//m^2/s The Diffusivity_0InW of Oxygen in Tungsten//https://doi.org/10.1016/0010-938X(80)90092-X
    public static double SurfaceOxygen = 3.1;//should be roughly 44.6 mole/(m^3) in air;//1000/22.4;
    public static double InterfaceConcentration1 = 3.0;
    public static double InterfaceConcentration2 = 2.9;
    public static double InterfaceConcentration3 = 2.722;
    public static double InterfaceConcentration4 = 2.0;

    double molarMass_w = 183.84;////g/mole
    double molarMass_oxy = 16;////g/mole
    double rho_w_mass = 19.3;//////19.3g/cc//19.3/183.84 * (1e6) mole/(m^3) //19.3/183.84 mole/cc
    double rho_w_molar = rho_w_mass / molarMass_w * 1e6;//[mole/(m^3)]//19.3/183.84 mole/cc * 1e6 ~= 1.05e5 mole/(m^3)

    //Oxy In WO3, WO2
    public static double Diffusivity_0WO3 = 8.3e-6;//6.83e-6;//m^2/s The Diffusivity_0InW of Oxygen in Tungsten trioxide//https://doi.org/10.1016/0010-938X(80)90092-X
    static double MigrationEnergyInWO3 = 1.296153;//https://doi.org/10.1016/0010-938X(80)90092-X
    public static double Diffusivity_0WO2 = 3.087e-5;//1.8523718e-5 * 5 / 3;//m^2/s
    static double MigrationEnergyInWO2 = 1.86;//1.86;//by my dft calculation: 1.86eV is Em
    //Oxy In tungsten
    static double D0_InW = 1e-6;//1.3e-4;//1e-6;//1.3 cm2/s
    static double Em_InW = 1.04964;//2.7;//eV         //https://www.nature.com/articles/199337a0.pdf:D0_InW = 1e-6; Em_InW = 2.7;
    //https://www.nature.com/articles/2001310a0.pdf:D0_InW = 1.3e-4;Em_InW = 1.04964;






    //double MigrationEnergyInW = 1.3; //eV; Migration energy of O in W
    public static double  Temperature = 1179; //K
    //public static double MigrationEnergyInOxide = 1.8;//1.296; //eV; Migration energy of O in Oxide//https://doi.org/10.1016/0010-938X(80)90092-X
    //public static double tempDiff = Diffusivity_0WO3 * Math.exp(- MigrationEnergyInOxide / (Boltzmanns * Temperature));
    public static double DiffInOxide1 = Diffusivity_0WO3 * Math.exp(- MigrationEnergyInWO3 / (Boltzmanns * Temperature));// The diffusivity of Oxygen in Oxide
    //public static double DiffInOxide2 = 1 * tempDiff;//Diffusivity_0InOXide2 * Math.exp(- MigrationEnergyInOxide / (Boltzmanns * Temperature));// The diffusivity of Oxygen in Oxide
    //public static double DiffInOxide3 = 0.8 * tempDiff;//Diffusivity_0InOXide2 * Math.exp(- MigrationEnergyInOxide / (Boltzmanns * Temperature));// The diffusivity of Oxygen in Oxide
    public static double DiffInOxide4 = Diffusivity_0WO2 * Math.exp(- MigrationEnergyInWO2 / (Boltzmanns * Temperature));// The diffusivity of Oxygen in Oxide
    public static double DiffInW = D0_InW * Math.exp(- Em_InW / (Boltzmanns * Temperature));//Diffusivity_0InW * Math.exp(- MigrationEnergyInOxide / (Boltzmanns * Temperature));//m^2/s;// The diffusivity of Oxygen in Tungsten




    //implement Initial condition:
    public static void InitialCondition(List<Double> Concentration, double dx, int depthIterations,
                                        double SurfaceOxygen,
                                        double InterfaceConcentration1,
                                        double InterfaceConcentration2,
                                        double InterfaceConcentration3,
                                        double InterfaceConcentration4,
                                        int sIter_1,
                                        int sIter_2,
                                        int sIter_3,
                                        int sIter_4
                                        ) {
//        Boundary condition: Surface concentration

            Concentration.add(SurfaceOxygen);
            for(int i = 1; i <=sIter_1; i++)
                Concentration.add(InterfaceConcentration1);
            for(int i =sIter_1 + 1;  i <= sIter_2; i++)
                Concentration.add(InterfaceConcentration2);
            for(int i =sIter_2 + 1;  i <= sIter_3; i++)
                Concentration.add(InterfaceConcentration3);
            for(int i =sIter_3 + 1;  i <= sIter_4; i++)
                Concentration.add(InterfaceConcentration4);
            for (int i = sIter_4 + 1; i < depthIterations; i++) {
                Concentration.add(0.0);
            }
    }

    //getting getGradient of the previous time step(step [j - 1]):

    public static double getGradient(List<Double> PreviousConcentration, long j, int i, double dx, boolean Left)  {
        if(Left) {
            return (PreviousConcentration.get(i) - PreviousConcentration.get(i - 1)) / dx;
        }
        return (PreviousConcentration.get(i + 1) - PreviousConcentration.get(i)) / dx;
    }

    //add new C_O point to current list of C_O
    //if the function UpdateConcentration is applied for a segment of a pure phase, give interfaceConcentration a dummy value of 0
    public static double UpdateConcentration(List<Double> PreviousConcentration,List<Double> OxygenConcentration,
                                           int startDepth, int enddepth,
                                           double dx, double dt, long timeIter,
                                             double Diff_left, double Diff_right,
                                             boolean IsAtInterface,
                                             double interfaceConcentration
                                            ) {
        double ChangeRate = 0;//(net flux maintained at the current grid point)
        for (int i = startDepth; i < enddepth; i++) {
            double gradient_left = getGradient( PreviousConcentration, timeIter,  i, dx, true);
            double gradient_right = getGradient( PreviousConcentration, timeIter,  i, dx, false);
            ChangeRate = (Diff_right * gradient_right - Diff_left * gradient_left)/dx;
            if(!IsAtInterface){
            OxygenConcentration.add(//i * dx, timeIter*dt,
                    PreviousConcentration.get(i) + ChangeRate * dt );
            }
            else {
                return ChangeRate * dt;
            }


        }
        //complete the last profile point which is the interface
        if(!IsAtInterface) {
            OxygenConcentration.add(enddepth, interfaceConcentration);
        }

            return ChangeRate * dt;
    }


    public static int GetNewInterface(List<Double> OxygenConcentration,
                                             double dx,
                                                int sIter,
                                             double current,
                                             double interfaceConcentration,
                                                int order
    ) {

        while (current > 0) {
            if (sIter + 1>= ConcentrationOfOxigen.depthIterations) {return sIter;}
            double cntOfLayerToFill = OxygenConcentration.get(sIter + 1);
            double gap = interfaceConcentration - cntOfLayerToFill;


            //In the if condition, time Area on both sides of the expression to make sense
            if(current >= dx * gap) {
                //System.out.println("Now current of s" + order + " is: " + current + "; dx * gap is: " + dx * gap+".");
                current -= dx * gap;

                OxygenConcentration.set(sIter + 1,interfaceConcentration);
                sIter++;
                //System.out.println("New interface of s" + order + " is forming, Current sIter_" + order+": " + sIter);
            }
            else {
                OxygenConcentration.set(sIter + 1,cntOfLayerToFill + current / dx);
                break;
            }


        }
        return sIter;
    }



    public double GetMassGain(
            int sIter_1,
            int sIter_2,
            int sIter_3,
            int sIter_4
    ) {
//Calculate total oxygen amount: //in [g/(m^3)]
        double result = 0;
        result += sIter_1 * ConcentrationOfOxigen.dx * (InterfaceConcentration1) * rho_w_molar * molarMass_oxy;
        result += (sIter_2 - sIter_1) * ConcentrationOfOxigen.dx * (InterfaceConcentration2) * rho_w_molar * molarMass_oxy;
        result += (sIter_3 - sIter_2) * ConcentrationOfOxigen.dx * (InterfaceConcentration3) * rho_w_molar * molarMass_oxy;
        result += (sIter_4 -sIter_3) * ConcentrationOfOxigen.dx * (InterfaceConcentration4) * rho_w_molar * molarMass_oxy;

        return result;
    }








}


