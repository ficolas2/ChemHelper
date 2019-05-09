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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hornedhorn.chemhelper.MainActivity;
import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.Utils;
import com.hornedhorn.chemhelper.data.Compound;
import com.hornedhorn.chemhelper.data.ReactionSolution;
import com.hornedhorn.chemhelper.data.Solution;
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
    private ArrayList<ReactionSolutionView> solutionViews = new ArrayList<>();

    private SolutionEditor solutionEditor;

    private LinearLayout reactantsLayout;
    private LinearLayout productsLayout;

    private TextView yieldView, errorText;

    private ReactionSolutionView currentSolutionView;
    private boolean currentSolutionReactant;

    private TextView reactionText;
    private Button reactionSubmit;

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

        reactionSubmit = view.findViewById(R.id.reaction_submit);
        reactionSubmit.setOnClickListener(new View.OnClickListener() {
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

        errorText = view.findViewById(R.id.reaction_error);

        addSolutionViews();

        super.onViewCreated(view, savedInstanceState);
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
            solutionViews.add(solutionView);

            ((LinearLayout.LayoutParams)solutionView.getLayoutParams()).gravity = Gravity.CENTER;
        }

        for (ReactionSolution solution : products){
            ReactionSolutionView solutionView = new ReactionSolutionView(getContext(), solution, this);
            productsLayout.addView(solutionView);
            productViews.add(solutionView);
            solutionViews.add(solutionView);

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
        for (ReactionSolutionView view : solutionViews)
            view.update();

        String error = getError();

        errorText.setVisibility(error != null ? View.VISIBLE:View.GONE);
        reactionSubmit.setEnabled(error == null);
        if (error!=null)
            errorText.setText(error);

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
        solutionEditor.setReactant(reactantViews.contains(solutionView));
        solutionEditor.update(solutionView.solution);
    }

    public void clearSolutionSelection(){
        for (ReactionSolutionView view : solutionViews){
            view.clearSelection();
        }
    }

    private String getError() {
        if (products.size() == 0 && reactants.size() == 0)
            return "Add products or reactants by clicking a + symbol.";
        else if (products.size() == 0)
            return "Add products by clicking the right + symbol.";
        else if (reactants.size() == 0)
            return "Add reactants by clicking the left + symbol.";

        double yield = getYield();

        double reactantsEquivalent = Utils.getEquivalent(reactants);
        double productsEquivalent = Utils.getEquivalent(products);

        if (productsEquivalent<0)
            return "Your products amounts don't match. Tip: leave only one amount.";
        if (reactantsEquivalent<0)
            return "Your reactants amounts don't match. Tip: leave only one amount.";

        if ( yield == 0 )
            if (productsEquivalent!= 0 && reactantsEquivalent != 0)
                yield = productsEquivalent / reactantsEquivalent; // Yield
            else
                return "Cannot calculate yield without at least one product and reactant amount.";

        if ( productsEquivalent != 0 && reactantsEquivalent != 0  &&
                !Utils.epsilonEqual(reactantsEquivalent, productsEquivalent / yield, 1./1000)){
            return "Your reactants amounts don't match with your products. Tip: leave only one amount.";
        }

        if ( reactantsEquivalent == 0 && productsEquivalent == 0) {
            return "Not enough data.";
        }

        String text = "";
        for (Solution solution : solutions){
            if (solution.needsDensity() && solution.getDensity() <= 0)
                text += solution.compound.getName() + " needs density.\n";
            if (solution.needsPureDensity() && solution.getPureDensity() <= 0)
                text += solution.compound.getName() + " needs pure density.\n";
        }

        return text.isEmpty() ? null:text;
    }

    private void calculate(){
        if (getError() != null)
            return;

        double yield = getYield();

        double reactantsEquivalent = Utils.getEquivalent(reactants);
        double productsEquivalent = Utils.getEquivalent(products);


        if ( yield == 0 ){
            if (productsEquivalent!= 0 && reactantsEquivalent != 0){
                yield = productsEquivalent / reactantsEquivalent; // Yield
                yieldView.setText(Utils.formatInputDouble(yield));
            }
        }

        // Reactants/products equivalent from the other one and yield
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
            double moles = equivalent * solution.stoichiometricCoefficient * ( 1 + solution.excess/100 );
            if (solution.amount.getValue() <= 0) {
                solution.setSolute(moles, Amount.Unit.MOLE);
            } else if ( !solution.concentration.isPure() && solution.concentration.concentrationValue <= 0){
                auxAmount.setValue(moles, Amount.Unit.MOLE); //Solute
                auxAmount.setMolecularMass( solution.compound.getMolecularWeight() );
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
