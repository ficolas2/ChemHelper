package com.hornedhorn.chemhelper.views;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.utils.TextUtils;
import com.hornedhorn.chemhelper.utils.Utils;
import com.hornedhorn.chemhelper.data.ReactionSolution;
import com.hornedhorn.chemhelper.fragments.ReactionFragment;

public class ReactionSolutionView extends RelativeLayout {

    public final ReactionSolution solution;
    private TextView stoichiometry, formula, name, amount, concentration;
    private LinearLayout background;

    @ColorInt private int textColor, calculatedTextColor;

    public ReactionSolutionView(Context context, ReactionSolution soln, final ReactionFragment reactionFragment){
        super(context);

        this.solution = soln;

        inflate(context, R.layout.reaction_solution, this);
        stoichiometry = findViewById(R.id.reaction_solution_stoi_coeff);
        formula = findViewById(R.id.reaction_solution_formula);
        name = findViewById(R.id.reaction_solution_name);
        amount = findViewById(R.id.reaction_solution_amount);
        background = findViewById(R.id.reaction_solution_background);
        concentration = findViewById(R.id.reaction_solution_concentration);


        textColor = amount.getCurrentTextColor();
        calculatedTextColor = getResources().getColor(R.color.calculated);

        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reactionFragment.selectSolution(ReactionSolutionView.this);

                background.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        });
    }

    public void update(){
        stoichiometry.setText(Utils.formatDisplayDouble(solution.stoichiometricCoefficient));

        SpannableStringBuilder formulaStr = new SpannableStringBuilder( solution.compound.getFormulaString() );
        Utils.addSubscripts(formulaStr);
        formula.setText( formulaStr, TextView.BufferType.SPANNABLE );

        if ( !solution.isPure())
            concentration.setText( TextUtils.getConcentrationText(solution) );
        else
            concentration.setText("");
        concentration.setTextColor( solution.isConcentrationCalculated() ? calculatedTextColor : textColor);

        name.setText( solution.compound.getName() );

        String amountText = TextUtils.getAmountText(solution);

        if (solution.excess != 0)
            amountText += " (" + Utils.formatDisplayDouble(solution.excess) + "% excess)";

        amount.setText( amountText );
        amount.setTextColor( solution.isAmountCalculated() ? calculatedTextColor : textColor);
    }

    public void clearSelection(){
        background.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    public void revertSolution(){
        solution.revert();
    }



}
