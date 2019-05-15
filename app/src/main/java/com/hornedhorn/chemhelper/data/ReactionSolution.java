package com.hornedhorn.chemhelper.data;

import com.hornedhorn.chemhelper.data.Units.Amount;

public class ReactionSolution extends Solution {

    public int stoichiometricCoefficient = 1;
    public double excess = 0;

    public ReactionSolution(Compound compound){
        super(compound);
    }

    public ReactionSolution(ReactionSolution reactionSolution){
        super(reactionSolution);
        this.stoichiometricCoefficient = reactionSolution.stoichiometricCoefficient;
        this.excess = reactionSolution.excess;
    }

    public double getEquivalent() {
        if (concentration.concentrationValue <= 0 || amount.getValue() <= 0 )
            return 0;
        return getSolute().getSI(Amount.UnitType.MOLE) / stoichiometricCoefficient / ( 1 + excess/100 );
    }

}
