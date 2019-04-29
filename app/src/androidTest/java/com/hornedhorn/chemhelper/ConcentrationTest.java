package com.hornedhorn.chemhelper;

import com.hornedhorn.chemhelper.data.Units.Amount;
import com.hornedhorn.chemhelper.data.Units.Concentration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConcentrationTest {

    private Concentration molar, wtp, volp, molp, kgl, molal, pure;
    private double epsilon = 0.0000001;

    {
        molar = new Concentration();
        molar.concentrationUnit = Concentration.ConcentrationUnit.MOLAR;
        molar.concentrationValue = 25;

        wtp = new Concentration();
        wtp.concentrationUnit = Concentration.ConcentrationUnit.MASS_PERCENT;
        wtp.concentrationValue = 25;

        volp = new Concentration();
        volp.concentrationUnit = Concentration.ConcentrationUnit.VOLUME_PERCENT;
        volp.concentrationValue = 25;

        molp = new Concentration();
        molp.concentrationUnit = Concentration.ConcentrationUnit.MOL_PERCENT;
        molp.concentrationValue = 25;

        kgl = new Concentration();
        kgl.concentrationUnit = Concentration.ConcentrationUnit.MASS_VOLUME;
        kgl.concentrationValue = 25;

        molal = new Concentration();
        molal.concentrationUnit = Concentration.ConcentrationUnit.MOLAL;
        molal.concentrationValue = 25;

        pure = new Concentration();
        pure.concentrationUnit = Concentration.ConcentrationUnit.PURE;

    }

    @Test
    public void getSoluteFromSolution(){
        Amount solution = new Amount();
        Amount solute = new Amount();
        solute.molecularMass = 1;
        solute.density = 1;

        solution.setSI(5, Amount.UnitType.VOLUME);
        molar.getSoluteFromSolution(solute, solution);
        assertEquals(125.0, solute.getSI(Amount.UnitType.MOLE), epsilon);

        solution.setSI(5, Amount.UnitType.MASS);
        wtp.getSoluteFromSolution(solute, solution);
        assertEquals(5.0*0.25, solute.getSI(Amount.UnitType.MASS), epsilon);

        solution.setSI(5, Amount.UnitType.VOLUME);
        volp.getSoluteFromSolution(solute, solution);
        assertEquals(5.0*0.25, solute.getSI(Amount.UnitType.VOLUME), epsilon);

        solution.setSI(5, Amount.UnitType.MOLE);
        molp.getSoluteFromSolution(solute, solution);
        assertEquals(5.0*0.25, solute.getSI(Amount.UnitType.MOLE), epsilon);

        solution.setSI(5, Amount.UnitType.VOLUME);
        kgl.getSoluteFromSolution(solute, solution);
        assertEquals(125, solute.getSI(Amount.UnitType.MASS), epsilon);

        solution.setSI(5, Amount.UnitType.MASS);
        molal.getSoluteFromSolution(solute, solution);
        assertEquals(125, solute.getSI(Amount.UnitType.MOLE), epsilon);

        solution.setSI(5, Amount.UnitType.MASS);
        pure.getSoluteFromSolution(solute, solution);
        assertEquals(5.0, solute.getSI(Amount.UnitType.MASS), epsilon);

    }

    @Test
    public void getSolutionFromSolute(){
        Amount solution = new Amount();
        Amount solute = new Amount();
        solution.molecularMass = 1;
        solution.density = 1;

        solute.setSI(125, Amount.UnitType.MOLE);
        molar.getSolutionFromSolute(solution, solute);
        assertEquals(5.0, solution.getSI(Amount.UnitType.VOLUME), epsilon);

        solute.setSI(5.0*0.25, Amount.UnitType.MASS);
        wtp.getSolutionFromSolute(solution, solute);
        assertEquals(5.0, solution.getSI(Amount.UnitType.MASS), epsilon);

        solute.setSI(5.0*0.25, Amount.UnitType.VOLUME);
        volp.getSolutionFromSolute(solution, solute);
        assertEquals(5.0, solution.getSI(Amount.UnitType.VOLUME), epsilon);

        solute.setSI(5.0*0.25, Amount.UnitType.MOLE);
        molp.getSolutionFromSolute(solution, solute);
        assertEquals(5.0, solution.getSI(Amount.UnitType.MOLE), epsilon);

        solute.setSI(125, Amount.UnitType.MASS);
        kgl.getSolutionFromSolute(solution, solute);
        assertEquals(5, solution.getSI(Amount.UnitType.VOLUME), epsilon);

        solute.setSI(125, Amount.UnitType.MOLE);
        molal.getSolutionFromSolute(solution, solute);
        assertEquals(5, solution.getSI(Amount.UnitType.MASS), epsilon);

        solute.setSI(5, Amount.UnitType.MASS);
        pure.getSolutionFromSolute(solution, solute);
        assertEquals(5, solution.getSI(Amount.UnitType.MASS), epsilon);
    }

    @Test
    public void setFromSolution(){
        Amount solution = new Amount(10, Amount.UnitType.MASS);
        Amount solute = new Amount(2.5, Amount.UnitType.MASS);
        wtp.setFromSolution(solution, solute);
    }

}
