package org.iogp.gigs;

public class ParsingUtils {

    
    public static double parseDouble(String stringValue) {
        if (stringValue.equals("Null")) {
            return Double.NaN;
        }
        return Double.parseDouble(stringValue);
    }
    
    public static int parseInt(String stringValue) {
        if (stringValue.equals("Null")) {
            return -Integer.MAX_VALUE;
        }
        return Integer.parseInt(stringValue);
    }
    
    public static String parseCode(String stringValue) {
        if (stringValue.equals("Null")) {
            return null;
        }
        return stringValue;
    }
}
