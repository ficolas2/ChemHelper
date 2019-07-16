package com.hornedhorn.chemhelper.utils;

import android.widget.EditText;
import android.widget.TextView;

import com.hornedhorn.chemhelper.data.Solution;
import com.hornedhorn.chemhelper.data.Units.Amount;
import com.hornedhorn.chemhelper.data.Units.Concentration;

public class TextUtils {

    public static void setTextWithoutTriggering(EditText editText, String string){
    }

    public static String getConcentrationText(Solution solution){
        return getConcentrationText(solution.getConcentration());
    }

    public static String getConcentrationText(Concentration concentration){
        return Utils.formatDisplayDouble(concentration.concentrationValue) + " " +
                concentration.concentrationUnit.str;
    }

    public static String getAmountText(Solution solution){
        return getAmountText(solution.getAmount());
    }

    public static String getAmountText(Amount amount){
        return ( Utils.formatDisplayDouble(amount.getValue()) + " " + amount.getUnit().str );
    }

}
