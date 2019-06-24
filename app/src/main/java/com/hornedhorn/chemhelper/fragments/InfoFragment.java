package com.hornedhorn.chemhelper.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.data.Compound;
import com.hornedhorn.chemhelper.data.Element;

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
        nameView.setText(compound.name.substring(0, 1).toUpperCase() + compound.name.substring(1));

        TextView formulaView = view.findViewById(R.id.info_formula);
        String formulaStr = compound.getFormulaString();
        if (compound instanceof Element)
            formulaStr += " (" + ((Element) compound).atomicNumber + ")";
        formulaView.setText(formulaStr);

        LinearLayout meltingLayout = view.findViewById(R.id.info_melting);
        meltingLayout.setVisibility(compound.meltingPoint == null ? View.GONE:View.VISIBLE);
        TextView meltingView = view.findViewById(R.id.info_melting_point);
        meltingView.setText(compound.getMeltingPointString());

        LinearLayout boilingLayout = view.findViewById(R.id.info_boiling);
        boilingLayout.setVisibility(compound.boilingPoint == null ? View.GONE:View.VISIBLE);
        TextView boilingView = view.findViewById(R.id.info_boiling_point);
        boilingView.setText(compound.getBoilingPointString());
    }

    public void setCompound(Compound compound){
        this.compound = compound;
    }
}
