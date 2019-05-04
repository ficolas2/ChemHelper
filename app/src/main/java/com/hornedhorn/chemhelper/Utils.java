package com.hornedhorn.chemhelper;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.regex.Pattern;

public class Utils {

    private static final int significantDigits = 6;
    private static final Pattern formulaPattern = Pattern.compile("(\\(?([A-Z][a-z]?)[0-9]*\\)?)*");

    public static String formatInputDouble(double value){
        if ( value <= 0 )
            return "";
        int offset = (int)Math.log10(value) + 1;
        offset = Math.min(significantDigits, offset);

        String str = String.format( "%." + (significantDigits - offset) + "f", value );
        str = str.replaceAll(",*0+$", "");
        return str;
    }

    public static String formatDisplayDouble(double value){
        if ( value <= 0 )
            return "?";
        String str = formatInputDouble(value);
        int exponent = (int) Math.log10(value);
        if ( exponent >= significantDigits)
            str = str.charAt(0) + "," + str.substring(1, significantDigits) + "e" + exponent;
        return str;
    }

    public static boolean epsilonEqual(double d1, double d2, double epsilon) {
        double d = d1 / d2;
        return (Math.abs(d - 1.0) < epsilon);
    }

    public static double parseDouble(String str){
        if (str.contains("?"))
            return -1;
        return str.isEmpty() ? 0:Double.parseDouble( str.replace(",", ".") );
    }

    public static String[] JSONArrayToStringArray(JSONArray json) throws  JSONException{
        String[] arr = new String[json.length()];
        for (int i = 0; i<json.length();i++)
            arr[i] = json.getString(i);
        return arr;
    }

    public static double[] JSONArrayToDoubleArray(Object object) throws JSONException{
        double[] arr;
        if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            arr = new double[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++)
                arr[i] = jsonArray.getDouble(i);
        }else{
            if (object instanceof String)
                arr = new double[]{Double.parseDouble((String) object)};
            else if (object instanceof Integer)
                arr = new double[]{(Integer)object};
            else
                arr = new double[]{(double)object};
        }
        return arr;
    }

    public static boolean isFormula(String str){
        return formulaPattern.matcher(str).matches();
    }

    public static boolean isAlphanumberic(char c){
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= '0' && c <= '9');
    }

    public static void addSubscripts(SpannableStringBuilder builder) {
        for (int i=1; i<builder.length(); i++){
            char c = builder.charAt(i);
            char prevC = builder.charAt(i-1);
            if (Character.isDigit(c) && (isAlphanumberic(prevC) || prevC == ')' ||prevC == ']')){
                builder.setSpan(new SubscriptSpan(), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new RelativeSizeSpan(0.65f), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
}
