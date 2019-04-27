package com.hornedhorn.chemhelper.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.data.Compound;

public class InfoFragment extends CompoundReciverFragment {

    private Compound compound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.content_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (compound == null)
            return;

        TextView nameView = view.findViewById(R.id.info_name);
        nameView.setText(compound.name);

        TextView formulaView = view.findViewById(R.id.info_formula);
        formulaView.setText(compound.getFormulaString());

        TextView meltingView = view.findViewById(R.id.info_melting_point);
        meltingView.setText(compound.getMeltingPointString());

        TextView boilingView = view.findViewById(R.id.info_boiling_point);
        boilingView.setText(compound.getBoilingPointString());
    }

    public void setCompound(Compound compound){
        this.compound = compound;
    }
}
