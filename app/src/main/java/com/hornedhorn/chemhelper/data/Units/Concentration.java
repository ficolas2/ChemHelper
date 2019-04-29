package com.hornedhorn.chemhelper.data.Units;

import android.util.Log;

import com.hornedhorn.chemhelper.data.Units.Amount.UnitType;

public class Concentration {

    public enum ConcentrationUnit{
        MOLAR(UnitType.VOLUME, UnitType.MOLE, "M"), MASS_PERCENT(UnitType.MASS, UnitType.MASS, "wt%", true),
        VOLUME_PERCENT(UnitType.VOLUME, UnitType.VOLUME, "vol%", true),
        MOL_PERCENT(UnitType.MOLE, UnitType.MOLE, "mol%", true), MASS_VOLUME(UnitType.VOLUME, UnitType.MASS, "kg/L"),
        MOLAL(UnitType.MASS, UnitType.MOLE, "b"), PURE(UnitType.MOLE, UnitType.MOLE, "Pure");


        public final String str;
        public final UnitType solutionUnit, soluteUnit;
        public final boolean percent;

        ConcentrationUnit(UnitType solutionUnit, UnitType soluteUnit, String str){
            this.solutionUnit = solutionUnit;
            this.soluteUnit = soluteUnit;
            this.str = str;
            this.percent = false;
        }

        ConcentrationUnit(UnitType solutionUnit, UnitType soluteUnit, String str, boolean percent){
            this.solutionUnit = solutionUnit;
            this.soluteUnit = soluteUnit;
            this.str = str;
            this.percent = percent;
        }
    }

    public double concentrationValue = 1;
    public ConcentrationUnit concentrationUnit = ConcentrationUnit.PURE;

    public double pureDensity = 1;

    public void setConcentrationUnit(String str) {
        for (ConcentrationUnit concentrationUnit : ConcentrationUnit.values())
            if (concentrationUnit.str.equals(str)) {
                this.concentrationUnit = concentrationUnit;
                return;
            }
    }

    public void getSoluteFromSolution(Amount soluteAmount, Amount solutionAmount){
        if ( isPure() )
            soluteAmount.setFromSI(solutionAmount.SIValue, solutionAmount.unitType);
        else{
            soluteAmount.density = pureDensity;
            soluteAmount.setFromSI(solutionAmount.getSI(concentrationUnit.solutionUnit) * concentrationValue
                            / (concentrationUnit.percent ? 100.:1),
                    concentrationUnit.soluteUnit);
        }
    }

    public void getSolutionFromSolute(Amount solutionAmount, Amount soluteAmount){
        if ( isPure() )
            solutionAmount.setFromSI(soluteAmount.SIValue, soluteAmount.unitType);
        else{
            soluteAmount.density = pureDensity;
            solutionAmount.setFromSI(soluteAmount.getSI(concentrationUnit.soluteUnit) / concentrationValue
                            * (concentrationUnit.percent ? 100.:1),
                    concentrationUnit.solutionUnit );
        }
    }

    public void setFromSolution(Amount solution, Amount solute) {
        solute.density = pureDensity;
        concentrationValue = solute.getSI(concentrationUnit.soluteUnit) / solution.getSI(concentrationUnit.solutionUnit)
                * (concentrationUnit.percent ? 100:1);
    }

    public boolean isPure() {
        if (concentrationUnit == ConcentrationUnit.PURE)
            return true;
        if (concentrationUnit.percent && concentrationValue == 100)
            return true;
        return false;
    }
}
