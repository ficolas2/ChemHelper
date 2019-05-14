package com.hornedhorn.chemhelper.fragments;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hornedhorn.chemhelper.ChemApplication;
import com.hornedhorn.chemhelper.MainActivity;
import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.Utils;
import com.hornedhorn.chemhelper.data.Compound;

public class NewCompoundFragment extends CompoundReciverFragment {

    private Compound compound;
    private EditText name, formula;

    @ColorInt private int textColor;
    @ColorInt private int wrongTextColor;

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

        textColor = formula.getCurrentTextColor();
        wrongTextColor = getResources().getColor(R.color.wrong);

        formula.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                formula.removeTextChangedListener(this);

                int selectionStart = formula.getSelectionStart();

                s.clearSpans();
                if (s.length()>0)
                    Utils.addSubscripts(s);
                formula.setText(s);

                formula.setSelection(selectionStart);
                formula.addTextChangedListener(this);

                formula.setTextColor(Utils.isFormula(s.toString(), (ChemApplication) getActivity().getApplication()) ?
                        textColor:wrongTextColor);
            }


        });

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

        if (nameStr.isEmpty()){
            Utils.errorBox("Name can't be empty.", getContext());
            return;
        }
        if (formulaStr.isEmpty()){
            Utils.errorBox("Formula can't be empty.", getContext());
            return;
        }
        if (!Utils.isFormula(formulaStr, (ChemApplication) getActivity().getApplication())) {
            Utils.errorBox("Invalid formula.", getContext());
            return;
        }

        if (compound == null)
            compound = application.createCustomCompound();
        compound.name = nameStr;
        compound.setFormulaString(formulaStr);
        application.saveCompounds();
        activity.back();
    }
}
