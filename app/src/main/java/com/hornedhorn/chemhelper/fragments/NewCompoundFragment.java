package com.hornedhorn.chemhelper.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hornedhorn.chemhelper.ChemApplication;
import com.hornedhorn.chemhelper.MainActivity;
import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.data.Compound;

public class NewCompoundFragment extends CompoundReciverFragment {

    private Compound compound;
    private EditText name, formula;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_compound_creation, container, false);

        view.findViewById(R.id.new_compound_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCompound();
            }
        });

        name = view.findViewById(R.id.new_compound_name);
        formula = view.findViewById(R.id.new_compound_formula);

        if (compound!=null){
            name.setText(compound.getName());
            formula.setText(compound.getFormulaString());
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setCompound(Compound compound){
        this.compound = compound;
    }

    private void createCompound(){
        MainActivity activity = (MainActivity) getActivity();
        ChemApplication application = (ChemApplication)activity.getApplication();
        String nameStr = name.getText().toString();
        String formulaStr = formula.getText().toString();
        if (compound == null)
            compound = application.createCustomCompound();
        compound.name = nameStr;
        compound.setFormulaString(formulaStr);
        application.saveCompounds();
        activity.back();
    }
}
