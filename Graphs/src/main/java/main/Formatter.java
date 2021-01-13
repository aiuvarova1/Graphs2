package main;

import java.text.DecimalFormat;
import java.util.Locale;

public class Formatter {
    private static DecimalFormat formatter = (DecimalFormat)DecimalFormat.getInstance(Locale.US);
    static{
        formatter.setMaximumFractionDigits(5);
        formatter.setMinimumFractionDigits(0);
    }

    public static String format (double val){
        String res =  formatter.format(val);
        return res.equals("-0") ? "0" : res;
    }
}
