import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SolveDiffusionODE {
    boolean SolveODE(ArrayList<Double> PreviousConcentration, ArrayList<Double> OxygenConcentration, ArrayList<Integer> sIterArray,
                         ProfileUtilities tools, long j,
                  double DiffInOxide1,
                  double DiffInOxide2,
                  double DiffInOxide3,
                  double DiffInOxide4,
                  double DiffInW

    ) throws IOException {

        double SurfaceOxygen = tools.SurfaceOxygen;
        double InterfaceConcentration1 = tools.InterfaceConcentration1;//3.0;
        double InterfaceConcentration2 = tools.InterfaceConcentration2;//2.9;
        double InterfaceConcentration3 = tools.InterfaceConcentration3;//2.722;
        double InterfaceConcentration4 = tools.InterfaceConcentration4;//2.0;
        boolean LockS = ConcentrationOfOxigen.LockS, LockS2 = ConcentrationOfOxigen.LockS2, LockS3 = ConcentrationOfOxigen.LockS3, LockS4 = ConcentrationOfOxigen.LockS4;//true;

        int sIter_1 = sIterArray.get(0);
        int sIter_2 = sIterArray.get(1);
        int sIter_3 = sIterArray.get(2);
        int sIter_4 = sIterArray.get(3);

        //boundary condition; at depth i = 0; for the loop to add the follow positions;
        OxygenConcentration.add( SurfaceOxygen );//OxygenConcentration is the concentration profile at time j


        int depthIterations = ConcentrationOfOxigen.depthIterations;

        if (!IsValid(sIter_1, sIter_2, sIter_3, sIter_4)) return false;

        double dx = ConcentrationOfOxigen.dx;
        double dt = ConcentrationOfOxigen.dt;
/*
        double DiffInOxide1 = tools.DiffInOxide1;
        double DiffInOxide2 = tools.DiffInOxide2;
        double DiffInOxide3 = tools.DiffInOxide3;
        double DiffInOxide4 = tools.DiffInOxide4;
        double DiffInW = tools.DiffInW;*/


/*
//Profile consider no interface motion:
*/
        //1.Topmost Oxide part profile //[start_depth, end_depth): with endDepth excluded
        tools.UpdateConcentration(PreviousConcentration,OxygenConcentration, 1,  sIter_1, dx,  dt,  j,  DiffInOxide1, DiffInOxide1, false, InterfaceConcentration1);
        //3.between 1st and 2nd interfaces
        tools.UpdateConcentration(PreviousConcentration,OxygenConcentration, sIter_1 + 1,  sIter_2, dx,  dt,  j,  DiffInOxide2, DiffInOxide2, false,InterfaceConcentration2);
        //5.between 2nd and 3rd interfaces
        tools.UpdateConcentration(PreviousConcentration,OxygenConcentration, sIter_2 + 1,  sIter_3, dx,  dt,  j,  DiffInOxide3, DiffInOxide3, false,InterfaceConcentration3);
        //7.between 3rd and 4th interfaces
        tools.UpdateConcentration(PreviousConcentration,OxygenConcentration, sIter_3 + 1,  sIter_4, dx,  dt,  j,  DiffInOxide4, DiffInOxide4, false,InterfaceConcentration4);
        //9.Metal part profile
        tools.UpdateConcentration(PreviousConcentration,OxygenConcentration, sIter_4 + 1,  depthIterations - 1, dx,  dt,  j,  DiffInW, DiffInW, false,0);//intfaceCon is useless here
        //BC(boundary condition):This is the 0 flux boundary condition at the far end;
        // get value at ("depth of film thickness" - dx):
        double valueAtL_dx = OxygenConcentration.get(depthIterations - 2);//
        // at every time j,get the left value before the last point(far end boundary point)
        OxygenConcentration.set(depthIterations - 1, valueAtL_dx );//finish the last position of the Oxy profile at time j and at (depthIterations - 1) * dx;



/*
//Handle the interfaces:
*/

// 2.At the 1st interface, calculate how much the interface is going to move forward
        double Ats_1 = tools.UpdateConcentration(PreviousConcentration,OxygenConcentration, sIter_1,  sIter_1 + 1, dx,  dt,  j,
                DiffInOxide1, DiffInOxide2, true, InterfaceConcentration1);//returns the concentration change at s_1
        //System.out.println("Ats_1 is: "+Ats_1);
        double current1 = Ats_1 * dx; //the current (or integrated flux) change at s_1;
        current1 = (current1 > 0 && (!LockS)) ? current1 : 0;//stabilize the profile if the net accumulation of oxygen at the interface is negative, instead of change the interface's position
        //fill up the atomic layer following the interface
        sIter_1 = tools.GetNewInterface(OxygenConcentration, dx, sIter_1, current1, InterfaceConcentration1, 1);
        if(sIter_1 >= sIter_2) { sIter_2 = sIter_1 + 1; }//System.out.println("s1 passed s2");}


// 4.At the 2nd interface, calculate how much the interface is going to move forward.
        if (!IsValid(sIter_1, sIter_2, sIter_3, sIter_4)) return false;
        double Ats_2 = tools.UpdateConcentration(PreviousConcentration,OxygenConcentration, sIter_2,  sIter_2 + 1, dx,  dt,  j,
                DiffInOxide2, DiffInOxide3, true, InterfaceConcentration2);
        double current2 = Ats_2 * dx; //the current (or integrated flux) change at s_1;
        current2 = (current2 > 0 && (!LockS2)) ? current2 : 0;//stabilize the profile if the net accumulation of oxygen at the interface is negative, instead of change the interface's position

        sIter_2 = tools.GetNewInterface(OxygenConcentration, dx, sIter_2, current2, InterfaceConcentration2, 2);
        if(sIter_2 >= sIter_3) { sIter_3 = sIter_2 + 1;}// System.out.println("s2 passed s3 ");}


//6. At the 3rd interface
        if (!IsValid(sIter_1, sIter_2, sIter_3, sIter_4)) return false;
        double Ats_3 = tools.UpdateConcentration(PreviousConcentration,OxygenConcentration, sIter_3,  sIter_3 + 1, dx,  dt,  j,
                DiffInOxide3, DiffInOxide4, true, InterfaceConcentration3);
        double current3 = Ats_3 * dx; //the current (or integrated flux) change at s_1;
        current3 = (current3 > 0 && (!LockS3)) ? current3 : 0;
        sIter_3 = tools.GetNewInterface(OxygenConcentration, dx, sIter_3, current3, InterfaceConcentration3, 3);
        if(sIter_3 >= sIter_4) { sIter_4 = sIter_3 + 1; }//System.out.println("s3 passed s4 ");}



//8. At the 4th(near metal) interface, calculate how much the interface is going to move forward.
        if (!IsValid(sIter_1, sIter_2, sIter_3, sIter_4)) return false;
        double Ats_4 = tools.UpdateConcentration(PreviousConcentration,OxygenConcentration, sIter_4,  sIter_4 + 1, dx,  dt,  j,
                DiffInOxide4, DiffInW, true, InterfaceConcentration4);
        double current4 = Ats_4 * dx;
        current4 = (current4 > 0 && (!LockS4)) ? current4 : 0;//stabilize the profile if the net accumulation of oxygen at the interface is negative, instead of change the interface's position
        sIter_4 = tools.GetNewInterface(OxygenConcentration, dx, sIter_4, current4, InterfaceConcentration4, 4);
        if(sIter_4 >= depthIterations) { sIter_4 = depthIterations; }//System.out.print("s4>end "); }





        sIterArray.set(0,sIter_1);
        sIterArray.set(1,sIter_2);
        sIterArray.set(2,sIter_3);
        sIterArray.set(3,sIter_4);

        return true;

    }

    public boolean IsValid(int sIter_1, int sIter_2, int sIter_3, int sIter_4) {
        if(sIter_1 + 1 >= ConcentrationOfOxigen.depthIterations ||
                sIter_2 + 1>= ConcentrationOfOxigen.depthIterations ||
                sIter_3 + 1>= ConcentrationOfOxigen.depthIterations ||
                sIter_4 + 1>= ConcentrationOfOxigen.depthIterations)
            return false;
        return true;
    }
}
