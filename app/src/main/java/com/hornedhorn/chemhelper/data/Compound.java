package com.hornedhorn.chemhelper.data;

import android.util.Log;
import android.util.SparseArray;

import com.hornedhorn.chemhelper.Utils;
import com.hornedhorn.chemhelper.cdk.MolecularFormulaManipulator;

import org.apache.commons.math3.optim.InitialGuess;
import org.json.JSONException;
import org.json.JSONObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;

public class Compound {

    public final int id;
    public String name;
    private String formulaString;
    private IMolecularFormula formula;
    public double[] meltingPoint;
    public double[] boilingPoint;
    public Double logP;
    public String[] otherNames;
    public String molecularFormulaString;
    public final boolean custom;

    //solubility

    //heat capacity
    //density
    //Decomposition
    //Hydratation
    //pKa
    //Other names

    public Compound( JSONObject compoundJSON, int id ){
        this.id = id;
        try{
            name = compoundJSON.getString("Name");
            formulaString = compoundJSON.getString("Formula");

            formula = MolecularFormulaManipulator.getMolecularFormula(formulaString, DefaultChemObjectBuilder.getInstance());
            molecularFormulaString = MolecularFormulaManipulator.getString(formula);

            if (compoundJSON.has("Melting point"))
                meltingPoint = Utils.JSONArrayToDoubleArray(compoundJSON.get("Melting point"));
            if (compoundJSON.has("Boiling point"))
                boilingPoint = Utils.JSONArrayToDoubleArray(compoundJSON.get("Boiling point"));

            if (compoundJSON.has("LogP"))
                logP = compoundJSON.getDouble("LogP");

            if (compoundJSON.has("Other names"))
                otherNames = Utils.JSONArrayToStringArray(compoundJSON.getJSONArray("Other names"));
        }catch (JSONException e){
            Log.e("JSON", e.getMessage());
        }

        custom = false;
    }

    public Compound(int id) {
        this.id = id;
        custom = true;
    }

    public String getFormulaString(){
        return formulaString;
    }

    public void setFormulaString(String formulaString){
        this.formulaString = formulaString;
        formula = MolecularFormulaManipulator.getMolecularFormula(formulaString, DefaultChemObjectBuilder.getInstance());
        molecularFormulaString = MolecularFormulaManipulator.getString(formula);
    }

    public double getMolecularWeight(){
        return MolecularFormulaManipulator.getMajorIsotopeMass(formula);
    }

    public String getMeltingPointString(){
        if (meltingPoint == null)
            return "Unknown";
        String str = Double.toString(meltingPoint[0]);
        if (meltingPoint.length>1)
            str += " - " + meltingPoint[1];
        str += " ºC";
        return str;
    }

    public String getBoilingPointString(){
        if (boilingPoint == null)
            return "Unknown";
        String str = Double.toString(boilingPoint[0]);
        if (boilingPoint.length>1)
            str += " - " + boilingPoint[1];
        return str;
    }

    public String getName(){
        String str = name.replace("_", " ");
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public boolean sameFormula(IMolecularFormula otherFormula){
        return sameFormula(MolecularFormulaManipulator.getString(otherFormula));
    }

    public boolean sameFormula(String str){
        return molecularFormulaString.equals(str);
    }

    public JSONObject toJSON(){
        JSONObject object = new JSONObject();
        try {
            object.put("Name", name);
            object.put("Formula", formulaString);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return object;
    }

    public SparseArray<Integer> getElements(){
        SparseArray<Integer> elements = new SparseArray<>();
        for (IElement element : MolecularFormulaManipulator.elements(formula))
            elements.put(element.getAtomicNumber(), MolecularFormulaManipulator.getElementCount(formula, element));

        return elements;
    }

}
