import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.ArrayList;

public class FittingCurve {



    public double[] fit(final ArrayList<Double> x, final ArrayList<Double> y) {
        // Collect data.
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for(int i = 0; i < x.size(); i++) {
            obs.add(x.get(i), y.get(i));
        }
        // Instantiate a third-degree polynomial fitter.
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);

// Retrieve fitted parameters (coefficients of the polynomial function).
        double[] coeff = {0, 0};
        try {
            coeff = fitter.fit(obs.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return coeff;
    }

    public static boolean IsValid(double target, double tolerance, double calculation) {
        double difference = Math.abs(target - calculation) / target;
        if( difference < tolerance ) {
            System.out.println("Math.abs(target - calculation)/target: " + difference);
            return true;
        }
        return false;
    }
}
