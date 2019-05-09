package com.hornedhorn.chemhelper.data;

import com.hornedhorn.chemhelper.data.Units.Amount;

public class ReactionSolution extends Solution {

    public double stoichiometricCoefficient = 1;
    public double excess = 0;

    public ReactionSolution(Compound compound){
        super(compound);
    }

    public double getEquivalent() {
        if (concentration.concentrationValue <= 0 || amount.getValue() <= 0 )
            return 0;
        return getSolute().getSI(Amount.UnitType.MOLE) / stoichiometricCoefficient / ( 1 + excess/100 );
    }
}
