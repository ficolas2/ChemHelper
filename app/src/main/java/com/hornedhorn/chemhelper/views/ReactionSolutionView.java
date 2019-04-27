package com.hornedhorn.chemhelper.views;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.Utils;
import com.hornedhorn.chemhelper.data.ReactionSolution;
import com.hornedhorn.chemhelper.fragments.ReactionFragment;

public class ReactionSolutionView extends RelativeLayout {

    public final ReactionSolution solution;
    private TextView stoichiometry, formula, name, amount, concentration;
    private LinearLayout background;

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

        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reactionFragment.selectSolution(ReactionSolutionView.this);

                background.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        });
    }

    public void update(boolean wrong){
        @ColorInt int wrongTextColor = getResources().getColor(R.color.wrong);
        @ColorInt int textColor = getResources().getColor(R.color.text);

        stoichiometry.setTextColor(solution.stoichiometricCoefficient<=0 ? wrongTextColor:textColor);
        stoichiometry.setText(Utils.formatDisplayDouble(solution.stoichiometricCoefficient));
        formula.setText( solution.compound.getFormulaString());

        if ( !solution.concentration.isPure())
            concentration.setText( Utils.formatDisplayDouble(solution.concentration.concentrationValue) + " " +
                    solution.concentration.concentrationUnit.str + " ");
        else
            concentration.setText("");

        name.setText( solution.compound.getName() );


        amount.setText( Utils.formatDisplayDouble(solution.amount.SIValue) + " " + solution.amount.unitType.str );

        amount.setTextColor( wrong ? wrongTextColor:textColor );
    }

    public void clearSelection(){
        background.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

}
