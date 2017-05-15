package ultimate.fit.ultimatefit.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Pham on 15/5/17.
 */

public class CalculationMethods {
    public static double weightMaxForOneRep(double weight, double reps){
        double oneRepMax = weight * reps * 0.0333 + weight;
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        twoDForm.setRoundingMode(RoundingMode.DOWN);
        return Double.valueOf(twoDForm.format(oneRepMax));
    }
}
