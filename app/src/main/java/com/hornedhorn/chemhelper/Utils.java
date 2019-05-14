package com.hornedhorn.chemhelper;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.util.Log;

import com.hornedhorn.chemhelper.data.Element;
import com.hornedhorn.chemhelper.data.ReactionSolution;
import com.hornedhorn.chemhelper.data.Units.Amount;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final int significantDigits = 6;
    private static final Pattern formulaPattern = Pattern.compile("\\(?([A-Z][a-z]?)[0-9]*\\)?[0-9]*");
    private static final Pattern subscriptPattern = Pattern.compile("(?<=[A-Z]|[a-z])[0-9]+");

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

    public static boolean isFormula(String str, ChemApplication application){
        if (str.isEmpty())
            return false;
        Matcher m = formulaPattern.matcher(str);

        regexLoop:
        while (m.find()){
            String symbol = m.group(1);
            if (symbol.isEmpty())
                continue;

            for (int i = 0; i<application.elements.size(); i++) {
                Element element = application.elements.valueAt(i);
                if (element.molecularFormulaString.equals(symbol))
                    continue regexLoop;
            }
            return false;
        }

        if (!m.replaceAll("").isEmpty())
            return false;

        return true;
    }

 /*   public static void addSubscripts(SpannableStringBuilder builder) {
        Matcher m = subscriptPattern.matcher(builder.toString());
        while (m.find()){
            builder.setSpan(new SubscriptSpan(), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new RelativeSizeSpan(0.65f), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }*/

    public static void addSubscripts(Editable editable) {
        Matcher m = subscriptPattern.matcher(editable.toString());
        while (m.find()){
                editable.setSpan(new SubscriptSpan(), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                editable.setSpan(new RelativeSizeSpan(0.65f), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public static double getEquivalent(ArrayList<ReactionSolution> solutions){
        double equivalent = 0;
        for (ReactionSolution solution : solutions){
            if (solution.stoichiometricCoefficient==0)
                return -1;
            double solEq = solution.getEquivalent();

            if (solEq != 0) {
                if (equivalent != 0 && !Utils.epsilonEqual(solEq, equivalent, 1. / 1000))
                    return -1;
                equivalent = solEq;
            }
        }
        return equivalent;
    }

    public static int getDenominator(double number) {
        double num1 = 1; double num2 = 0;
        double den1 = 0; double den2 = 1;
        double b = number;
        do {
            double a = Math.floor(b);
            double aux = num1; num1 = a*num1+num2; num2 = aux;
            aux = den1; den1 = a*den1+den2; den2 = aux;
            b = 1/(b-a);

        }while (!epsilonEqual(number, num1/den1, 1E-6));

        return (int)Math.round(den1);
    }

    public static void errorBox(String message, Context context){
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, null)
                .show();
    }
}
