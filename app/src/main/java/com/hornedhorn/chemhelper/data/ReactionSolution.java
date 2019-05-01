package com.hornedhorn.chemhelper.data;

public class ReactionSolution extends Solution {

    public double stoichiometricCoefficient = 1;
    public double excess = 0;

    public ReactionSolution(Compound compound){
        super(compound);
    }
}
