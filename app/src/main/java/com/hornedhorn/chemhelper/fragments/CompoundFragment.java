package com.hornedhorn.chemhelper.fragments;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import com.hornedhorn.chemhelper.ChemApplication;
import com.hornedhorn.chemhelper.MainActivity;
import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.Utils;
import com.hornedhorn.chemhelper.cdk.MolecularFormulaManipulator;
import com.hornedhorn.chemhelper.data.Compound;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CompoundFragment extends Fragment {

    private CompoundReciverFragment receiverFragment;
    private boolean setFragment;
    private CheckBox includedCheckbox, customCheckbox;
    private ListView compoundList;
    private String lastSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_compound_search, container, false);

        final SearchView searchView = view.findViewById(R.id.compound_search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(getContext(), MainActivity.class);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        includedCheckbox = view.findViewById(R.id.compound_included);
        customCheckbox = view.findViewById(R.id.compound_custom);

        CompoundButton.OnCheckedChangeListener checkboxListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                searchCompound(lastSearch);
            }
        };

        includedCheckbox.setOnCheckedChangeListener(checkboxListener);
        customCheckbox.setOnCheckedChangeListener(checkboxListener);

        compoundList = view.findViewById(R.id.compound_list);

        view.findViewById(R.id.compound_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setNewCompoundFragment();
            }
        });

        return view;
    }

    public void searchCompound( String str ){
        lastSearch = str;
        final ChemApplication application = (ChemApplication) getActivity().getApplication();
        ArrayList<Compound> searchCompounds;
        if (customCheckbox.isChecked() && includedCheckbox.isChecked())
            searchCompounds = application.allCompounds;
        else if (!customCheckbox.isChecked() && includedCheckbox.isChecked())
            searchCompounds = application.includedCompounds;
        else if (customCheckbox.isChecked() && !includedCheckbox.isChecked())
            searchCompounds = application.customCompounds;
        else {
            compoundList.setAdapter(null);
            return;
        }


        final ArrayList<Map<String, String>> compounds = new ArrayList<>();

        String formulaStr = null;
        if (Utils.isFormula(str)) {
            IMolecularFormula molecularFormula = MolecularFormulaManipulator.getMolecularFormula(str, DefaultChemObjectBuilder.getInstance());
            formulaStr = MolecularFormulaManipulator.getString(molecularFormula);
            Log.e("formula", formulaStr);
        }else{
            Log.e("nor formula", str);
        }

        str = str.toLowerCase();
        for (Compound compound : searchCompounds){
            String name = compound.name.replace("_", " ").toLowerCase();
            boolean found = name.contains(str);
            if (formulaStr != null && compound.sameFormula(formulaStr))
                    found = true;

            if (compound.otherNames != null && !found)
                for (String otherName : compound.otherNames)
                    if (otherName.toLowerCase().contains((str)))
                        found = true;

            if (found){
                Map<String, String> compoundMap = new HashMap<>();
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                compoundMap.put("name", name);
                compoundMap.put("formula", compound.getFormulaString());
                compoundMap.put("id", Integer.toString(compound.id));
                compounds.add(compoundMap);
            }
        }

        Collections.sort(compounds, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                return Integer.signum(o1.get("name").length() - o2.get("name").length());
            }
        });

        String[] fromArray = {"name", "formula"};
        int[] to = {R.id.compound_name, R.id.compound_formula};

        SimpleAdapter adapter = new SimpleAdapter(getContext(), compounds, R.layout.compound_search_layout, fromArray, to);
        compoundList.setAdapter(adapter);

        compoundList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity activity = (MainActivity) getActivity();
                int compoundId = Integer.parseInt(compounds.get(i).get("id"));
                Compound compound = application.allCompounds.get(compoundId);

                receiverFragment.setCompound(compound);
                if (setFragment)
                    activity.setContentFragment(receiverFragment, true);
                else
                    activity.back();
            }
        });

    }

    public void setReceiverFragment(CompoundReciverFragment receiverFragment, boolean setFragment){
        this.receiverFragment = receiverFragment;
        this.setFragment = setFragment;
    }
}
