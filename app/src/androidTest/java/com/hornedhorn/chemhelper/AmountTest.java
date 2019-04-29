package com.hornedhorn.chemhelper;

import com.hornedhorn.chemhelper.data.Units.Amount;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AmountTest {

    private double epsilon = 0.0000001;
    private double molecularMass = 23;
    private double density = 12;
    private double mol = 5;
    private double mass = mol * molecularMass / 1000;
    private double volume = mass / density;

    @Test
    public void getSI() {
        Amount amount = new Amount();
        amount.molecularMass = molecularMass;
        amount.density = density;

        amount.setSI(5, Amount.UnitType.MOLE);
        assertEquals(amount.getSI(Amount.UnitType.MOLE), mol, epsilon);
        assertEquals(amount.getSI(Amount.UnitType.MASS), mass, epsilon);
        assertEquals(amount.getSI(Amount.UnitType.VOLUME), volume, epsilon);

        amount.setSI(mass, Amount.UnitType.MASS);
        assertEquals(amount.getSI(Amount.UnitType.MOLE), mol, epsilon);
        assertEquals(amount.getSI(Amount.UnitType.MASS), mass, epsilon);
        assertEquals(amount.getSI(Amount.UnitType.VOLUME), volume, epsilon);

        amount.setSI(volume, Amount.UnitType.VOLUME);
        assertEquals(amount.getSI(Amount.UnitType.MOLE), mol, epsilon);
        assertEquals(amount.getSI(Amount.UnitType.MASS), mass, epsilon);
        assertEquals(amount.getSI(Amount.UnitType.VOLUME), volume, epsilon);
    }

    @Test
    public void setFromSI(){
        Amount amount = new Amount();
        amount.molecularMass = molecularMass;
        amount.density = density;

        //Mole
        amount.setSI(0, Amount.UnitType.MOLE);

        amount.setFromSI(mass, Amount.UnitType.MASS);
        assertEquals(amount.SIValue, mol, epsilon);
        amount.setFromSI(volume, Amount.UnitType.VOLUME);
        assertEquals(amount.SIValue, mol, epsilon);
        amount.setFromSI(mol, Amount.UnitType.MOLE);
        assertEquals(amount.SIValue, mol, epsilon);

        //Mass
        amount.setSI(0, Amount.UnitType.MASS);

        amount.setFromSI(mass, Amount.UnitType.MASS);
        assertEquals(amount.SIValue, mass, epsilon);
        amount.setFromSI(volume, Amount.UnitType.VOLUME);
        assertEquals(amount.SIValue, mass, epsilon);
        amount.setFromSI(mol, Amount.UnitType.MOLE);
        assertEquals(amount.SIValue, mass, epsilon);

        //Volume
        amount.setSI(0, Amount.UnitType.VOLUME);

        amount.setFromSI(mass, Amount.UnitType.MASS);
        assertEquals(amount.SIValue, volume, epsilon);
        amount.setFromSI(volume, Amount.UnitType.VOLUME);
        assertEquals(amount.SIValue, volume, epsilon);
        amount.setFromSI(mol, Amount.UnitType.MOLE);
        assertEquals(amount.SIValue, volume, epsilon);
    }
}
