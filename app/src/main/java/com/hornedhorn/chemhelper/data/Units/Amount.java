package com.hornedhorn.chemhelper.data.Units;

import android.util.Log;

public class Amount {

    public void setFromSI(double value, UnitType fromUnitType) {
        UnitType currentUnitType = unitType;
        setSI(value, fromUnitType);
        setSI(getSI(currentUnitType), currentUnitType);
    }

    public enum UnitType {
        MASS("kg"), VOLUME("L"), MOLE("mol");

        public final String str;
        UnitType(String str){
            this.str = str;
        }
    }

    public double SIValue;
    public UnitType unitType = UnitType.MASS;
    
    public double density;
    public double molecularMass;

    public Amount(){ }

    public  Amount(UnitType unitType){
        this.unitType = unitType;
    }

    public Amount(double value, UnitType unitType){
        this.unitType = unitType;
        this.SIValue = value;
    }

    public void setSI(double value, UnitType unitType){
        this.unitType = unitType;
        this.SIValue = value;
    }

    public void setUnitType(String str) {
        for ( UnitType unitType : UnitType.values())
            if (unitType.str.equals(str)) {
                this.unitType = unitType;
                return;
            }
    }


    public double getSI(UnitType wantedUnitType){
        switch (wantedUnitType){
            case MASS:
                if (unitType == UnitType.MASS)
                    return SIValue;
                if (unitType == UnitType.VOLUME)
                    return SIValue * density;
                if (unitType == UnitType.MOLE)
                    return SIValue * molecularMass / 1000;
                return 0;
            case VOLUME:
                if (unitType == UnitType.VOLUME)
                    return SIValue;
                return getSI(UnitType.MASS) / density;
            case MOLE:
                if ( unitType == UnitType.MOLE )
                    return SIValue;
                return getSI(UnitType.MASS) / molecularMass * 1000;
        }
        return 0;
    }

    public boolean hasDensity(){
        return density > 0;
    }

    public boolean hasMolecularMass(){
        return density > 0;
    }

}
