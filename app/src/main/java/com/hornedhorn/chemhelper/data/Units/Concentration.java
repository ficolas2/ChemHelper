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

        public static ConcentrationUnit getConcentrationUnit(String str) {
            for (ConcentrationUnit concentrationUnit : ConcentrationUnit.values()){
                if (concentrationUnit.str.equals(str)) {
                    return concentrationUnit;
                }
            }
            return null;
        }
    }

    public double concentrationValue = 1;
    public ConcentrationUnit concentrationUnit = ConcentrationUnit.PURE;

    public double pureDensity;

    public void set(Concentration concentration) {
        this.concentrationValue = concentration.concentrationValue;
        this.concentrationUnit = concentration.concentrationUnit;
        this.pureDensity = concentration.pureDensity;
    }

    public void setConcentrationUnit(ConcentrationUnit concentrationUnit) {
        this.concentrationUnit = concentrationUnit;
    }

    public void getSoluteFromSolution(Amount soluteAmount, Amount solutionAmount){
        if ( isPure() )
            soluteAmount.setFromValue(solutionAmount.getValue(), solutionAmount.getUnit());
        else{
            soluteAmount.setDensity( pureDensity );
            soluteAmount.setFromValue(solutionAmount.getSI(concentrationUnit.solutionUnit) * concentrationValue
                            / (concentrationUnit.percent ? 100.:1),
                    concentrationUnit.soluteUnit.getSIUnit());
        }
    }

    public void getSolutionFromSolute(Amount solutionAmount, Amount soluteAmount){
        if ( isPure() )
            solutionAmount.setFromValue(soluteAmount.getValue(), soluteAmount.getUnit());
        else{
            soluteAmount.setDensity( pureDensity );
            solutionAmount.setFromValue(soluteAmount.getSI(concentrationUnit.soluteUnit) / concentrationValue
                            * (concentrationUnit.percent ? 100.:1),
                    concentrationUnit.solutionUnit.getSIUnit() );
        }
    }

    public void setFromSolution(Amount solution, Amount solute) {
        solute.setDensity( pureDensity );
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
