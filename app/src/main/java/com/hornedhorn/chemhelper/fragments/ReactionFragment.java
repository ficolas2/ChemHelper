package com.hornedhorn.chemhelper.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
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
import com.hornedhorn.chemhelper.utils.Utils;
import com.hornedhorn.chemhelper.data.Compound;
import com.hornedhorn.chemhelper.data.ReactionSolution;
import com.hornedhorn.chemhelper.data.Solution;
import com.hornedhorn.chemhelper.data.Units.Amount;
import com.hornedhorn.chemhelper.utils.InputFilterMinMax;
import com.hornedhorn.chemhelper.views.ReactionSolutionView;
import com.hornedhorn.chemhelper.views.SolutionEditor;
import com.hornedhorn.chemhelper.views.SolutionEditorCaller;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;

import java.util.ArrayList;

public class ReactionFragment extends CompoundReciverFragment implements SolutionEditorCaller {

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

    private TextView yieldView, errorText, balanceText;

    private TextView reactionText;
    private Button reactionSubmit, revertCalculation;

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

        revertCalculation = view.findViewById(R.id.reaction_revert);
        revertCalculation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ReactionSolutionView reactionSolutionView : solutionViews)
                    reactionSolutionView.revertSolution();
                updateSolutionViews();
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
        solutionEditor.setSolutionEditorCaller( this );

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
        balanceText = view.findViewById(R.id.reaction_balance);
        balanceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balance();
            }
        });

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

        balanceText.setVisibility(isBalanced() ? View.GONE : View.VISIBLE);
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
        revertCalculation.setVisibility(View.GONE);
        for (ReactionSolutionView view : solutionViews) {
            view.update();
            if (view.solution.isCalculated())
                revertCalculation.setVisibility(View.VISIBLE);
        }

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
        ReactionSolution solution = new ReactionSolution(compound, receivingReactant);
        solutions.add(solution);
        if (receivingReactant) {
            reactants.add(solution);
        }else{
            products.add(solution);
        }
    }

    public void selectSolution(ReactionSolutionView solutionView){
        this.solutionEditor.editingSolution = solutionView.solution;
        clearSolutionSelection();
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

        String text = "";
        for (Solution solution : solutions){
            if (solution.needsDensity() && solution.getDensity() <= 0)
                text += solution.compound.getName() + " needs density.\n";
            if (solution.needsPureDensity() && solution.getPureDensity() <= 0)
                text += solution.compound.getName() + " needs pure density.\n";
        }
        if (!text.isEmpty())
            return text.substring(0, text.length()-1);

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
                !Utils.epsilonEqual(reactantsEquivalent, productsEquivalent / yield, 1E-5)){
            return "Your reactants amounts don't match with your products. Tip: leave only one amount.";
        }

        if ( reactantsEquivalent == 0 && productsEquivalent == 0) {
            return "Not enough data.";
        }

        return null;
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
        solutionEditor.update();
    }

    private void setEmptyEquivalents(ArrayList<ReactionSolution> solutions, double equivalent){

        for (ReactionSolution solution : solutions){
            double moles = equivalent * solution.stoichiometricCoefficient * ( 1 + solution.excess/100 );
            if ( !solution.hasAmount() ) {
                solution.setCalculatedSolute(moles, Amount.Unit.MOLE);
            } else if ( !solution.hasConcentration() ){
                auxAmount.setValue(moles, Amount.Unit.MOLE); //Solute
                auxAmount.setMolecularMass( solution.compound.getMolecularWeight() );
                solution.setCalculatedConcentrationFromSolution(solution.getAmount(), auxAmount);
            }
        }
    }

    public void deleteEditingSolution() {
        ReactionSolution solution = (ReactionSolution) solutionEditor.editingSolution;
        solutions.remove(solution);
        products.remove(solution);
        reactants.remove(solution);
        addSolutionViews();
        solutionEditor.editingSolution = null;
        solutionEditor.setVisibility(View.GONE);
    }

    private double getYield(){
        return Utils.parseDouble(yieldView.getText().toString())/100;
    }

    private void balance(){
        SparseArray<Integer> reactantElements = getElements(reactants);
        SparseArray<Integer> productElements = getElements(products);
        ArrayList<Integer> atomicNumbers = new ArrayList<>();

        if (reactantElements.size() != productElements.size()) {
            Utils.errorBox("Reactants and products don't have the same elements.", getContext());
            return;
        }

        for (int i = 0; i<reactantElements.size(); i++){
            int atomicNumber = reactantElements.keyAt(i);
            if (productElements.get(atomicNumber) == null){
                Utils.errorBox("Reactants and products don't have the same elements.", getContext());
                return;
            }

            atomicNumbers.add(atomicNumber);
        }

        double[][] matrixData = new double[atomicNumbers.size()][solutions.size()];

        //Set up matrix
        for (int i = 0; i<solutions.size(); i++){
            Solution solution = solutions.get(i);
            SparseArray<Integer> elements = solution.compound.getElements();
            int multiplier = reactants.contains(solution) ? 1:-1;
            for (int j =0; j<elements.size(); j++){
                int atomicNumber = elements.keyAt(j);
                matrixData[atomicNumbers.indexOf(atomicNumber)][i] = elements.get(atomicNumber) * multiplier;
            }
        }

        RealVector realVector = new ArrayRealVector(atomicNumbers.size());
        RealMatrix coefficients = MatrixUtils.createRealMatrix(atomicNumbers.size(), solutions.size() - 1);
        RealVector systemResults;

        for (int i = 0; i < solutions.size(); i++){
            getMatrix(realVector, coefficients, matrixData, i);

            try {
                DecompositionSolver solver = new QRDecomposition(coefficients).getSolver();
                systemResults = solver.solve(realVector);

                int unitCoeff = 1;

                for (int j = 0; j<solutions.size(); j++){
                    ReactionSolution solution = solutions.get(j);
                    if (j == i)
                        Log.e (solution.compound.getName(), "" + unitCoeff);
                    else
                        Log.e (solution.compound.getName(), "" + systemResults.getEntry(j>i ? j-1:j));
                }

                int minDenominator;
                do{
                    minDenominator = -1;
                    for (int solutionNumber = 0; solutionNumber < systemResults.getDimension(); solutionNumber++){
                        double result = systemResults.getEntry(solutionNumber);
                        if (result - Math.floor(result + 1E-6)>1E-6){
                            int denominator = Utils.getDenominator(result);
                            if (minDenominator>denominator || minDenominator<0)
                                minDenominator = denominator;
                        }
                    }
                    if (minDenominator>0){
                        unitCoeff *= minDenominator;
                        systemResults.mapMultiplyToSelf(minDenominator);
                        Log.e("Multiplied by", "" + minDenominator);
                    }
                }while (minDenominator>0);

                for (int j = 0; j<solutions.size(); j++){
                    ReactionSolution solution = solutions.get(j);
                    if (j == i)
                        Log.e (solution.compound.getName(), "" + unitCoeff);
                    else
                        Log.e (solution.compound.getName(), "" + systemResults.getEntry(j>i ? j-1:j));
                }

                for (int j = 0; j<solutions.size(); j++){
                    ReactionSolution solution = solutions.get(j);
                    if (j == i)
                        solution.stoichiometricCoefficient = unitCoeff;
                    else
                        solution.stoichiometricCoefficient = (int)Math.round(systemResults.getEntry(j>i ? j-1:j));
                }

                updateSolutionViews();
                break;
            }catch (SingularMatrixException e){
            }
        }

    }

    private void getMatrix(RealVector vector, RealMatrix coefficients, double[][] matrixData, int deletedColumn){
        for (int x = 0; x<matrixData.length; x++){
            for (int y = 0; y<matrixData[0].length; y++){
                if (y == deletedColumn)
                    vector.setEntry(x,  -matrixData[x][y]);
                else
                    coefficients.setEntry(x, y>deletedColumn ? y-1:y, matrixData[x][y]);
            }
        }
    }

    private boolean isBalanced(){

        SparseArray<Integer> reactantElements = getElements(reactants);
        SparseArray<Integer> productElements = getElements(products);

        if (reactantElements.size() != productElements.size())
            return false;

        for (int i = 0; i<reactantElements.size(); i++){
            int atomicNumber = reactantElements.keyAt(i);
            if (reactantElements.get(atomicNumber, 0) != productElements.get(atomicNumber, 0))
                return false;
        }

        return true;
    }

    private SparseArray<Integer> getElements(ArrayList<ReactionSolution> solutions){
        SparseArray<Integer> totalElements = new SparseArray<>();
        for (ReactionSolution solution : solutions){
            SparseArray<Integer> elements = solution.compound.getElements();
            for (int i = 0; i<elements.size(); i++){
                int atomicNumber = elements.keyAt(i);
                totalElements.put(atomicNumber, totalElements.get(atomicNumber,
                        0) + elements.get(atomicNumber) * solution.stoichiometricCoefficient);
            }
        }
        return totalElements;
    }

    @Override
    public void solutionEdited() {
        updateSolutionViews();
    }
}
