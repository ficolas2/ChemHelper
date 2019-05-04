package com.hornedhorn.chemhelper.data;

import com.hornedhorn.chemhelper.data.Units.Concentration;
import com.hornedhorn.chemhelper.data.Units.Amount;

public class Solution {

    public static Amount auxAmount = new Amount();

    public final Compound compound;
    public Compound solvent;

    public final Amount amount = new Amount();
    public final Concentration concentration = new Concentration();

    public double pureDensity, density;


    public Solution(Compound compound){
        this.compound = compound;
        amount.setMolecularMass( compound.getMolecularWeight() );
    }

    public Amount getSolute(){
        Amount solute = new Amount(Amount.Unit.MOLE);
        solute.setMolecularMass( compound.getMolecularWeight());
        concentration.getSoluteFromSolution(solute, amount);
        return solute;
    }

    public void setSolute(Amount soluteAmount){
        concentration.getSolutionFromSolute(amount, soluteAmount);
    }

    public void setSolute(double soluteAmount, Amount.Unit soluteUnit){
        auxAmount.setMolecularMass( compound.getMolecularWeight() );
        auxAmount.setFromValue(soluteAmount, soluteUnit);
        setSolute(auxAmount);
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
}
