package com.hornedhorn.chemhelper.views;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.Utils;
import com.hornedhorn.chemhelper.data.ReactionSolution;
import com.hornedhorn.chemhelper.data.Units.Concentration;
import com.hornedhorn.chemhelper.fragments.ReactionFragment;
import com.hornedhorn.chemhelper.utils.InputFilterMinMax;

public class SolutionEditor extends RelativeLayout {

    private ReactionFragment reactionFragment;

    private final TextView name, formula;
    private final EditText stoichiometry;
    private final EditText concentrationEditText;
    private final EditText amount;
    private final Spinner concentrationUnit;
    private final Spinner amountUnit;
    private final EditText density;
    private final View densityLayout;

    private final EditText pureDensity;
    private final View pureDensityLayout;

    public SolutionEditor(Context context) {
        this(context, null);
    }
    public SolutionEditor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public SolutionEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.solution_editor, this);

        name = findViewById(R.id.solution_name);
        formula = findViewById(R.id.solution_formula);

        // Stoichiometry
        stoichiometry = findViewById(R.id.solution_stoichiometry);
        stoichiometry.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                ReactionSolutionView currentSolutionView = reactionFragment.getCurrentSolutionView();
                if ( currentSolutionView == null )
                    return;
                currentSolutionView.solution.stoichiometricCoefficient = Utils.parseDouble(s.toString());
                reactionFragment.updateSolutionViews();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override  public void afterTextChanged(Editable s) {}
        });

        //Concentration (value)
        concentrationEditText = findViewById(R.id.solution_concentration);
        concentrationEditText.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                ReactionSolutionView currentSolutionView = reactionFragment.getCurrentSolutionView();
                if ( currentSolutionView == null )
                    return;
                currentSolutionView.solution.concentration.concentrationValue = Utils.parseDouble(s.toString());
                reactionFragment.updateSolutionViews();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override  public void afterTextChanged(Editable s) {}
        });

        //Amount (value)
        amount = findViewById(R.id.solution_amount);
        amount.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                ReactionSolutionView currentSolutionView = reactionFragment.getCurrentSolutionView();
                if ( currentSolutionView == null  )
                    return;
                String str = s.toString();
                currentSolutionView.solution.amount.SIValue = Utils.parseDouble(s.toString());
                reactionFragment.updateSolutionViews();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override  public void afterTextChanged(Editable s) {}
        });

        amountUnit = findViewById(R.id.solution_amount_unit);
        amountUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReactionSolutionView currentSolutionView = reactionFragment.getCurrentSolutionView();
                if ( currentSolutionView == null )
                    return;
                currentSolutionView.solution.amount.setUnitType((String)amountUnit.getItemAtPosition(position));
                reactionFragment.updateSolutionViews();
                updateDensity(currentSolutionView.solution);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        concentrationUnit = findViewById(R.id.solution_concentration_unit);
        concentrationUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReactionSolutionView currentSolutionView = reactionFragment.getCurrentSolutionView();
                if ( currentSolutionView == null )
                    return;

                currentSolutionView.solution.concentration.setConcentrationUnit((String)concentrationUnit.getItemAtPosition(position));
                reactionFragment.updateSolutionViews();

                Concentration.ConcentrationUnit concentrationUnit = currentSolutionView.solution.concentration.concentrationUnit;
                concentrationEditText.setVisibility(
                        concentrationUnit == Concentration.ConcentrationUnit.PURE ?  View.GONE:View.VISIBLE);
                if ( concentrationUnit.percent )
                    concentrationEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 100)});
                else
                    concentrationEditText.setFilters(new InputFilter[]{});
                updateDensity(currentSolutionView.solution);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        density = findViewById(R.id.solution_density);
        density.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ReactionSolutionView currentSolutionView = reactionFragment.getCurrentSolutionView();
                if ( currentSolutionView == null )
                    return;
                currentSolutionView.solution.setDensity(Utils.parseDouble(s.toString()));
                reactionFragment.updateSolutionViews();
            }
        });

        densityLayout = findViewById(R.id.solution_density_layout);

        pureDensity = findViewById(R.id.solution_pure_density);
        pureDensity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ReactionSolutionView currentSolutionView = reactionFragment.getCurrentSolutionView();
                if ( currentSolutionView == null )
                    return;
                currentSolutionView.solution.setPureDensity(Utils.parseDouble(s.toString()));
                reactionFragment.updateSolutionViews();
            }
        });

        pureDensityLayout = findViewById(R.id.solution_pure_density_layout);
    }

    public void update(ReactionSolution solution){
        setVisibility(View.VISIBLE);

        name.setText(solution.compound.getName());
        formula.setText(solution.compound.getFormulaString());

        stoichiometry.setText(Utils.formatInputDouble(solution.stoichiometricCoefficient));


        concentrationEditText.setVisibility(
                solution.concentration.concentrationUnit == Concentration.ConcentrationUnit.PURE ? View.GONE:View.VISIBLE);
        concentrationEditText.setText(Utils.formatInputDouble(solution.concentration.concentrationValue));

        amount.setText(Utils.formatInputDouble(solution.amount.SIValue));

        String[] concentrationArr = getResources().getStringArray(R.array.concentration_units);
        for (int i = 0; i<concentrationArr.length; i++){
            if (concentrationArr[i].equals( solution.concentration.concentrationUnit.str )) {
                concentrationUnit.setSelection(i);
                break;
            }
        }

        String[] amountArr = getResources().getStringArray(R.array.amount_units);
        for (int i = 0; i<amountArr.length; i++){
            if (amountArr[i].equals( solution.amount.unitType.str )) {
                amountUnit.setSelection(i);
                break;
            }
        }

        updateDensity(solution);

    }

    private void updateDensity(ReactionSolution solution){
        pureDensityLayout.setVisibility(solution.needsPureDensity() ? VISIBLE:GONE);
        pureDensity.setText( Utils.formatInputDouble( solution.getPureDensity() ));

        densityLayout.setVisibility(solution.needsDensity() ? VISIBLE:GONE);
        density.setText( Utils.formatInputDouble( solution.getDensity() ));
    }

    public void setReactionFragment(ReactionFragment reactionFragment) {
        this.reactionFragment = reactionFragment;
    }
}
