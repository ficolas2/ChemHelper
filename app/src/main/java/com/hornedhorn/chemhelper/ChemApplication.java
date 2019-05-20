package com.hornedhorn.chemhelper;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.SparseArray;

import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.data.Compound;
import com.hornedhorn.chemhelper.data.Data;
import com.hornedhorn.chemhelper.data.Element;
import com.hornedhorn.chemhelper.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class ChemApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Resources res = getResources();

        InputStream compoundStream = res.openRawResource(R.raw.compounds);

        try{
            JSONArray compoundJsonArr = new JSONArray(Utils.convertStreamToString(compoundStream));

            for (int i = 0; i< compoundJsonArr.length(); i++){
                try{
                    Compound compound = new Compound( compoundJsonArr.getJSONObject(i), Data.allCompounds.size() );
                    Data.allCompounds.add(compound);
                    Data.includedCompounds.add(compound);
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
            JSONArray elementJsonArr = new JSONArray(Utils.convertStreamToString(elementStream));

            for (int i = 0; i< elementJsonArr.length(); i++){
                try{
                    Element element = new Element( elementJsonArr.getJSONObject(i), Data.allCompounds.size() );
                    for (Compound compound : Data.allCompounds)
                        if (compound.name.toLowerCase().equals(element.name.toLowerCase())){
                            element.name = element.name + " (Monoatomic)";
                            break;
                        }
                    Data.elements.put(element.atomicNumber, element);
                    Data.allCompounds.add(element);
                    Data.includedCompounds.add(element);
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
            JSONArray jsonArray = new JSONArray(Utils.convertStreamToString(inputStream));
            inputStream.close();

            for (int i=0; i<jsonArray.length(); i++){
                Compound compound = new Compound(jsonArray.getJSONObject(i), Data.allCompounds.size());
                Data.customCompounds.add(compound);
                Data.allCompounds.add(compound);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void saveCompounds(){

        JSONArray jsonArray = new JSONArray();

        for (Compound compound : Data.customCompounds)
            jsonArray.put(compound.toJSON());

        try {
            FileOutputStream outputStream = openFileOutput("customCompounds.json", Context.MODE_PRIVATE);
            outputStream.write(jsonArray.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Compound createCustomCompound() {
        Compound compound = new Compound(Data.allCompounds.size());
        Data.customCompounds.add(compound);
        Data.allCompounds.add(compound);
        return compound;
    }
}
