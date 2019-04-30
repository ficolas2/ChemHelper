package com.hornedhorn.chemhelper.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hornedhorn.chemhelper.MainActivity;
import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.Utils;
import com.hornedhorn.chemhelper.data.Compound;
import com.hornedhorn.chemhelper.data.ReactionSolution;
import com.hornedhorn.chemhelper.data.Units.Amount;
import com.hornedhorn.chemhelper.utils.InputFilterMinMax;
import com.hornedhorn.chemhelper.views.ReactionSolutionView;
import com.hornedhorn.chemhelper.views.SolutionEditor;

import java.util.ArrayList;

public class ReactionFragment extends CompoundReciverFragment {

    boolean receivingReactant;

    private ArrayList<ReactionSolution> reactants = new ArrayList<>();
    private ArrayList<ReactionSolution> products = new ArrayList<>();
    private ArrayList<ReactionSolution> solutions = new ArrayList<>();

    private ArrayList<ReactionSolutionView> reactantViews = new ArrayList<>();
    private ArrayList<ReactionSolutionView> productViews = new ArrayList<>();
    private ArrayList<ReactionSolutionView> reactantSolutionViews = new ArrayList<>();

    private SolutionEditor solutionEditor;

    private LinearLayout reactantsLayout;
    private LinearLayout productsLayout;

    private TextView yieldView;

    private ReactionSolutionView currentSolutionView;

    private TextView reactionText;

    private Amount auxAmount = new Amount();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_reaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final MainActivity activity = (MainActivity)getActivity();

        reactionText = view.findViewById(R.id.reaction_text);

        view.findViewById(R.id.reaction_add_reactant).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setCompoundSearchFragment(ReactionFragment.this);
                receivingReactant = true;
            }
        } );

        view.findViewById(R.id.reaction_add_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setCompoundSearchFragment(ReactionFragment.this);
                receivingReactant = false;
            }
        } );

        view.findViewById(R.id.reaction_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Clear reaction")
                        .setMessage("Do you really want to clear the reaction?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                reactants.clear();
                                products.clear();
                                addSolutionViews();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

        view.findViewById(R.id.reaction_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });


        reactantsLayout = view.findViewById(R.id.reaction_reactants);
        productsLayout = view.findViewById(R.id.reaction_products);

        final HorizontalScrollView scroll = view.findViewById(R.id.reaction_scroll);
        ViewTreeObserver observer = scroll.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        View view = ReactionFragment.this.getView();
                        if (view == null)
                            return;
                        view.findViewById(R.id.reaction_linear_layout).setMinimumWidth(scroll.getWidth());
                    }
                });

        solutionEditor = view.findViewById(R.id.solution_editor);
        solutionEditor.setReactionFragment( this );

        yieldView = view.findViewById(R.id.reaction_yield);
        yieldView.setFilters(new InputFilter[]{new InputFilterMinMax(0, 100)});
        yieldView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateSolutionViews();
                }

            });

        addSolutionViews();

        super.onViewCreated(view, savedInstanceState);
    }

    private double getEquivalent(ReactionSolution solution){
        if (solution.concentration.concentrationValue <= 0 || solution.amount.SIValue <= 0 )
            return 0;
        return solution.getSolute().getSI(Amount.UnitType.MOLE) / solution.stoichiometricCoefficient;
    }

    private double getEquivalent(ArrayList<ReactionSolution> solutions){
        double equivalent = 0;
        for (ReactionSolution solution : solutions){
            if (solution.stoichiometricCoefficient==0)
                return -1;
            double solEq = getEquivalent(solution);

            if (solEq != 0) {
                if (equivalent != 0 && !Utils.epsilonEqual(solEq, equivalent, 1. / 1000))
                    return -1;
                equivalent = solEq;
            }
        }
        return equivalent;
    }

    private void addSolutionViews(){
        reactantsLayout.removeAllViews();
        productsLayout.removeAllViews();
        if ( reactants.size() == 0 && products.size() == 0 ){
            clearViewEmpty();
            reactionText.setVisibility(View.GONE);
            return;
        }

        for (ReactionSolution solution : reactants){
            ReactionSolutionView solutionView = new ReactionSolutionView(getContext(), solution, this);
            reactantsLayout.addView(solutionView);
            reactantViews.add(solutionView);
            reactantSolutionViews.add(solutionView);

            ((LinearLayout.LayoutParams)solutionView.getLayoutParams()).gravity = Gravity.CENTER;
        }

        for (ReactionSolution solution : products){
            ReactionSolutionView solutionView = new ReactionSolutionView(getContext(), solution, this);
            productsLayout.addView(solutionView);
            productViews.add(solutionView);
            reactantSolutionViews.add(solutionView);

            ((LinearLayout.LayoutParams)solutionView.getLayoutParams()).gravity = Gravity.CENTER;
        }

        updateSolutionViews();
    }

    public void updateReactionText(){
        SpannableStringBuilder reactionStr = new SpannableStringBuilder();

        boolean first = true;

        for (ReactionSolution solution : reactants){
            appendCompound(first, reactionStr, solution);
            first = false;
        }

        reactionStr.append(" -> ");

        first = true;

        for (ReactionSolution solution : products){
            appendCompound(first, reactionStr, solution);
            first = false;
        }

        Utils.addSubscripts(reactionStr);
        reactionText.setText(reactionStr, TextView.BufferType.SPANNABLE);
    }

    private void appendCompound(boolean first, SpannableStringBuilder reactionStr, ReactionSolution solution ){
        if (!first)
            reactionStr.append(" + ");
        if (solution.stoichiometricCoefficient != 1)
            reactionStr.append(Utils.formatDisplayDouble(solution.stoichiometricCoefficient));
        String formulaStr = solution.compound.getFormulaString();
        reactionStr.append(formulaStr);
    }

    public void updateSolutionViews(){
        @ColorInt int wrongTextColor = getResources().getColor(R.color.wrong);
        @ColorInt int textColor = getResources().getColor(R.color.text);
        double reactantsEq = getEquivalent(reactants);
        double productsEq = getEquivalent(products);
        double yield = getYield();

        boolean wrongReactants = reactantsEq<0;
        boolean wrongProducts = productsEq<0;

        yieldView.setTextColor(textColor);
        if (!wrongReactants && !wrongProducts &&
                reactantsEq != 0 && productsEq != 0 &&
                !Utils.epsilonEqual(reactantsEq, productsEq / yield, 1./1000)){
            yieldView.setTextColor( wrongTextColor );
            wrongProducts = wrongReactants = true;
        }

        for (ReactionSolutionView view : reactantViews){
            view.update(wrongReactants ? wrongTextColor : textColor);
        }
        for (ReactionSolutionView view : productViews)
            view.update(wrongProducts ? wrongTextColor : textColor);

        updateReactionText();
    }

    private void clearViewEmpty(){
        View view = getView();
        LinearLayout linearLayout = view.findViewById(R.id.reaction_linear_layout);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        params.gravity = Gravity.CENTER;

        view.findViewById(R.id.reaction_clear).setVisibility(View.GONE);
        solutionEditor.setVisibility(View.GONE);
    }

    @Override
    public void setCompound(Compound compound) {
        ReactionSolution solution = new ReactionSolution(compound);
        solutions.add(solution);
        if (receivingReactant) {
            reactants.add(solution);
        }else{
            products.add(solution);
        }
    }

    public void selectSolution(ReactionSolutionView solutionView){
        this.currentSolutionView = solutionView;
        clearSolutionSelection();
        solutionEditor.update(solutionView.solution);
    }

    public void clearSolutionSelection(){
        for (ReactionSolutionView view : reactantSolutionViews){
            view.clearSelection();
        }
    }

    private void calculationError(String error){
        new AlertDialog.Builder(getContext())
                .setTitle("Error calculating")
                .setMessage(error)
                .setPositiveButton(android.R.string.yes, null)
                .show();
    }

    private void calculate(){
        double yield = getYield();

        double reactantsEquivalent = getEquivalent(reactants);
        double productsEquivalent = getEquivalent(products);

        if (productsEquivalent<0) {
            calculationError("Your products amounts don't match. Tip: leave only one amount.");
            return;
        }
        if (reactantsEquivalent<0) {
            calculationError("Your reactants amounts don't match. Tip: leave only one amount.");
            return;
        }

        if ( yield == 0 ){
            if (productsEquivalent!= 0 && reactantsEquivalent != 0){
                yield = productsEquivalent / reactantsEquivalent;
                yieldView.setText(Utils.formatInputDouble(yield));
            }else{
                calculationError("Cannot calculate yield without at least one product and reactant amount.");
                return;
            }
        }

        if ( productsEquivalent != 0 && reactantsEquivalent != 0  &&
                        !Utils.epsilonEqual(reactantsEquivalent, productsEquivalent / yield, 1./1000)){
            calculationError("Your reactants amounts don't match with your products. Tip: leave only one amount.");
            return;
        }

        if ( reactantsEquivalent == 0 && productsEquivalent == 0) {
            calculationError("Not enough data");
            return;
        }

        Log.e("" + reactantsEquivalent, "" + productsEquivalent);

        if (reactantsEquivalent == 0)
            reactantsEquivalent = productsEquivalent / yield;
        else if (productsEquivalent == 0)
            productsEquivalent = reactantsEquivalent * yield;


        setEmptyEquivalents(reactants, reactantsEquivalent);
        setEmptyEquivalents(products, productsEquivalent);


        updateSolutionViews();
        if ( currentSolutionView != null )
            solutionEditor.update(currentSolutionView.solution);
    }

    private void setEmptyEquivalents(ArrayList<ReactionSolution> solutions, double equivalent){

        for (ReactionSolution solution : solutions){
            double moles = equivalent * solution.stoichiometricCoefficient;
            if (solution.amount.SIValue <= 0) {
                solution.setSolute(moles, Amount.UnitType.MOLE);
            } else if ( !solution.concentration.isPure() && solution.concentration.concentrationValue <= 0){
                auxAmount.setSI(moles, Amount.UnitType.MOLE); //Solute
                auxAmount.molecularMass = solution.compound.getMolecularWeight();
                solution.concentration.setFromSolution(solution.amount, auxAmount);
            }
        }
    }

    public ReactionSolutionView getCurrentSolutionView() {
        return currentSolutionView;
    }

    public void deleteCurrentSolution() {
        solutions.remove(currentSolutionView.solution);
        products.remove(currentSolutionView.solution);
        reactants.remove(currentSolutionView.solution);
        addSolutionViews();
        currentSolutionView = null;
        solutionEditor.setVisibility(View.GONE);
    }

    private double getYield(){
        return Utils.parseDouble(yieldView.getText().toString())/100;
    }
}
