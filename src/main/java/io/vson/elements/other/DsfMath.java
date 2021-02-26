package io.vson.elements.other;

import io.vson.VsonValue;
import io.vson.elements.VsonNumber;
import io.vson.other.IVsonProvider;

public class DsfMath implements IVsonProvider {

    public String getName() {
        return "math";
    }

    public String getDescription() {
        return "support for Inf/inf, -Inf/-inf, Nan/naN and -0";
    }

    public VsonValue parse(String text) {
        switch (text) {
            case "+inf":
            case "inf":
            case "+Inf":
            case "Inf":
                return new VsonNumber(Double.POSITIVE_INFINITY);
            case "-inf":
            case "-Inf":
                return new VsonNumber(Double.NEGATIVE_INFINITY);
            case "nan":
            case "NaN":
                return new VsonNumber(Double.NaN);
            default:
                return null;
        }
    }

    public String stringify(VsonValue value) {
        if (!value.isNumber()) return null;
        double val = value.asDouble();
        if (val == Double.POSITIVE_INFINITY) return "Inf";
        else if (val == Double.NEGATIVE_INFINITY) return "-Inf";
        else if (Double.isNaN(val)) return "NaN";
        else if (val == 0.0 && 1 / val == Double.NEGATIVE_INFINITY) return "-0";
        else return null;
    }
}
