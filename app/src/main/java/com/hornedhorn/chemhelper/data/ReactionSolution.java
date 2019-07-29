package com.hornedhorn.chemhelper.data;

import com.hornedhorn.chemhelper.data.Units.Amount;

public class ReactionSolution extends Solution {

    public int stoichiometricCoefficient = 1;
    public double excess = 0;
    public boolean isReactant;

    public ReactionSolution(Compound compound, boolean isReactant){
        super(compound);
        this.isReactant = isReactant;
    }

    public ReactionSolution(ReactionSolution reactionSolution){
        super(reactionSolution);
        this.stoichiometricCoefficient = reactionSolution.stoichiometricCoefficient;
        this.excess = reactionSolution.excess;
    }

    public double getEquivalent() {
        if ( !hasConcentration() || !hasAmount() )
            return 0;
        return getSolute().getSI(Amount.UnitType.MOLE) / stoichiometricCoefficient / ( 1 + excess/100 );
    }

}
