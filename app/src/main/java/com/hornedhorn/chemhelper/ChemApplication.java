package com.hornedhorn.chemhelper;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.SparseArray;

import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.data.Compound;
import com.hornedhorn.chemhelper.data.Element;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class ChemApplication extends Application {

    public final SparseArray<Element> elements = new SparseArray<>();
    public final ArrayList<Compound> allCompounds = new ArrayList<>();
    public final ArrayList<Compound> includedCompounds = new ArrayList<>();
    public final ArrayList<Compound> customCompounds = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        Resources res = getResources();

        InputStream compoundStream = res.openRawResource(R.raw.compounds);

        try{
            JSONArray compoundJsonArr = new JSONArray(convertStreamToString(compoundStream));

            for (int i = 0; i< compoundJsonArr.length(); i++){
                try{
                    Compound compound = new Compound( compoundJsonArr.getJSONObject(i), allCompounds.size() );
                    allCompounds.add(compound);
                    includedCompounds.add(compound);
                }catch(JSONException e)
                {
                    Log.e("ChemApplication", "Error while reading compound json.");
                }
            }
        }catch(JSONException e)
        {
            Log.e("ChemApplication", "Error while reading compounds json array.");
        }

        InputStream elementStream = res.openRawResource(R.raw.elements);

        try{
            JSONArray elementJsonArr = new JSONArray(convertStreamToString(elementStream));

            for (int i = 0; i< elementJsonArr.length(); i++){
                try{
                    Element element = new Element( elementJsonArr.getJSONObject(i), allCompounds.size() );
                    for (Compound compound : allCompounds)
                        if (compound.name.toLowerCase().equals(element.name.toLowerCase())){
                            element.name = element.name + " (Monoatomic)";
                            break;
                        }
                    elements.put(element.atomicNumber, element);
                    allCompounds.add(element);
                    includedCompounds.add(element);
                }catch(JSONException e)
                {
                    Log.e("ChemApplication", "Error while reading element json.");
                }
            }
        }catch(JSONException e)
        {
            Log.e("ChemApplication", "Error while reading elements json array.");
        }

        try{
            FileInputStream inputStream = openFileInput("customCompounds.json");
            JSONArray jsonArray = new JSONArray(convertStreamToString(inputStream));
            inputStream.close();

            for (int i=0; i<jsonArray.length(); i++){
                Compound compound = new Compound(jsonArray.getJSONObject(i), allCompounds.size());
                customCompounds.add(compound);
                allCompounds.add(compound);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String string = s.hasNext() ? s.next() : "";
        s.close();
        return string;
    }

    public void addCustomCompound(Compound compound){
        customCompounds.add(compound);
        allCompounds.add(compound);

        saveCompounds();
    }

    private void saveCompounds(){

        JSONArray jsonArray = new JSONArray();

        for (Compound compound : customCompounds)
            jsonArray.put(compound.toJSON());

        try {
            FileOutputStream outputStream = openFileOutput("customCompounds.json", Context.MODE_PRIVATE);
            outputStream.write(jsonArray.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
