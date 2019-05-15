package com.hornedhorn.chemhelper.data.Units;

import android.util.Log;

public class Amount {

    public enum Unit {
        // MASS //
        MILIGRAM("mg", 1.0/1000000, UnitType.MASS), GRAM("g", 1.0/1000, UnitType.MASS),
        KILOGRAM("kg", 1, UnitType.MASS),
        TON("t", 1000, UnitType.MASS),
        // VOLUME //
        MILILITER("mL", 1.0/1000, UnitType.VOLUME),
        LITER("L", 1, UnitType.VOLUME),
        KILOLITER("kL", 1000, UnitType.VOLUME),
        // MOLES //
        MOLE("mol", 1, UnitType.MOLE);

        public final String str;
        public final double multiplier;
        public final UnitType unitType;
        Unit(String str, double multiplier, UnitType unitType){
            this.str = str;
            this.multiplier = multiplier;
            this.unitType = unitType;
        }

        public static Unit getUnit(String str){
            for ( Unit unit : Unit.values()) {
                if (unit.str.equals(str)) {
                    return unit;
                }
            }
            return null;
        }
    }

    public enum UnitType {
        MASS, VOLUME, MOLE;

        public Unit getSIUnit(){
            for (Unit unit : Unit.values()){
                if (unit.unitType == this && unit.multiplier == 1)
                    return unit;
            }
            return null;
        }
    }

    private static Amount auxAmount = new Amount();

    private double SIValue;
    private Unit unit = Unit.KILOGRAM;
    
    private double density;
    private double molecularMass;

    public Amount(){ }

    public  Amount(Unit unit){
        this.unit = unit;
    }

    public Amount(double value, Unit unit){
        this.unit = unit;
        this.SIValue = value;
    }

    public double getSI(UnitType wantedUnitType){
        switch (wantedUnitType){
            case MASS:
                if (unit.unitType == UnitType.MASS)
                    return SIValue;
                if (unit.unitType == UnitType.VOLUME)
                    return SIValue * density;
                if (unit.unitType == UnitType.MOLE)
                    return SIValue * molecularMass / 1000;
                return 0;
            case VOLUME:
                if (unit.unitType == UnitType.VOLUME)
                    return SIValue;
                return getSI(UnitType.MASS) / density;
            case MOLE:
                if ( unit.unitType == UnitType.MOLE )
                    return SIValue;
                return getSI(UnitType.MASS) / molecularMass * 1000;
        }
        return 0;
    }

    public void set(Amount amount){
        this.density = amount.density;
        this.molecularMass = amount.molecularMass;
        this.unit = amount.unit;
        this.SIValue = amount.SIValue;
    }

    public void setFromValue(double value, Unit fromUnit) {
        auxAmount.set(this);
        auxAmount.setValue(value, fromUnit);

        this.SIValue = auxAmount.getSI( unit.unitType );
    }

    public double getValue(){
        return SIValue / unit.multiplier;
    }


    public void setValue( double value ) {
        this.SIValue = value * unit.multiplier;
    }

    public void setValue( double value, Unit unit){
        this.unit = unit;
        setValue(value);
    }


    public boolean hasDensity(){
        return density > 0;
    }

    public boolean hasMolecularMass(){
        return molecularMass > 0;
    }


    public double getSIValue() {
        return SIValue;
    }

    public void setSIValue(double SIValue) {
        this.SIValue = SIValue;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public double getMolecularMass() {
        return molecularMass;
    }

    public void setMolecularMass(double molecularMass) {
        this.molecularMass = molecularMass;
    }

}
