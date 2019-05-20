package com.hornedhorn.chemhelper.fragments;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;

import com.hornedhorn.chemhelper.ChemApplication;
import com.hornedhorn.chemhelper.MainActivity;
import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.data.Data;
import com.hornedhorn.chemhelper.utils.Utils;
import com.hornedhorn.chemhelper.cdk.MolecularFormulaManipulator;
import com.hornedhorn.chemhelper.data.Compound;
import com.hornedhorn.chemhelper.views.CompoundAdapter;

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
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_compound_search, container, false);

        searchView = view.findViewById(R.id.compound_search);

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

        if (lastSearch!=null)
            searchCompound(lastSearch);

        return view;
    }

    public void searchCompound( String str ){
        if (str == null)
            return;
        lastSearch = str;
        ArrayList<Compound> searchCompounds;
        if (customCheckbox.isChecked() && includedCheckbox.isChecked())
            searchCompounds = Data.allCompounds;
        else if (customCheckbox.isChecked() && !includedCheckbox.isChecked())
            searchCompounds = Data.customCompounds;
        else if (!customCheckbox.isChecked() && includedCheckbox.isChecked())
            searchCompounds = Data.includedCompounds;
        else {
            compoundList.setAdapter(null);
            return;
        }


        final ArrayList<Map<CompoundAdapter.CompoundAdapterData, String>> compounds = new ArrayList<>();

        String formulaStr = null;
        if (Utils.isFormula(str)) {
            IMolecularFormula molecularFormula = MolecularFormulaManipulator.getMolecularFormula(str, DefaultChemObjectBuilder.getInstance());
            formulaStr = MolecularFormulaManipulator.getString(molecularFormula);
        }

        str = str.toLowerCase();
        for (Compound compound : searchCompounds){
            String containingName = compound.getContainingName(str);
            boolean found = containingName!=null;
            if (formulaStr != null && compound.sameFormula(formulaStr))
                    found = true;

            if (found){
                Map<CompoundAdapter.CompoundAdapterData, String> compoundMap = new HashMap<>();
                compoundMap.put(CompoundAdapter.CompoundAdapterData.NAME, compound.getName());
                compoundMap.put(CompoundAdapter.CompoundAdapterData.FORMULA, compound.getFormulaString());
                compoundMap.put(CompoundAdapter.CompoundAdapterData.ID, Integer.toString(compound.id));
                compoundMap.put(CompoundAdapter.CompoundAdapterData.ORDER,
                        Integer.toString(containingName == null ? 0:containingName.length()));
                compounds.add(compoundMap);
            }
        }

        Collections.sort(compounds, new Comparator<Map<CompoundAdapter.CompoundAdapterData, String>>() {
            @Override
            public int compare(Map<CompoundAdapter.CompoundAdapterData, String> o1, Map<CompoundAdapter.CompoundAdapterData, String> o2) {
                return Integer.signum(Integer.parseInt(o1.get(CompoundAdapter.CompoundAdapterData.ORDER)) -
                        Integer.parseInt(o2.get(CompoundAdapter.CompoundAdapterData.ORDER)));
            }
        });

        CompoundAdapter adapter = new CompoundAdapter(getActivity(), compounds, this);
        compoundList.setAdapter(adapter);

    }

    public void setReceiverFragment(CompoundReciverFragment receiverFragment, boolean setFragment){
        this.receiverFragment = receiverFragment;
        this.setFragment = setFragment;
    }

    public void clickCompoundOptions(final Compound compound, View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.compound_menu, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MainActivity activity = (MainActivity) getActivity();
                final ChemApplication application = (ChemApplication)activity.getApplication();
                switch (item.getItemId()){
                    case R.id.delete:
                        new AlertDialog.Builder(getContext())
                                .setTitle("Delete " + compound.getName())
                                .setMessage("Do you really want to delete the compound?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Data.allCompounds.remove(compound);
                                        Data.customCompounds.remove(compound);
                                        application.saveCompounds();
                                        searchCompound(lastSearch);
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                        return true;
                    case R.id.edit:
                        activity.setEditCompoundFragment(compound);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void clickCompound(Compound compound) {
        MainActivity activity = (MainActivity) getActivity();
        receiverFragment.setCompound(compound);
        if (setFragment)
            activity.setContentFragment(receiverFragment, true);
        else
            activity.back();
    }

    public void selectSuggestion(String dataString) {
        searchView.setQuery(dataString, true);
    }
}
