package com.hornedhorn.chemhelper.data;

import com.hornedhorn.chemhelper.data.Units.Concentration;
import com.hornedhorn.chemhelper.data.Units.Amount;
import com.hornedhorn.chemhelper.utils.Utils;

public class Solution {

    public static Amount auxAmount = new Amount();
    public static Concentration auxConcentration = new Concentration();

    public final Compound compound;

    private final Amount amount = new Amount();
    private final Amount oldAmount = new Amount();
    private final Concentration concentration = new Concentration();
    private final Concentration oldConcentration = new Concentration();

    private boolean amountCalculated = false;
    private boolean concentrationCalculated = false;



    public Solution(Compound compound){
        this.compound = compound;
        amount.setMolecularMass( compound.getMolecularWeight() );
    }

    public Solution(Solution solution){
        this.compound = solution.compound;
        this.amount.set(solution.amount);
        this.concentration.set(solution.concentration);
    }

    public Amount getAmount(){
        auxAmount.set(amount);
        return auxAmount;
    }

    public void setAmountValue( double value){
        amountCalculated = concentrationCalculated = false;
        amount.setValue( value );
    }

    public void setAmountUnit(Amount.Unit unit){
        if (amount.getUnit().unitType == unit.unitType)
            amount.setUnit(unit);
        else {
            amount.setValue(amount.getValue(), unit);
            amountCalculated = concentrationCalculated = false;
        }
    }

    public Concentration getConcentration(){
        auxConcentration.set(concentration);
        return auxConcentration;
    }

    public void setConcentrationValue( double value ){
        amountCalculated = concentrationCalculated = false;
        concentration.concentrationValue = value;
    }

    public void setConcentrationUnit(Concentration.ConcentrationUnit concentrationUnit){
        concentration.concentrationUnit = concentrationUnit;
    }

    public boolean isPure(){
        return concentration.isPure();
    }

    public Amount getSolute(){
        Amount solute = new Amount(Amount.Unit.MOLE);
        solute.setMolecularMass( compound.getMolecularWeight());
        concentration.getSoluteFromSolution(solute, amount);
        return solute;
    }

    /*public Amount getSolute( float rangeFraction ){
        Amount solute = new Amount(Amount.Unit.MOLE);
        solute.setMolecularMass( compound.getMolecularWeight());
        concentration.getSoluteFromSolution(solute, amount, rangeFraction);

        return solute;
    }*/

    public void setSolute(Amount soluteAmount){
        concentration.getSolutionFromSolute(amount, soluteAmount);
    }

    public void setSolute(double soluteAmount, Amount.Unit soluteUnit){
        auxAmount.setMolecularMass( compound.getMolecularWeight() );
        auxAmount.setFromValue(soluteAmount, soluteUnit);
        setSolute(auxAmount);
    }

    public void setCalculatedSolute(Amount soluteAmount){
        setSolute(soluteAmount);
        amountCalculated = true;
    }

    public void setCalculatedSolute(double soluteAmount, Amount.Unit soluteUnit){
        setSolute(soluteAmount, soluteUnit);
        amountCalculated = true;
    }
    public void setCalculatedConcentrationFromSolution(Amount solution, Amount solute){
        concentration.setFromSolution(solution, solute);
        concentrationCalculated = true;
    }

    public void setDensity(double density){
        amount.setDensity(density);
    }

    public double getDensity(){
        return amount.getDensity();
    }

    public void setPureDensity(double density) {
        concentration.pureDensity = density;
    }

    public double getPureDensity() {
        return concentration.pureDensity;
    }

    public boolean needsDensity() {
        if (concentration.isPure())
            return amount.getUnit().unitType == Amount.UnitType.VOLUME;
        return (concentration.concentrationUnit.solutionUnit != Amount.UnitType.VOLUME) == (amount.getUnit().unitType == Amount.UnitType.VOLUME);
    }

    public boolean needsPureDensity(){
        if (concentration.isPure())
            return false;
        else{
            return concentration.concentrationUnit.soluteUnit == Amount.UnitType.VOLUME;
        }
    }

    public boolean isCalculated() { return concentrationCalculated ||amountCalculated; }

    public boolean isConcentrationCalculated(){
        return concentrationCalculated;
    }

    public boolean isAmountCalculated(){
        return amountCalculated;
    }

    public void revert(){
        if (isAmountCalculated())
            amount.set(oldAmount);
        if (isConcentrationCalculated())
            concentration.set(oldConcentration);

        amountCalculated = concentrationCalculated = false;
    }

    public boolean hasAmount(){
        return amount.getValue() > 0;
    }

    public boolean hasConcentration(){
        return isPure() || concentration.concentrationValue > 0;
    }
}
