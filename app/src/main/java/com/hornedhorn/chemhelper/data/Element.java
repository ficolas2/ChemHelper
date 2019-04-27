package com.hornedhorn.chemhelper.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Element extends Compound{

    public final int atomicNumber;
    public final double atomicWeight;

    public final int group;
    public final int period;
    public final String category;


    public Element(JSONObject element, int id){
        super(element, id);

        String name = "Error";
        int atomicNumber = 0;
        double atomicWeight = 0;
        int group = 0;
        int period = 0;
        String category = "unknown_chemical_properties";

        try {
            name = element.getString("Name");
            atomicNumber = element.getInt("Atomic number");
            atomicWeight = element.getDouble("Atomic weight");
            if (element.has("Group"))
                group = element.getInt("Group");
            period = element.getInt("Period");
            category = element.getString("Category").replace(" ", "_");
        }catch( JSONException e ){
            Log.e("Element", "Error while reading element.\n" + element.toString());
            Log.e("Element", e.getMessage());
        }

        this.name = name;
        this.atomicNumber = atomicNumber;
        this.atomicWeight = atomicWeight;
        this.group = group;
        this.period = period;
        this.category = category;

    }

    public double getMolecularWeight(){
        return atomicWeight;
    }
}
